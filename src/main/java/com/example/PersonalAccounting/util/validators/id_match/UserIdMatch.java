package com.example.PersonalAccounting.util.validators.id_match;

import com.example.PersonalAccounting.entity.datails.UserDetailsImpl;
import com.example.PersonalAccounting.services.UserDetailsServiceImpl;
import com.example.PersonalAccounting.services.entity_service_impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserIdMatch{

    //TODO: maybe use some pattern ugly extends structure

    private final UserService userService;

    @Autowired
    public UserIdMatch(UserService userService) {
        this.userService = userService;
    }

    public void matchUserId(int userId) {
        UserDetailsImpl userDetails = UserDetailsServiceImpl.getCurrentUserDetails();

        if(userDetails.isAdmin()) return;

        if(userService.getOne(userDetails.getUsername()).getId() != userId) {
            throw new IllegalArgumentException("Operation under another user object");
        }
    }
}
