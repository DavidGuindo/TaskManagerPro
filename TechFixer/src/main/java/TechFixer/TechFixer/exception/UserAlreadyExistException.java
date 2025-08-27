package TechFixer.TechFixer.exception;

public class UserAlreadyExistException extends RuntimeException{

    public UserAlreadyExistException(String exception){
        super(exception);
    }
}
