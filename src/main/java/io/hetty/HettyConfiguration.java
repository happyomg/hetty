package io.hetty;

import io.hetty.server.HettyServer;
import io.hetty.server.handler.HettyHttpHandler;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Created by wuxw on 2015/12/2.
 */
@Configuration
public class HettyConfiguration {


    @Bean
    public ServletContext servletContext(AnnotationConfigEmbeddedWebApplicationContext context) {
        MockServletContext servletContext = new MockServletContext();
        context.setServletContext(servletContext);
        return servletContext;
    }

    @Bean
    public ServletConfig servletConfig(AnnotationConfigEmbeddedWebApplicationContext context, ServletContext servletContext) {
        MockServletConfig servletConfig = new MockServletConfig(servletContext);
        context.setServletConfig(servletConfig);
        return servletConfig;
    }

    @Bean
    public DispatcherServlet dispatcherServlet(WebApplicationContext context,ServletConfig servletConfig) throws ServletException {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        dispatcherServlet.init(servletConfig);
        return dispatcherServlet;
    }

    @Bean
    public HettyHttpHandler hettyHttpHandler(DispatcherServlet dispatcherServlet, ServletContext servletContext) {
        return new HettyHttpHandler(dispatcherServlet, servletContext);
    }

    @Bean
    public HettyServer hettyServer() {
        return HettyServer.Builder.createDefault();
    }
}
