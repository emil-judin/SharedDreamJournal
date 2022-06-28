package com.judin.android.shareddreamjournal.exceptions;

public class EmptyInputException extends Exception{
    public EmptyInputException(){
        super("Required field is empty.");
    }
}
