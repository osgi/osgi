/*
* Copyright (c) 2023 Contributors to the Eclipse Foundation.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*   SmartCity Jena - initial
*   Stefan Bischof (bipolis.org) - initial
*/
package org.osgi.service.jakartaws.whiteboard;

public class SoapWhiteboardConstants {

    private SoapWhiteboardConstants() {
        // non-instantiable
    }

    public static final String SOAP = "osgi.soap";
    public static final String SOAP_PREFIX = SOAP+".";
    public static final String SOAP_ENDPOINT_PREFIX = SOAP + ".endpoint.";
    public static final String SOAP_ENDPOINT_IMPLEMENTOR = SOAP_ENDPOINT_PREFIX + "implementor";
    public static final String SOAP_ENDPOINT_PATH = SOAP_ENDPOINT_PREFIX + "contextpath";
    public static final String SOAP_ENDPOINT_SELECT = SOAP_ENDPOINT_PREFIX + "selector";

	public static final String	SOAP_SPECIFICATION_VERSION	= "1.0";
    
}
