/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.dmtree;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains static utility methods to manipulate DMT URIs.
 * <p>
 * Syntax of valid DMT URIs:
 * <ul>
 * <li>A slash (<code>'/'</code> &#92;u002F) is the separator of the node names.
 * Slashes used in node name must therefore be escaped using a backslash slash 
 * (<code>'\/'</code>). The backslash must be escaped with a double backslash 
 * sequence. A backslash found must be ignored when it is not followed by a 
 * slash or backslash.
 * <li>The node name can be constructed using full Unicode character set
 * (except the Supplementary code, not being supported by CLDC/CDC). However,
 * Using the full Unicode character set for node names is discouraged because
 * the encoding in the underlying storage as well as the encoding needed in
 * communications can create significant performance and memory usage overhead.
 * Names that are restricted to the URI set <code>[-a-zA-Z0-9_.!~*'()]</code>
 * are most efficient.
 * <li>URIs used in the DMT must be treated and interpreted as case sensitive.
 * <li>No End Slash: URI must not end with the delimiter slash (<code>'/'</code>
 * &#92;u002F). This implies that the root node must be denoted as 
 * <code>"."</code> and not <code>"./"</code>.
 * <li>No parent denotation: URI must not be constructed using the character
 * sequence <code>../</code> to traverse the tree upwards.
 * <li>Single Root: The character sequence <code>./</code> must not be used
 * anywhere else but in the beginning of a URI.
 * </ul>
 */
public final class Uri {
    /**
     * A private constructor to suppress the default public constructor.
     */
    private Uri() {}
    
    /**
     * Returns a node name that is valid for the tree operation methods, based
     * on the given node name. This transformation is not idempotent, so it must
     * not be called with a parameter that is the result of a previous
     * <code>mangle</code> method call.
     * <p>
     * Node name mangling is needed in the following cases:
     * <ul>
     * <li>if the name contains '/' or '\' characters
     * <li>if the length of the name exceeds the limit defined by the
     * implementation
     * </ul>
     * <p>
     * A node name that does not suffer from either of these problems is
     * guaranteed to remain unchanged by this method. Therefore the client may
     * skip the mangling if the node name is known to be valid (though it is
     * always safe to call this method).
     * <p>
     * The method returns the normalized <code>nodeName</code> as described
     * below. Invalid node names are normalized in different ways, depending on
     * the cause. If the length of the name does not exceed the limit, but the
     * name contains '/' or '\' characters, then these are simply escaped by
     * inserting an additional '\' before each occurrence. If the length of the
     * name does exceed the limit, the following mechanism is used to normalize
     * it:
     * <ul>
     * <li>the SHA 1 digest of the name is calculated
     * <li>the digest is encoded with the base 64 algorithm
     * <li>all '/' characters in the encoded digest are replaced with '_'
     * <li>trailing '=' signs are removed
     * </ul>
     * 
     * @param nodeName the node name to be mangled (if necessary), must not be
     *        <code>null</code> or empty
     * @return the normalized node name that is valid for tree operations
     * @throws NullPointerException if <code>nodeName</code> is
     *         <code>null</code>
     * @throws IllegalArgumentException if <code>nodeName</code> is empty
     */
    public static String mangle(String nodeName) {
        return nodeName;
    }

    /**
     * Construct a URI from the specified URI segments. The segments must
     * already be mangled.
     * <p>
     * If the specified path is an empty array then an empty URI 
     * (<code>""</code>) is returned.
     * 
     * @param path a possibly empty array of URI segments, must not be 
     *        <code>null</code>
     * @return the URI created from the specified segments
     * @throws NullPointerException if the specified path or any of its
     *         segments are <code>null</code>
     * @throws IllegalArgumentException if the specified path contains too many
     *         or malformed segments or the resulting URI is too long
     */
    public static String toUri(String[] path) {
        if (0 == path.length) {
            return "";
        }

        if (path.length > getMaxUriSegments()) {
            throw new IllegalArgumentException(
                    "Path contains too many segments.");
        }

        StringBuffer uri = new StringBuffer();
        int uriLength = 0;
        for (int i = 0; i < path.length; ++i) {
            // getSegmentLength throws exceptions on malformed segments.
            int segmentLength = getSegmentLength(path[i]);
            if (segmentLength > getMaxSegmentNameLength()) {
                throw new IllegalArgumentException("URI segment too long.");
            }
            if (i > 0) {
                uri.append('/');
                uriLength++;
            }
            uriLength += segmentLength;
            uri.append(path[i]);
        }
        if (uriLength > getMaxUriLength()) {
            throw new IllegalArgumentException("URI too long.");
        }
        return uri.toString();
    }

    /**
     * This method returns the length of a URI segment. The length of the URI
     * segment is defined as the number of bytes in the unescaped, UTF-8 encoded
     * represenation of the segment.
     * <p>
     * The method verifies that the URI segment is well-formed.
     * 
     * @param segment the URI segment
     * @return URI segment length
     * @throws NullPointerException if the specified segment is 
     *         <code>null</code>
     * @throws IllegalArgumentException if the specified URI segment is
     *         malformed
     */
    private static int getSegmentLength(String segment) {
        if (segment.length() == 0)
            throw new IllegalArgumentException("URI segmnet is empty.");

        StringBuffer newsegment = new StringBuffer(segment);
        int i = 0;
        while (i < newsegment.length()) { // length can decrease during the loop!
            if (newsegment.charAt(i) == '\\') {
                if (i == newsegment.length() - 1) // last character cannot be a '\'
                    throw new IllegalArgumentException(
                            "URI segment ends with the escape character.");

                newsegment.deleteCharAt(i); // remove the extra '\'
            } else if (newsegment.charAt(i) == '/')
                throw new IllegalArgumentException(
                        "URI segment contains an unescaped '/' character.");

            i++;
        }

        if (newsegment.toString().equals(".."))
            throw new IllegalArgumentException(
                    "URI segment must not be \"..\".");

        try {
            return newsegment.toString().getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            // This should never happen. All implementations must support
            // UTF-8 encoding;
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Split the specified URI along the path separator '/' charaters and return
     * an array of URI segments. The returned array may be empty if the specifed
     * URI was empty.
     * 
     * @param uri the URI to be split, must not be <code>null</code>
     * @return an array of URI segments created by splitting the specified URI
     * @throws NullPointerException if the specified URI is <code>null</code>
     * @throws IllegalArgumentException if the specified URI is malformed
     */
    public static String[] toPath(String uri) {
        if( null == uri ) {
            throw new NullPointerException("'uri' parameter is null.");
        }
        if( !isValidUri(uri) ) {
            throw new IllegalArgumentException("Malformed URI: " + uri);
        }
        if (uri.length() == 0)
            return new String[] {};

        List segments = new ArrayList();
        StringBuffer segment = new StringBuffer();

        boolean escape = false;
        for (int i = 0; i < uri.length(); i++) {
            char ch = uri.charAt(i);

            if (escape) {
                segment.append(ch);
                escape = false;
            } else if (ch == '/') {
                segments.add(segment.toString());
                segment = new StringBuffer();
            } else if (ch == '\\') {
                escape = true;
            } else
                segment.append(ch);
        }
        if (segment.length() > 0) {
            segments.add(segment.toString());
        }

        return (String[]) segments.toArray(new String[segments.size()]);
    }

    /**
     * Returns the maximum allowed number of URI segments. The returned value is
     * implementation specific.
     * 
     * @return maximum number of URI segments supported by the implementation
     */
    public static int getMaxUriSegments() {
        return 16;
    }

    /**
     * Returns the maximum allowed length of a URI. The value is implementation
     * specific. The length of the URI is defined as the number of bytes in the
     * unescaped, UTF-8 encoded represenation of the URI.
     * 
     * @return maximum URI length supported by the implementation
     */
    public static int getMaxUriLength() {
        return 256;
    }

    /**
     * This method returns the maximum allowed length of a URI segment. The
     * value is implementation specific. The length of the URI segment is
     * defined as the number of bytes in the unescaped, UTF-8 encoded
     * represenation of the segment.
     * 
     * @return maximum URI segment length supported by the implementation
     */
    public static int getMaxSegmentNameLength() {
        return 32;
    }

    /**
     * Checks whether the specified URI is an absolute URI. An absolute URI
     * contains the complete path to a node in the DMT starting from the DMT
     * root (".").
     * 
     * @param uri the URI to be checked, must not be <code>null</code> and must 
     *        contain a valid URI
     * @return whether the specified URI is absolute
     * @throws NullPointerException if the specified URI is <code>null</code>
     * @throws IllegalArgumentException if the specified URI is malformed
     */
    public static boolean isAbsoluteUri(String uri) {
        if( null == uri ) {
            throw new NullPointerException("'uri' parameter is null.");
        }
        if( !isValidUri(uri) ) 
            throw new IllegalArgumentException("Malformed URI: " + uri);
        return uri.equals(".") || uri.equals("\\.") || uri.startsWith("./") 
                 || uri.startsWith("\\./");
    }

    /**
     * Checks whether the specified URI is valid. A URI is considered valid if
     * it meets the following constraints:
     * <ul>
     * <li>the URI is not <code>null</code>;
     * <li>the URI follows the syntax defined for valid DMT URIs;
     * <li>the length of the URI is not more than {@link #getMaxUriLength()};
     * <li>the URI doesn't contain more than {@link #getMaxUriSegments()}
     * segments;
     * <li>the length of each segment of the URI is less than or equal to
     * {@link #getMaxSegmentNameLength()}.
     * </ul>
     * 
     * @param uri the URI to be validated
     * @return whether the specified URI is valid
     */
    public static boolean isValidUri(String uri) {
        // TODO This implementation is pretty dummy :)
        if (null == uri)
            return false;
        if( uri.length() == 0 ) 
            return true;
        if( uri.charAt(0) == '/' ) 
            return false;
        return true;
    }

}
