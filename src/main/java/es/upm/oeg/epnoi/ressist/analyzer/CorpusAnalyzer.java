package es.upm.oeg.epnoi.ressist.analyzer;

import es.upm.oeg.epnoi.harvester.domain.ResearchObject;
import es.upm.oeg.epnoi.matching.metrics.domain.entity.ConceptualResource;
import es.upm.oeg.epnoi.matching.metrics.domain.entity.RegularResource;
import es.upm.oeg.epnoi.matching.metrics.domain.entity.TopicalResource;
import es.upm.oeg.epnoi.matching.metrics.domain.space.ConceptsSpace;
import es.upm.oeg.epnoi.matching.metrics.domain.space.TopicsSpace;
import es.upm.oeg.epnoi.matching.metrics.similarity.SimilarityMatrix;
import es.upm.oeg.epnoi.matching.metrics.topics.LDASettings;
import es.upm.oeg.epnoi.ressist.comparator.SimilarityComparator;
import es.upm.oeg.epnoi.ressist.parser.CRParser;
import es.upm.oeg.epnoi.ressist.parser.ROPair;
import es.upm.oeg.epnoi.ressist.parser.ROParser;
import es.upm.oeg.epnoi.ressist.parser.RRParser;
import org.apache.commons.collections.buffer.PriorityBuffer;
import org.apache.commons.lang.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.rdd.RDD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import scala.Option;
import scala.Tuple2;
import scala.Tuple3;
import scala.collection.Iterable;
import scala.collection.JavaConverters;

import java.io.File;
import java.util.*;

/**
 * Created by cbadenes on 22/06/15.
 */
@Component
public class CorpusAnalyzer {


    private static final Logger log = LoggerFactory.getLogger(CorpusAnalyzer.class);

    @Value("${spark.node}")
    protected String sparkNode;

    @Value("${lda.maxIterations}")
    protected Integer ldaMaxIterations;

    @Value("${lda.topics}")
    protected Integer ldaTopics;

    @Value("${lda.alpha}")
    protected Double ldaAlpha;

    @Value("${lda.beta}")
    protected Double ldaBeta;

    @Value("${ro.directory}")
    protected String inputDir;

    @Value("${nsga.enabled}")
    protected Boolean learningEnabled;

    @Value("${nsga.maxEvaluations}")
    protected Integer learningMaxEvaluations;


    @Autowired
    ROParser roParser;

    @Autowired
    RRParser rrParser;

    @Autowired
    CRParser crParser;

    @Autowired
    ROPair roPair;

