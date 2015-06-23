package es.upm.oeg.epnoi.ressist.parser;

import es.upm.oeg.epnoi.matching.metrics.domain.entity.ConceptualResource;
import es.upm.oeg.epnoi.matching.metrics.domain.entity.RegularResource;
import org.apache.spark.api.java.function.Function;
import org.springframework.stereotype.Component;

/**
 * Created by cbadenes on 22/06/15.
 */
@Component
public class CRParser implements Function<RegularResource, ConceptualResource> {

    @Override
    public ConceptualResource call(RegularResource regularResource) throws Exception {

        return new ConceptualResource(regularResource);
    }
}
