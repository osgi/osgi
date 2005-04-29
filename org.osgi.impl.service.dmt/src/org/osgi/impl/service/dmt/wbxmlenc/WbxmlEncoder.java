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
import java.util.ArrayList;

import java.io.UnsupportedEncodingException;

import org.w3c.dom.*;

/**
 * Encodes a WBXML.
 *
 * @author      Norbert David
 * @version     0.14
 * @date        2004/04/26
 */
public class WbxmlEncoder implements WbxmlConstants
{

// -------------------------- CLASS VARIABLES ----------------------------------

    /** Wireless Village has public document identifier only. */
    public static Integer DocumentId = PUBLIC_DOCUMENT_ID;

    /** Encoding to be used for strings, Default is UTF-8. */
    public static String TEXT_ENCODING = "UTF-8";

    /** Flag for indicating that character entities are supported ot not. */
    public static boolean supportCharacterEntities = false;

// -------------------------- INSTANCE VARIABLES -------------------------------

    /** Instance of the Sun's Base 64 decoder. */
    private BASE64Decoder base64 = new BASE64Decoder();

    /** Code pages containing the tokens. */
    private WbxmlCodePages codePages;

    /** Array of the WBXML. */
    private ArrayList wbxmlArray = null;

    /** Container of the string table. */
    private ArrayList stringTable;

    /** Index of the start of the string table. */
    private int startOfStringTable;

    /** Flag indicating that the next content must be encoded as string. */
    private boolean isForcedString = false;

    /** Flag indicating that the next content must be encoded as opaque. */
    private boolean isForcedOpaque = false;

// -------------------------- CONSTRUCTORS -------------------------------------

    /**
     * Constructor of the encoder.
     * @param codePages The codepages.
     */
    public WbxmlEncoder (WbxmlCodePages codePages)
    {
        this.codePages = codePages;
    }

// -------------------------- PUBLIC METHODS -----------------------------------

    /**
     * Encodes the specified xml given as DOM.
     * @param xml XML to be encoded.
     * @return Encoded WBXML.
     */
    public ArrayList encode (Node xml)
    {
        codePages.setCodePage(0);
        stringTable = codePages.getStringTableClone();
        wbxmlArray  = new ArrayList(512);

        encodePrelude();
        encodeNode(xml);
        encodeStringTable();

        return wbxmlArray;
    }

// -------------------------- ENCODE METHODS -----------------------------------

    /**
     * Encodes the version of WBXML, public document id and charset.
     */
    private void encodePrelude()
    {
        wbxmlArray.add (WBXML_VERSION); dumpln (WBXML_VERSION);
        encodeMultiByte (DocumentId.intValue() );
        wbxmlArray.add (CHARSET_UTF8);  dumpln (CHARSET_UTF8);
        startOfStringTable = wbxmlArray.size();
    }

    /**
     * Creates and encodes the string table.
     */
    private void encodeStringTable()
    {
        ArrayList table = new ArrayList(32);

        for (int i=0; i<stringTable.size(); i++) {

            byte bytes[] = null;
            try {
                bytes = ((String) stringTable.get(i)).getBytes(TEXT_ENCODING);
            } catch (UnsupportedEncodingException e) {
                bytes = ((String) stringTable.get(i)).getBytes();
            }

            for (int j=0; j<bytes.length; j++) {
                table.add(new Byte(bytes[j]) );
            }
            table.add (STR_TERMINATOR);
        }

        encodeStringTableLength();
        wbxmlArray.addAll (startOfStringTable, table);
    }

    /**
     * Encodes the length of the string table as a multi-byte integer.
     */
    private void encodeStringTableLength ()
    {
        int number = getStringTableSize();
        byte bytes[] = { 0,0,0,0,0 };
        int  index   = 0;

        byte aByte = (byte) (number % 128);
        number /= 128;
        while (number != 0) {
            bytes[index++] = aByte;
            aByte = (byte) (number % 128);
            number /= 128;
        }
        bytes[index] = aByte;

        for (; index>0; index--) {
            dump (new Byte( (byte) (bytes[index] + 128)) );
            wbxmlArray.add  (startOfStringTable++, new Byte( (byte) (bytes[index] + 128)) );
        }

        dumpln (new Byte( (byte) (bytes[index])) );
        wbxmlArray.add (startOfStringTable++, new Byte( (byte) (bytes[index])) );
    }

