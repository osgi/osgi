/*
 * $Header$
 *
 * Copyright (c) The OSGi Alliance (2001, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.service.permissionadmin;

/**
 * Permission representation used by the Permission Admin service.
 *
 * <p>This class encapsulates three pieces of information: a Permission
 * <i>type</i> (class name), which must be a subclass of
 * <tt>java.security.Permission</tt>, and the <i>name</i> and <i>actions</i>
 * arguments passed to its constructor.
 *
 * <p>In order for a permission represented by a <tt>PermissionInfo</tt> to be
 * instantiated and considered during a permission check, its Permission class
 * must be available from the system classpath or an exported package.
 * This means that the instantiation of a permission represented by a
 * <tt>PermissionInfo</tt> may be delayed until the package containing
 * its Permission class has been exported by a bundle.
 *
 * @version $Revision$
 */

public class PermissionInfo
{
    private String type;
    private String name;
    private String actions;

    /**
     * Constructs a <tt>PermissionInfo</tt> from the given type, name, and actions.
     *
     * @param type The fully qualified class name of the permission
     * represented by this <tt>PermissionInfo</tt>. The class must be a subclass of
     * <tt>java.security.Permission</tt> and
     * must define a 2-argument constructor that takes a <i>name</i> string
     * and an <i>actions</i> string.
     *
     * @param name The permission name that will be passed as the first
     * argument to the constructor of the <tt>Permission</tt> class identified by
     * <tt>type</tt>.
     *
     * @param actions The permission actions
     * that will be passed as the second
     * argument to the constructor of the <tt>Permission</tt> class identified by
     * <tt>type</tt>.
     *
     * @exception java.lang.NullPointerException if <tt>type</tt> is
     * <tt>null</tt>.
     * @exception java.lang.IllegalArgumentException if <tt>action</tt> is not
     * <tt>null</tt> and <tt>name</tt> is <tt>null</tt>.
     */
    public PermissionInfo(String type, String name, String actions)
    {
        this.type = type;
        this.name = name;
        this.actions = actions;

        if (type == null)
        {
            throw new NullPointerException("type is null");
        }
        if ((name == null) && (actions != null))
        {
            throw new IllegalArgumentException("name missing");
        }
    }

