package javaman.testApp;

import javaman.testApp.Services.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {
    @Autowired
    private MyUserDetailService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry->{
                    registry.requestMatchers("/index").permitAll();
                    registry.requestMatchers("/books/**").hasRole("USER");
                    registry.requestMatchers("/admin/**").hasRole("ADMIN");
                    registry.anyRequest().authenticated();

                })
                .formLogin(httpSecurityFormLoginConfigurer ->{
                	httpSecurityFormLoginConfigurer
                            .loginPage("/login")
                            .successHandler(new AuthenticationSuccessHandler())
                            .permitAll();
                })
                .logout(logoutConfigurer -> {
                    logoutConfigurer
                            .logoutUrl("/logout") // URL for logging out
                            .logoutSuccessUrl("/index?logout") // Redirect URL after successful logout
                            .permitAll(); // Allow all to access the logout URL
                })

//              .formLogin(formLogin -> formLogin
//                .formLogin(form -> form
//                        .successHandler(new AuthenticationSuccessHandler())
//                        .failureUrl("/login?error=true")  // Redirect to login with error query on failure
//                        .permitAll()  // Allow all to access the login page
//                )
                .build();
    }

    //Genuine Coder Springboot 3 tutorial authentication and authorization
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails normalUser = User.builder()
//                .username("yonille")
//                .password("$2a$12$3QcfOKK7u9QvmiylbO9ccufv8xGhLgrGS9PqtptKpkqaEJ2pYrAo2")
//                .roles("user")
//                .build();
//        UserDetails adminUser = User.builder()
//                .username("admin")
//                .password("$2a$12$Y5zoGz9izVV3s69O5F4uXekIy8dGxfwTRleKtPU3hu5i9AQmUUI6y")
//                .roles("ADMIN","USER")
//                .build();
//        return new InMemoryUserDetailsManager(normalUser, adminUser);
//    }
    @Bean
    UserDetailsService userDetailsService(){
        return userDetailsService;
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


}
