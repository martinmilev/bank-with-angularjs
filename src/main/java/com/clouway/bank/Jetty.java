package com.clouway.bank;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class Jetty {
  private final Server server;

   public Jetty(int port) {
     this.server = new Server(port);
   }

   public void start() {
     ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
     servletContext.setContextPath("/");
     servletContext.addServlet(DefaultServlet.class, "/");
     servletContext.addFilter(GuiceFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE));

     servletContext.addEventListener(new GuiceServletContextListener() {
       @Override
       protected Injector getInjector() {
         return Guice.createInjector(new BankModule());
       }
     });

     ContextHandler staticResourceHandler = new ContextHandler();
     staticResourceHandler.setContextPath("/assets");
     ResourceHandler resourceHandler = new ResourceHandler();
     resourceHandler.setResourceBase("static");

     staticResourceHandler.setHandler(resourceHandler);

     HandlerList handlers = new HandlerList();
     handlers.setHandlers(new Handler[]{staticResourceHandler, servletContext});

     server.setHandler(handlers);
     try {
       server.start();
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
}
