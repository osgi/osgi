/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.io;
public abstract class FilterWriter extends java.io.Writer {
    protected FilterWriter(java.io.Writer var0) { }
    public void close() throws java.io.IOException { }
    public void flush() throws java.io.IOException { }
    public void write(char[] var0, int var1, int var2) throws java.io.IOException { }
    public void write(int var0) throws java.io.IOException { }
    public void write(java.lang.String var0, int var1, int var2) throws java.io.IOException { }
    protected java.io.Writer out;
}

