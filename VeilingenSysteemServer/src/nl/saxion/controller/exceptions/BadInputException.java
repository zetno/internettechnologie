package nl.saxion.controller.exceptions;

public class BadInputException extends Exception
{
    public BadInputException() {
    	
    }

    public BadInputException(String message)
    {
       super(message);
    }
}