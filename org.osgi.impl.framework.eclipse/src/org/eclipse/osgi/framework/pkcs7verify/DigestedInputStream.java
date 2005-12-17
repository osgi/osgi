/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.pkcs7verify;

import java.io.*;
import java.security.MessageDigest;

/**
 * This InputStream will calculate the digest of bytes as they are read. At the
 * end of the InputStream, it will calculate the digests and throw an exception
 * if the calculated digest do not match the expected digests.
 */
class DigestedInputStream extends FilterInputStream {
	MessageDigest digest[];
	byte result[][];
	long remaining;

	/**
	 * Constructs an InputStream that uses another InputStream as a source and
	 * calculates the digest. At the end of the stream an exception will be
	 * thrown if the calculated digest doesn't match the passed digest.
	 * 
	 * @param in the stream to use as an input source.
	 * @param digest the MessageDigest used to compute the digest.
	 * @param result the expected digest.
	 */
	DigestedInputStream(InputStream in, MessageDigest digest[], byte result[][], long size) {
		super(in);
		this.remaining = size;
		this.digest = new MessageDigest[digest.length];
		for (int i = 0; i < digest.length; i++) {
			try {
				this.digest[i] = (MessageDigest) digest[i].clone();
			} catch (CloneNotSupportedException e) {
				// This shouldn't happen since MessageDigest supports clone!
				throw new RuntimeException("MessageDigest must support clone"); //$NON-NLS-1$
			}
		}
		this.result = result;
	}

	/**
	 * Not supported.
	 */
	public synchronized void mark(int readlimit) {
		// Noop, we don't want to support this
	}

	/**
	 * Always returns false.
	 */
	public boolean markSupported() {
		return false;
	}

	/**
	 * Read a byte from the InputStream. Digests are calculated on reads. At the
	 * end of the stream the calculated digests must match the expected digests.
	 * 
	 * @return the character read or -1 at end of stream.
	 * @throws IOException if there was an problem reading the byte or at the
	 *         end of the stream the calculated digests do not match the
	 *         expected digests.
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		if (remaining <= 0) {
			return -1;
		}
		int c = super.read();
		if (c != -1) {
			for (int i = 0; i < digest.length; i++)
				digest[i].update((byte) c);
			remaining--;
		} else {
			// We hit eof so set remaining to zero
			remaining = 0;
		}
		if (remaining == 0) {
			verifyDigests();
		}
		return c;
	}

	private void verifyDigests() throws IOException {
		// Check the digest at end of file
		for (int i = 0; i < digest.length; i++) {
			byte rc[] = digest[i].digest();
			if (!MessageDigest.isEqual(result[i], rc)) {
				throw new IOException("Corrupted file"); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Read bytes from the InputStream. Digests are calculated on reads. At the
	 * end of the stream the calculated digests must match the expected digests.
	 * 
	 * @return the number of characters read or -1 at end of stream.
	 * @throws IOException if there was an problem reading or at the
	 *         end of the stream the calculated digests do not match the
	 *         expected digests.
	 * @see java.io.InputStream#read()
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		if (remaining <= 0) {
			return -1;
		}
		int rc = super.read(b, off, len);
		if (rc != -1) {
			for (int i = 0; i < digest.length; i++)
				digest[i].update(b, off, rc);
			remaining -= rc;
		} else {
			// We hit eof so set remaining to zero
			remaining = 0;
		}
		if (remaining <= 0) {
			verifyDigests();
		}
		return rc;
	}

	/**
	 * Not supported.
	 * 
	 * @throws IOException always thrown if this method is called since mark/reset is not supported.
	 * @see java.io.InputStream#reset()
	 */
	public synchronized void reset() throws IOException {
		// Throw IOException, we don't want to support this
		throw new IOException("Reset not supported"); //$NON-NLS-1$
	}

	/**
	 * This method is implemented as a read into a bitbucket.
	 */
	public long skip(long n) throws IOException {
		byte buffer[] = new byte[4096];
		int rc;
		long count = 0;
		while (n - count > 0) {
			rc = (n - count) > buffer.length ? buffer.length : (int) (n - count);
			rc = read(buffer, 0, rc);
			if (rc == -1)
				break;
			count += rc;
			n -= rc;
		}
		return count;
	}
}