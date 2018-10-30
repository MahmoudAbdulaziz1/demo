package com.taj.config;

import com.taj.security.JwtAuthenticationEntryPoint;
import com.taj.security.JwtAuthenticationProvider;
import com.taj.security.JwtAuthenticationTokenFilter;
import com.taj.security.JwtSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@EnableWebSecurity
@Configuration
public class JwtSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private JwtAuthenticationProvider authenticationProvider;
    @Autowired
    private JwtAuthenticationEntryPoint entryPoint;


    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }


    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(authenticationProvider));
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilter() {
        JwtAuthenticationTokenFilter filter = new JwtAuthenticationTokenFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(new JwtSuccessHandler());
        return filter;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .cors()
                .and().csrf().disable()
                .authorizeRequests().antMatchers("/service/evvaz/**").authenticated()
//                .and()
//                .authorizeRequests().antMatchers("/evvaz/companyOffer/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/cat/**").authenticated()
//                .and()
//                .authorizeRequests().antMatchers("/evvaz/companyOffer/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/response/school/request/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/seen/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/details/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/admin/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/profile/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/enquiry/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/enquiry/response/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/school/profile/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/receive/place/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/school/category/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/school/request/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/school/request/offer/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/offer/seen/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/takataf/category/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/takataf/first/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/tender/seen/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/takataf/second/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/takataf/third/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/takataf/tender/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/tender/request/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/tender/details/company/**").authenticated()
                .and()
                .authorizeRequests().antMatchers("/service/evvaz/follow/**").authenticated()
                .and()
//                .authorizeRequests().antMatchers("/token/**").authenticated()
//                .and()
                .exceptionHandling().authenticationEntryPoint(entryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers().cacheControl();

    }


}
