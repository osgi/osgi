package org.osgi.meg.demo.remote;


public class CommanderException extends Exception {
    
    private String code;
    private String trace;
    
	public String getCode() {
		return code;
	}
    
    public String getTrace() {
        return trace;
    }
    
    public CommanderException(String code, String trace) {
        this.code = code;
        this.trace = trace;
    }

}