    /**
     * Encodes the specified Node.
     * @param xml The node to be encoded.
     */
    private void encodeNode (Node xml)
    {
        /* encoding element node */
        if (xml.getNodeType() == Node.ELEMENT_NODE) {
            encode ( (Element) xml);
        } else

        /* encoding text node */
        if (xml.getNodeType() == Node.TEXT_NODE) {
            encode ( (Text) xml);
        }

        /* encoding attributes */
        if (xml.hasAttributes() ) {
            NamedNodeMap attributes = xml.getAttributes();
            int size = attributes.getLength();
            for (int i=0; i<size; i++) {
                encode ( (Attr) attributes.item(i) );
            }

            /* encoding end of attributes */
            if (xml.getNodeType() == Node.ELEMENT_NODE) {
                wbxmlArray.add (END);
                dumpln (END);
                //codePages.setCodePage(0);
            }
        }

        /* encoding child nodes */
        if (xml.hasChildNodes() ) {
            NodeList children = xml.getChildNodes();
            int size = children.getLength();
            for (int i=0; i<size; i++) {
                //codePages.setCodePage(0);
                encodeNode (children.item(i));
            }

            /* encoding end of tag */
            if (xml.getNodeType() == Node.ELEMENT_NODE) {
                wbxmlArray.add (END);
                dumpln (END);
                //codePages.setCodePage(0);
            }
        }
    }

    /**
     * Encodes the specified tag aka element.
     * @param element The tag to be encoded.
     */
    private void encode (Element element)
    {
        /* encoding node name */
        codePages.setState (TAG_STATE);
        String nodeName = element.getNodeName().trim();
        isForcedString = codePages.isForcedString(nodeName);
        isForcedOpaque = codePages.isForcedOpaque(nodeName);

        boolean literal = false;
        Byte token = searchToken(nodeName);
        if (token == null) {
            token = LITERAL;
            literal = true;
        }

        /* Default XML namespace is not encoded */
        String defaultNameSpace = element.getAttribute("xmlns");
        if (defaultNameSpace != null) {
            String cpNameSpace = codePages.getDefaultNameSpace();
            if (defaultNameSpace.equalsIgnoreCase(cpNameSpace) == true) {
                element.removeAttribute ("xmlns");
            }
        }

        if (element.hasAttributes()) {
            token = add (token,HAS_ATTRIBUTE);
        }

        if (element.hasChildNodes()) {
            token = add (token,HAS_CONTENT);
        }

        wbxmlArray.add (token);
        dumpln (token);

        /* encoding node name as literal */
        if (literal == true) {
            int index = getStringTableIndex(nodeName);
            encodeMultiByte(index);
        }

    }

    /**
     * Encodes the specified text node.
     * @param text The text node to be encoded.
     */
    private void encode (Text text)
    {
        /* encoding node value */
        String nodeValue = text.getNodeValue().trim();

        if (nodeValue.equals("") == false) {
            WbxmlCodePages.ValueFragment value =
                codePages.getValueFragment(nodeValue);

            int restLength = value.restName.length();
            int nodeLength = nodeValue.length();
            if (restLength > 0 && nodeLength-restLength < 3) {
                value.token = null;
            }

            if (isForcedString == false && value.token != null) {
                wbxmlArray.add (EXT_T_0);
                dumpln (EXT_T_0);
                wbxmlArray.add (value.token);
                dumpln (value.token);
                encode (value.restName);
            } else {
                encode (nodeValue);
            }

        }
    }

    /**
     * Encodes the specified attribute.
     * @param attribute The attribute to be encoded.
     */
    private void encode (Attr attribute)
    {
        codePages.setState (ATTR_STATE);

        String string = attribute.getName() + "=" + attribute.getValue();
        WbxmlCodePages.TokenFragment token = searchTokenFragment (string);
            //codePages.getTokenFragment (string);

        /* encoding attribute start as literal */
        if (token.token==null || token.token.byteValue()<0) {
            String attrName = attribute.getName() + "=";
            int index = getStringTableIndex(attrName);
            wbxmlArray.add (LITERAL);
            encodeMultiByte(index);
            encode (attribute.getValue() );
        }
        /* encoding attribute start as a well-known token */
        else {
            wbxmlArray.add (token.token);
            dumpln (token.token);
            encode (token.restName);
        }
    }

