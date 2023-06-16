package com.unyt.unytbankapp.service.impl;

import com.unyt.unytbankapp.dto.AccountInfo;
import com.unyt.unytbankapp.dto.BankResponse;
import com.unyt.unytbankapp.dto.UserRequest;
import com.unyt.unytbankapp.entity.User;
import com.unyt.unytbankapp.repository.UserRepository;
import com.unyt.unytbankapp.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(Optional.empty())
                    .build();
        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        //saved the created user to the database
        User savedUser = userRepository.save(newUser);
        return accountCreationResponse(savedUser);
    }

    private BankResponse accountCreationResponse(User savedUser) {
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(Optional.ofNullable(accountInfoResponse(savedUser)))
                .build();
    }

    private AccountInfo accountInfoResponse(User savedUser) {
        return AccountInfo.builder()
                .accountBalance(savedUser.getAccountBalance())
                .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                .accountNumber(savedUser.getAccountNumber())
                .build();
    }
}
