/*
 * $Header$
 *
 * Copyright (c) The Open Services Gateway Initiative (2002).
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

package org.osgi.service.upnp;

import java.io.InputStream;
import java.io.IOException;

/**
 * A UPnP icon representation.
 *
 * Each UPnP device can contain zero or more icons.
 **/

public interface UPnPIcon {

  /**
   * Returns the MIME type of the icon.
   *
   * This method returns the format in which the icon graphics,
   * read from the <tt>InputStream</tt> object obtained by the <tt>getInputStream()</tt>
   * method, is encoded.
   * <p>
   * The format of the returned string is in accordance to RFC2046.
   * A list of valid MIME types is maintained by the IANA at
   * <a href="ftp://ftp.isi.edu/in-notes/iana/assignments/media-types/media-types">ftp://ftp.isi.edu/in-notes/iana/assignments/media-types/media-types</a>.
   * <p>
   * Typical values returned include: "image/jpeg" or
   * "image/gif"
   *
   * @return The MIME type of the encoded icon.
   **/
  String getMimeType();

  /**
   * Returns the width of the icon in pixels.
   *
   * If the actual width of the icon is unknown, -1 is returned.
   *
   * @return The width in pixels, or -1 if unknown.
   **/

  int getWidth();

  /**
   * Returns the height of the icon in pixels.
   *
   * If the actual height of the icon is unknown, -1 is returned.
   *
   * @return The height in pixels, or -1 if unknown.
   **/

  int getHeight();

  /**
   * Returns the size of the icon in bytes.
   *
   * This method returns the number of bytes of the icon available to
   * read from the <tt>InputStream</tt> object obtained by the <tt>getInputStream()</tt> method.
   * If the actual size can not be determined, -1 is returned.
   *
   * @return The icon size in bytes, or -1 if the size is unknown.
   **/
  int getSize();

  /**
   * Returns the color depth of the icon in bits.
   *
   * @return The color depth in bits. If the actual color depth of the icon is unknown, -1 is returned.
   **/


  int getDepth();

  /**
   * Returns an <tt>InputStream</tt> object for the icon data.
   *
   * The <tt>InputStream</tt> object provides a way for a client to read the actual
   * icon graphics data. The number of bytes available from this
   * <tt>InputStream</tt> object can be determined via the <tt>getSize()</tt> method.
   * The format of the data encoded can be determined by the MIME type
   * availble via the <tt>getMimeType()</tt> method.
   *
   * @return An InputStream to read the icon graphics data from.
   * @see UPnPIcon#getMimeType()
   **/
  InputStream getInputStream() throws IOException;
}
