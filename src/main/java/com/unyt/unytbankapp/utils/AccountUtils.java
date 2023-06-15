package com.unyt.unytbankapp.utils;

import java.time.Year;

public class AccountUtils {

    public static String generateAccountNumber() {
        /**
         * Creating an account by concatenating current year with six other digits
         */

        Year currentYear = Year.now();

        int min = 100000;
        int max = 999999;

        //generate a random number between min and max
        int randNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);
        //Convert current year and random number to a string and concatenate

        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randomNumber).toString();
    }
}
