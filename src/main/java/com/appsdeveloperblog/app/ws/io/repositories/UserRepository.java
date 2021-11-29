package com.appsdeveloperblog.app.ws.io.repositories;

import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    /*
    *This allows us to call generic repository methods that interact with the database.
    *It is included in the spring framework class. Because you are passing in the entity you would like to handle
    *you don't need to list the methods to update the repo here.


    *We are extending the CrudRepository class meaning we can add additional methods for the purposes of validation
    *when interacting with the database. We then need to implement the method in the UserService Implementation
    *
    */

    UserEntity findByEmail(String email);



}
