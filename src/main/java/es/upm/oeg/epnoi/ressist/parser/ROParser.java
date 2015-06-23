package es.upm.oeg.epnoi.ressist.parser;

import com.google.gson.Gson;
import es.upm.oeg.epnoi.harvester.domain.ResearchObject;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.util.Arrays;


/**
 * Created by cbadenes on 22/06/15.
 */
@Component
public class ROParser implements FlatMapFunction<Tuple2<String, String>, ResearchObject> {

    @Override
    public Iterable<ResearchObject> call(Tuple2<String, String> tuple) throws Exception {

        String fileName = tuple._1();
        String content  = tuple._2();

        return Arrays.asList( new ResearchObject[]{new Gson().fromJson(content, ResearchObject.class)});
    }
}
