/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000-2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.io.tbc;

import org.osgi.service.io.ConnectionFactory;
import javax.microedition.io.Connection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;
import java.io.*;

public class TestConnectionFactory implements ConnectionFactory {
	String	uri;
	int		mode	= -1;
	boolean	timeouts;

	public Connection createConnection(String name, int mode, boolean timeouts)
			throws IOException {
		this.uri = name;
		this.mode = mode;
		this.timeouts = timeouts;
		return new TestConnection();
	}

	void clean() {
		uri = null;
		mode = -1;
		timeouts = false;
	}

	static class TestConnection implements Connection, InputConnection,
			OutputConnection {
		public void close() throws IOException {
		}

		public DataInputStream openDataInputStream() throws IOException {
			return new DataInputStream(new ByteArrayInputStream(new byte[] {}));
		}

		public InputStream openInputStream() throws IOException {
			return new ByteArrayInputStream(new byte[] {});
		}

		public DataOutputStream openDataOutputStream() throws IOException {
			return new DataOutputStream(new ByteArrayOutputStream());
		}

		public OutputStream openOutputStream() throws IOException {
			return new ByteArrayOutputStream();
		}
	}
}
