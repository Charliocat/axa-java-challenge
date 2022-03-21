package jp.co.axa.apidemo.configuration;

import javax.sql.DataSource;
import jp.co.axa.apidemo.security.CustomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

  private static final String ROLE_ADMIN = "ADMIN";
  private static final String ROLE_MANAGER = "MANAGER";

  @Autowired
  private DataSource dataSource;

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication()
        .dataSource(dataSource)
        .withDefaultSchema()
        .passwordEncoder(passwordEncoder())
        .withUser(
            User.withUsername("manager")
                .password(passwordEncoder().encode("manager"))
                .roles(ROLE_MANAGER)
        )
        .withUser(
            User.withUsername("admin")
                .password(passwordEncoder().encode("admin"))
                .roles(ROLE_ADMIN)
        );
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/").permitAll()
        .antMatchers("/api/v1/**").authenticated()
        .and()
        .httpBasic()
        .and().csrf().disable();

    //necessary to load h2-console
    http.headers()
        .frameOptions()
        .sameOrigin();

    http.addFilterAfter(new CustomFilter(),
                        BasicAuthenticationFilter.class);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
