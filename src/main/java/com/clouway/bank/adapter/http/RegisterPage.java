package com.clouway.bank.adapter.http;

import com.clouway.bank.core.User;
import com.clouway.bank.core.UserRepository;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.Show;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.http.Post;

import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
@At("/register")
@Show("RegisterPage.html")
public class RegisterPage {
  private final UserRepository userRepository;
  private boolean showSuccessMessage = false;
  private boolean showErrorMessage = false;
  private String successMessage;
  private String errorMessage;

  @Inject
  public RegisterPage(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Post
  public String register(Request request) {
    String name = request.param("name");
    String password = request.param("password");
    String passwordValidation = request.param("passwordValidation");

    if(!password.equals(passwordValidation)) {
      showErrorMessage = true;
      errorMessage = "Passwords should match.";
      return null;
    }

    Optional<User> possibleUser = userRepository.findByUserName(name);

    if(possibleUser.isPresent()) {
      showErrorMessage = true;
      errorMessage = "Username is already taken.";
      return null;
    }

    userRepository.register(name,password);
    successMessage = "You've succesfully registered new User Account: " + name + "!";
    showSuccessMessage = true;
    return null;
  }

  public String getSuccessMessage() {
    return successMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public boolean isShowSuccessMessage() {
    return showSuccessMessage;
  }

  public boolean isShowErrorMessage() {
    return showErrorMessage;
  }
}
