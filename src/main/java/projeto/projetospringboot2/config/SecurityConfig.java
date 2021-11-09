package projeto.projetospringboot2.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import projeto.projetospringboot2.service.ProjectUserDetailsService;

@EnableWebSecurity
@Log4j2
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ProjectUserDetailsService projectUserDetailsService;

    /**
     * BasicAuthenticationFilter
     * UsernamePasswordAuthenticationFilter
     * DefaultLoginPageGeneratingFilter
     * DefaultLogutPageGeneratingFilter
     * DefaultLoginPageGeneratingFilter
     * FilterSecurityInterceptor
     * Authentication -> Authorization
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                //.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .authorizeRequests()
                .antMatchers("/animes/admin/**").hasRole("ADMIN")
                .antMatchers("/animes/**").hasRole("USER")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        log.info("Password Encoded {}",passwordEncoder.encode("caio2304"));

        auth.inMemoryAuthentication()
                .withUser("Caio2")
                .password(passwordEncoder.encode("caio2304"))
                .roles("USER","ADMIN")
                .and()
                .withUser("caiohgp2")
                .password(passwordEncoder.encode("caio2304"))
                .roles("USER");;

        auth.userDetailsService(projectUserDetailsService).passwordEncoder(passwordEncoder);
    }
}
