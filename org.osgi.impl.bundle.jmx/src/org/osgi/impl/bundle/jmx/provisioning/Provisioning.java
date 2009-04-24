/*
 * Copyright 2008 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.osgi.impl.bundle.jmx.provisioning;

import static org.osgi.jmx.codec.OSGiProperties.propertiesFrom;
import static org.osgi.jmx.codec.OSGiProperties.tableFrom;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipInputStream;

import javax.management.openmbean.TabularData;

import org.osgi.jmx.provisioning.ProvisioningMBean;
import org.osgi.service.provisioning.ProvisioningService;

/** 
 * 
 */
public class Provisioning implements ProvisioningMBean {
	protected ProvisioningService provisioning;

	public Provisioning(ProvisioningService provisioning) {
		this.provisioning = provisioning;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.jmx.compendium.ProvisioningMBean#addInformation(java.lang.String
	 * )
	 */
	public void addInformation(String zipURL) throws IOException {
		InputStream is = new URL(zipURL).openStream();
		ZipInputStream zis = new ZipInputStream(is);
		try {
			provisioning.addInformation(zis);
		} finally {
			zis.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.jmx.compendium.ProvisioningMBean#addInformation(javax.management
	 * .openmbean.TabularData)
	 */
	public void addInformation(TabularData info) throws IOException {
		provisioning.addInformation(propertiesFrom(info));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.jmx.compendium.ProvisioningMBean#getInformation()
	 */
	public TabularData getInformation() throws IOException {
		return tableFrom(provisioning.getInformation());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.jmx.compendium.ProvisioningMBean#setInformation(javax.management
	 * .openmbean.TabularData)
	 */
	public void setInformation(TabularData info) throws IOException {
		provisioning.setInformation(propertiesFrom(info));
	}

}
