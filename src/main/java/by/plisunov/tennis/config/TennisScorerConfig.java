package by.plisunov.tennis.config;

import by.plisunov.tennis.model.Game;
import org.springframework.context.annotation.*;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Application spring configuration
 * 
 * @author Andrey
 *
 */
@Configuration
@ComponentScan(basePackages = { "by.plisunov.tennis" })
@EnableWebMvc
@EnableScheduling
public class TennisScorerConfig extends WebMvcConfigurerAdapter {

	@Bean
	public Executor taskExecutor() {
		return new SimpleAsyncTaskExecutor();
	}

	@Bean(name = "currentGames")
	public Map<String, Game> currentGames() {
		return new ConcurrentHashMap<>();
	}

	@Bean(name = "futureGames")
	public Map<String, Game> futureGames() {
		return new ConcurrentHashMap<>();
	}

	@Bean(name = "lastGames")
	public Map<String, Game> lastGames() {
		return new ConcurrentHashMap<>();
	}

	@Bean
	public ObjectMapper mapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		Jdk8Module module = new Jdk8Module();
		module.configureAbsentsAsNulls(true);
		objectMapper.registerModule(module);
		return objectMapper;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		resolver.setViewClass(JstlView.class);
		registry.viewResolver(resolver);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}
}