    /**
     * Constructs a <tt>PermissionInfo</tt> object from the given encoded <tt>PermissionInfo</tt> string.
     *
     * @param encodedPermission The encoded <tt>PermissionInfo</tt>.
     * @see #getEncoded
     * @exception java.lang.IllegalArgumentException if <tt>encodedPermission</tt> is
     * not properly formatted.
     */
    public PermissionInfo(String encodedPermission)
    {
        if (encodedPermission == null)
        {
            throw new NullPointerException("missing encoded permission");
        }
        if (encodedPermission.length() == 0)
        {
            throw new IllegalArgumentException("empty encoded permission");
        }

        try
        {
            char[] encoded = encodedPermission.toCharArray();

            /* the first character must be '(' */
            if (encoded[0] != '(')
            {
                throw new IllegalArgumentException("first character not open parenthesis");
            }

            /* type is not quoted or encoded */
            int end = 1;
            int begin = end;

            while ((encoded[end] != ' ') && (encoded[end] != ')'))
            {
                end++;
            }

            if (end == begin)
            {
                throw new IllegalArgumentException("expecting type");
            }

            this.type = new String(encoded, begin, end-begin);

            /* type may be followed by name which is quoted and encoded */
            if (encoded[end] == ' ')
            {
                end++;

                if (encoded[end] != '"')
                {
                    throw new IllegalArgumentException("expecting quoted name");
                }

                end++;
                begin = end;

                while (encoded[end] != '"')
                {
                    if (encoded[end] == '\\')
                    {
                        end++;
                    }

                    end++;
                }

                this.name = decodeString(encoded, begin, end);
                end++;

                /* name may be followed by actions which is quoted and encoded */
                if (encoded[end] == ' ')
                {
                    end++;

                    if (encoded[end] != '"')
                    {
                        throw new IllegalArgumentException("expecting quoted actions");
                    }

                    end++;
                    begin = end;

                    while (encoded[end] != '"')
                    {
                        if (encoded[end] == '\\')
                        {
                            end++;
                        }

                        end++;
                    }

                    this.actions = decodeString(encoded, begin, end);
                    end++;
                }
            }

            /* the final character must be ')' */
            if ((encoded[end] != ')') || (end+1 != encoded.length))
            {
                throw new IllegalArgumentException("last character not " +
                                                   "close parenthesis");
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException("parsing terminated abruptly");
        }
    }

    /**
     * Returns the string encoding of this <tt>PermissionInfo</tt> in a
     * form suitable for restoring this <tt>PermissionInfo</tt>.
     *
     * <p>The encoding format is:
     * <pre>
     * (type)
     * </pre>
     * or
     * <pre>
     * (type "<i>name</i>")
     * </pre>
     * or
     * <pre>
     * (type "<i>name</i>" "<i>actions</i>")
     * </pre>
     * where <i>name</i> and <i>actions</i> are strings that are encoded
     * for proper parsing.  Specifically,
     * the <tt>"</tt>, <tt>\</tt>, carriage return, and linefeed characters are
     * escaped using <tt>\"</tt>, <tt>\\</tt>, <tt>\r</tt>, and <tt>\n</tt>,
     * respectively.
     *
     * <p>The encoded string must contain no leading or trailing whitespace
     * characters. A single space character must be used between type and
     * "<i>name</i>" and between "<i>name</i>" and "<i>actions</i>".
     *
     * @return The string encoding of this <tt>PermissionInfo</tt>.
     */
    public final String getEncoded()
    {
        StringBuffer output =
        new StringBuffer(8 + type.length() +
                         ((((name == null) ? 0 : name.length()) +
                           ((actions == null) ? 0 : actions.length()))<<1));
        output.append('(');
        output.append(type);

        if (name != null)
        {
            output.append(" \"");
            encodeString(name, output);
            if (actions != null)
            {
                output.append("\" \"");
                encodeString(actions, output);
            }
            output.append('\"');
        }

        output.append(')');

        return(output.toString());
    }

    /**
     * Returns the string representation of this <tt>PermissionInfo</tt>.
     * The string is created by calling the <tt>getEncoded</tt> method on
     * this <tt>PermissionInfo</tt>.
     *
     * @return The string representation of this <tt>PermissionInfo</tt>.
     */
    public String toString()
    {
        return(getEncoded());
    }

    /**
     * Returns the fully qualified class name of the permission
     * represented by this
     * <tt>PermissionInfo</tt>.
     *
     * @return The fully qualified class name of the permission
     * represented by this <tt>PermissionInfo</tt>.
     */
    public final String getType()
    {
        return(type);
    }

    /**
     * Returns the name of the permission represented by this
     * <tt>PermissionInfo</tt>.
     *
     * @return The name of the permission represented by this
     * <tt>PermissionInfo</tt>, or <tt>null</tt> if the permission does not
     * have a name.
     */
    public final String getName()
    {
        return(name);
    }

    /**
     * Returns the actions of the permission represented by this
     * <tt>PermissionInfo</tt>.
     *
     * @return The actions of the permission represented by this
     * <tt>PermissionInfo</tt>, or <tt>null</tt> if the permission does
     * not have any actions associated with it.
     */
    public final String getActions()
    {
        return(actions);
    }

    /**
     * Determines the equality of two <tt>PermissionInfo</tt> objects.
     *
     * This method checks that specified object has the same type, name and
     * actions as this <tt>PermissionInfo</tt> object.
     *
     * @param obj The object to test for equality with this
     * <tt>PermissionInfo</tt> object.
     * @return <tt>true</tt> if <tt>obj</tt> is a
     * <tt>PermissionInfo</tt>, and has the same type, name and actions as
     * this <tt>PermissionInfo</tt> object; <tt>false</tt> otherwise.
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return(true);
        }

        if (!(obj instanceof PermissionInfo))
        {
            return(false);
        }

        PermissionInfo other = (PermissionInfo) obj;

        if (!type.equals(other.type) ||
            ((name == null) ^ (other.name == null)) ||
            ((actions == null) ^ (other.actions == null)))
        {
            return(false);
        }

        if (name != null)
        {
            if (actions != null)
            {
                return(name.equals(other.name) &&
                       actions.equals(other.actions));
            }
            else
            {
                return(name.equals(other.name));
            }
        }
        else
        {
            return(true);
        }
    }

    /**
     * Returns the hash code value for this object.
     *
     * @return A hash code value for this object.
     */

    public int hashCode()
    {
        int hash = type.hashCode();

        if (name != null)
        {
            hash ^= name.hashCode();
            if (actions != null)
            {
                hash ^= actions.hashCode();
            }
        }

        return(hash);
    }

    /**
     * This escapes the quotes, backslashes, \n, and \r in the string using a
     * backslash and appends the newly escaped string to a
     * StringBuffer.
     */
    private static void encodeString(String str, StringBuffer output)
    {
            int len = str.length();

            for (int i = 0; i < len; i++)
            {
                char c = str.charAt(i);

            switch (c)
            {
                case '"':
                case '\\':
                    output.append('\\');
                    output.append(c);
                    break;
                case '\r':
                    output.append("\\r");
                    break;
                case '\n':
                    output.append("\\n");
                    break;
                default:
                    output.append(c);
                    break;
            }
        }
    }

    /**
     * Takes an encoded character array and decodes it into a new String.
     */
    private static String decodeString(char[] str, int begin, int end)
    {
        StringBuffer output = new StringBuffer(end - begin);

        for (int i = begin; i < end; i++)
        {
            char c = str[i];

            if (c == '\\')
            {
                i++;

                if (i < end)
                {
                    c = str[i];

                    if (c == 'n')
                    {
                        c = '\n';
                    }
                    else if (c == 'r')
                    {
                        c = '\r';
                    }
                }
            }

            output.append(c);
        }

        return(output.toString());
    }
}
