package com.judin.android.shareddreamjournal.exceptions;

public class UsernameTakenException extends Exception{
    public UsernameTakenException(){
        super("Username is already taken.");
    }
}
