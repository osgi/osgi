package org.osgi.service.dmt;

import java.io.PrintWriter;
import java.io.PrintStream;

import java.util.Vector;
import java.util.Iterator;

/**
 * Checked exception received when a DMT operation fails. 
 */
public class DmtException extends Exception {
 
    private String    uri     = null;
    private int       code    = 0;
    private String    message = null;
    private Vector    causes  = null;
 
    public static final int NODE_NOT_FOUND        = 404;
    public static final int UNAUTHORIZED          = 405;
    public static final int COMMAND_NOT_ALLOWED   = 405;
    public static final int FEATURE_NOT_SUPPORTED = 406;
    public static final int URI_TOO_LONG          = 414;
    public static final int FORMAT_NOT_SUPPORTED  = 415;
    public static final int NODE_ALREADY_EXISTS   = 418;
    public static final int DEVICE_FULL           = 420;
    public static final int PERMISSION_DENIED     = 425;
    public static final int COMMAND_FAILED        = 500;
    public static final int DATA_STORE_FAILURE    = 510;
    public static final int ROLLBACK_FAILED       = 516;

    public static final int OTHER_ERROR       = 0;
    public static final int REMOTE_ERROR      = 1;
    public static final int METADATA_MISMATCH = 2;
    public static final int CONCURRENT_ACCESS = 3;
    public static final int INVALID_DATA      = 4; // TODO is METADATA_MISMATCH also good for this purpose?

    // TODO other standard DMT errors
  
    /**
     * Create an instance of the exception.  
     * No originating exception is specified.
     * @param uri The node on which the failed DMT operation was issued
     * @param code The error code of the failure
     * @param message Message associated with the exception
     */
    public DmtException(String uri, int code, String message) {
        this.uri = uri;
        this.code = code;
        this.message = message;
        causes = new Vector();
    }

    /**
     * Create an instance of the exception, specifying the cause exception.
     * @param uri The node on which the failed DMT operation was issued
     * @param code The error code of the failure
     * @param message Message associated with the exception
     * @param cause The originating exception
     */
    public DmtException(String uri, int code, String message,
                        Throwable cause) {
        this(uri, code, message);
        causes.add(cause);
    }
 
    /**
     * Create an instance of the exception, specifying the list of cause
     * exceptions.
     * @param uri The node on which the failed DMT operation was issued
     * @param code The error code of the failure
     * @param message Message associated with the exception
     * @param causes The list of originating exceptions
     */
    public DmtException(String uri, int code, String message,
                        Vector causes) {
        this(uri, code, message);
        this.causes = (Vector) causes.clone();
    }
 
    /**
     * Get the node on which the failed DMT operation was issued. Some 
     * operations like <code>DmtSession.close()</code> don't require an URI,
     * in this case this method returns <code>null</code>.
     * @return the URI of the node, or <code>null</code> 
     */
    public String getURI() {
        return uri;
    }
 
    /** 
     * Get the error code associated with this exception. Most of the error
     * codes (returned by <code>getCode()</code>) within this exception
     * correspond to OMA DM error codes.
     * @return the error code
     */
    public int getCode() {
        return code;
    }
    /** 
     * Get the message associated with this exception.  The message also
     * contains the associated URI and the exception code, if specified.
     * @return the error message, or <code>null</code> if not specified
     */
    public String getMessage() {
        String fullMessage = message == null ? "" : message;

        if(uri != null)
            fullMessage = "'" + uri + "': " + fullMessage;

        if(code != OTHER_ERROR)
            fullMessage = getCodeText(code) + ": " + fullMessage;

        return fullMessage;
    }
 
    /** 
     * Get the cause of this exception.  Returns non-<code>null</code>, 
     * if this exception is caused by one or more other exceptions 
     * (like a <code>NullPointerException</code> in a Dmt Plugin).
     */
    public Throwable getCause() {
        return causes.size() == 0 ? null : (Throwable) causes.firstElement();
    }

    /**
     * Get all causes of this exception.  Returns the causing exceptions 
     * in a vector.  If no cause was specified, an empty vector is returned.
     */
    public Vector getCauses() {
        return causes;
    }

    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        Iterator i = causes.iterator();
        for(int n = 1; i.hasNext(); n++) {
            s.print("Caused by" + (n > 1 ? " (" + n + ")" : "") + ": ");
            ((Throwable) i.next()).printStackTrace(s);
        }
    }

    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        Iterator i = causes.iterator();
        for(int n = 1; i.hasNext(); n++) {
            s.print("Caused by" + (n > 1 ? " (" + n + ")" : "") + ": ");
            ((Throwable) i.next()).printStackTrace(s);
        }
    }

    // TODO write names instead of numbers in the switch
    private String getCodeText(int code) {
        switch(code) {
        case 404: return "NODE_NOT_FOUND";
        case 405: return "UNAUTHORIZED/COMMAND_NOT_ALLOWED";
        case 406: return "FEATURE_NOT_SUPPORTED";
        case 414: return "URI_TOO_LONG";
        case 415: return "FORMAT_NOT_SUPPORTED";
        case 418: return "NODE_ALREADY_EXISTS";
        case 420: return "DEVICE_FULL";
        case 425: return "PERMISSION_DENIED";
        case 500: return "COMMAND_FAILED";
        case 510: return "DATA_STORE_FAILURE";
        case 516: return "ROLLBACK_FAILED";
        case 0:   return "OTHER_ERROR";
        case 1:   return "REMOTE_ERROR";
        case 2:   return "METADATA_MISMATCH";
        case 3:   return "CONCURRENT_ACCESS";
        case 4:   return "INVALID_DATA";
        default:  
            return "<unknown code>";
        }
    }
}
