package es.upm.oeg.epnoi.ressist;

import es.upm.oeg.epnoi.harvester.HarvesterRouteBuilder;
import groovy.lang.GroovyClassLoader;
import org.apache.camel.EndpointInject;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

/**
 * Created by cbadenes on 17/06/15.
 */


public class HarvestingTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Autowired
    HarvesterRouteBuilder harvesterRouteBuilder;


    @Test
    public void addResource() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\" xmlns:provenance=\"http://www.openarchives.org/OAI/2.0/provenance\" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
                "    <responseDate>2015-06-17T12:50:35Z</responseDate>\n" +
                "    <request verb=\"ListRecords\" resumptionToken=\"from%3D2015-04-01T00%253A00%253A00Z%26metadataPrefix%3Doai_dc%26offset%3D26286%26until%3D2015-05-01T00%253A00%253A00Z\">http://oa.upm.es/cgi/oai2</request>\n" +
                "    <ListRecords>\n" +
                "        <record>\n" +
                "            <header>\n" +
                "                <identifier>oai:oa.upm.es:26833</identifier>\n" +
                "                <datestamp>2015-04-01T22:56:05Z</datestamp>\n" +
                "                <setSpec>7374617475733D707562</setSpec>\n" +
                "                <setSpec>7375626A656374733D74656C65636F6D756E69636163696F6E6573</setSpec>\n" +
                "                <setSpec>7375626A656374733D656C656374726F6E696361</setSpec>\n" +
                "                <setSpec>747970653D61727469636C65</setSpec>\n" +
                "            </header>\n" +
                "            <metadata>\n" +
                "                <dc>\n" +
                "                    <dc:title>Virtual-bound, filamentary and layered states in a box-shaped quantum dot of square potential form the exact numerical solution of the effective mass Schrodinger equation</dc:title>\n" +
                "                    <dc:creator>Luque López, Antonio</dc:creator>\n" +
                "                    <dc:creator>Mellor Null, Alexander Virgil</dc:creator>\n" +
                "                    <dc:creator>Tobías Galicia, Ignacio</dc:creator>\n" +
                "                    <dc:creator>Antolín Fernández, Elisa</dc:creator>\n" +
                "                    <dc:creator>García-Linares Fontes, Pablo</dc:creator>\n" +
                "                    <dc:creator>Ramiro Gonzalez, Iñigo</dc:creator>\n" +
                "                    <dc:creator>Martí Vega, Antonio</dc:creator>\n" +
                "                    <dc:subject>Telecomunicaciones</dc:subject>\n" +
                "                    <dc:subject>Electrónica</dc:subject>\n" +
                "                    <dc:description>The effective mass Schrodinger equation of a QD of parallelepipedic shape with a square potential well is solved by diagonalizing the exact Hamiltonian matrix developed in a basis of separation-of-variables wavefunctions. The expected below bandgap bound states are found not to differ very much from the former approximate calculations. In addition, the presence of bound states within the conduction band is confirmed. Furthermore, filamentary states bounded in two dimensions and extended in one dimension and layered states with only one dimension bounded, all within the conduction band which are similar to those originated in quantum wires and quantum wells coexist with the ordinary continuum spectrum of plane waves. All these subtleties are absent in spherically shaped quantum dots, often used for modeling.</dc:description>\n" +
                "                    <dc:publisher>E.T.S.I. Telecomunicación (UPM)</dc:publisher>\n" +
                "                    <dc:rights>http://creativecommons.org/licenses/by-nc-nd/3.0/es/</dc:rights>\n" +
                "                    <dc:date>2013-03</dc:date>\n" +
                "                    <dc:type>info:eu-repo/semantics/article</dc:type>\n" +
                "                    <dc:type>Artículo</dc:type>\n" +
                "                    <dc:source>Physica B: Condensed Matter, ISSN 0921-4526, 2013-03, Vol. 413, N. </dc:source>\n" +
                "                    <dc:type>PeerReviewed</dc:type>\n" +
                "                    <dc:format>application/pdf</dc:format>\n" +
                "                    <dc:language>eng</dc:language>\n" +
                "                    <dc:relation>http://oa.upm.es/26833/1/INVE_MEM_2013_165687.pdf</dc:relation>\n" +
                "                    <dc:relation>http://www.sciencedirect.com/science/article/pii/S0921452612010988</dc:relation>\n" +
                "                    <dc:relation>info:eu-repo/semantics/altIdentifier/doi/http://dx.doi.org/10.1016/j.physb.2012.12.047</dc:relation>\n" +
                "                    <dc:identifier>http://oa.upm.es/26833/</dc:identifier>\n" +
                "                </dc>\n" +
                "            </metadata>\n" +
                "        </record>\n" +
                "    </ListRecords>\n" +
                "</OAI-PMH>\n";

        resultEndpoint.expectedMessageCount(1);
//        resultEndpoint.expectedHeaderReceived(HarvesterRouteBuilder.PUBLICATION_TITLE,"EU Free Data Roaming, Net Neutrality Plans In Jeopardy");
//        resultEndpoint.expectedHeaderReceived(HarvesterRouteBuilder.PUBLICATION_DESCRIPTION,"An anonymous reader writes EU free");
//        resultEndpoint.expectedHeaderReceived(HarvesterRouteBuilder.PUBLICATION_PUBLISHED,"2015-03-05T22:00:00Z");
//        resultEndpoint.expectedHeaderReceived(HarvesterRouteBuilder.PUBLICATION_URI,"http://rss.slashdot.org/~r/Slashdot/slashdot/~3/7DWYLD8XMjk/eu-free-data-roaming-net-neutrality-plans-in-jeopardy");
//        resultEndpoint.expectedHeaderReceived(HarvesterRouteBuilder.PUBLICATION_URL,"http://rss.slashdot.org/~r/Slashdot/slashdot/~3/7DWYLD8XMjk/eu-free-data-roaming-net-neutrality-plans-in-jeopardy");
//        resultEndpoint.expectedHeaderReceived(HarvesterRouteBuilder.PUBLICATION_LANGUAGE,"en-us");
//        resultEndpoint.expectedHeaderReceived(HarvesterRouteBuilder.PUBLICATION_RIGHTS,"Copyright 1997-2015, Dice. All Rights Reserved. Slashdot is a Dice Holdings, Inc. service");
//        resultEndpoint.expectedHeaderReceived(HarvesterRouteBuilder.PUBLICATION_CREATORS,"timothy");


        template.sendBody(xml);

        resultEndpoint.assertIsSatisfied();
    }


    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();

        // lets create our black box as a camel context and a set of routes
        DefaultCamelContext blackBox = new DefaultCamelContext(registry);
        blackBox.setName("blackBox");
        blackBox.addRoutes(harvesterRouteBuilder);
        blackBox.start();

        registry.bind("harvester", blackBox);
        return registry;
    }




    @Override
    protected RouteBuilder createRouteBuilder() throws IllegalAccessException, InstantiationException, IOException {

          return new RouteBuilder() {
            public void configure() {

                from("direct:start").
                        to("harvester:setCommonOaipmhXpathExpressions").
                        to("mock:result");
            }
        };



    }

}
