/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.io;
public abstract interface ObjectInputValidation {
    public abstract void validateObject() throws java.io.InvalidObjectException;
}

