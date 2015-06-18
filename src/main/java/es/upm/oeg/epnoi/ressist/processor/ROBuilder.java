package es.upm.oeg.epnoi.ressist.processor;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import es.upm.oeg.camel.euia.model.Context;
import es.upm.oeg.camel.euia.model.Publication;
import es.upm.oeg.camel.euia.model.Reference;
import es.upm.oeg.camel.euia.model.Source;
import es.upm.oeg.epnoi.harvester.HarvesterRouteBuilder;
import es.upm.oeg.epnoi.matching.metrics.domain.entity.Author;
import es.upm.oeg.epnoi.matching.metrics.domain.entity.DigitalID;
import es.upm.oeg.epnoi.matching.metrics.domain.entity.Metadata;
import es.upm.oeg.epnoi.matching.metrics.domain.entity.RegularResource;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import scala.None;
import scala.Some;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbadenes on 17/06/15.
 */
@Component
public class ROBuilder implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(ROBuilder.class);

    @Override
    public void process(Exchange exchange) throws Exception {

//        // UIA Context
//        Context context = new Context();
//
//        // Source of data
//        Source source = new Source();
//        source.setName(exchange.getProperty(HarvesterRouteBuilder.SOURCE_NAME,String.class));
//        source.setUri(exchange.getProperty(HarvesterRouteBuilder.SOURCE_URI,String.class));
//        source.setUrl(exchange.getProperty(HarvesterRouteBuilder.SOURCE_URL,String.class));
//        source.setProtocol(exchange.getProperty(HarvesterRouteBuilder.SOURCE_PROTOCOL,String.class));
//        context.setSource(source);
//
//        // Publication
//        Publication publication = new Publication();
//
//        // Metadata
//        Reference reference = new Reference();
//        reference.setFormat(exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_METADATA_FORMAT,String.class));
//        reference.setUrl("file://"+exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_REFERENCE_URL,String.class));
//        publication.setReference(reference);
//
//        publication.setTitle(exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_TITLE,String.class));
//        publication.setUri(exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_URI, String.class));
//        publication.setFormat(exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_FORMAT, String.class));
//        publication.setLanguage(exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_LANGUAGE,String.class));
//        publication.setPublished(exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_PUBLISHED,String.class));
//        publication.setRights(exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_RIGHTS,String.class));
//        publication.setUrl("file://" + exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_URL_LOCAL,String.class).replace("."+reference.getFormat(), "."+publication.getFormat()));
//
//
//        publication.setDescription(exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_DESCRIPTION,String.class));
//        context.add(publication);
//
//        Iterable<String> iterator = Splitter.on(';').trimResults().omitEmptyStrings().split(exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_CREATORS, String.class));
//        ArrayList<String> creators = Lists.newArrayList(iterator);
//        publication.setCreators(creators);
//
//
//        Gson gson = new Gson();
//        String json = gson.toJson(context);
//
//        exchange.getIn().setBody(json,String.class);
//
//        LOG.debug("Json: {}", json);



        // create a regular resource

        String uri          = exchange.getProperty(HarvesterRouteBuilder.SOURCE_URI,String.class);
        String url          = exchange.getProperty(HarvesterRouteBuilder.SOURCE_URL,String.class);

        String title        = exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_TITLE,String.class);
        String published    = exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_PUBLISHED,String.class);
        String format       = exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_FORMAT, String.class);
        String language     = exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_LANGUAGE,String.class);
        String rights       = exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_RIGHTS,String.class);
        String description  = exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_DESCRIPTION,String.class);


        Iterable<String> iterator = Splitter.on(';').trimResults().omitEmptyStrings().split(exchange.getProperty(HarvesterRouteBuilder.PUBLICATION_CREATORS, String.class));
        ArrayList<String> creators = Lists.newArrayList(iterator);
        List<Author> authors = new ArrayList<Author>();

        for(String creator: creators){

            creator.split(",");


            String authorUri        = "";
            String authorName       = "";
            String authorSurname    = "";

            String orcid            = creator+"-orcid";
            String dblpid           = creator+"-dblpid";
            String adsid            = creator+"-asdid";

            Some<DigitalID> authorDigId   =  new Some<DigitalID>(new DigitalID(new Some(orcid),new Some(dblpid),new Some(adsid)));
            Author author = new Author(authorUri,authorName,authorSurname,authorDigId);
            authors.add(author);
        }


        Metadata metadata = new Metadata(title, published, new Some(authors));
        String words = null;
        RegularResource ro = new RegularResource(uri,url,metadata,new Some(words),new Some(null));



        // pass the research object to camel flow
        exchange.getIn().setBody(ro,RegularResource.class);

        LOG.debug("ResearchObject as RegularResource: {}", ro);

    }
}
