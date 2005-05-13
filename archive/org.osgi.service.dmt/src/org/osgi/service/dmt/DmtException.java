/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.dmt;

import java.io.PrintWriter;
import java.io.PrintStream;

import java.util.Vector;
import java.util.Iterator;

/**
 * Checked exception received when a DMT operation fails. Beside the exception
 * message, a <code>DmtException</code> always contains an error code (one of
 * the constants specified in this class), and may optionally contain the URI of
 * the related node, and information about the cause of the exception.
 * <p>
 * The cause (if specified) can either be a single <code>Throwable</code>
 * instance, or a list of such instances if several problems occured during the
 * execution of a method. An example for the latter is the <code>close</code>
 * method of <code>DmtSession</code> that tries to close multiple plugins, and
 * has to report the exceptions of all failures.
 * <p>
 * Getter methods are provided to retrieve the values of the additional
 * parameters, and the <code>printStackTrace</code> methods are extended to
 * print the stack trace of all causing throwables as well.
 */
public class DmtException extends Exception {
	// TODO add static final serialVersionUID

    //----- Public constants -----//
    
    /**
     * The requested target node was not found. No indication is given as to
     * whether this is a temporary or permanent condition.
     * This error code corresponds to the OMA DM response status code 404.
     */
    public static final int NODE_NOT_FOUND        = 404;

    /**
     * The requested command is not allowed on the target node. Examples are
     * executing a non-executable node or trying to read the value of an
     * interior node.
     * This error code corresponds to the OMA DM response status code 405.
     */
    public static final int COMMAND_NOT_ALLOWED   = 405;

    /**
     * The requested command failed because an optional feature in the request
     * was not supported.
     * This error code corresponds to the OMA DM response status code 406.
     */
    public static final int FEATURE_NOT_SUPPORTED = 406;

    /**
     * The requested command failed because the target URI is too long for
     * what the recipient is able or willing to process.
     * This error code corresponds to the OMA DM response status code 414.
     */
    public static final int URI_TOO_LONG          = 414;

    /**
     * Unsupported media type or format.
     * This error code corresponds to the OMA DM response status code 415.
     */
    public static final int FORMAT_NOT_SUPPORTED  = 415;

    /**
     * The requested <code>Add</code> or <code>Copy</code> command failed
     * because the target already exists.
     * This error code corresponds to the OMA DM response status code 418.
     */
    public static final int NODE_ALREADY_EXISTS   = 418;

    /**
     * The requested command failed because the sender does not have adequate
     * access control permissions (ACL) on the target.
     * This error code corresponds to the OMA DM response status code 425.
     */
    public static final int PERMISSION_DENIED     = 425;

    /**
     * The recipient encountered an unexpected condition which prevented it
     * from fulfilling the request.
     * This error code corresponds to the OMA DM response status code 500.
     */
    public static final int COMMAND_FAILED        = 500;

    /**
     * An error related to the recipient data store occurred while processing
     * the request.
     * This error code corresponds to the OMA DM response status code 510.
     */
    public static final int DATA_STORE_FAILURE    = 510;

    /**
     * The rollback command was not completed successfully. It should be tried
     * to recover the client back into original state.
     * This error code corresponds to the OMA DM response status code 516.
     */
    public static final int ROLLBACK_FAILED       = 516;

    /**
     * An error occurred that does not fit naturally into any of the other error
     * categories.
     * This error code does not correspond to any OMA DM response status code.
     */
    public static final int OTHER_ERROR           = 0;

    /**
     * A device initiated remote operation failed.
     * This error code does not correspond to any OMA DM response status code.
     */
    public static final int REMOTE_ERROR          = 1;

    /**
     * Invalid data, operation failed bacause of meta data restrictions.
     * Examples can be violating referential integrity constraints or
     * exceeding maximum node value limits, etc.
     * This error code does not correspond to any OMA DM response status code.
     */
    public static final int METADATA_MISMATCH     = 2;

    /**
     * The received URI string was <code>null</code>, contained not allowed
     * characters or was not parseable to a valid URI.
     * This error code does not correspond to any OMA DM response status code.
     */
    public static final int INVALID_URI           = 3;

    /**
     * An error occurred related to concurrent access of nodes. For example a
     * configuration node was deleted through the Config Admin while the node
     * was manipulated via DMT.
     * This error code does not correspond to any OMA DM response status code.
     */
    public static final int CONCURRENT_ACCESS     = 4;

