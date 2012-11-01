/*
 * Copyright (c) 2012 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.serviceloader.secure.impl;

import org.osgi.test.cases.serviceloader.secure.spi.ColorProvider;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 
 */
public class ColorProviderImpl implements ColorProvider {

    /**
     * @see org.osgi.test.cases.serviceloader.secure.spi.ColorProvider#getColor()
     */
    public String getColor() {
        return "green";
    }

}
