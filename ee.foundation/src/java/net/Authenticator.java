/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.net;
public abstract class Authenticator {
	public Authenticator() { }
	protected java.net.PasswordAuthentication getPasswordAuthentication() { return null; }
	protected final int getRequestingPort() { return 0; }
	protected final java.net.InetAddress getRequestingSite() { return null; }
	protected final java.lang.String getRequestingPrompt() { return null; }
	protected final java.lang.String getRequestingProtocol() { return null; }
	protected final java.lang.String getRequestingScheme() { return null; }
	public static java.net.PasswordAuthentication requestPasswordAuthentication(java.net.InetAddress var0, int var1, java.lang.String var2, java.lang.String var3, java.lang.String var4) { return null; }
	public static void setDefault(java.net.Authenticator var0) { }
}

