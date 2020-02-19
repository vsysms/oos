package exceptions;

public class CrudException extends RuntimeException {
  public CrudException(String message, Throwable cause) {
    super(message, cause);
  }
}
