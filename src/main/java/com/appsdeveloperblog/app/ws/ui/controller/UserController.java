package com.appsdeveloperblog.app.ws.ui.controller;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.*;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")//http:/localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;


    //this is how you can accept a path variable passed in as a part of the URL.
    //You can also respond with different types of data formats including json or xml or both as below.
    //The order does matter aas it will default to the first media type if one is not provided in the header
    // of the request
    @GetMapping(
            path="/{id}", produces ={ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public UserRest getUser(@PathVariable String id)
    {
        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);

        return returnValue;

    }

    @GetMapping(
            produces ={ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public List<UserRest> getUsers(@RequestParam(value="page", defaultValue="0") int page,
                                   @RequestParam(value="limit", defaultValue="25") int limit
                                  )
    {
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

        for(UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }

        return returnValue;
    }


    //We can accept and respond with json and xml data using the consumes and produces arguments in addition to the
    //@PostMapping Annotation
    @PostMapping(
            consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception
    {

        UserRest returnValue = new UserRest();


        //this is a generic exception and the message attached to the enum will not display unless you get additional packages to the pom.xml
        // you can create a specific exception and return it to the user
        //if(userDetails.getFirstName().isEmpty()) throw new Exception(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        //This is an inherited implementation of the RuntimeException where we can use the Exception Enum model to pass the exception
        if(userDetails.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        //This handles all other exceptions. You can update to handle the appropriate pipeline request as needed.
        if(userDetails.getLastName().isEmpty()) throw new Exception((ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage()));

        UserDto userDto = new UserDto();

        //copies one object into another. In this case it is transferring the data from the request into the DTO
        BeanUtils.copyProperties(userDetails, userDto);

        //this user service is how we talk to the database. the service has not been created yet.
        UserDto createdUser = userService.createUser(userDto);

        //we then copy the created user into the return value which is of type UserRest which is the structure of all
        // returned user objects;
        BeanUtils.copyProperties(createdUser, returnValue);

        return returnValue;

    }

    @PutMapping(
            path = "/{id}",
            consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails)
    {
        UserRest returnValue = new UserRest();
        UserDto userDto = new UserDto();

        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);

        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;

    }

    @DeleteMapping(
            path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel deleteUser(@PathVariable String id)
    {
        OperationStatusModel returnValue = new OperationStatusModel();

        userService.deleteUser(id);

        returnValue.setOperationName(RequestOperationName.DELETE.name());

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }



}
