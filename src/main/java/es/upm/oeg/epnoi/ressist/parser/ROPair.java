package es.upm.oeg.epnoi.ressist.parser;

import es.upm.oeg.epnoi.harvester.domain.ResearchObject;
import org.springframework.stereotype.Component;
import scala.Tuple2;

/**
 * Created by cbadenes on 22/06/15.
 */
@Component
public class ROPair implements org.apache.spark.api.java.function.PairFunction<es.upm.oeg.epnoi.harvester.domain.ResearchObject, String, String> {
    @Override
    public Tuple2<String, String> call(ResearchObject researchObject) throws Exception {
        return new Tuple2<String,String>(researchObject.getUri(),researchObject.getMetainformation().getTitle().replace("\n"," "));
    }
}
