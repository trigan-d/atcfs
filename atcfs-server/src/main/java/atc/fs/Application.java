package atc.fs;

import java.util.Arrays;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableCaching
//can't figure out why application fails to start if this is uncommented
//@PropertySource("${catalina.home}/conf/atcfs.properties")
public class Application extends SpringBootServletInitializer{
	private static Class<Application> applicationClass = Application.class;

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
	    TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
	    factory.getTomcatConnectorCustomizers().add(new TomcatConnectorCustomizer() {
			@Override
			public void customize(Connector connector) {
				connector.setURIEncoding("UTF-8");
				//connector.setUseBodyEncodingForURI(true);
			}
		});
	    return factory;
	}
	
	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("vis")));
		return cacheManager;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(applicationClass);
	}
}
