/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.enocean;

/**
 * This class contains code and definitions necessary to support common EnOcean
 * exceptions. This class is mostly used with low-level, gateway-interacting
 * code : EnOceanHost.
 * 
 * @author $Id$
 */
public class EnOceanException extends Exception {

    /** generated */
    private static final long serialVersionUID         = -2401739486671107362L;

    /**
     * SUCCESS status code.
     */
    public static final short SUCCESS                  = 0;

    /**
     * Unexpected failure.
     */
    public static final short ESP_UNEXPECTED_FAILURE   = 1;

    /**
     * Operation is not supported by the target device.
     */
    public static final short ESP_RET_NOT_SUPPORTED    = 2;

    /**
     * One of the parameters was badly specified or missing.
     */
    public static final short ESP_RET_WRONG_PARAM      = 3;

    /**
     * The operation was denied.
     */
    public static final short ESP_RET_OPERATION_DENIED = 4;

    /**
     * The message was invalid.
     */
    public static final short INVALID_TELEGRAM         = 0xF0;

    private final int         errorCode;

    /**
     * Constructor for EnOceanException
     * 
     * @param errordesc exception error description
     */
    public EnOceanException(String errordesc) {
        super(errordesc);
        errorCode = 0;
    }

    /**
     * Constructor for EnOceanException
     * 
     * @param errorCode the error code.
     * @param errorDesc the description.
     */
    public EnOceanException(int errorCode, String errorDesc) {
        super(errorDesc);
        this.errorCode = errorCode;
    }

    /**
     * Constructor for EnOceanException
     * 
     * @param errorCode An error code.
     */
    public EnOceanException(int errorCode) {
        this(errorCode, null);
    }

    /**
     * Constructor for EnOceanException
     * 
     * @return An EnOcean error code, defined by the EnOcean Forum working
     *         committee or an EnOcean vendor.
     */
    public int errorCode() {
        return errorCode;
    }

}
