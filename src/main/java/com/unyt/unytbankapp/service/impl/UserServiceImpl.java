package com.unyt.unytbankapp.service.impl;

import com.unyt.unytbankapp.dto.BankResponse;
import com.unyt.unytbankapp.dto.UserRequest;
import com.unyt.unytbankapp.entity.User;

public class UserServiceImpl implements UserService {

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber()
                .build();
    }
}
