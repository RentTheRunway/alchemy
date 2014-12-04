package io.rtr.alchemy.ui;

import java.security.InvalidParameterException;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class EnvironmentParameterValidator implements IParameterValidator {
    
    public void validate(String name, String value) throws ParameterException {
        try {
            Environment.valueOf(value);
        } catch ( IllegalArgumentException ia ) {
            throw new InvalidParameterException("Environment must be one of \"qa\", \"stage\", \"prod\".");
        }
    }

}
