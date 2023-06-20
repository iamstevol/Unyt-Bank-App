package com.unyt.unytbankapp.service.impl;

import com.unyt.unytbankapp.dto.*;
import com.unyt.unytbankapp.entity.User;
import com.unyt.unytbankapp.repository.UserRepository;
import com.unyt.unytbankapp.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

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

        //Send email to the saved user
//        emailService.sendEmailAlert(emaildetails(savedUser));
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

    private EmailDetails emaildetails(User savedUser) {
        return EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your account has been successfully created.\nYour Account Details: \n" +
                        "Account name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\n" +
                        "Account number: " + savedUser.getAccountNumber())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist) {
            return BankResponse.builder()
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .accountInfo(Optional.empty())
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(Optional.ofNullable(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .accountNumber(foundUser.getAccountNumber())
                        .build()))
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist) {
            return BankResponse.builder()
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .accountInfo(Optional.empty())
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .accountInfo(Optional.ofNullable(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build()))
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //check if the account exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist) {
            return BankResponse.builder()
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .accountInfo(Optional.empty())
                    .build();
        }
        //check if the amount you intend to withdraw is not more than the current account balance
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if(availableBalance.intValue() < debitAmount.intValue()) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(Optional.empty())
                    .build();
        }
        userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(userToDebit);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBITED_CODE)
                .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                .accountInfo(Optional.ofNullable(AccountInfo.builder()
                        .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                        .accountNumber(userToDebit.getAccountNumber())
                        .accountBalance(userToDebit.getAccountBalance())
                        .build()))
                .build();

    }

//    @Override
//    public BankResponse transfer(TransferRequest request) {
//        //Check if destination and owned account exist
//        //Debit from owned account and credit the other account
//        boolean isOwnAccountExist = userRepository.existsByEmail(request.getOwnAccountNumber());
//        if(!isOwnAccountExist) {
//            return BankResponse.builder()
//                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
//                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
//                    .accountInfo(Optional.empty())
//                    .build();
//        }
//        User ownerAccountToDebit = userRepository.findByAccountNumber(request.getOwnAccountNumber());
//        BigInteger ownerAvailableBalance = ownerAccountToDebit.getAccountBalance().toBigInteger();
//        BigInteger amountToDebit = request.getAmount().toBigInteger();
//        if(ownerAvailableBalance.intValue() < amountToDebit.intValue()) {
//            return BankResponse.builder()
//                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
//                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
//                    .accountInfo(Optional.ofNullable(AccountInfo.builder()
//                            .accountName(ownerAccountToDebit.getFirstName() + " " + ownerAccountToDebit.getLastName() + " " + ownerAccountToDebit.getOtherName())
//                            .accountNumber(ownerAccountToDebit.getAccountNumber())
//                            .accountBalance(ownerAccountToDebit.getAccountBalance())
//                            .build()))
//                    .build();
//        } else {
//        boolean isRecipientAccountExist = userRepository.existsByEmail(request.getRecipientAccountNumber());
//            ownerAccountToDebit.setAccountBalance(ownerAccountToDebit.getAccountBalance().subtract(request.getAmount()));
//            userRepository.save(ownerAccountToDebit);
//            if(!isRecipientAccountExist) {
//                return BankResponse.builder()
//                        .responseCode(AccountUtils.ACCOUNT_DEBITED_CODE)
//                        .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
//                        .accountInfo(Optional.ofNullable(AccountInfo.builder()
//                                .accountName(ownerAccountToDebit.getFirstName() + " " + ownerAccountToDebit.getLastName() + " " + ownerAccountToDebit.getOtherName())
//                                .accountNumber(ownerAccountToDebit.getAccountNumber())
//                                .accountBalance(ownerAccountToDebit.getAccountBalance())
//                                .build()))
//                        .build();
//            }
//            User recipientAccount = userRepository.findByAccountNumber(request.getRecipientAccountNumber());
//            recipientAccount.setAccountBalance(recipientAccount.getAccountBalance().add(request.getAmount()));
//            userRepository.save(recipientAccount);
//            return BankResponse.builder()
//                    .responseCode(AccountUtils.ACCOUNT_CREDITED_CODE)
//                    .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
//                    .accountInfo(Optional.ofNullable(AccountInfo.builder()
//                            .accountName(recipientAccount.getFirstName() + " " + recipientAccount.getLastName() + " " + recipientAccount.getOtherName())
//                            .accountBalance(recipientAccount.getAccountBalance())
//                            .accountNumber(request.getRecipientAccountNumber())
//                            .build()))
//                    .build();
//        }
//    }
    @Override
    public BankResponse transferFunds(TransferRequest request) {
        // Check if the source account exists
        boolean isSourceAccountExist = userRepository.existsByAccountNumber(request.getSourceAccountNumber());
        if (!isSourceAccountExist) {
            return BankResponse.builder()
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .accountInfo(Optional.empty())
                    .build();
        }

        // Check if the destination account exists
        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isDestinationAccountExist) {
            return BankResponse.builder()
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .accountInfo(Optional.empty())
                    .build();
        }

        // Retrieve the source user account
        User sourceUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());

        // Check if the source account has sufficient balance
        BigDecimal availableBalance = sourceUser.getAccountBalance();
        BigDecimal transferAmount = request.getAmount();
        if (availableBalance.compareTo(transferAmount) < 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(Optional.empty())
                    .build();
        }

        // Perform the debit operation from the source account
        sourceUser.setAccountBalance(availableBalance.subtract(transferAmount));
        userRepository.save(sourceUser);

        // Retrieve the destination user account
        User destinationUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        destinationUser.setAccountBalance(destinationUser.getAccountBalance().add(transferAmount));
        userRepository.save(destinationUser);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
                .accountInfo(Optional.ofNullable(AccountInfo.builder()
                        .accountName(sourceUser.getFirstName() + " " + sourceUser.getLastName() + " " + sourceUser.getOtherName())
                        .accountNumber(sourceUser.getAccountNumber())
                        .accountName(destinationUser.getFirstName() + " " + destinationUser.getLastName() + " " + destinationUser.getOtherName())
                        .accountNumber(destinationUser.getAccountNumber())
                        .accountBalance(destinationUser.getAccountBalance())
                        .build()))
                .build();
    }

}
