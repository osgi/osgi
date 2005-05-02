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
import java.io.*;
import java.util.ArrayList;

// ----------- INTERNAL IMPORTS --------

/**
 * This class holds the tokens in the different codepages.
 * Tokens are read from a .tokens file.
 * There are the following processing instructions in the .tokens file:
 * - ?CODEPAGE <n>  : switch to the specified codepage.
 * - ?XMLNS <name>  : default namespace for the current codepage.
 * - ?TAG           : set the parser state to TAG_STATE.
 * - ?ATTRIBUTE     : set the parser state to ATTR_STATE.
 * - ?VALUE         : set the parser state to VALUE_STATE (WV extension)
 * - ?TOKEN <t>     : sets the automatic token counter to <t>.
 * - ?STRING_TABLE  : put the following string to the static string table.
 * - <tag_name>     : name of the token to b tokenised.
 * - *<tag_name>    : force that the specified tag must be encoded as string.
 * - !<tag_name>    : XML tag is Base64 encoded and must be encoded as opaque.
 *
 * @author      Norbert David
 * @version     0.06
 * @date        2004/04/26
 */
public class WbxmlCodePages implements WbxmlConstants
{

// -------------------------- CLASS VARIABLES ----------------------------------

    /** String that contains the comment characters. */
    public static final char[] COMMENTS = { '#', ';' };

    /** Processing instruction: default namespace for the current codepage. */
    public static final String PI_CODEPAGE = "?CODEPAGE";

    /** Processing instruction: new codepage has to be created. */
    public static final String PI_XMLNS = "?XMLNS";

    /** Processing instruction: set the token counter to the specified value. */
    public static final String PI_TOKEN = "?TOKEN";

    /** Processing instruction: set the state to TAG_STATE. */
    public static final String PI_TAG = "?TAG";

    /** Processing instruction: set the state to ATTR_STATE. */
    public static final String PI_ATTR = "?ATTRIBUTE";

    /** Processing instruction: set the state to VALUE_STATE. */
    public static final String PI_VALUE = "?VALUE";

    /** Processing instruction: followed by the elements of the string table. */
    public static final String PI_STRING_TABLE = "?STRINGTABLE";

    /** Sign of forcing token must be encoded as string. */
    public static final char FORCE_STRING = '*';

    /** Sign of forcing token must be encoded as string. */
    public static final char FORCE_OPAQUE = '!';

    /** Used by getTokenFragment() method. Indicates whether string must be started with a well-known token or not. */
    public static boolean prevNameAllowed = false;

// -------------------------- INSTANCE VARIABLES -------------------------------

    /** Number of the active codepage in TAG space. */
    private int tagCodePageNum = 0;

    /** Number of the active codepage in TAG space. */
    private int attrCodePageNum = 0;

    /** Container of the tokens belonging to the actual codepage. */
    private ArrayList codePage = null;

    /** Static string table read from the .tokens file. */
    private ArrayList stringTable;

    /** Container of the default namespaces. */
    private ArrayList defaultNameSpaces;

    /** Container of the tag codepages. */
    private ArrayList tagCodePageList;

    /** Container of the attribute codepages. */
    private ArrayList attrCodePageList;

    /** Extension_0: predefined values of the WV. */
    private ArrayList extensionValues;

    /** Contains tokens which are forced to encode as string. */
    private ArrayList forceString;

    /** Contains tokens which are forced to encode as opaque. */
    private ArrayList forceOpaque;

    /** State of the parser. */
    private int state = TAG_STATE;

    /** The strings are elements of the string table or not. */
    private boolean toStringTable = false;

    /** Structure of the start token and the rest of the token name. */
    public class TokenFragment
    {
        public String prevName;
        public Byte   token;
        public String restName;
    };

    /** Structure of the start token and the rest of the token name. */
    public class ValueFragment
    {
        public Byte   token;
        public String restName;
    };

// -------------------------- CONSTRUCTORS -------------------------------------

    /**
     * Constructs the codepages from a .tokens file.
     * @param tokensFileName Name of the files of the tokens.
     * @exception IOException if the .tokens file is not found
     */
    public WbxmlCodePages (InputStream tokensInputStream) throws IOException
    {
        defaultNameSpaces = new ArrayList();
        stringTable = new ArrayList();
        tagCodePageList = new ArrayList();
        attrCodePageList = new ArrayList();
        extensionValues = new ArrayList();
        forceString = new ArrayList();
        forceOpaque = new ArrayList();
        load (tokensInputStream);

        tagCodePageNum = 0;
        attrCodePageNum = 0;
        state = TAG_STATE;
        codePage = (ArrayList) tagCodePageList.get (0);
    }

// -------------------------- PUBLIC METHODS -----------------------------------

