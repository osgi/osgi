/*
 * Copyright (c) 2012 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.serviceloader.impl;

import org.osgi.test.cases.serviceloader.spi.ShapeProvider;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */
public class ShapeProviderImpl2 implements ShapeProvider {

    public String getShape() {
        return "square";
    }

}
