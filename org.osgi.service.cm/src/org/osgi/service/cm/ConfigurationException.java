/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.service.cm;


/**
 * An <tt>Exception</tt> class to inform the Configuration Admin service of problems with configuration
 * data.
 *
 * @version $Revision$
*/
public class ConfigurationException extends Exception {
    String          property;
    String          reason;

    /**
     * Create a <tt>ConfigurationException</tt> object.
     *
     * @param property name of the property that caused the problem, <tt>null</tt> if no specific property was the cause
     * @param reason reason for failure
    */
    public ConfigurationException( String property, String reason ) {
        super( property + " : " + reason );
        this.property = property;
        this.reason = reason;
    }

    /**
     * Return the property name that caused the failure or null.
     *
     * @return name of property or null if no specific property caused the problem
    */
    public String getProperty() { return property; }

    /**
     * Return the reason for this exception.
     *
      * @return reason of the failure
    */
    public String getReason() { return reason; }

}


