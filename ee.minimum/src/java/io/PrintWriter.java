/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.io;
public class PrintWriter extends java.io.Writer {
    public PrintWriter(java.io.OutputStream var0) { }
    public PrintWriter(java.io.OutputStream var0, boolean var1) { }
    public PrintWriter(java.io.Writer var0) { }
    public PrintWriter(java.io.Writer var0, boolean var1) { }
    public boolean checkError() { return false; }
    public void close() { }
    public void flush() { }
    public void print(char[] var0) { }
    public void print(char var0) { }
    public void print(double var0) { }
    public void print(float var0) { }
    public void print(int var0) { }
    public void print(long var0) { }
    public void print(java.lang.Object var0) { }
    public void print(java.lang.String var0) { }
    public void print(boolean var0) { }
    public void println() { }
    public void println(char[] var0) { }
    public void println(char var0) { }
    public void println(double var0) { }
    public void println(float var0) { }
    public void println(int var0) { }
    public void println(long var0) { }
    public void println(java.lang.Object var0) { }
    public void println(java.lang.String var0) { }
    public void println(boolean var0) { }
    protected void setError() { }
    public void write(char[] var0) { }
    public void write(char[] var0, int var1, int var2) { }
    public void write(int var0) { }
    public void write(java.lang.String var0) { }
    public void write(java.lang.String var0, int var1, int var2) { }
    protected java.io.Writer out;
}

