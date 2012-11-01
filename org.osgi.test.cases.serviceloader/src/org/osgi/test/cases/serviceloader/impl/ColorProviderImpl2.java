/*
 * Copyright (c) 2011 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.serviceloader.impl;

import org.osgi.test.cases.serviceloader.spi.ColorProvider;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 
 */
public class ColorProviderImpl2 implements ColorProvider {

    /**
     * @see org.osgi.test.cases.serviceloader.spi.ColorProvider#getColor()
     */
    public String getColor() {
        return "red";
    }

}
