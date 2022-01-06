package com.appsdeveloperblog.app.ws.security;


import com.appsdeveloperblog.app.ws.SpringApplicationContext;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.UserLoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


/**
 * We have wired the project to use jwt json wb token to secure our application but before this class we could only
 * authorize a loin using the generates 'user' login and a token that was generated in the console.
 *
 * Here we are adding a token that secures the application using a web token automatically generated and sent via
 * header to the logged-in user after they are authorized.
 *
 * This is basically injecting the jason web token service into the HTTP pipeline by using the built in bethods of
 * jwt to handle an authentication request.
 *
 * Notice this class is extending the usernamePasswordAuthenticationFilter a spring framework class.
 *
 * It creates an instance of the authentication manager in the constructor and then invokes the methods required to
 * authenticate and then create and send the auth token
 *
 * NOTICE This just creates the ilter. to add it to the http pipeline you need to add the filter to the WebSecurity.java file
 * */

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

   /**
    * THis is what runs when you attempt to authenticate a user. It already knows the reqeut model content because you
    * pass it in as the creds, mapping the incoming request json object to the login request model for the user.
    *
    * After parsing the credentials you then attempt to authenticate the user with the email and password.
    * */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                         HttpServletResponse res) throws AuthenticationException {
        try{
            UserLoginRequestModel creds = new ObjectMapper()
                    .readValue(req.getInputStream(), UserLoginRequestModel.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Upon successful sign in need to build a web token and add it to the header of the response along with the
     * token signature.
     *
     * The web token will contain the username, expiration of the token, and the signature which will hold a
     * signature algorithm as well as the token secret. The token secret is kept in the SecurityConstants folder and is
     * currently a 16-character alpha-numeric string (check the security package for the SecurityConstants class).
     * You can change this constant at any time just make sure it is unique.
     *
     * After the toke nis created you send the response header containing the header string, the token prefix and the
     * token itself.
     *
     * When you employ this class you call on the authenticate method above and when this comes back successful you
     * invoke the method below to give out the token.
     * */
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth)
        throws IOException, ServletException
    {

        String userName = ((User) auth.getPrincipal()).getUsername();



        String token = Jwts.builder()
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();

        /*
         * So here is something weird and new to me about java: how certain instances of classes get
         * instantiated/injected. So you can create a class and store/reference it as a bean which allows it to be
         * autowired into other classes within the same application context. But if you instantiate the class using
         * normal way ( new class instance) you somewhat loose the advantages of Autowiring. From what
         * I understand Autowiring used to do with a legacy XML file needed to create and reference certain context
         * throughout the application. Spring boot does this for you.

         * By creating a SpringApplicationContext class we get access to all "beans" or
         * created classes within different packages of our application. Foe example below we are creating an instance
         * of the user service to get additional information about the user that is attempting to log in (like the email
         * we have on file for them in the database).

         * But because in our WebSecurity.java file we instantiated an instance of this AuthenticationFilter class classically
         * we CANNOT use beans to reference other classes lke we have done in other areas of our application.
         * So why not just instantiate a new class whenever necessary? BECAUSE we need to get access to the existing
         * service we need to have the existing CONTEXT of the service in use instead of creating a new one.

         * Because of that we need to get the existing bean by creating the SpringApplicationContext class.
         * We used the @Service tag above the UserServiceImpl class we basically creaeted a bean that can be accessible
         * throughout the application BUT ONLY AFTER WE CREATE THE CONTEXT. You do this in the SpringApplicationConext
         * class at the root of the app.ws module.
         *
         * */

        //create a new instance of th userService by invoking the application context and looking for the bean

        UserService userService = (UserService)SpringApplicationContext.getBean("userServiceImpl");

        UserDto userDto = userService.getUser(userName);


        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);

        /*customized header returning the system assigned userId*/
        res.addHeader("UserID", userDto.getUserId());

        /*
         * After this token is distributed we need to confirm this token in the header of every request that comes
         *to our user service. We allow certain methods to communicate for authientication and this is set up in the
         * WebSecurity.java file. We can also specify which endpoind is allowed to authenticate or else it would default
         * to url/login
        */


    }

}

