package com.ftunram.secsurf.toolkit;

import java.util.Arrays;

public class PassValidator {
    public static boolean isPasswordCorrect(char[] input) {
        boolean isCorrect;
        char[] correctPassword = new FileRW().read("setting\\db.txt")[0].toCharArray();
        if (input.length != correctPassword.length) {
            isCorrect = false;
        } else {
            isCorrect = Arrays.equals(input, correctPassword);
        }
        Arrays.fill(correctPassword, '0');
        return isCorrect;
    }
}
