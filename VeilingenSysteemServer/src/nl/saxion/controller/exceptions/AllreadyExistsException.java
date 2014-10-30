package nl.saxion.controller.exceptions;

public class AllreadyExistsException extends Exception
{
    public AllreadyExistsException() {
    	
    }

    public AllreadyExistsException(String message)
    {
       super(message);
    }
}