    public void execute(){

        // directory of research objects as json files
        String directory = "file://" + new File(inputDir).getAbsolutePath();


        // Initialize Spark Context
        SparkConf conf = new SparkConf().setMaster(sparkNode).setAppName("Ressist");
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Load directory as RDD of file (name,content)
        JavaPairRDD<String, String> input = sc.wholeTextFiles(directory);

        // Convert files to Research Objects
        JavaRDD<ResearchObject> researchObjects = input.flatMap(roParser);
        log.info("Number of Research Objects: " + researchObjects.count());

        // Map of uri/title
        Map<String, String> roPairs = researchObjects.mapToPair(roPair).collectAsMap();


        // Convert Research Objects to Regular Resources
        JavaRDD<RegularResource> regularResources = researchObjects.map(rrParser);
        log.info("Number of Regular Resources: " + regularResources.count());


        // Convert Regular Resources to Conceptual Resources
        JavaRDD<ConceptualResource> conceptualResources = regularResources.map(crParser);
        log.info("Number of Conceptual Resources: " + regularResources.count());


        // Create the Concepts Space
        ConceptsSpace conceptsSpace = new ConceptsSpace(conceptualResources.rdd());

        if (learningEnabled){
            // LDA Optimization based on NSGA-III
            LDASettings.learn(conceptsSpace.featureVectors(),learningMaxEvaluations,ldaMaxIterations);
        }else{
            // Manual Configuration
            LDASettings.setMaxIterations(ldaMaxIterations);
            LDASettings.setTopics(ldaTopics);
            LDASettings.setAlpha(ldaAlpha);
            LDASettings.setBeta(ldaBeta);
        }

        // ro-graph
        StringBuilder graph = new StringBuilder();
        graph.append("\n").append("N1 N2\n");

        // Create the Topics Space
        TopicsSpace topicsSpace = new TopicsSpace(conceptsSpace);

        // Obtain the topical resources
        RDD<TopicalResource> topicalResources = topicsSpace.topicalResources();

        // Create the similarity matrix
//        RDD<Tuple2<TopicalResource, Iterable<Tuple3<TopicalResource, TopicalResource, Object>>>> similarityMatrixRDD = topicsSpace.cross(topicalResources);

        RDD<Tuple2<TopicalResource, Iterable<Tuple3<TopicalResource, TopicalResource, Object>>>> similarityMatrixRDD = SimilarityMatrix.apply(topicalResources);

        List<Tuple2<TopicalResource, Iterable<Tuple3<TopicalResource, TopicalResource, Object>>>> similarityMatrix = similarityMatrixRDD.toJavaRDD().collect();

        log.info("Conceptual Resources: " + conceptualResources.count() + ", Matrix Size: " + similarityMatrix.size());

        Map<String,Long> record = new HashMap<String,Long>();

        // [Temporal] Only to print similarity. Do not use 'collect' for production mode
        for(Tuple2<TopicalResource, Iterable<Tuple3<TopicalResource, TopicalResource, Object>>> tuple: similarityMatrix){
            StringBuilder similarityDescription = new StringBuilder();

            TopicalResource topicalResourceRef = tuple._1();

            similarityDescription.append("## ").append(roPairs.get(topicalResourceRef.conceptualResource().resource().uri())).append("\n");

            Collection<Tuple3<TopicalResource, TopicalResource, Object>> others = JavaConverters.asJavaCollectionConverter(tuple._2()).asJavaCollection();


            PriorityBuffer buffer = new PriorityBuffer(false,new SimilarityComparator());



            for (Tuple3<TopicalResource, TopicalResource, Object> tuple3: others){

                buffer.add(tuple3);
//
//                similarityDescription.append("\t ").append(tuple3._3()).append("\t").
//                        append(roPairs.get(tuple3._2().conceptualResource().resource().uri())).append("\n");
            }



            while(!buffer.isEmpty()){
                Tuple3<TopicalResource, TopicalResource, Object> tuple3 = (Tuple3<TopicalResource, TopicalResource, Object>) buffer.remove();
                Double similarity = (Double) tuple3._3();

                String sourceNode = StringUtils.substringAfterLast(tuple3._1().conceptualResource().resource().uri(),"/");
                String sourceType = StringUtils.substringBefore(StringUtils.substringAfterLast(tuple3._1().conceptualResource().resource().url(),"oaipmh/"),"/");
                String targetNode = StringUtils.substringAfterLast(tuple3._2().conceptualResource().resource().uri(),"/");
                String targetType = StringUtils.substringBefore(StringUtils.substringAfterLast(tuple3._2().conceptualResource().resource().url(), "oaipmh/"), "/");
                String targetTitle = tuple3._2().conceptualResource().resource().metadata().title().replace("\n"," ");

                if (similarity > 0.5 && similarity < 1.0){
                    // Add to ro-graph

                    String key1 = sourceNode+"-"+targetNode;
                    String key2 = targetNode+"-"+sourceNode;
                    Boolean validation = !record.containsKey(key1) && !record.containsKey(key2);
                    if (validation){
                        graph.append(sourceNode).append(" ").append(sourceType).append(" ").append(targetNode).append(" ").append(targetType).append(" ").append(similarity).append("\n");
                        record.put(key1, 1L);
                        record.put(key2, 1L);
                    }


                }


                similarityDescription.append("\t ").append(similarity).append("\t").
                        append("[").append(targetType).append("]").append("[").append(targetNode).append("]").
                        append(targetTitle).append("\n");
            }


            log.info(similarityDescription.toString());
        }




        // Distribution of topics by documents

        Map<Long, ConceptualResource> documents = new HashMap<>();

        for (Tuple2<Object, ConceptualResource> docEl : conceptsSpace.conceptualResourcesMap().toJavaRDD().collect()){

            documents.put((Long) docEl._1(), docEl._2());
        }

        for (Tuple2<Object, Vector> topicTuple : topicsSpace.model().ldaModel().topicDistributions().toJavaRDD().collect()){

            StringBuilder docDescription = new StringBuilder();

            Long docId = (Long) topicTuple._1();

            String docTitle = documents.get(docId).resource().metadata().title();

            String docSource = StringUtils.substringBefore(StringUtils.substringAfterLast(documents.get(docId).resource().url(), "oaipmh/"), "/");

            docDescription.append("'").append(docTitle).append("'[").append(docSource).append("]: (");

            for (Double dist: topicTuple._2().toArray()){
                docDescription.append(dist).append(",");
            }
            docDescription.append(")");

            log.info(docDescription.toString());
        }

        // Distribution of words by topics
        Integer maxWords = 20;
        Integer topicId = 0;
        for (Tuple2<int[], double[]> topicDist: topicsSpace.model().ldaModel().describeTopics(maxWords)){
            StringBuilder topicDescription = new StringBuilder();
            topicDescription.append("Topic[").append(topicId++).append("]: ");
            for(int i = 0; i < maxWords; i++){
                String word = conceptsSpace.vocabulary().wordsByKeyMap().get(topicDist._1()[i]).get();
                topicDescription.append("'").append(word).append("',");
            }
            log.info(topicDescription.toString());
        }


        log.info("Graph: ");
        log.info(graph.toString());


    }

}
