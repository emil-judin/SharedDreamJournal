package com.judin.android.shareddreamjournal.exceptions;

public class PasswordsNotMatchingException extends Exception{
    public PasswordsNotMatchingException(){
        super("Passwords do not match.");
    }
}
