/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.net;
public abstract class ContentHandler {
    public ContentHandler() { }
    public abstract java.lang.Object getContent(java.net.URLConnection var0) throws java.io.IOException;
}

