package by.plisunov.scoreboard.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	/*
	 * @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;
	 */
	@Autowired
	private DataSource dataSource;

	private String usersQuery = "select username, password, active from modx_users where username=?";

	private String rolesQuery = "select u.username, ur.name from modx_users u inner join modx_user_group_roles ur on (u.primary_group=ur.id)  where u.username=?";

	@Autowired
	private AccessDeniedHandler accessDeniedHandler;

	@Autowired
	private AuthenticationEntryPoint authEntryPoint;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		auth.jdbcAuthentication().usersByUsernameQuery(usersQuery).authoritiesByUsernameQuery(rolesQuery)
				.dataSource(dataSource);// .passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable().authorizeRequests().antMatchers("/login", "/logout").permitAll().anyRequest().permitAll();
		http.authorizeRequests().antMatchers("/index").hasAnyRole("ADMIN").anyRequest().authenticated().and()
				.exceptionHandling().accessDeniedHandler(accessDeniedHandler);

		http.authorizeRequests().antMatchers("/api/**").hasRole("ADMIN").and().httpBasic()
				.authenticationEntryPoint(authEntryPoint);

		http.formLogin().loginPage("/login").loginProcessingUrl("/j_spring_security_check").defaultSuccessUrl("/index")
				.usernameParameter("j_username").passwordParameter("j_password").permitAll().and().exceptionHandling()
				.accessDeniedPage("/error/403");

	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
	}
}
