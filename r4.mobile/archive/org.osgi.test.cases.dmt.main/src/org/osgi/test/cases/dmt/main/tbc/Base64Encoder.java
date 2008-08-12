/* Project: DELTA
 *
 * Class Name: MotoPhotoFile.java
 * Created on: January 19, 2004
 *
 * Copyright � 2004 by Motorola Inc.
 * Motorola Confidential Proprietary
 *      This document and the information contained in it is
 *      CONFIDENTIAL INFORMATION of Motorola , and shall not
 *      be used, or published, or disclosed, or disseminated
 *      outside of Motorola  in  whole  or  in  part without
 *      Motorola�s  consent. This  document  contains  trade
 *      secrets of Motorola.   Reverse engineering of any or
 *      all of the information in this document is prohibited.
 *      The copyright notice  does not  imply publication of
 *      this document.
 *
 * REVISION HISTORY:
 * Date         Author         Doc Tracebility
 * CR           CR Headline
 * ===========  ==============================================================
 * 13/JAN/2004  Angelo Ribeiro/Gustavo de Paula
 * 1            Initial creation
 * ===========  ==============================================================
 * 02/FEB/2004  Angelo Ribeiro/Gustavo de Paula
 * 1            Inspection rework (DELTA_MPCLIENT_CODE-INSPR-002)
 * ===========  ==============================================================
 * 11/FEB/2004  Angelo Ribeiro/Gustavo de Paula
 * CR: 06       Rework after inspection (DELTA_MPCLIENT_CODE-INSPR-003).
 * ===========  ==============================================================
 * 20/FEB/2004  Angelo Ribeiro/Gustavo de Paula
 * CR: 06       Rework after moderator followup onDELTA_MPCLIENT_CODE-INSPR-003)
 * ===========  ==============================================================
 * 09/SET/2004  Leonardo Medeiros/Rog�rio Pontual
 * CR: 248      Upload Image greater then 50k
 * ===========  ==============================================================
 */


package org.osgi.test.cases.dmt.main.tbc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A class to encode Base64 streams and strings.
 * See RFC 1521 section 5.2 for details of the Base64 algorithm.
 * <p>
 * This class can be used for encoding strings:
 * <blockquote><pre>
 * String unencoded = "webmaster:try2gueSS";
 * String encoded = Base64Encoder.encode(unencoded);
 * </pre></blockquote>
 * or for encoding streams:
 * <blockquote><pre>
 * OutputStream out = new Base64Encoder(System.out);
 * </pre></blockquote>
 */

/**
 * This class was changed to ru in J2ME environment. The original implementation
 * targets J2SE and was using some classes that are not available in MIDP. Most
 * of the orignal code was not changed and that why it is out of the project
 * coding standard
 * Original Copyright:
 * Copyright (C) 1999-2002 by Jason Hunter <jhunter_AT_acm_DOT_org>.
 * All rights reserved.  Use of this class is limited.
 */
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
    public static byte[] encode(byte[] bytes) {
        ByteArrayOutputStream out =
                new ByteArrayOutputStream((int) (bytes.length * 137 / 100));
        Base64Encoder encodedOut = new Base64Encoder(out);

        try {
            encodedOut.write(bytes);
            encodedOut.close();

            return out.toByteArray();
        } catch (IOException ignored) {
            return null;
        }
    }
}