    /**
     * Encodes the specified string.
     * @param text The string to be encoded.
     */
    private void encode (String text)
    {
        if (text == null) {
            return;
        }

        //text = text.trim();
        if (text.equals("") == true ) {
            return;
        }

        /* encoding as Base64 encoded opaque */
        if (isForcedOpaque == true) {
            encodeOpaque (text);
        }

        /* encoding as integer (opaque) */
        else if (isForcedString == false && isInteger(text) ) {
            encodeOpaque (Integer.parseInt(text) );
        }

        /* encoding as string */
        else {
            if (isForcedString == true) {
                encodeInlineString (text);
            } else {
                WbxmlCodePages.TokenFragment token = codePages.getTokenFragment(text);
                encodeInlineString(token.prevName);

                if (token.token != null) {
                    wbxmlArray.add (token.token);
                    dumpln (token.token);
                    encode(token.restName);
                } else {
                    encodeInlineString(token.restName);
                }
            }
        }
    }

    /**
     * Encodes the specified text as inline string.
     * @param text The text to be encoded.
     */
    private void encodeInlineString (String text)
    {
        if (text == null) {
            return;
        }

        text = text.trim();
        if (text.equals("") ) {
            return;
        }

        if (text.length() == 1 && supportCharacterEntities == true) {
            encodeCharacterEntity (text.charAt(0) );
            return;
        }

        wbxmlArray.add (STR_I);
        dumpln (STR_I);

        byte bytes[] = null;
        try {
            bytes = text.getBytes (TEXT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            bytes = text.getBytes ();
        }

        for (int i=0; i<bytes.length; i++) {
            wbxmlArray.add (new Byte(bytes[i]) );
            dump (new Byte(bytes[i]) );
        }

        wbxmlArray.add (STR_TERMINATOR);
        dumpln (STR_TERMINATOR);
    }

    /**
     * Encodes the specified character as an entity.
     * @param character The character to be encoded.
     */
    private void encodeCharacterEntity (char character)
    {
        wbxmlArray.add (ENTITY);
        dumpln (ENTITY);

        int code = character;
        encodeMultiByte (code);
    }

    /**
     * Encodes the specified Base64 encoded value as opaque data.
     * @param base64 Base64 encoded data.
     */
    private void encodeOpaque (String base64)
    {
        try {
            byte data[] = this.base64.decodeBuffer (base64);

            wbxmlArray.add(OPAQUE); dumpln(OPAQUE);
            encodeMultiByte (data.length);

            for (int i=0; i<data.length; i++) {
                wbxmlArray.add (new Byte (data[i]) );
                dump (new Byte(data[i]) );
            }
        } catch (Exception exception) {
            encodeInlineString (base64);
        }
    }

    /**
     * Encodes the specified number as opaque data.
     * @param number The number to be encoded.
     */
    private void encodeOpaque (int number)
    {
        ArrayList opq = new ArrayList();

        while (number != 0) {
            byte aByte = (byte) (number % 256);
            opq.add (new Byte(aByte) );
            number /= 256;
        }

        int len = opq.size();
        wbxmlArray.add(OPAQUE); dumpln(OPAQUE);
        encodeMultiByte (len);
        for (int i=len-1; i>=0; i--) {
            wbxmlArray.add ((Byte) opq.get(i));
            dump((Byte) opq.get(i));
        }
    }

    /**
     * Encodes the specified integer as a multi-byte integer.
     * @param number The integer to be encoded as a multi-byte integer.
     */
    private void encodeMultiByte (int number)
    {
        byte bytes[] = new byte[5];
        int  index   = 0;

        byte aByte = (byte) (number % 128);
        number /= 128;
        while (number != 0) {
            bytes[index++] = aByte;
            aByte = (byte) (number % 128);
            number /= 128;
        }
        bytes[index] = aByte;

        for (; index>0; index--) {
            dump (new Byte( (byte) (bytes[index] + 128)) );
            wbxmlArray.add  (new Byte( (byte) (bytes[index] + 128)) );
        }

        dumpln (new Byte( (byte) (bytes[index])) );
        wbxmlArray.add (new Byte( (byte) (bytes[index])) );
    }

    /**
     * Searches for the specified token.
     * It can change codepage, if necessary.
     * After searching for the token it resets the 0. codepage.
     * @param tokenName Name of the token to be searched for.
     * @return Token.
     */
    private Byte searchToken (String tokenName)
    {
        Byte token = codePages.getToken(tokenName);

        int number = codePages.getNumberOfCodePages();
        for (int i=0; i<number && token==null; i++) {
            codePages.setCodePage(i);
            token = codePages.getToken(tokenName);

            if (token != null) {
                wbxmlArray.add (SWITCH_PAGE);
                wbxmlArray.add (new Byte((byte) i) );
                dumpln (SWITCH_PAGE);
                dumpln (new Byte((byte) i) );
            }
        }

        return token;
    }

    /**
     * Searches for the specified start token.
     * It can change codepage, if necessary.
     * After searching for the start token it resets the 0. codepage.
     * @param tokenName Name of the token to be searched for.
     * @return Start token.
     */
    private WbxmlCodePages.TokenFragment searchTokenFragment (String tokenName)
    {
        WbxmlCodePages.TokenFragment token = codePages.getTokenFragment(tokenName);

        /* searching for the whole string in all the codepages */
        int currentCodePage = codePages.getCodePage();
        Byte wholeToken = searchToken(tokenName);
        if (wholeToken != null) {
            token.prevName = "";
            token.token = wholeToken;
            token.restName = "";
        }

        /* searching for fragments */
        else {
            codePages.setCodePage(currentCodePage);
            int number = codePages.getNumberOfCodePages();
            for (int i=0; i<number && token.token==null; i++) {
                codePages.setCodePage(i);
                token = codePages.getTokenFragment(tokenName);
                if (i>0 && token.token!=null) {
                    wbxmlArray.add (SWITCH_PAGE);
                    wbxmlArray.add (new Byte((byte) i) );
                    dumpln(SWITCH_PAGE);
                    dumpln(new Byte((byte) i) );
                }
            }
        }

        return token;
    }

// -------------------------- PRIVATE METHODS ----------------------------------

    /**
     * Returns the sum of the specified tags.
     * @param tag1 The first tag.
     * @param tag2 The second tag.
     * @return Sum of the tags.
     */
    private Byte add (Byte tag1, Byte tag2)
    {
        byte sum = (byte) (tag1.byteValue() + tag2.byteValue() );
        return new Byte(sum);
    }

    /**
     * Checks whether the specified text can be encoded as an integer.
     * @param text The text to be checked.
     * @return true or false.
     */
    private boolean isInteger (String text)
    {
        try {
            int number = Integer.parseInt(text);

            if (number < 0) {
                return false;
            } else {
                String string = String.valueOf(number);
                return text.equals(string);
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns the index of the specified name in the string table.
     * It creates a new entry if the string table does not contain
     * the specified name.
     * @param name String to be searched.
     * @return Index of the specified name.
     */
    private int getStringTableIndex (String name)
    {
        int index = 0;

        /* searching in the string table */
        for (int i=0; i<stringTable.size(); i++) {
            String string = (String) stringTable.get(i);
            if (string.equals(name) == true) return index;
            index += string.length()+1;
        }

        /* creating new entry in the string table */
        stringTable.add (name);
        return index;
    }

    /**
     * Returns the size of the string table in octets.
     * @return Size of the string table.
     */
    private int getStringTableSize()
    {
        int size = 0;

        for (int i=0; i<stringTable.size(); i++) {
            String string = (String) stringTable.get(i);
            size += string.length()+1;
        }

        return size;
    }

// -------------------------- TEST METHODS -------------------------------------

    private void dump (Byte aByte)
    {
        //String hexCode = Integer.toHexString(aByte.byteValue());
        //while (hexCode.length() > 2) hexCode = hexCode.substring(1);
        //while (hexCode.length() < 2) hexCode = "0" + hexCode;
        //System.out.print(hexCode + " ");
    }

    private void dumpln (Byte aByte)
    {
        //dump (aByte);
        //System.out.println();
    }

}

/*
 * Change history:
 *      0.00    Norbert David       2002/02/26  Starting of development
 *      0.01    Norbert David       2002/03/18  First working version
 *      0.02    Norbert David       2002/04/22  Logger
 *      0.03    Norbert David       2002/07/03  Quote characters are not encoded, extension tokens
 *      0.04    Norbert David       2002/10/04  Finer isInteger() method
 *      0.05    Norbert David       2002/10/08  Supports tokens forced encoding as string
 *      0.06    Norbert David       2002/10/10  Fix for 'Encoding empty elements' bug
 *      0.07    Norbert David       2002/11/07  Fix for 'Codepage must be 0 when starting' bug
 *      0.08    Norbert David       2002/11/21  Negative numbers cannot be encoded as opaque
 *      0.09    Norbert David       2002/12/04  JDK's logger used
 *      0.10    Norbert David       2003/01/20  ISO-8859-1 is used as default encoding
 *      0.11    Norbert David       2003/02/27  Encoding character entities is optional
 *      0.12    Norbert David       2003/08/06  'Forced as string' bugfix
 *      0.13    Norbert David       2004/02/26  Document ID is a multibyte
 *      0.14    Norbert David       2004/04/26  Support for Base64 encoded string
 *
 * Future improvements:
 *      - Encode processing instructions
 */