    /**
     * An alert can not be sent from the device to the Remote Management
     * Server because of missing routing information.
     * This error code does not correspond to any OMA DM response status code.
     */
    public static final int ALERT_NOT_ROUTED      = 5;
    
    /**
     * This error is caused by one of the following situations:
     * <li> An updating method within an atomic session can not be executed
     * because the underlying plugin does not support atomic transactions.
     * <li> A commit operation at the end of an atomic session failed because 
     * of lack of two phase commit in the underlying plugins. An example: plugin
     * A has committed successfully but plugin B failed, so the whole session 
     * must fail, but A can not undo the commit
     * This error code does not correspond to any OMA DM response status code.
     */
    public static final int TRANSACTION_ERROR     = 6;

    /**
     * Creation of a session timed out because of another ongoing session.
     * This error code does not correspond to any OMA DM response status code.
     * OMA has several status codes related to timeout, but these are meant to
     * be used when a request times out, not when a session can not be 
     * established. 
     */
    public static final int TIMEOUT               = 7;

    //----- Private fields -----//
    
    private String    uri     = null;
    private int       code    = 0;
    private String    message = null;
    private Vector    causes  = null;
    private boolean   fatal   = false;

    
    //----- Constructors -----//
    
    /**
     * Create an instance of the exception. The <code>uri</code> and
     * <code>message</code> parameters are optional. No originating exception
     * is specified.
     * 
     * @param uri The node on which the failed DMT operation was issued, or
     *        <code>null</code> if the operation is not associated with a node
     * @param code The error code of the failure
     * @param message The message associated with the exception, or
     *        <code>null</code> if there is no error message
     */
    public DmtException(String uri, int code, String message) {
        this.uri = uri;
        this.code = code;
        this.message = message;
        causes = new Vector();
    }

    /**
     * Create an instance of the exception, specifying the cause exception. The
     * <code>uri</code>, <code>message</code> and <code>cause</code>
     * parameters are optional.
     * 
     * @param uri The node on which the failed DMT operation was issued, or
     *        <code>null</code> if the operation is not associated with a node
     * @param code The error code of the failure
     * @param message The message associated with the exception, or
     *        <code>null</code> if there is no error message
     * @param cause The originating exception, or <code>null</code> if there
     *        is no originating exception
     */
    public DmtException(String uri, int code, String message,
                        Throwable cause) {
        this(uri, code, message);
        if(cause != null)
            causes.add(cause);
    }

    /**
     * Create an instance of the exception, specifying the list of cause
     * exceptions. The <code>uri</code>, <code>message</code> and
     * <code>causes</code> parameters are optional.
     * 
     * @param uri The node on which the failed DMT operation was issued, or
     *        <code>null</code> if the operation is not associated with a node
     * @param code The error code of the failure
     * @param message The message associated with the exception, or
     *        <code>null</code> if there is no error message
     * @param causes The list of originating exceptions, or empty list or
     *        <code>null</code> if there are no originating exceptions
     */
    public DmtException(String uri, int code, String message,
                        Vector causes) {
        this(uri, code, message);
        if(causes != null)
            this.causes = (Vector) causes.clone();
    }
    
    /**
     * Create an instance of the exception, specifying the list of cause
     * exceptions and whether the exception is a fatal one. This constructor is
     * meant to be used by plugins wishing to indicate that a serious error
     * occurred which should invalidate the ongoing atomic session. The
     * <code>uri</code>, <code>message</code> and <code>causes</code>
     * parameters are optional.
     * <p>
     * If a fatal exception is thrown, no further business methods will be
     * called on the originator plugin. In case of atomic sessions, all other 
     * open plugins will be rolled back automatically, except if the fatal
     * exception was thrown during commit.
     * 
     * @param uri The node on which the failed DMT operation was issued, or
     *        <code>null</code> if the operation is not associated with a node
     * @param code The error code of the failure
     * @param message The message associated with the exception, or
     *        <code>null</code> if there is no error message
     * @param causes The list of originating exceptions, or empty list or
     *        <code>null</code> if there are no originating exceptions
     * @param fatal Whether the exception is fatal
     */
    public DmtException(String uri, int code, String message,
                        Vector causes, boolean fatal) {
        this(uri, code, message, causes);
        this.fatal = fatal;
    }
    
