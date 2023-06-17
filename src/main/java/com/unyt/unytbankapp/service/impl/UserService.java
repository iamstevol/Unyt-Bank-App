package com.unyt.unytbankapp.service.impl;

import com.unyt.unytbankapp.dto.BankResponse;
import com.unyt.unytbankapp.dto.EnquiryRequest;
import com.unyt.unytbankapp.dto.UserRequest;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
}
