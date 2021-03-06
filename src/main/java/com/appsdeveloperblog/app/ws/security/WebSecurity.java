package com.appsdeveloperblog.app.ws.security;

import com.appsdeveloperblog.app.ws.service.UserService;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final UserService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //We register the password encoder within the HTTP security pipeline by initializing the constructor with the pw
    //encoder and the userDetails service which is a customized implementation of the base user details service
    // (inherited through the UserService Interface(not the implementation as the implementations inherits the interface)

    public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Here we are authorizing only certain urls to get into the application without a token. We allow post url to
     * create a user and then adding an authentication filter that was created in the AuthenticationFilter.java
     * class which filters based on email and password. Here is how we add it to the http pipeline
     *
     * When configuring the security we are allowing all post methods to '/users' controller to allow users to create  a
     * user without identifying who they are. Outside this we need all users to provide a token to access the endpoints
     * resources
     *
     * We add the filter at the end. here we are configuring the security pipeline. We want to allow entry into certain
     * endpoints to allow authentication & authorization but require a token in the header for the rest. We do that here.
     *
     * We need to add certain behaviors we want our applicaions security config to have such as caching the token or
     * requiring it at ever point. By default it caches the token. If we want to get rid of this option we must make
     * the app stateless
     * */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users")
                .permitAll()
                .anyRequest()
                .authenticated().and()
                .addFilter(getAuthenticationFilter())
                .addFilter(new AuthorizationFilter(authenticationManager()))/*instead of creating a new auth filter we invoke the method below that points user to correct endpoint*/
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        ;
    }

    //A LOT is being done under the hood to scaffold the spring application together using the boot framework.
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    /**
     * We are adding an authentication endpoint to prevent the default route of url/users.
     * We are changing it to url/users/login
     * */
    public AuthenticationFilter getAuthenticationFilter() throws Exception {
        final AuthenticationFilter filter = new AuthenticationFilter(authenticationManager());
        filter.setFilterProcessesUrl("/users/login"); //this is the authentication path.
        return filter;
    }


}
