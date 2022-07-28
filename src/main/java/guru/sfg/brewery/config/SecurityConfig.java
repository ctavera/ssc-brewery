package guru.sfg.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder(){ // override the default implementation of password encoder, {noop} is not needed
        return new StandardPasswordEncoder(); //only use this encoder for legacy
//        return new LdapShaPasswordEncoder();
//        return NoOpPasswordEncoder.getInstance(); //only use this encoder for legacy
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll() //this needs to show static resources on /
                        .antMatchers("/beers/find", "/beers*").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll() //another implementation
                )
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();

        return httpSecurity.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() { //replace the AuthenticationManagerBuilder
        UserDetails admin = User.withDefaultPasswordEncoder() //doc: Using this method is not considered safe for production, but is acceptable for demos and getting started.
                .username("spring")
                .password("{SSHA}EdMjVPV27Ut88qU5td1m1YDAXBl2GBE8infd8Q==") //LDAP
//                .password("kahlua")
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password("5cdbdd61f5fe9faf0893cad669643946e44c5f63482d4a46ad7f9d4ebc5395fd22035927fd8fdb22") //SHA-256
//                .password("{SSHA}EdMjVPV27Ut88qU5td1m1YDAXBl2GBE8infd8Q==") //LDAP
//                .password("password")
//                .password("{noop}password") //{noop} no op password encoder
                .roles("USER")
                .build();

        UserDetails customer = User.builder()
                .username("scott")
                .password("tiger")
                .roles("CUSTOMER")
                .build();

        return new InMemoryUserDetailsManager(admin, user, customer);
    }

//    @Bean
//    public UserDetailsService userDetailsService(){
//        UserDetails admin = User.withDefaultPasswordEncoder() //doc: Using this method is not considered safe for production, but is acceptable for demos and getting started.
//                .username("spring")
//                .password("kahlua")
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }
}
