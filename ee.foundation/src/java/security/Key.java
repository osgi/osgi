/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security;
public abstract interface Key extends java.io.Serializable {
    public abstract java.lang.String getAlgorithm();
    public abstract byte[] getEncoded();
    public abstract java.lang.String getFormat();
    public final static long serialVersionUID = 6603384152749567654l;
}

