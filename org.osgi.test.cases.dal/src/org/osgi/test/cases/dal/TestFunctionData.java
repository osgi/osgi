/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal;

import java.util.Map;

import org.osgi.service.dal.FunctionData;

final class TestFunctionData extends FunctionData {

	public TestFunctionData(long timestamp, Map<String,Object> metadata) {
		super(timestamp, metadata);
	}

	public TestFunctionData(Map<String,Object> fields) {
		super(fields);
	}
}
