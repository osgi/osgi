/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.net;
public abstract class ContentHandler {
	public ContentHandler() { }
	public abstract java.lang.Object getContent(java.net.URLConnection var0) throws java.io.IOException;
}

