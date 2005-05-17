/*
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
 * 
 */

package org.osgi.test.cases.policy.tbc.util;

import java.io.*;

public class Base64Encoder extends OutputStream {

    private static final char[] chars = {
                                        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                                        'I', 'J',
                                        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
                                        'S', 'T',
                                        'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
                                        'c', 'd',
                                        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                                        'm', 'n',
                                        'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
                                        'w', 'x',
                                        'y', 'z', '0', '1', '2', '3', '4', '5',
                                        '6', '7',
                                        '8', '9', '+', '/'
    };

    private int charCount;
    private int carryOver;

    private OutputStream out;

    /**
     * Constructs a new Base64 encoder that writes output to the given
     * OutputStream.
     *
     * @param out the output stream
     */
    public Base64Encoder(OutputStream out) {
        this.out = out;
    }

    /**
     * Writes the given byte to the output stream in an encoded form.
     *
     * @exception IOException if an I/O error occurs
     */
    public void write(int b) throws IOException {
        // Take 24-bits from three octets, translate into four encoded chars
        // Break lines at 76 chars
        // If necessary, pad with 0 bits on the right at the end
        // Use = signs as padding at the end to ensure encodedLength % 4 == 0

        // Remove the sign bit,
        // thanks to Christian Schweingruber <chrigu@lorraine.ch>
        if (b < 0) {
            b += 256;
        }

        // First byte use first six bits, save last two bits
        if (charCount % 3 == 0) {
            int lookup = b >> 2;
            carryOver = b & 3; // last two bits
            out.write(chars[lookup]);
        }
        // Second byte use previous two bits and first four new bits,
        // save last four bits
        else if (charCount % 3 == 1) {
            int lookup = ((carryOver << 4) + (b >> 4)) & 63;
            carryOver = b & 15; // last four bits
            out.write(chars[lookup]);
        }
        // Third byte use previous four bits and first two new bits,
        // then use last six new bits
        else if (charCount % 3 == 2) {
            int lookup = ((carryOver << 2) + (b >> 6)) & 63;
            out.write(chars[lookup]);
            lookup = b & 63; // last six bits
            out.write(chars[lookup]);
            carryOver = 0;
        }
        charCount++;

        // Add newline every 76 output chars (that's 57 input chars)
        if (charCount % 57 == 0) {
            out.write('\n');
        }
    }

    /**
     * Writes the given byte array to the output stream in an
     * encoded form.
     *
     * @param b the data to be written
     * @param off the start offset of the data
     * @param len the length of the data
     * @exception IOException if an I/O error occurs
     */
    public void write(byte[] buf, int off, int len) throws IOException {
        // This could of course be optimized
        for (int i = 0; i < len; i++) {
            write(buf[off + i]);
        }
    }

    /**
     * Closes the stream, this MUST be called to ensure proper padding is
     * written to the end of the output stream.
     *
     * @exception IOException if an I/O error occurs
     */
    public void close() throws IOException {
        // Handle leftover bytes
        if (charCount % 3 == 1) { // one leftover
            int lookup = (carryOver << 4) & 63;
            out.write(chars[lookup]);
            out.write('=');
            out.write('=');
        } else if (charCount % 3 == 2) { // two leftovers
            int lookup = (carryOver << 2) & 63;
            out.write(chars[lookup]);
            out.write('=');
        }
        super.close();
    }

    /**
     * Returns the encoded form of the given unencoded string.
     *
     * @param unencoded the string to encode
     * @return the encoded form of the unencoded string
     */
    public static String encode(byte[] bytes) {
    	ByteArrayOutputStream out =
            new ByteArrayOutputStream((int) (bytes.length * 137 / 100));
    	Base64Encoder encodedOut = new Base64Encoder(out);

    	try {
    		encodedOut.write(bytes);
    		encodedOut.close();

    		return out.toString();
    	} catch (IOException ignored) {
    		return null;
    	}
    }
}
