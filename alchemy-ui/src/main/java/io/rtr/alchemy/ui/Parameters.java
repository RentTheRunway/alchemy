package io.rtr.alchemy.ui;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class Parameters {
    
    @Parameter(description = "Pick an environment.", required = true, validateWith = EnvironmentParameterValidator.class)
    public List<String> environment = new ArrayList<String>(); 

}
