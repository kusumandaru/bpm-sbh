package com.sbh.bpm.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {
  // digit + lowercase char + uppercase char + punctuation + symbol
  private static final String PASSWORD_PATTERN =
          "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,50}$";

  private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

  public static boolean isValid(final String password) {
      Matcher matcher = pattern.matcher(password);
      return matcher.matches();
  }
}