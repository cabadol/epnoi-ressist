package es.upm.oeg.epnoi.ressist.parser;

import es.upm.oeg.epnoi.harvester.domain.Creator;
import es.upm.oeg.epnoi.harvester.domain.ResearchObject;
import es.upm.oeg.epnoi.matching.metrics.domain.entity.Author;
import es.upm.oeg.epnoi.matching.metrics.domain.entity.Metadata;
import es.upm.oeg.epnoi.matching.metrics.domain.entity.RegularResource;
import org.apache.spark.api.java.function.Function;
import org.springframework.stereotype.Component;
import scala.collection.JavaConverters;
import scala.collection.mutable.Buffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbadenes on 22/06/15.
 */
@Component
public class RRParser implements Function<ResearchObject, RegularResource> {

    @Override
    public RegularResource call(ResearchObject researchObject) throws Exception {

        String uri                      = researchObject.getUri();
        String url                      = researchObject.getUrl();
        String title                    = researchObject.getMetainformation().getTitle();
        String published                = researchObject.getMetainformation().getPublished();
        Buffer<String> bagOfWords       = JavaConverters.asScalaBufferConverter(researchObject.getBagOfWords()).asScala();
        Buffer<RegularResource> innerResources   = JavaConverters.asScalaBufferConverter(new ArrayList<RegularResource>()).asScala();

        List<Author> authorsList = new ArrayList<Author>();

        for (Creator creator: researchObject.getMetainformation().getCreators()){

            String authorUri    = creator.getUri();
            String name         = creator.getName();
            String surname      = creator.getSurname();

            authorsList.add(new Author(authorUri,name,surname));
        }

        Buffer<Author> authors = JavaConverters.asScalaBufferConverter(authorsList).asScala();


        Metadata metadata = new Metadata(title, published,authors);



        RegularResource regularResource = new RegularResource(uri,url,metadata,bagOfWords,innerResources);

        return regularResource;
    }
}
