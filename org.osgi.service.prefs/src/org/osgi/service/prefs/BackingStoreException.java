/*
 * @(#)BackingStoreException.java   1.3 01/07/18
 * $Header$
 *
 * Open Services Gateway Initiative (OSGi) Confidential.
 *
 * (C) Copyright 1996-2001 Sun Microsystems, Inc.
 *
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 *
 */

package org.osgi.service.prefs;

/**
 * Thrown to indicate that a preferences operation could not complete because
 * of a failure in the backing store, or a failure to contact the backing
 * store.
 *
 * @version $Revision$
 */

public class BackingStoreException extends Exception {
    /**
     * Constructs a <tt>BackingStoreException</tt> with the specified detail message.
     *
     * @param s the detail message.
     */
    public BackingStoreException(String s) {
        super(s);
    }
}
