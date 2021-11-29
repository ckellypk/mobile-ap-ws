package com.appsdeveloperblog.app.ws.security;

import com.appsdeveloperblog.app.ws.service.UserService;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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
     * user without identifying who they are. Outside of this we need all users to provide a token to access the endpoints
     * resources
     *
     * We add the filter at the end
     * */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users")
                .permitAll()
                .anyRequest()
                .authenticated().and().addFilter(new AuthenticationFilter((authenticationManager())));
        ;
    }

    //A LOT is being done under the hood to scaffold the spring application together using the boot framework.
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }


}
