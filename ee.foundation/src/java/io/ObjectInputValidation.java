/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.io;
public abstract interface ObjectInputValidation {
    public abstract void validateObject() throws java.io.InvalidObjectException;
}

