/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.util.zip;
public class Deflater {
    public int deflate(byte[] var0) { return 0; }
    public int deflate(byte[] var0, int var1, int var2) { return 0; }
    public void end() { }
    protected void finalize() { }
    public void finish() { }
    public boolean finished() { return false; }
    public int getAdler() { return 0; }
    public int getTotalIn() { return 0; }
    public int getTotalOut() { return 0; }
    public boolean needsInput() { return false; }
    public void reset() { }
    public void setDictionary(byte[] var0) { }
    public void setDictionary(byte[] var0, int var1, int var2) { }
    public void setInput(byte[] var0) { }
    public void setInput(byte[] var0, int var1, int var2) { }
    public void setLevel(int var0) { }
    public void setStrategy(int var0) { }
    public Deflater() { }
    public Deflater(int var0, boolean var1) { }
    public Deflater(int var0) { }
    public final static int BEST_COMPRESSION = 9;
    public final static int BEST_SPEED = 1;
    public final static int DEFAULT_COMPRESSION = -1;
    public final static int DEFAULT_STRATEGY = 0;
    public final static int DEFLATED = 8;
    public final static int FILTERED = 1;
    public final static int HUFFMAN_ONLY = 2;
    public final static int NO_COMPRESSION = 0;
}