    //----- Public methods -----//
    
    /**
     * Get the node on which the failed DMT operation was issued. Some
     * operations like <code>DmtSession.close()</code> don't require an URI,
     * in this case this method returns <code>null</code>.
     * 
     * @return the URI of the node, or <code>null</code>
     */
    public String getURI() {
        return uri;
    }

    /**
     * Get the error code associated with this exception. Most of the error
     * codes within this exception correspond to OMA DM error codes.
     * 
     * @return the error code
     */
    public int getCode() {
        return code;
    }
    /**
     * Get the message associated with this exception. The message also contains
     * the associated URI and the exception code, if specified.
     * 
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
     * Get the cause of this exception. Returns non-<code>null</code>, if
     * this exception is caused by one or more other exceptions (like a
     * <code>NullPointerException</code> in a Dmt Plugin).  If there are more
     * than one cause exceptions, the first one is returned.
     * 
     * @return the cause of this exception, or <code>null</code> if no cause
     *         was given
     */
    public Throwable getCause() {
        return causes.size() == 0 ? null : (Throwable) causes.firstElement();
    }

    /**
     * Get all causes of this exception.  Returns the causing exceptions
     * in a vector.  If no cause was specified, an empty vector is returned.
     * 
     * @return the list of causes of this exception
     */
    public Vector getCauses() {
        return causes;
    }
    
    /**
     * Check whether this exception is marked as fatal in the session. Fatal
     * exceptions trigger an automatic rollback of atomic sessions.
     * 
     * @return whether the exception is marked as fatal
     */
    public boolean isFatal() {
        return fatal;
    }

    /**
     * Prints the exception and its backtrace to the specified print stream. Any
     * causes that were specified for this exception are also printed, together
     * with their backtraces.
     *
     * @param s <code>PrintStream</code> to use for output
     */
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        Iterator i = causes.iterator();
        for(int n = 1; i.hasNext(); n++) {
            s.print("Caused by" + (n > 1 ? " (" + n + ")" : "") + ": ");
            ((Throwable) i.next()).printStackTrace(s);
        }
    }

    /**
     * Prints the exception and its backtrace to the specified print writer. Any
     * causes that were specified for this exception are also printed, together
     * with their backtraces.
     *
     * @param s <code>PrintWriter</code> to use for output
     */
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        Iterator i = causes.iterator();
        for(int n = 1; i.hasNext(); n++) {
            s.print("Caused by" + (n > 1 ? " (" + n + ")" : "") + ": ");
            ((Throwable) i.next()).printStackTrace(s);
        }
    }
    
    //----- Private methods -----//

    private String getCodeText(int code) {
        // todo sync codes
        switch(code) {
        case NODE_NOT_FOUND:        return "NODE_NOT_FOUND";
        case COMMAND_NOT_ALLOWED:   return "COMMAND_NOT_ALLOWED";
        case FEATURE_NOT_SUPPORTED: return "FEATURE_NOT_SUPPORTED";
        case URI_TOO_LONG:          return "URI_TOO_LONG";
        case FORMAT_NOT_SUPPORTED:  return "FORMAT_NOT_SUPPORTED";
        case NODE_ALREADY_EXISTS:   return "NODE_ALREADY_EXISTS";
        case PERMISSION_DENIED:     return "PERMISSION_DENIED";
        case COMMAND_FAILED:        return "COMMAND_FAILED";
        case DATA_STORE_FAILURE:    return "DATA_STORE_FAILURE";
        case ROLLBACK_FAILED:       return "ROLLBACK_FAILED";

        case OTHER_ERROR:           return "OTHER_ERROR";
        case REMOTE_ERROR:          return "REMOTE_ERROR";
        case METADATA_MISMATCH:     return "METADATA_MISMATCH";
        case INVALID_URI:           return "INVALID_URI";
        case CONCURRENT_ACCESS:     return "CONCURRENT_ACCESS";
        case ALERT_NOT_ROUTED:      return "ALERT_NOT_ROUTED";
        case TRANSACTION_ERROR:     return "TRANSACTION_ERROR";
        case TIMEOUT:               return "TIMEOUT";
        default:
            return "<unknown code>";
        }
    }
}
