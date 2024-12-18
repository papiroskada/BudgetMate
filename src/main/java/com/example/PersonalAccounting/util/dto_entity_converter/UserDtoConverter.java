package com.example.PersonalAccounting.util.dto_entity_converter;

import com.example.PersonalAccounting.dto.UserDTO;
import com.example.PersonalAccounting.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter {

    private final ModelMapper modelMapper;

    @Autowired
    public UserDtoConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User convertToUser(UserDTO userDTO) {return modelMapper.map(userDTO, User.class);}

    public UserDTO convertToUserDTO(User user) {return modelMapper.map(user, UserDTO.class);}
}
