/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.impl.service.dal;

import java.util.Map;

import org.osgi.service.dal.OperationMetadata;
import org.osgi.service.dal.PropertyMetadata;

/**
 * Basic implementation of the operation metadata.
 */
public final class OperationMetadataImpl implements OperationMetadata {

	private final Map<String, ? >		metadata;
	private final PropertyMetadata		returnValueMetadata;
	private final PropertyMetadata[]	parametersMetadata;

	/**
	 * Constructs the operation metadata.
	 * 
	 * @param metadata Additional metadata.
	 * @param returnValueMetadata The return value metadata.
	 * @param parametersMetadata The parameters metadata.
	 */
	public OperationMetadataImpl(Map<String, ? > metadata,
			PropertyMetadata returnValueMetadata,
			PropertyMetadata[] parametersMetadata) {
		this.metadata = metadata;
		this.returnValueMetadata = returnValueMetadata;
		this.parametersMetadata = parametersMetadata;
	}

	@Override
	public Map<String, ? > getMetadata() {
		return this.metadata;
	}

	@Override
	public PropertyMetadata getReturnValueMetadata() {
		return this.returnValueMetadata;
	}

	@Override
	public PropertyMetadata[] getParametersMetadata() {
		return this.parametersMetadata;
	}
}