    /**
     * Sets an initial, static string table.
     * @param table The static string table.
     */
    public void setStringTable (ArrayList table)
    {
        stringTable = (ArrayList) table.clone();
    }

    /**
     * Returns a clone of the static string table.
     * Static string table is read from the .tokens file.
     * @return An initial instance of the string table.
     */
    public ArrayList getStringTableClone()
    {
        return (ArrayList) stringTable.clone();
    }

    /**
     * Sets the actual codepage.
     * @param number Number of the codepage.
     */
    public void setCodePage (int number)
    {
        if (state == TAG_STATE) {
            tagCodePageNum = number;
            codePage = (ArrayList) tagCodePageList.get(number);
        } else {
            attrCodePageNum = number;
            codePage = (ArrayList) attrCodePageList.get(number);
        }
    }

    /**
     * Returns the number of the actual code page.
     * @return The actual code page.
     */
    public int getCodePage()
    {
        return state == TAG_STATE ? tagCodePageNum : attrCodePageNum;
    }

    /**
     * Returns the number of codepages in the actual state.
     * @return The number of codepages.
     */
    public int getNumberOfCodePages()
    {
        return state == TAG_STATE
            ? tagCodePageList.size()
            : attrCodePageList.size();
    }

    /**
     * Returns the default namespace for the current codepage.
     * @return Default namespace for the current codepage.
     */
    public String getDefaultNameSpace()
    {
        return getDefaultNameSpace (getCodePage() );
    }

    /**
     * Returns the default namespace for the specified codepage.
     * @param codePageNum Code page number.
     * @return Default namespace for the specified codepage.
     */
    public String getDefaultNameSpace (int codePageNum)
    {
        Object object = defaultNameSpaces.get(codePageNum);
        return object == null ? null : object.toString();
    }

    /**
     * Sets the state of the parser.
     * @param newState New state of the parser.
     */
    public void setState (int newState)
    {
        if (state != newState) {
            state = newState;
            setCodePage (getCodePage() );
        }
    }

    /**
     * Returns the state of the parser.
     * @return State of the parser.
     */
    public int getState()
    {
        return state;
    }

    /**
     * Returns the token associated with the specified token name.
     * @param tokenName Token name.
     * @return The token.
     */
    public Byte getToken (String tokenName)
    {
        int token = codePage.indexOf(tokenName);
        return (token == -1) ? null : new Byte ((byte) token);
    }

    /**
     * Returns the token and the rest of the token name.
     * @param tokenName The original token name.
     * @return The token fragment.
     */
    public TokenFragment getTokenFragment (String tokenName)
    {
        TokenFragment tokenFragment = new TokenFragment();
        tokenFragment.prevName = null;
        tokenFragment.token    = null;
        tokenFragment.restName = tokenName;

        int size = codePage.size();
        for (int i=0; i<size; i++) {
            String tokenFragmentName = (String) codePage.get(i);
            if (tokenFragmentName == null) continue;

            int index = tokenName.indexOf(tokenFragmentName);
            boolean success = (prevNameAllowed) ? index!=-1 : index==0;
            if (success) {
                tokenFragment.prevName = tokenName.substring(0, index);
                tokenFragment.token    = new Byte ((byte) i);
                tokenFragment.restName = tokenName.substring(index + tokenFragmentName.length());
                return tokenFragment;
            }
        }

        return tokenFragment;
    }

    /**
     * Returns the token and the rest of the token name.
     * @param tokenName The original token name.
     * @return The value fragment.
     */
    public ValueFragment getValueFragment (String tokenName)
    {
        ValueFragment valueFragment = new ValueFragment();
        valueFragment.token    = null;
        valueFragment.restName = tokenName;

        int size = extensionValues.size();
        for (byte i=0; i<size; i++) {
            String token = (String) extensionValues.get(i);
            if (token!=null && tokenName.startsWith(token) == true) {
                valueFragment.token = new Byte(i);
                valueFragment.restName = tokenName.substring(token.length() );
                break;
            }
        }

        return valueFragment;
    }

    /**
     * Returns the token name associated with the specified token.
     * @param token Token.
     * @return The token name.
     */
    public String getTokenName (byte token)
    {
        int index = token;
        if (index < 0) index += 256;
        return (String) codePage.get(index);
    }

    /**
     * Returns the extension value token name associated with the
     * specified token.
     * @param token Token.
     * @return The value token name.
     */
    public String getValueTokenName (byte token)
    {
        int index = token;
        if (index < 0) index += 256;
        return (String) extensionValues.get(index);
    }

