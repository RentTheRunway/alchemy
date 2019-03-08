package io.rtr.alchemy.models;
import javax.xml.bind.ValidationException;

class NameValidation {

    public String validate(String name) throws ValidationException {
        String pattern = "^[A-Za-z0-9-_]*$";
        if (! name.matches(pattern)) {
            throw new ValidationException("Invalid name {}, must match {}".format(name, pattern));
        }
        return name;
    }
}