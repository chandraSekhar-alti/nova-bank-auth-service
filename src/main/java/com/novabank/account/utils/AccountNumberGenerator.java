package com.novabank.account.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AccountNumberGenerator {

    public String generateAccountNumber() {

        /*
         * Need to implement later :
         * Branch/Region Code (2–3 digits): Identifies where the account was opened.
         * Product/Account Type Code (2 digits): Identifies if it's Savings (e.g., 10), Current (20), Loan (30), etc.
         * Unique Sequence Number (5–7 digits): A sequential or masked auto-incrementing number from your database.
         * Check Digit (1 digit): A final digit calculated using a checksum algorithm (like Luhn or Modulo 11) to catch typos and data entry errors.
         */
        Random random = new Random();

        return "NB"
                + (100000000
                + random.nextInt(900000000));
    }
}
