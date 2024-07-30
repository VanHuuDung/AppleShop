package com.example.onlineshopapp.Utils;

import android.util.Patterns;

public class InputValidator {

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.length() >= 10 && phone.matches("\\d+");
    }

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && !name.matches(".*\\d.*");
    }
}
