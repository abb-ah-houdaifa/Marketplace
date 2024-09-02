package com.marketplace.marketplace.exception;

public class ImageNotFoundException extends RuntimeException{
    public ImageNotFoundException(String msg){
        super(msg);
    }
}
