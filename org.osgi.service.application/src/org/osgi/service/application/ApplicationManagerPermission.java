package org.osgi.service.application;

import java.security.BasicPermission;

/**
 * This class implements permissions for the Application Manager
 * 
 * @modelguid {7356FDC7-F93F-4976-AF3F-9BD019851354}
 */
public class ApplicationManagerPermission extends BasicPermission {
	/** @modelguid {ECDFA049-AC8D-4A83-BBE8-983885D497C7} */
	public static final String	LAUNCH		= "launch";
	/** @modelguid {910201A6-85DF-4998-BF3E-1756958D9962} */
	public static final String	CONTENT		= "content";
	/** @modelguid {EC19473B-3DA4-4C9D-9F35-5C6DBC47F2BA} */
	public static final String	PROVIDE		= "provide";
	/** @modelguid {2ACA0AF5-686F-4944-A8FA-A74F46E3CC7F} */
	public static final String	ENUMERATE	= "enumerate";
	/** @modelguid {D5651EE6-1030-4534-9DEA-7FC37C23DD1A} */
	public static final String	SCHEDULE	= "schedule";

	/**
	 * Constructs a ApplicationManagerPermission.
	 * 
	 * @param actions - read and write
	 * @modelguid {C7A16B53-A240-498C-9F59-7F22FE2E94BB}
	 */
	public ApplicationManagerPermission(String actions) {
		super(actions);
	}

	/**
	 * Constructs a ApplicationManagerPermission.
	 * 
	 * @param name - name of the permission
	 * @param actions - read and write
	 * @modelguid {BE4BF220-EBCF-4303-A5DD-39CFDFE660BA}
	 */
	public ApplicationManagerPermission(String name, String actions) {
		super(name, actions);
	}
}
