package com.judin.android.shareddreamjournal.exceptions;

public class InvalidUsernameException extends Exception{
    public InvalidUsernameException(){
        super("Username is invalid.");
    }
}