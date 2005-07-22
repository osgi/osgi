package org.osgi.impl.service.deploymentadmin.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.osgi.impl.service.deploymentadmin.DAConstants;

public class UserPrompt {
    
    private static boolean enabled;
    static {
        String s = System.getProperty(DAConstants.USER_PROMPT);
        if (null == s)
            enabled = false;
        enabled = Boolean.valueOf(s).booleanValue(); 
    }
    
    static String prompt(String info, String question, String def) {
        if (!enabled)
            return def;
        
        System.out.println(info);
        System.out.print(question + " [" + (null == def ? "" : def) + "]: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        try {
            line = br.readLine();
        }
        catch (IOException e) {
        }
        if (line.trim().equals(""))
            return def;
        return line;
    }

}
