package nl.saxion.controller.exceptions;

public class ForbiddenException extends Exception
{
    public ForbiddenException() {
    	
    }

    public ForbiddenException(String message)
    {
       super(message);
    }
}