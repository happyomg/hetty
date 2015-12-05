package io.hetty;

import io.hetty.server.HettyConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * Created by yuck on 2015/12/1.
 */
@SpringBootApplication
@Import(HettyConfiguration.class)
public class ApplicationDemo {


    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ApplicationDemo.class, args);
    }
}
