package es.upm.oeg.epnoi.ressist;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
/**
 * Created by cbadenes on 18/06/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@BootstrapWith(CamelTestContextBootstrapper.class)
@ContextConfiguration
public class ProcessingTest {

    @Autowired
    protected CamelContext camelContext;

    @EndpointInject(uri = "mock:foo")
    protected MockEndpoint foo;


    @Test
    public void testMocksAreValid() throws Exception {

        foo.message(0).header("bar").isEqualTo("ABC");

        MockEndpoint.assertIsSatisfied(camelContext);
    }

}
