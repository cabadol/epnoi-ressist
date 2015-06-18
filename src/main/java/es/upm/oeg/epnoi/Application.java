package es.upm.oeg.epnoi;

import es.upm.oeg.epnoi.harvester.HarvesterRouteBuilder;
import es.upm.oeg.epnoi.ressist.InternalRouteBuilder;
import groovy.lang.GroovyClassLoader;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.spring.Main;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.File;
import java.io.IOException;

/**
 * Created by cbadenes on 17/06/15.
 */
@Configuration
@EnableAutoConfiguration(exclude={es.upm.oeg.epnoi.harvester.Application.class})
@ComponentScan
public class Application {

    static{
        System.setProperty("spring.config.name","ressist");
    }


    @Value("${ressist.camel.groovy}")
    File groovyFile;


    @Autowired
    RouteBuilder routeBuilder;

    public static void main(String[] args) throws Exception {
        // Initialize Spring Context
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        // Launch Camel Context
        Main main = new Main();
        main.enableHangupSupport();
        main.setApplicationContext((AbstractApplicationContext) context);
        main.addRouteBuilder(context.getBean(InternalRouteBuilder.class));
        main.run();

    }

    @Bean
    public RouteBuilder groovyRouteBuilder() throws IOException, IllegalAccessException, InstantiationException {
        // Loading groovy class
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class clazz = gcl.parseClass(groovyFile);
        return (InternalRouteBuilder) clazz.newInstance();
    }


    @Bean
    public SpringCamelContext camelContext(ApplicationContext applicationContext) throws Exception {
        SpringCamelContext camelContext = new SpringCamelContext(applicationContext);
        camelContext.addRoutes(routeBuilder);


//        // Register Harvester Camel Context into the Registry
//        PropertyPlaceholderDelegateRegistry delegateRegistry = (PropertyPlaceholderDelegateRegistry) camelContext.getRegistry();
//        JndiRegistry registry = (JndiRegistry) delegateRegistry.getRegistry();
//
//
//        // lets create our black box as a camel context and a set of routes
//        DefaultCamelContext blackBox = new DefaultCamelContext(registry);
//        blackBox.setName("blackBox");
//        blackBox.addRoutes(new HarvesterRouteBuilder());
//        blackBox.start();
//
//        registry.bind("harvester", blackBox);

        return camelContext;
    }



}