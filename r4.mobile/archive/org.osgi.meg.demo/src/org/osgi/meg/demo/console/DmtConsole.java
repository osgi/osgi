/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.meg.demo.console;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import info.dmtree.DmtAdmin;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import org.osgi.tools.command.CommandInterpreter;
import org.osgi.tools.command.CommandProvider;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class DmtConsole implements BundleActivator, CommandProvider {
	ServiceTracker	dmtTracker;
	static String spaces = "                                                                                             ";

	public void start(BundleContext context) throws Exception {
		dmtTracker = new ServiceTracker(context, DmtAdmin.class.getName(),
				null);
		dmtTracker.open();
		context.registerService(CommandProvider.class.getName(), this, null);
	}

	public void stop(BundleContext context) throws Exception {
	}

	public String getHelp() {
		return "DMT CONSOLE\r\n" + "dmtcd <uri>\r\n"
				+ "list dmt [-recursive] [-show] uri\r\n";
	}

	public Object toString(Object o) {
		if (o instanceof Node) {
			return ((Node) o).toString();
		}
		return null;
	}

	public Object _dmtcd(CommandInterpreter intp) {
		String pwd = calcURI(intp, intp.nextArgument());
		if (!pwd.endsWith("/"))
			pwd = pwd + "/";
		intp.setVariable("dmt.pwd", pwd);
		return null;
	}

	private String calcURI(CommandInterpreter intp, String string) {
		if (string == null)
			string = "";
		if (string.startsWith("."))
			return string;
		String prefix = (String) intp.getVariable("dmt.pwd");
		if (prefix == null)
			prefix = "./";
		if (string.length() == 0)
			return prefix.substring(0, prefix.length() - 1);

        return prefix + string;
	}

	public Object _listdmt(CommandInterpreter intp) throws DmtException {
		boolean recursive = false;
		boolean show = false;
		boolean extensive = false;
		String arg = intp.nextArgument();
		while (arg != null) {
			if (arg.startsWith("-r"))
				recursive = true;
			else
				if (arg.startsWith("-x"))
					extensive = true;
				else
					if (arg.startsWith("-s"))
						show = true;
					else {
						if (arg.startsWith("-"))
							throw new RuntimeException("Invalid option " + arg);
						break;
					}
			arg = intp.nextArgument();
		}
		return new Node(intp, arg, intp.getUser(), show, recursive, extensive);
	}

	class Node {
		boolean				show;
		boolean				recursive;
		CommandInterpreter	intp;
		String				uri;
		String				principal;
		boolean				extensive;

		Node(CommandInterpreter intp, String uri, String principal,
				boolean show, boolean recursive, boolean extensive) {
			this.intp = intp;
			this.uri = calcURI(intp, uri);
			this.show = show;
			this.recursive = recursive;
			this.principal = principal;
			this.extensive = extensive;
		}

		public String toString() {
			try {
				DmtAdmin dmt = (DmtAdmin) dmtTracker.getService();
				if (dmt == null)
					throw new RuntimeException("No DmtAdmin service present");
				DmtSession session = dmt.getSession(".");
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(bout,
						"UTF-8"));
				try {
					toString(pw, session, uri, 0);
				}
				catch (Exception e) {
					pw.println("Exception in printing this node " + uri + " "
							+ e);
					e.printStackTrace();
				}
				pw.close();
				return new String(bout.toByteArray(), "UTF-8");
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return super.toString();
		}

		void toString(PrintWriter pw, DmtSession session, String uri, int level)
				throws DmtException {
			String indent = spaces
					.substring(0, level);
			String title = null;
			try {
				title = getLast(uri) + " : " + session.getNodeTitle(uri);
			}
			catch (Exception e) {
				title = getLast(uri);
			}
			if (show && extensive) {
				pw.println(indent + title);
				indent = indent + " -";
				// TODO: Spec should not allow errors for optional attributes
				// but require a well known default value
				pw.println(indent + "ACL                 : "
						+ session.getNodeAcl(uri));
				try {
					pw.println(indent + "Timestamp           : "
							+ session.getNodeTimestamp(uri));
				}
				catch (DmtException e1) {
				}
				if (session.isLeafNode(uri)) {
					try {
						pw.println(indent + "Version             : "
								+ session.getNodeVersion(uri));
					}
					catch (DmtException e2) {
					}
					try {
						pw.println(indent + "Size                : "
								+ session.getNodeSize(uri));
					}
					catch (DmtException e3) {
					}
					try {
						pw.println(indent + "Type                : "
								+ session.getNodeType(uri));
					}
					catch (DmtException e4) {
					}
					try {
						pw.println(indent + "Value               : "
								+ session.getNodeValue(uri));
					}
					catch (DmtException e5) {
					}
				}
			}
			else {
				String header = indent + title;
				pw.print(header);
				
				if ( session.isLeafNode(uri)) {
					String value = "<no value>";
					try {
						value = session.getNodeValue(uri).toString();
					}
					catch (DmtException e5) {
					}
					
					pw.println( spaces.substring(0,Math.max(0,60-header.length())) + " = " + value);
				} else
					pw.println();
			}
			if (recursive && !session.isLeafNode(uri)) {
				String[] children = session.getChildNodeNames(uri);
				for (int i = 0; children != null && i < children.length; i++) {
					toString(pw, session, uri + "/" + children[i], level + 4);
				}
			}
		}
	}

	String getLast(String uri) {
		int n = uri.lastIndexOf("/");
		if (n < 0)
			return uri;
		return uri.substring(n + 1);
	}
}
