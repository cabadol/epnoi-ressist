import es.upm.oeg.epnoi.ressist.InternalRouteBuilder
import org.apache.camel.LoggingLevel
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class routes extends InternalRouteBuilder{

    protected static final Logger LOG = LoggerFactory.getLogger(routes.class);

    @Override
    public void configure() throws Exception {
        super.configure()

        /*********************************************************************************************************************************
         * SOURCE: OAI-PMH UPM
         *********************************************************************************************************************************/
        from("file:"+basedir+"/oaipmh?recursive=true&include=.*\\.xml&doneFileName=\${file:name}.done").
                to("direct:setCommonOaipmhXpathExpressions").
                to("seda:notifyUIA")


        /*********************************************************************************************************************************
         * -> to RESSIST
         *********************************************************************************************************************************/
        from("seda:notifyUIA").
                process(roBuilder).
                log(LoggingLevel.INFO,LOG,"File Read: '\${header.CamelFileName}'").
        //to("euia:out?servers="+ uiaServers)
                to("stream:out")

    }

}
