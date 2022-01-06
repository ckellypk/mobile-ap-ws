package com.appsdeveloperblog.app.ws.exceptions;

import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;


//This annotation allows us to handle different custom exceptions throughout the application
@ControllerAdvice
public class AppExceptionsHandler {



    //This method returns a response entity responsible for handling a specific custom exception. This cleans up the
    //response quite a bit only returning the response message in the body with the HTTP error supplied.You can modify
    //quite a bit of error handling with this. Also a note the console does not output a message when this method is used.
    //This is saying that whenever the UserServiceException.class is invoked, intercept the response and put it in this format

    //If you wanted this to handle multiple exceptions you need to pass each into exception handler annotation,
    // als we need to start pass in the generic exception object (as all other exceptions inherit this class)
    @ExceptionHandler(value = {UserServiceException.class}/*, additional exceptions here*/)
    public ResponseEntity<Object> handlesUserServiceException(UserServiceException ex, WebRequest request){

        //Creating a new instance of the ErrorMessage class which inherits the built-in RuntimeException object, passing
        //in the current dateTime and the UserServiceException class. The actual UserServiceException
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());

        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //Now that we know how to handle a customized exception lets all other standard exceptions in a custom manner.
    //Notice how we pass in the Exception.class into the annotationn, and the method

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handlesOtherException(Exception ex, WebRequest request){

        //Creating a new instance of the ErrorMessage class which inherits the built-in RuntimeException object, passing
        //in the current dateTime and the UserServiceException class. The actual UserServiceException
        ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());

        return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }





}
