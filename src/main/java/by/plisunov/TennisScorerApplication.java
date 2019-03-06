package by.plisunov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import by.plisunov.tennis.config.TennisScorerConfig;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * (non-Javadoc)
 * 
 * @see org.springframework.boot.web.support.SpringBootServletInitializer
 * @author Andrey
 *
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class TennisScorerApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TennisScorerApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(new Class[] { TennisScorerApplication.class, TennisScorerConfig.class }, args);
	}
}
