package es.upm.oeg.epnoi.ressist;

import es.upm.oeg.epnoi.ressist.processor.ROBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by cbadenes on 17/06/15.
 */
public class InternalRouteBuilder extends RouteBuilder {

    protected static final Logger LOG = LoggerFactory.getLogger(InternalRouteBuilder.class);

    @Value("${storage.path}")
    protected String basedir;

    @Autowired
    protected ROBuilder roBuilder;

    @Override
    public void configure() throws Exception {

    }
}