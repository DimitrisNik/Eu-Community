package com.Logs;

import java.io.Serializable;

public class OtherException extends Exception implements Serializable{

    /**
     * this method create new exception
     */
    public OtherException(String message) {
        super(message);
    }
}
