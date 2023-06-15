package com.unyt.unytbankapp.service.impl;

import com.unyt.unytbankapp.dto.BankResponse;
import com.unyt.unytbankapp.dto.UserRequest;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
}
