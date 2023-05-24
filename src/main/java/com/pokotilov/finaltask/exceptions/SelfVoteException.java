package com.pokotilov.finaltask.exceptions;

public class SelfVoteException extends RuntimeException{
    public SelfVoteException(String message) {
        super(message);
    }
}
