package jp.co.axa.apidemo.services;

public class EmployeeServiceException extends RuntimeException {

  public EmployeeServiceException(String message, Exception cause) {
    super(message, cause);
  }

  public EmployeeServiceException(String message) {
    super(message);
  }

}
