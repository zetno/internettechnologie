package nl.saxion.controller;

public class BadInputException extends Exception
{
    public BadInputException() {
    	
    }

    public BadInputException(String message)
    {
       super(message);
    }
}