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

package org.osgi.service.provisioning;
import java.util.Dictionary;
import java.util.zip.ZipInputStream;
import java.io.IOException;

/**
 * Service for managing the initial provisioning information.
 * <p>
 * Initial provisioning of an OSGi device is a multi step process that
 * culminates with the installation and execution of the initial management
 * agent.  At each step of the process, information is collected for the
 * next step.  Multiple bundles may be involved and this service provides
 * a means for these bundles to exchange information.  It also provides a
 * means for the initial Management Bundle to get its initial configuration
 * information.
 * <p>
 * The provisioning information is collected in a <tt>Dictionary</tt> object, called the Provisioning Dictionary.  Any bundle
 * that can access the service can get a reference to this object and
 * read and update provisioning information.
 * The key of the dictionary is a
 * <tt>String</tt> object and the value is a <tt>String</tt> or
 * <tt>byte[]</tt> object.  The single exception is the
 * PROVISIONING_UPDATE_COUNT value which is an Integer.
 * The <tt>provisioning</tt> prefix
 * is reserved for keys defined by OSGi, other key names may be used for
 * implementation dependent provisioning systems.
 * <p>
 * Any changes to the provisioning information will be reflected immediately
 * in all the dictionary objects obtained from the Provisioning Service.
 * <p>
 * Because of the specific application of the Provisioning Service, there should
 * be only one Provisioning Service registered.  This restriction will not be
 * enforced by the Framework.  Gateway operators or manufactures should ensure
 * that a Provisioning Service bundle is not installed on a device that already
 * has a bundle providing the Provisioning Service.
 * <p>
 * The provisioning information has the potential to contain sensitive
 * information.  Also, the ability to modify provisioning information can
 * have drastic consequences.  Thus, only trusted bundles should be allowed to
 * register and get the Provisioning Service.  The <tt>ServicePermission</tt> is
 * used to limit the bundles that can gain access to the
 * Provisioning Service.  There is no check of <tt>Permission</tt> objects to read or
 * modify the provisioning information, so care must be taken not to leak the
 * Provisioning Dictionary received from <tt>getInformation</tt> method.
 * @version $Revision$
 */


public interface ProvisioningService {
    /** The key to the provisioning information that uniquely identifies
     *  the Service Platform.  The value must be of type <tt>String</tt>. */
    public final static String PROVISIONING_SPID = "provisioning.spid";
    /** The key to the provisioning information that contains the location
     *  of the provision data provider.  The value must be of type
     *  <tt>String</tt>. */
    public final static String PROVISIONING_REFERENCE =
	"provisioning.reference";
    /** The key to the provisioning information that contains the initial
     *  configuration information of the initial Management Agent.  The
     *  value will be of type <tt>byte[]</tt>. */
    public final static String PROVISIONING_AGENT_CONFIG =
	"provisioning.agent.config";
    /** The key to the provisioning information that contains the update count
     *  of the info data.  Each set of changes to the provisioning information
     *  must end with this value being incremented.  The value must be of
     *  type <tt>Integer</tt>.  This key/value pair is also reflected in
     *  the properties of the ProvisioningService in the service registry. */
    public final static String PROVISIONING_UPDATE_COUNT =
	"provisioning.update.count";
    /** The key to the provisioning information that contains the location
     *  of the bundle to start with <tt>AllPermission</tt>.  The bundle must have
     *  be previously installed for this entry to have any effect. */
    public final static String PROVISIONING_START_BUNDLE =
	"provisioning.start.bundle";
    /** The key to the provisioning information that contains the root
     *  X509 certificate used to esatblish trust with operator when using
     *  HTTPS. */
    public final static String PROVISIONING_ROOTX509 =
	"provisioning.rootx509";
    /** The key to the provisioning information that contains the shared
     *  secret used in conjunction with the RSH protocol. */
    public final static String PROVISIONING_RSH_SECRET =
	"provisioning.rsh.secret";
    /** MIME type to be stored in the extra field of a <tt>ZipEntry</tt> object for String
     *  data.  */
    public final static String MIME_STRING =
	"text/plain;charset=utf-8";
    /** MIME type to be stored in the extra field of a <tt>ZipEntry</tt> object for <tt>byte[]</tt>
     *  data.  */
    public final static String MIME_BYTE_ARRAY =
	"application/octet-stream";
    /** MIME type to be stored in the extra field of a <tt>ZipEntry</tt> object for an
     *  installable bundle file.  Zip entries of this type will be installed
     *  in the framework, but not started.  The entry will also not be put
     *  into the information dictionary.  */
    public final static String MIME_BUNDLE =
	"application/x-osgi-bundle";
    /** MIME type to be stored in the extra field of a ZipEntry for a String
     *  that represents a URL for a bundle.  Zip entries of this type will
     *  be used to install (but not start) a bundle from the URL.  The entry
     *  will not be put into the information dictionary.  */
    public final static String MIME_BUNDLE_URL =
	"text/x-osgi-bundle-url";
    /**
     *  Returns a reference to the Provisioning Dictionary.
     *  Any change operations (put and remove) to the dictionary
     *  will cause an <tt>UnsupportedOperationException</tt> to be thrown.
     *  Changes must be done using the <tt>setInformation</tt> and
     *  <tt>addInformation</tt> methods of this service.
     */
    public Dictionary getInformation();

    /**
     *  Replaces the Provisioning Information dictionary with the key/value
     *  pairs contained in <tt>info</tt>.  Any key/value pairs not in
     *  <tt>info</tt> will be removed from the Provisioning Information
     *  dictionary.  This method causes the <tt>PROVISIONING_UPDATE_COUNT</tt>
     *  to be incremented.
     *  @param info the new set of Provisioning Information key/value pairs.
     *  Any keys are values that are of an invalid type will be silently
     *  ignored.
     */
    public void setInformation(Dictionary info);

    /**
     *  Adds the key/value pairs contained in <tt>info</tt> to the
     *  Provisioning Information dictionary.  This method causes the
     *  <tt>PROVISIONING_UPDATE_COUNT</tt> to be incremented.
     *  @param info the set of Provisioning Information key/value pairs to
     *  add to the Provisioning Information dictionary.
     *  Any keys are values that are of an invalid type will be silently
     *  ignored.
     */
    public void addInformation(Dictionary info);

    /**
     *  Processes the <tt>ZipInputStream</tt> and extracts information
     *  to add to the Provisioning Information dictionary, as well as,
     *  install/update and start bundles.
     *  This method causes the <tt>PROVISIONING_UPDATE_COUNT</tt> to
     *  be incremented.
     *  @param zis the <tt>ZipInputStream</tt> that will be used to
     *  add key/value pairs to the Provisioning Information dictionary and
     *  install and start bundles.  If a <tt>ZipEntry</tt> does not have
     *  an <tt>Extra</tt> field that corresponds to one of the four defined MIME
     *  types (<tt>MIME_STRING</tt>, <tt>MIME_BYTE_ARRAY</tt>,
     *  <tt>MIME_BUNDLE</tt>, and <tt>MIME_BUNDLE_URL</tt>) in will be
     *  silently ignored.
     *  @exception IOException if an error occurs while processing the
     *  ZipInputStream.  No additions will be made to the Provisioning
     *  Information dictionary and no bundles must be started or installed.
     */
    public void addInformation(ZipInputStream zis) throws IOException;
}
