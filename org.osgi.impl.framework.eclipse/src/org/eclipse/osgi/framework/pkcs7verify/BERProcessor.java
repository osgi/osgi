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

import java.math.BigInteger;

/**
 * This is a simple class that processes BER structures. This class
 * uses BER processing as outlined in X.690.
 */
public class BERProcessor {
	/**
	 * This is the buffer that contains the BER structures that are being interrogated.
	 */
	public byte buffer[];
	/**
	 * The offset into <code>buffer</code> to the start of the structure being interrogated.
	 * If the offset is -1 that means that we have read the last structure.
	 */
	public int offset;
	/**
	 * The last valid offset in <code>buffer</code>.
	 */
	public int lastOffset;
	/**
	 * The offset into <code>buffer</code> to the start of the content of the structure
	 * being interrogated.
	 */
	public int contentOffset;
	/**
	 * The length of the content of the structure being interrogated.
	 */
	public int contentLength;
	/**
	 * The offset into <code>buffer</code> of the end of the structure being interrogated.
	 */
	public int endOffset;
	/**
	 * The class of the tag of the current structure.
	 */
	public int classOfTag;
	public static final int UNIVERSAL_TAGCLASS = 0;
	public static final int APPLICATION_TAGCLASS = 1;
	public static final int CONTEXTSPECIFIC_TAGCLASS = 2;
	public static final int PRIVATE_TAGCLASS = 3;

	static final byte BOOLTAG = 1;
	static final byte INTTAG = 2;
	static final byte OIDTAG = 6;
	static final byte SEQTAG = 16;
	static final byte SETTAG = 17;
	static final byte NULLTAG = 5;

