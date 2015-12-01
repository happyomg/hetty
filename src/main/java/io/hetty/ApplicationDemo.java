package io.hetty;

import io.hetty.server.HettyEmbeddedServletContainerFactory;
import io.hetty.server.HettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Created by yuck on 2015/12/1.
 */
@SpringBootApplication
@EnableWebMvc
@ConditionalOnWebApplication
@ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurerAdapter.class})
@ConditionalOnMissingBean({WebMvcConfigurationSupport.class})
@AutoConfigureAfter({DispatcherServletAutoConfiguration.class})
@Import({WebMvcAutoConfiguration.EnableWebMvcConfiguration.class})
public class ApplicationDemo {

    //    @Bean
//    public DispatcherServlet dispatcherServlet(XmlWebApplicationContext context) {
//        return new DispatcherServlet(context);
//    }
//    @Bean
//    public ServletContext servletContext() {
//        return new MockServletContext();
//    }
//
//    @Bean
//    public ServletConfig servletConfig(ServletContext servletContext) {
//        return new MockServletConfig(servletContext);
//    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ApplicationDemo.class, args);
        System.out.println(ctx);
//        HettyServer hettyServer = HettyServer.Builder.createDefault();
//        hettyServer.startAsync();
//        hettyServer.awaitRunning();
    }
}