    /**
     * Checks whether the specified token is forced to encode as string.
     * @param token Token to be checked.
     * @return true or false.
     */
    public boolean isForcedString (String token)
    {
        if (token == null) {
            return false;
        } else {
            boolean forced = forceString.contains (token);
            return forced;
        }
    }

    /**
     * Checks whether the specified token is forced to encode as opaque.
     * @param token Token to be checked.
     * @return true or false.
     */
    public boolean isForcedOpaque (String token)
    {
        if (token == null) {
            return false;
        } else {
            boolean forced = forceOpaque.contains (token);
            return forced;
        }
    }

// -------------------------- PRIVATE METHODS ----------------------------------

    /**
     * Loads the tokens from the specified file.
     * Firstly the file is searched in the working directory,
     * if it is not found, than it is searched in the package of this class.
     * @param filename Name of the .tokens file.
     * @throws IOException in case of IO related errors.
     */
    private void load (InputStream fis) throws IOException
    {
        InputStreamReader isr = new InputStreamReader (fis);
        BufferedReader    br  = new BufferedReader    (isr);
        String line = br.readLine();

        int token = LOCAL_TOKEN;
        while (line != null) {
            line = line.trim();

            if (line.startsWith(PI_CODEPAGE) ) {
                int number = Integer.parseInt (line.substring(PI_CODEPAGE.length()).trim());
                set (tagCodePageList, number, new ArrayList() );
                set (attrCodePageList, number, new ArrayList() );
                set (defaultNameSpaces, number, "");
                state = TAG_STATE;
                setCodePage (number);
                token = LOCAL_TOKEN;
                toStringTable = false;
            } else
            if (line.startsWith(PI_XMLNS) ) {
                String xmlns = line.substring(PI_XMLNS.length()).trim();
                set (defaultNameSpaces, getCodePage(), xmlns);
            } else
            if (line.startsWith(PI_TOKEN) ) {
                token = Integer.parseInt (line.substring(PI_TOKEN.length()).trim());
                toStringTable = false;;
            } else
            if (line.startsWith(PI_TAG) ) {
                int number = getCodePage();
                state = TAG_STATE;
                codePage = (ArrayList) tagCodePageList.get(number);
                token = LOCAL_TOKEN;
                toStringTable = false;
            } else
            if (line.startsWith(PI_ATTR) ) {
                int number = getCodePage();
                state = ATTR_STATE;
                codePage = (ArrayList) attrCodePageList.get(number);
                token = LOCAL_TOKEN;
                toStringTable = false;
            } else
            if (line.startsWith(PI_VALUE) ) {
                state = VALUE_STATE;
                codePage = extensionValues;
                token = 0;
                toStringTable = false;
            } else
            if (line.startsWith(PI_STRING_TABLE) ) {
                toStringTable = true;
            } else
            if (isComment(line) == false) {

                /* to string table */
                if (toStringTable == true) {
                    stringTable.add (line);
                }

                /* to a token codepage */
                else {
                    if (line.charAt(0) == FORCE_STRING) {
                        line = line.substring(1);
                        forceString.add (line);
                    }

                    if (line.charAt(0) == FORCE_OPAQUE) {
                        line = line.substring(1);
                        forceOpaque.add (line);
                    }

                    set (codePage, token, line);
                    token++;
                }
            }

            line = br.readLine();
        }

    }

    /**
     * Replaces the element at the specified position.
     * It can expand the list, if necessary.
     * @param list   The list where the object has to be replaced.
     * @param index  Index of the object.
     * @param object The object to be replaced.
     * @return The original element that was replaced.
     */
    private Object set (ArrayList list, int index, Object object)
    {
        int size = list.size();
        while (size < index+1) {
            list.add (null);
            size++;
        }

        return list.set (index, object);
    }

    /**
     * Returns true, if the specified line is a comment.
     * @param line The line.
     * @return true or false.
     */
    private boolean isComment (String line)
    {
        boolean comment = false;

        if (line != null) {
            if (line.equals("") ) return true;
            int len = COMMENTS.length;
            for (int i=0; i<len && comment==false; i++) {
                if (line.charAt(0) == COMMENTS[i]) {
                    comment = true;
                }
            }
        }

        return comment;
    }

}

/*
 * Change history:
 *      0.00    Norbert David       2002/02/27  Starting of development
 *      0.01    Norbert David       2002/04/02  First working version
 *      0.02    Norbert David       2002/07/03  Extension tokens
 *      0.03    Norbert David       2002/10/08  Supports tokens forced encoding as string
 *      0.04    Norbert David       2003/02/24  load() ordering modified.
 *      0.05    Norbert David       2004/02/27  XMLNS support
 *      0.06    Norbert David       2004/04/26  Support for Base64 encoded string
 */
