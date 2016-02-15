/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.service.transaction.control.jpa;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.osgi.service.jpa.EntityManagerFactoryBuilder;

/**
 * A factory for creating JPAEntityManagerProvider instances
 * <p>
 * This factory can be used if the {@link JPAEntityManagerProvider} should not
 * be a public service, for example to protect a username/password.
 */
public interface JPAEntityManagerProviderFactory {

	/**
	 * The property used to determine whether XA enlistment is enabled for this
	 * resource provider
	 */
	public static final String	XA_ENLISTMENT_ENABLED		= "osgi.xa.enabled";

	/**
	 * The property used to determine whether local enlistment is enabled for
	 * this resource provider
	 */
	public static final String	LOCAL_ENLISTMENT_ENABLED	= "osgi.local.enabled";

	/**
	 * The property used to provide a {@link JPAEntityManagerProvider} to the
	 * resource provider. This will be converted into a DataSource by the
	 * factory, and passed to the {@link EntityManagerFactoryBuilder} using the
	 * javax.persistence.jtaDataSource property
	 */
	public static final String	TRANSACTIONAL_DB_CONNECTION	= "osgi.jdbc.provider";

	/**
	 * The property used to indicate that database connections will be
	 * automatically enlisted in ongoing transactions without intervention from
	 * the JPA provider
	 */
	public static final String	PRE_ENLISTED_DB_CONNECTION	= "osgi.jdbc.enlisted";

	/**
	 * Create a private {@link JPAEntityManagerProvider} using an
	 * {@link EntityManagerFactoryBuilder}
	 * 
	 * @param emfb
	 * @param jpaProperties The properties to pass to the
	 *            {@link EntityManagerFactoryBuilder} in order to create the
	 *            underlying {@link EntityManagerFactory} and
	 *            {@link EntityManager} instances
	 * @param resourceProviderProperties Configuration properties to pass to the
	 *            JPA Resource Provider runtime
	 * @return A {@link JPAEntityManagerProvider} that can be used in
	 *         transactions
	 */
	JPAEntityManagerProvider getProviderFor(EntityManagerFactoryBuilder emfb,
			Map<String,Object> jpaProperties,
			Map<String,Object> resourceProviderProperties);

	/**
	 * Create a private {@link JPAEntityManagerProvider} using an existing
	 * {@link EntityManagerFactory}.
	 * 
	 * @param emf
	 * @param resourceProviderProperties Configuration properties to pass to the
	 *            JDBC Resource Provider runtime
	 * @return A {@link JPAEntityManagerProvider} that can be used in
	 *         transactions
	 */
	JPAEntityManagerProvider getProviderFor(EntityManagerFactory emf,
			Map<String,Object> resourceProviderProperties);

}
