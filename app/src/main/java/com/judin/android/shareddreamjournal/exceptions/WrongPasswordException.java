package com.judin.android.shareddreamjournal.exceptions;

public class WrongPasswordException extends Exception {
    public WrongPasswordException(){
        super("Wrong password.");
    }
}
