/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.reliablefile;

import java.io.*;

/**
 * A ReliableFile FileInputStream replacement class.
 * This class can be used just like FileInputStream. The class
 * is in partnership with ReliableFileOutputStream to avoid losing
 * file data by using multiple files.
 *
 * @see			ReliableFileOutputStream
 */
public class ReliableFileInputStream extends FilterInputStream {
	/**
	 * ReliableFile object for this file.
	 */
	private ReliableFile reliable;

	/** 
	 * size of crc and signature
	 */
	private int sigSize;

	/** 
	 * current position reading from file
	 */
	private int readPos;

	/** 
	 * total file length available for reading
	 */
	private int length;

	/**
	 * Constructs a new ReliableFileInputStream on the file named <code>name</code>.  If the
	 * file does not exist, the <code>FileNotFoundException</code> is thrown.
	 * The <code>name</code> may be absolute or relative
	 * to the System property <code>"user.dir"</code>.
	 *
	 * @param		name	the file on which to stream reads.
	 * @exception 	java.io.IOException If an error occurs opening the file.
	 */
	public ReliableFileInputStream(String name) throws IOException {
		this(ReliableFile.getReliableFile(name));
	}

	/**
	 * Constructs a new ReliableFileInputStream on the File <code>file</code>.  If the
	 * file does not exist, the <code>FileNotFoundException</code> is thrown.
	 *
	 * @param		file		the File on which to stream reads.
	 * @exception 	java.io.IOException If an error occurs opening the file.
	 */
	public ReliableFileInputStream(File file) throws IOException {
		this(ReliableFile.getReliableFile(file));
	}

	/**
	 * Private constructor used by other constructors.
	 *
	 * @param		reliable		the ReliableFile on which to read.
	 * @exception 	java.io.IOException If an error occurs opening the file.
	 */
	private ReliableFileInputStream(ReliableFile reliable) throws IOException {
		super(reliable.getInputStream());

		this.reliable = reliable;
		sigSize = reliable.getSignatureSize();
		readPos = 0;
		length = super.available();
		if (sigSize > length)
			length = 0; // shouldn't ever happen
		else
			length -= sigSize;
	}

	/**
	 * Closes this input stream and releases any system resources associated
	 * with the stream.
	 *
	 * @exception 	java.io.IOException If an error occurs closing the file.
	 */
	public synchronized void close() throws IOException {
		if (reliable != null) {
			try {
				super.close();
			} finally {
				reliable.closeInputFile();
				reliable = null;
			}
		}
	}

	/**
	 * Override default FilterInputStream method.
	 */
	public synchronized int read(byte b[], int off, int len) throws IOException {
		if (readPos >= length) {
			return -1;
		}
		int num = super.read(b, off, len);

		if (num != -1) {
			if (num + readPos > length) {
				num = length - readPos;
			}
			readPos += num;
		}
		return num;
	}

	/**
	 * Override default FilterInputStream method.
	 */
	public synchronized int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}

	/**
	 * Override default FilterInputStream method.
	 */
	public synchronized int read() throws IOException {
		if (readPos >= length) {
			return -1;
		}
		int num = super.read();

		if (num != -1) {
			readPos++;
		}
		return num;
	}

	/**
	 * Override default available method.
	 */
	public synchronized int available() throws IOException {
		if (readPos < length) // just in case
			return (length - readPos);
		else
			return 0;
	}

	/**
	 * Override default skip method.
	 */
	public synchronized long skip(long n) throws IOException {
		long len = super.skip(n);
		if (readPos + len > length)
			len = length - readPos;
		readPos += len;
		return len;
	}

	/**
	 * Override default markSupported method.
	 */
	public boolean markSupported() {
		return false;
	}

	/**
	 * Override default mark method.
	 */
	public void mark(int readlimit) {
	}

	/**
	 * Override default reset method.
	 */
	public void reset() throws IOException {
		throw new IOException("reset not supported."); //$NON-NLS-1$
	}
}
