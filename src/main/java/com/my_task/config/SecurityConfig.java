package com.my_task.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.my_task.filter.BearerTokenFilter;
import com.my_task.model.Role;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private BearerTokenFilter bearerTokenFilter;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize ->{
			authorize.requestMatchers("/api/tasks/**").hasAuthority(Role.USER.name())
			.requestMatchers("/api/auth/refresh").authenticated()
			.anyRequest().permitAll();
		});
		http.csrf(csrf->{
			csrf.disable();
		});
		
		http.sessionManagement(session -> {
			session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		});
		
		http.addFilterBefore(bearerTokenFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
	
	@Bean
	AuthenticationManager authenticationManager(
			UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(authenticationProvider);
	}
	
	
	@Bean
	public FilterRegistrationBean<BearerTokenFilter> tenantFilterRegistration(BearerTokenFilter filter) {
	    FilterRegistrationBean<BearerTokenFilter> registration = new FilterRegistrationBean<>(filter);
	    registration.setEnabled(false);
	    return registration;
	}
	
}
