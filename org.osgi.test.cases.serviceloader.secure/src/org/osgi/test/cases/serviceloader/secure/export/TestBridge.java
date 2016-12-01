/*
 * Copyright (c) 2012 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.serviceloader.secure.export;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 
 */
public interface TestBridge {
    /**
     * @param expectedResult The result to compare against
     * @throws Exception
     */
    public void run(String expectedResult) throws Exception;
}
