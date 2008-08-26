
package org.osgi.test.cases.util;

public abstract class Logger extends Assert {
    
    /**
     * The log method that should send the log to the director. Should 
     * be implemented by a subclass that knows about the infrastructure.
     */
    public abstract void log(String message);

    /**
     * Overridden Assert method that logs a comment when things passes.
     */
    public void pass(String message) {
        log("#" + message);
    }

    public void trace(String message) {
        log("#" + message);
    }

    public void log(String test, String result) {
        log(test + ": \"" + result + "\"");
    }

    /**
     * Method for logging exceptions. The tested code may throw an
     * exception that is a subclass of the specified, so the parameter
     * <code>want</code> specifies the expected class. If the parameter
     * <code>got</code> is of the wanted type (or a subtype of the 
     * wanted type), the classname of <code>want</code> is logged. If 
     * <code>got</code> is of an unexpected type, the classname of
     * <code>got</code> is logged.
     *
     * @param test the log description
     * @param want the exception that is specified to be thrown
     * @param got the exception that was thrown
     */
    public void log(String test, Class want, Throwable got) {
        if(want.isInstance(got)) {
            log(test, want.getName());
        }
        else {
            log(test, got.getClass().getName());
        }
    }

}