	/**
	 * Tagnames used in toString()
	 */
	static final String tagNames[] = {"<null>", "boolean", "int", "bitstring", "octetstring", "null", "objid", "objdesc", "external", "real", "enum", "pdv", "utf8", "relobjid", "resv", "resv", "sequence", "set", "char string"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$

	/**
	 * True if this is a structure for a constructed encoding.
	 */
	public boolean constructed;
	/**
	 * The tag type. Note that X.690 specifies encodings for tags with values greater than 31,
	 * but currently this class does not handle these kinds of tags.
	 */
	public byte tag;

	/**
	 * Constructs a BERProcessor to operate on the passed buffer. The first structure in the
	 * buffer will be processed before this method returns.
	 * 
	 * @param buffer the buffer containing the BER structures.
	 * @param offset the offset into <code>buffer</code> to the start of the first structure.
	 * @param len the length of the BER structure.
	 */
	public BERProcessor(byte buffer[], int offset, int len) {
		this.buffer = buffer;
		this.offset = offset;
		lastOffset = len + offset;
		processStructure();
	}

	/**
	 * Parse the structure found at the current <code>offset</code> into <code>buffer</code>.
	 * Most methods, constructor, and stepinto, will call this method automatically. If 
	 * <code>offset</code> is modified outside of those methods, this method will need to
	 * be invoked.
	 */
	public void processStructure() {
		// Don't process if we are at the end
		if (offset == -1)
			return;
		endOffset = offset;
		// section 8.1.2.2
		classOfTag = (buffer[offset] & 0xff) >> 6;
		// section 8.1.2.5
		constructed = (buffer[offset] & 0x20) != 0;
		// section 8.1.2.3
		byte tagNumber = (byte) (buffer[offset] & 0x1f);
		if (tagNumber < 31) {
			tag = tagNumber;
			endOffset = offset + 1;
		} else {
			throw new IllegalArgumentException("Can't handle tags > 32"); //$NON-NLS-1$
		}
		if ((buffer[endOffset] & 0x80) == 0) {
			// section 8.1.3.4 (doing the short form of the length)
			contentLength = buffer[endOffset];
			endOffset++;
		} else {
			// section 8.1.3.5 (doing the long form of the length)
			int octetCount = buffer[endOffset] & 0x7f;
			if (octetCount > 3)
				throw new ArrayIndexOutOfBoundsException("ContentLength octet count too large: " + octetCount); //$NON-NLS-1$
			contentLength = 0;
			endOffset++;
			for (int i = 0; i < octetCount; i++) {
				contentLength <<= 8;
				contentLength |= buffer[endOffset] & 0xff;
				endOffset++;
			}
			// section 8.1.3.6 (doing the indefinite form
			if (octetCount == 0)
				contentLength = -1;
		}
		contentOffset = endOffset;
		if (contentLength != -1)
			endOffset += contentLength;
		if (endOffset > lastOffset)
			throw new ArrayIndexOutOfBoundsException(endOffset + " > " + lastOffset); //$NON-NLS-1$
	}

	/**
	 * Returns a String representation of the current BER structure.
	 * @return a String representation of the current BER structure.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		switch (classOfTag) {
			case UNIVERSAL_TAGCLASS :
				sb.append('U');
				break;
			case APPLICATION_TAGCLASS :
				sb.append('A');
				break;
			case CONTEXTSPECIFIC_TAGCLASS :
				sb.append('C');
				break;
			case PRIVATE_TAGCLASS :
				sb.append('P');
				break;
		}
		sb.append(constructed ? 'C' : 'P');
		sb.append(" tag=" + tag); //$NON-NLS-1$
		if (tag < tagNames.length) {
			sb.append("(" + tagNames[tag] + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		sb.append(" len="); //$NON-NLS-1$
		sb.append(contentLength);
		switch (tag) {
			case INTTAG :
				sb.append(" value=" + getIntValue()); //$NON-NLS-1$
				break;
			case OIDTAG :
				sb.append(" value="); //$NON-NLS-1$
				int oid[] = getObjId();
				for (int i = 0; i < oid.length; i++) {
					if (i > 0)
						sb.append('.');
					sb.append(oid[i]);
				}
		}
		if (tag == 12 || (tag >= 18 && tag <= 22) || (tag >= 25 && tag <= 30)) {
			sb.append(" value="); //$NON-NLS-1$
			sb.append(getString());
		}
		return sb.toString();
	}

	/**
	 * Returns a BERProcessor for the content of the current structure.
	 */
	public BERProcessor stepInto() {
		return new BERProcessor(buffer, contentOffset, contentLength);
	}

	public void stepOver() {
		offset = endOffset;
		if (endOffset >= lastOffset) {
			offset = -1;
			return;
		}
		processStructure();
	}

	public boolean endOfSequence() {
		return offset == -1;
	}

	/**
	 * Gets the content from the current structure as a String.
	 * @return the content from the current structure as a String.
	 */
	public String getString() {
		return new String(buffer, contentOffset, contentLength);
	}

	/**
	 * Gets the content from the current structure as an int.
	 * @return the content from the current structure as an int.
	 */
	public BigInteger getIntValue() {
		byte bytes[] = new byte[contentLength];
		System.arraycopy(buffer, contentOffset, bytes, 0, contentLength);
		return new BigInteger(bytes);
	}

	/**
	 * Gets the content from the current structure as an object id (int[]).
	 * @return the content from the current structure as an object id (int[]).
	 */
	public int[] getObjId() {
		// First count the ids
		int count = 0;
		for (int i = 0; i < contentLength; i++) {
			// section 8.19.2
			if ((buffer[contentOffset + i] & 0x80) == 0)
				count++;
		}
		count++; // section 8.19.3
		int oid[] = new int[count];
		int index = 0;
		int currentValue = 0;
		for (int i = 0; i < contentLength; i++) {
			currentValue <<= 7;
			currentValue |= buffer[contentOffset + i] & 0x7f;
			// section 8.19.2
			if ((buffer[contentOffset + i] & 0x80) == 0) {
				if (index == 0) {
					// section 8.19.4 special processing
					oid[index++] = currentValue / 40;
					oid[index++] = currentValue % 40;
				} else {
					oid[index++] = currentValue;
				}
				currentValue = 0;
			}
		}
		return oid;
	}

	/**
	 * Get a copy of the bytes in the content of the current structure.
	 * @return a copy of the bytes in the content of the current structure.
	 */
	public byte[] getBytes() {
		byte v[] = new byte[contentLength];
		System.arraycopy(buffer, contentOffset, v, 0, contentLength);
		return v;
	}

}
