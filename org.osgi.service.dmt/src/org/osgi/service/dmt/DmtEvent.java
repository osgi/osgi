package org.osgi.service.dmt;

import java.util.Hashtable;

/** @modelguid {80A64117-4A85-434A-8320-23E7E8F7E37C} */
public interface DmtEvent {
	/** @modelguid {81EC0912-FD23-41E5-9284-B1433362476A} */
	public static final int ADD = 1;
	/** @modelguid {C2A15F6A-449F-4590-A595-13197024FFB5} */
	public static final int REPLACE = 2;
	/** @modelguid {BDC3FE80-AF1D-4D72-80AE-24377626460A} */
	public static final int DELETE = 3;
	/** @modelguid {CB7F31A9-079E-4CDC-A0D9-E7A7EADD9B94} */
	public String getURI();
	/** @modelguid {9F8C1CC3-5239-4F1D-A989-1718C5417904} */
	public Hashtable getNodes();
	/** @modelguid {2395427D-4E60-4E31-B44F-296A1F5865B2} */
	public int getSessionId();
}
