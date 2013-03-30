/**
 * 
 */
package cz.cecrdlem.blog.web.configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author milan.cecrdle
 *
 */
public class Initializer  implements WebApplicationInitializer {
		 public void onStartup(ServletContext servletContext)
		   throws ServletException {
		  AnnotationConfigWebApplicationContext mvcContext = new AnnotationConfigWebApplicationContext();
		  mvcContext.register(MvcConfig.class);
		 
		  ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
		    "dispatcher", new DispatcherServlet(mvcContext));
		  dispatcher.setLoadOnStartup(1);
		  dispatcher.addMapping("/");
		 }
}
