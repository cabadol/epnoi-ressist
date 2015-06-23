package es.upm.oeg.epnoi.ressist.comparator;

import es.upm.oeg.epnoi.matching.metrics.domain.entity.TopicalResource;
import scala.Tuple3;

import java.util.Comparator;

/**
 * Created by cbadenes on 22/06/15.
 */
public class SimilarityComparator implements Comparator<Tuple3<TopicalResource, TopicalResource, Object>> {
    @Override
    public int compare(Tuple3<TopicalResource, TopicalResource, Object> o1, Tuple3<TopicalResource, TopicalResource, Object> o2) {
        return Double.compare((Double)o1._3(), (Double)o2._3());
    }
}
