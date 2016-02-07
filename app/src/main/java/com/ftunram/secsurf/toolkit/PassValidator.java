/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ftunram.secsurf.toolkit;

import java.util.Arrays;

/**
 *
 * @author I
 */
public class PassValidator {
    public static boolean isPasswordCorrect(char[] input) {
    boolean isCorrect = true;
    FileRW readerr= new FileRW();
    char[] correctPassword = readerr.read("setting\\db.txt")[0].toCharArray();

    if (input.length != correctPassword.length) {
        isCorrect = false;
    } else {
        isCorrect = Arrays.equals (input, correctPassword);
    }

    //Zero out the password.
    Arrays.fill(correctPassword,'0');

    return isCorrect;
}
    
}
