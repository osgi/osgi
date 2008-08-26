/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.shared;

import java.io.*;

public interface IHandler {
	/**
	 * Option logging. This means that no comparisons are made, only the raw
	 * bundle output is send to the applet. value = 0x0001
	 */
	public final static int	OPTION_LOGGING	= 0x0001;
	/**
	 * Option debug. Send debug messages to the console to see the progress.
	 * value = 0x0002
	 */
	public final static int	OPTION_DEBUG	= 0x0002;
	/**
	 * Option forever. Disable timeouts. value = 0x0004
	 */
	public final static int	OPTION_FOREVER	= 0x0004;

	void close();

	void installBundle(String name, InputStream in) throws Exception;

	void startRun(ID target, ID[] testcases, int options) throws Exception;

	void stopRun() throws IOException;

	public ID[] getTargets();

	public ID[] getTestCases();

	public void setProgess(int percent) throws IOException;

	public void setMessage(String message) throws IOException;

	public void setError(String message) throws IOException;

	public void finished(IRun run);

	public void local(boolean on) throws IOException;

	public String getDescription(ID testcase);

	public ID[] getBundles();

	public void startBundle(ID id);

	public void stopBundle(ID id);

	public void uninstallBundle(ID id);

	public void connectManual(String host);

	void setApplet(IApplet applet) throws Exception;
	//	ScriptEditor newScriptEditor(String name, URL path);
	//	ScriptEditor getScriptEditor( ID id );
}
