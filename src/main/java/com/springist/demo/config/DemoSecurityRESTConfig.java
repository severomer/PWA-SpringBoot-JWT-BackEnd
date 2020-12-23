package com.springist.demo.config;

import java.util.Arrays;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.springist.demo.service.UserService;


@EnableWebSecurity
public class DemoSecurityRESTConfig {

	// add a reference to our security data source
    @Autowired
    private  UserService userService;
	
    @Autowired
    private  CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    
    @Autowired
    private  RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    
    @Configuration
    @Order(1)
    public  class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
    	
    
   @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }


    	@Override
    	protected void configure(HttpSecurity http) throws Exception {

    	http.csrf().disable(); 
    	http.headers().frameOptions().disable();
    	http
    	.antMatcher("/api/**")
    	.authorizeRequests()
    	.antMatchers("/api/restlogin").permitAll()
    	.antMatchers("/api/register").permitAll()
    	.anyRequest().authenticated()
    	.and()
    	.addFilter(new IncomeFilter(authenticationManager()))
    	.addFilter(getAuthenticationFilter());
    	http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.cors();
    	
    }  
    	
    	private AuthenticationFilter getAuthenticationFilter() throws Exception{

    		// Filter doFilter = new AnonymousAuthenticationFilter(null);
    		AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, authenticationManager());
    		authenticationFilter.setFilterProcessesUrl("/api/restlogin");
    		return authenticationFilter;
    	}
		
    	@Bean
        public CorsConfigurationSource corsConfigurationSource() {
            final CorsConfiguration configuration = new CorsConfiguration();
       
            configuration.setAllowedOrigins(Arrays.asList("http://localhost:8100"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
            configuration.setAllowCredentials(true);
            configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
            final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }

    }
	  
    @Configuration
    public  class FormSecurityConfig extends WebSecurityConfigurerAdapter { 

    	
    	@Override
    	protected void configure(HttpSecurity http) throws Exception {
    		
    	http.headers().frameOptions().disable();
		http
		.csrf().disable()   //csrf i disable etmedikce rest GET disindakiler POST PUT calismiyor Request method 'PUT' not supported
//	    .exceptionHandling()
//	    .authenticationEntryPoint(restAuthenticationEntryPoint)
//	    .and()
	    .authorizeRequests()
			.antMatchers("/").permitAll()
//			.antMatchers("/api").permitAll()
			.antMatchers("/links").permitAll()
			.antMatchers("/", "/error", "/webjars/**","/js/**").permitAll()
			.antMatchers("/api/greetings").permitAll()
			.antMatchers("/api/postdeneme").permitAll()
			.antMatchers("/restlogin").permitAll()
//			.antMatchers("/api/greetings/**").permitAll()
			.antMatchers("/leaders/**").hasRole("MANAGER")
			.antMatchers("/systems/**").hasRole("ADMIN")
			.antMatchers("/register/**").permitAll()
			.antMatchers("/**").hasRole("EMPLOYEE")
	//		.anyRequest().authenticated() //yeni ekledim
			.and()
			.formLogin()
				.loginPage("/showMyLoginPage")
				.loginProcessingUrl("/authenticateTheUser")
				.successHandler(customAuthenticationSuccessHandler)
				.permitAll()
			.and()
//			.oauth2Login()
//			.and()
			.logout().permitAll()
			.and()
			.exceptionHandling().accessDeniedPage("/access-denied")
			.and()
			.httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint)
			.and();
//			.addFilter(new IncomeFilter(authenticationManager()));
//		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			//rest api erisimi icin ekledim
//		http.addFilter(getAuthenticationFilter());
//		http.addFilter(new IncomeFilter(authenticationManager()));
	}
	

    }


	//beans
	//bcrypt bean definition
	@Bean
	public  BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//authenticationProvider bean definition
	@Bean
	public  DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(userService); //set the custom user details service
		auth.setPasswordEncoder(passwordEncoder()); //set the password encoder - bcrypt
		return auth;
	}
	  
}

/*
@Configuration
@EnableWebSecurity
public class DemoSecurityRESTConfig extends WebSecurityConfigurerAdapter {

	// add a reference to our security data source
    @Autowired
    private UserService userService;
	
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    
   @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

    	http.headers().frameOptions().disable();
		http
		.csrf().disable()   //csrf i disable etmedikce rest GET disindakiler POST PUT calismiyor Request method 'PUT' not supported
//	    .exceptionHandling()
//	    .authenticationEntryPoint(restAuthenticationEntryPoint)
//	    .and()
	    .authorizeRequests()
			.antMatchers("/").permitAll()
//			.antMatchers("/api").permitAll()
			.antMatchers("/links").permitAll()
			.antMatchers("/", "/error", "/webjars/**","/js/**").permitAll()
			.antMatchers("/api/greetings").permitAll()
			.antMatchers("/api/postdeneme").permitAll()
//			.antMatchers("/api/greetings/**").permitAll()
			.antMatchers("/leaders/**").hasRole("MANAGER")
			.antMatchers("/systems/**").hasRole("ADMIN")
			.antMatchers("/register/**").permitAll()
			.antMatchers("/**").hasRole("EMPLOYEE")
			.anyRequest().authenticated() //yeni ekledim
			.and()
			.formLogin()
				.loginPage("/showMyLoginPage")
				.loginProcessingUrl("/authenticateTheUser")
				.successHandler(customAuthenticationSuccessHandler)
				.permitAll()
			.and()
//			.oauth2Login()
//			.and()
			.logout().permitAll()
			.and()
			.exceptionHandling().accessDeniedPage("/access-denied")
			.and()
			.httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint)
			.and()
			.addFilter(new IncomeFilter(authenticationManager()));
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			//rest api erisimi icin ekledim
		http.addFilter(getAuthenticationFilter());
//		http.addFilter(new IncomeFilter(authenticationManager()));
	}
	
	private AuthenticationFilter getAuthenticationFilter() throws Exception{
		// TODO Auto-generated method stub
		// Filter doFilter = new AnonymousAuthenticationFilter(null);
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, authenticationManager());
		authenticationFilter.setFilterProcessesUrl("/restlogin");
		return authenticationFilter;
	}

	//beans
	//bcrypt bean definition
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//authenticationProvider bean definition
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(userService); //set the custom user details service
		auth.setPasswordEncoder(passwordEncoder()); //set the password encoder - bcrypt
		return auth;
	}
	  
}
*/


