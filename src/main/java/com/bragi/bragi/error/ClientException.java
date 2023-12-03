package com.bragi.bragi.error;

public class ClientException extends RuntimeException{

    public ClientException(){
        super();
    }

    public ClientException(String message){
        super(message);
    }
}
