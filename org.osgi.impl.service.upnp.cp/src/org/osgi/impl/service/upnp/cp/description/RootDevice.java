package org.osgi.impl.service.upnp.cp.description;

import java.net.URL;
import org.osgi.framework.BundleContext;

public class RootDevice {
	private String			urn;
	private String			urlbase;
	private int				major;
	private int				minor;
	private String			deviceType;
	private String			friendlyName;
	private String			manufacturer;
	private String			manufacturerURL;
	private String			modelDescription;
	private String			modelName;
	private String			modelNumber;
	private String			modelURL;
	private String			serialNumber;
	private String			udn;
	private String			upc;
	private String			presentationURL	= "";
	private RootDevice		devices[];
	private RootDevice		rdev;
	private RootDevice		device;
	private Icon			icons[];
	private ServiceInfo		serviceList[];
	private ServiceInfo		servi1[];
	public BundleContext	bc1;

	// This constructor creates the RootDevice object
	RootDevice(RootDevice rd, BundleContext bc) {
		rdev = rd;
		bc1 = bc;
	}

	// This constructor creates the RootDevice object
	RootDevice(BundleContext bc) {
		bc1 = bc;
	}

	// This method returns the root attributes of the device xml file.
	public String getRootAttribute() {
		return urn;
	}

	// This method returns the URLBase of the device xml file.
	public String getURLBase() {
		return urlbase;
	}

	// This method returns the type of the device.
	public String getDeviceType() {
		return deviceType;
	}

	// This method returns the friendly name of the device.
	public String getFriendlyName() {
		return friendlyName;
	}

	// This method returns the manufacturers name of the device.
	public String getManufacturer() {
		return manufacturer;
	}

	// This method returns the manufacturers URL of the device.
	public String getManufacturerURL() {
		return manufacturerURL;
	}

	// This method returns the model description of the device.
	public String getModelDescription() {
		return modelDescription;
	}

	// This method returns the model number of the device.
	public String getModelNumber() {
		return modelNumber;
	}

	// This method returns the model name of the device.
	public String getModelName() {
		return modelName;
	}

	// This method returns the modelURL of the device.
	public String getModelURL() {
		return modelURL;
	}

	// This method returns the serial number of the device.
	public String getSerialNumber() {
		return serialNumber;
	}

	// This method returns the UDN of the device.
	public String getUDN() {
		return udn;
	}

	// This method returns the UPC of the device.
	public String getUPC() {
		return upc;
	}

	// This method returns the presentationURL of the device.
	public String getPresentationURL() {
		return presentationURL;
	}

	// This method returns the embedded devices of the device.
	public RootDevice[] getEmbededDevices() {
		if (devices == null) {
			return new RootDevice[0];
		}
		else {
			return devices;
		}
	}

	// This method returns the icons of the device.
	public Icon[] getIcons() {
		return icons;
	}

	// This method returns all the services which are matching the eventSubURL
	// of the services.
	public ServiceInfo getServiceBasedOnURL(String urlValue) {
		ServiceInfo serv[] = servi1;
		if (urlValue == null) {
			urlValue = "";
		}
		urlValue = urlValue.trim();
		for (int servCtr = 0; servCtr < serv.length; servCtr++) {
			if (serv[servCtr].getEventSubURL().trim()
					.equalsIgnoreCase(urlValue)) {
				return serv[servCtr];
			}
		}
		return null;
	}

	// This method returns the services of the device.
	public ServiceInfo[] getServices() {
		return servi1;
	}

	// This method sets the service type for the device.
	public void setDeviceType(String type) {
		deviceType = type;
	}

	// This method sets the friendly name for the device.
	public void setFriendlyName(String friendlyname) {
		friendlyName = friendlyname;
	}

	// This method sets the manyfacturers name for the device.
	public void setManufacturer(String manufacturerName) {
		manufacturer = manufacturerName;
	}

	// This method sets the manufacturer URL for the device.
	public void setManufacturerURL(String manufacturerurl) {
		manufacturerURL = manufacturerurl;
	}

	// This method sets the model description for the device.
	public void setModelDescription(String modelDesc) {
		modelDescription = modelDesc;
	}

	// This method sets the model number for the device.
	public void setModelNumber(String nam) {
		modelNumber = nam;
	}

	// This method sets the model name for the device.
	public void setModelName(String modelname) {
		modelName = modelname;
	}

	// This method sets the model URL for the device.
	public void setModelURL(String modelurl) {
		modelURL = modelurl;
	}

	// This method sets the serial number for the device.
	public void setSerialNumber(String serNumber) {
		serialNumber = serNumber;
	}

	// This method sets the UDN for the device.
	public void setUDN(String udnValue) {
		udn = udnValue;
	}

	// This method sets the UPC for the device.
	public void setUPC(String upcValue) {
		upc = upcValue;
	}

	// This method sets the presentation URL for the device.
	public void setPresentationURL(String presURL) {
		if (presURL == null) {
			presURL = "";
		}
		presentationURL = presURL;
	}

	// This method sets the embedded devices for the device.
	public void setEmbeddedDevices(RootDevice arr[]) {
		devices = arr;
	}

	// This method sets the icons for the device.
	public void setIcons(Icon allIcons[]) {
		icons = allIcons;
	}

	// This method sets the services for the device.
	public void setServices(ServiceInfo sl[], RootDevice rootdev) {
		serviceList = sl;
		servi1 = new ServiceInfo[serviceList.length];
		final Description desc = new Description(bc1);
		String urlBase6 = "";
		String ub = rootdev.getURLBase();
		// Sets the urlBase if the urlBase is not specified.
		if (ub == null || ub.trim().length() == 0) {
			int slashCtr = Description.cdPath.lastIndexOf('/');
			ub = Description.cdPath.substring(0, slashCtr);
			rootdev.setURLBase(ub);
		}
		String bas = ub;
		//Attaches the services to device based on the SCPDURL.
		final String bas1 = bas;
		//Thread t = new Thread(){
		//	public void run() {
		Document doc1 = null;
		for (int k = 0; k < serviceList.length; k++) {
			try {
				URL base = new URL(bas1);
				URL docurl = new URL(base, serviceList[k].getSCPDURL());
				//System.out.println(docurl.toString());
				doc1 = desc.getDocument(docurl.toString());
				servi1[k] = doc1.getServiceInfo();
				servi1[k].setSCPDURL(serviceList[k].getSCPDURL());
				servi1[k].setControlURL(serviceList[k].getControlURL());
				servi1[k].setEventSubURL(serviceList[k].getEventSubURL());
				servi1[k].setServiceID(serviceList[k].getServiceID());
				servi1[k].setServiceType(serviceList[k].getServiceType());
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		//}
		//};
	}

	// This method returns the major version of the device xml file.
	public int getMajor() {
		return major;
	}

	// This method returns the minor version of the device xml file.
	public int getMinor() {
		return minor;
	}

	// This method returns the device object of the device xml file.
	public RootDevice getDevice() {
		return this;
	}

	// This method sets the root attributes of the device xml file.
	public void setRootAttribute(String attrKey, String attrValue) {
		urn = attrValue;
	}

	// This method sets the URLBase for the device object from the device xml
	// file.
	public void setURLBase(String base) {
		urlbase = base;
	}

	// This method sets the major version for the device object from the device
	// xml file.
	public void setMajor(int ma) {
		major = ma;
	}

	// This method sets the minor version for the device object from the device
	// xml file.
	public void setMinor(int mi) {
		minor = mi;
	}

	// This method sets the device object.
	public void setDevice(RootDevice deviceDetails) {
		device = deviceDetails;
	}
}
