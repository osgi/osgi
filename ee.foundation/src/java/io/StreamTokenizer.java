/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.io;
public class StreamTokenizer {
    private StreamTokenizer() { }
    public StreamTokenizer(java.io.Reader var0) { }
    public void commentChar(int var0) { }
    public void eolIsSignificant(boolean var0) { }
    public int lineno() { return 0; }
    public void lowerCaseMode(boolean var0) { }
    public int nextToken() throws java.io.IOException { return 0; }
    public void ordinaryChar(int var0) { }
    public void ordinaryChars(int var0, int var1) { }
    public void parseNumbers() { }
    public void pushBack() { }
    public void quoteChar(int var0) { }
    public void resetSyntax() { }
    public void slashSlashComments(boolean var0) { }
    public void slashStarComments(boolean var0) { }
    public java.lang.String toString() { return null; }
    public void whitespaceChars(int var0, int var1) { }
    public void wordChars(int var0, int var1) { }
    public double nval;
    public java.lang.String sval;
    public final static int TT_EOF = -1;
    public final static int TT_EOL = 10;
    public final static int TT_NUMBER = -2;
    public final static int TT_WORD = -3;
    public int ttype;
}

