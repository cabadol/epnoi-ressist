package es.upm.oeg.epnoi.ressist;

import es.upm.oeg.epnoi.ressist.analyzer.CorpusAnalyzer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by cbadenes on 17/06/15.
 */
@Configuration
@EnableAutoConfiguration(exclude={es.upm.oeg.epnoi.harvester.Application.class})
@ComponentScan
public class Application {

    static{
        System.setProperty("spring.config.name","ressist");
        System.setProperty("banner.location","ressist-banner.txt");
    }


    public static void main(String[] args) throws Exception {
        // Initialize Spring Context
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        // Launch Analysis
        CorpusAnalyzer analyzer = context.getBean(CorpusAnalyzer.class);
        analyzer.execute();

    }

}