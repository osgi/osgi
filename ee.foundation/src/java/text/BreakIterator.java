/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.text;
public abstract class BreakIterator implements java.lang.Cloneable {
    protected BreakIterator() { }
    public java.lang.Object clone() { return null; }
    public abstract int first();
    public abstract int last();
    public abstract int next(int var0);
    public abstract int next();
    public abstract int previous();
    public abstract int following(int var0);
    public int preceding(int var0) { return 0; }
    public boolean isBoundary(int var0) { return false; }
    public abstract int current();
    public abstract java.text.CharacterIterator getText();
    public void setText(java.lang.String var0) { }
    public abstract void setText(java.text.CharacterIterator var0);
    public static java.text.BreakIterator getWordInstance() { return null; }
    public static java.text.BreakIterator getWordInstance(java.util.Locale var0) { return null; }
    public static java.text.BreakIterator getLineInstance() { return null; }
    public static java.text.BreakIterator getLineInstance(java.util.Locale var0) { return null; }
    public static java.text.BreakIterator getCharacterInstance() { return null; }
    public static java.text.BreakIterator getCharacterInstance(java.util.Locale var0) { return null; }
    public static java.text.BreakIterator getSentenceInstance() { return null; }
    public static java.text.BreakIterator getSentenceInstance(java.util.Locale var0) { return null; }
    public static java.util.Locale[] getAvailableLocales() { return null; }
    public final static int DONE = -1;
}

