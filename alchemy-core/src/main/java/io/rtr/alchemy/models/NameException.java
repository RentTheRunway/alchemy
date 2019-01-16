package io.rtr.alchemy.models;

public class NameException extends RuntimeException{
    // based on https://www.baeldung.com/java-new-custom-exception
    public NameException(String errorMessage) {
        super(errorMessage);
    }
}
