package nl.saxion.controller.exceptions;

public class BidTooLowException extends Exception
{
    public BidTooLowException() {
    	
    }

    public BidTooLowException(String message)
    {
       super(message);
    }
}