/* 
 * Copyright (c) The Open Services Gateway Initiative (2000, 2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative (OSGI)
 * Specification may be subject to third party intellectual property rights,
 * including without limitation, patent rights (such a third party may or may
 * not be a member of OSGi). OSGi is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL NOT
 * INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY LOSS OF
 * PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF BUSINESS,
 * OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL, PUNITIVE OR
 * CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS DOCUMENT OR THE
 * INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.impl.service.dmt.wbxmlenc;

// ----------- JDK IMPORTS -------------

// ----------- INTERNAL IMPORTS --------

/**
 * This interface contains constants for the WBXML converter.
 *
 * @author      Norbert David
 * @version     0.03
 * @date        2004/02/26
 */
public interface WbxmlConstants
{

// -------------------------- VARIABLES ----------------------------------------

    /** First token to be allowed to use for specific purpose. */
    public static final int LOCAL_TOKEN = 5;

    /** Parser state is TAG_STATE. */
    public static final int TAG_STATE = 0;

    /** Parser state is ATTR_STATE. */
    public static final int ATTR_STATE = 1;

    /** Parser state is VALUE_STATE. */
    public static final int VALUE_STATE = 2;

    /** WBXML version number: 1.3 */
    public Byte WBXML_VERSION = new Byte ((byte) 0x03);
    public byte wbxml_version = WBXML_VERSION.byteValue();

    /** he public document identifier. */
    public Integer PUBLIC_DOCUMENT_ID = new Integer ((int) 0x01);
    public int public_document_id = PUBLIC_DOCUMENT_ID.intValue();

    /** UTF-8 character set. */
    public Byte CHARSET_UTF8 = new Byte ((byte) 0x6A);
    public byte charset_utf8 = CHARSET_UTF8.byteValue();

    /** Bit 7 indicates whether attributes follow the tag code. */
    public Byte HAS_ATTRIBUTE = new Byte ((byte) 0x80);
    public byte has_attribute = HAS_ATTRIBUTE.byteValue();

    /** Bit 6 indicates whether tag code has content. */
    public Byte HAS_CONTENT = new Byte ((byte) 0x40);
    public byte has_content = HAS_CONTENT.byteValue();

    /** String terminator. */
    public Byte STR_TERMINATOR = new Byte ((byte) 0x00);
    public byte str_terminator = STR_TERMINATOR.byteValue();

    /** Change the code page token. */
    public Byte SWITCH_PAGE = new Byte ((byte) 0x00);
    public byte switch_page = SWITCH_PAGE.byteValue();

    /** End of attribute list or element. */
    public Byte END = new Byte ((byte) 0x01);
    public byte end = END.byteValue();

    /** Character entity. */
    public Byte ENTITY = new Byte ((byte) 0x02);
    public byte entity = ENTITY.byteValue();

    /** Inline string. */
    public Byte STR_I = new Byte ((byte) 0x03);
    public byte str_i = STR_I.byteValue();

    /** Unknown attribute name or unknown tag name (with no attribute and content). */
    public Byte LITERAL = new Byte ((byte) 0x04);
    public byte literal = LITERAL.byteValue();

    /** Processing instruction. */
    public Byte PI = new Byte ((byte) 0x43);
    public byte pi = PI.byteValue();

    /** Unknown tag name with content. */
    public Byte LITERAL_C = new Byte ((byte) 0x44);
    public byte literal_c = LITERAL_C.byteValue();

    /** String table reference. */
    public Byte STR_T = new Byte ((byte) 0x83);
    public byte str_t = STR_T.byteValue();

    /** Unknown tag name with attribute. */
    public Byte LITERAL_A = new Byte ((byte) 0x84);
    public byte literal_a = LITERAL_A.byteValue();

    /** Opaque document-type-specific data. */
    public Byte OPAQUE = new Byte ((byte) 0xC3);
    public byte opaque = OPAQUE.byteValue();

    /** Unknown tag name with attribute and content. */
    public Byte LITERAL_AC = new Byte ((byte) 0xC4);
    public byte literal_ac = LITERAL_AC.byteValue();

    /** Extension token. */
    public Byte EXT_T_0 = new Byte ((byte) 0x80);
    public byte ext_t_0 = EXT_T_0.byteValue();

    /** Extension token. */
    public Byte EXT_T_1 = new Byte ((byte) 0x81);
    public byte ext_t_1 = EXT_T_1.byteValue();

    /** Extension token. */
    public Byte EXT_T_2 = new Byte ((byte) 0x82);
    public byte ext_t_2 = EXT_T_2.byteValue();

// -------------------------- ACCESSORS & MUTATORS -----------------------------

// -------------------------- METHODS ------------------------------------------

}

/*
 * Change history:
 *      0.00    Norbert David       2002/02/26  Starting of development
 *      0.01    Norbert David       2002/03/01  First working version
 *      0.02    Norbert David       2002/07/03  Extension tokens
 *      0.02    Norbert David       2004/02/26  Document ID is an integer
 */
