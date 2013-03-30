/**
 * 
 */
package cz.cecrdlem.blog.web.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

/**
 * @author milan.cecrdle
 * 
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "cz.cecrdlem.blog")
public class MvcConfig {
	@Autowired
	freemarker.template.Configuration configuration;

	@Bean
	public FreeMarkerViewResolver configureFreeMarkerViewResolver() {
		FreeMarkerViewResolver freeMarkerViewResolver = new FreeMarkerViewResolver();
		freeMarkerViewResolver.setCache(true);
		freeMarkerViewResolver.setPrefix("");
		freeMarkerViewResolver.setSuffix(".ftl");
		return freeMarkerViewResolver;
	}

	@Bean
	public FreeMarkerConfigurationFactoryBean createFreeMarketConfigururer() {
		FreeMarkerConfigurationFactoryBean fmc = new FreeMarkerConfigurationFactoryBean();
		fmc.setTemplateLoaderPath("classpath:/templates");
		return fmc;
	}

	@Bean
	public FreeMarkerConfigurer createFreeMarkerConfigurer() {
		FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
		freeMarkerConfigurer.setConfiguration(configuration);
		return freeMarkerConfigurer;
	}

}
