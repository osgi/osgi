package org.osgi.service.application;

import java.security.BasicPermission;

/**
 * This class implements permissions for manipulating applications and
 * application instances.
 * <P>
 * ApplicationAdminPermission can be targeted to a particular application (using
 * its PID as an identifier) or to all applications (when no PID is specified).
 * <P>
 * ApplicationAdminPermission may be granted for different actions: launch,
 * schedule, manipulate, schedule and lock. There are some implication rules
 * between these: <BR>
 * <UL>
 * <LI>schedule => launch
 * <LI>manipulate => launch
 * </UL>
 * 
 * @modelguid {7356FDC7-F93F-4976-AF3F-9BD019851354}
 */
public class ApplicationAdminPermission extends BasicPermission {

	/**
	 * Allows launching all the other applications or those with the specified
	 * unique identifier.
	 * 
	 * @modelguid {ECDFA049-AC8D-4A83-BBE8-983885D497C7}
	 */
	public static final String LAUNCH = "launch";

	/**
	 * Allows scheduling all the other applications or those with the specified
	 * unique identifier.
	 * 
	 * Permission to schedule an application implies the permission launch on
	 * the same application.
	 * 
	 * @modelguid {D5651EE6-1030-4534-9DEA-7FC37C23DD1A}
	 */
	public static final String SCHEDULE = "schedule";

	/**
	 * Allows manipulating the lifecycle state of instances of all the other
	 * applications or the instances of applications where application has the
	 * specified unique identifier.
	 * 
	 * Permission to manipulate instances of an application implies the
	 * permission launch on the same application.
	 * 
	 * @modelguid {910201A6-85DF-4998-BF3E-1756958D9962}
	 */
	public static final String MANIPULATE = "manipulate";

	/**
	 * Allows setting/unsetting the locking state of other applications or those
	 * with the specified unique identifier.
	 * 
	 * @modelguid {EC19473B-3DA4-4C9D-9F35-5C6DBC47F2BA}
	 */
	public static final String LOCK = "lock";

	/**
	 * Constructs an ApplicationAdminPermission. It is equal to the
	 * ApplicationAdminPermission(null, actions) call.
	 * 
	 * @param actions -
	 *            comma-separated list of the desired actions granted on the
	 *            applications. It must not be null. The order of the actions in
	 *            the list is not significant.
	 * 
	 * @return NullPointerException is thrown if the actions parameter is null
	 * 
	 * @modelguid {C7A16B53-A240-498C-9F59-7F22FE2E94BB}
	 */
	public ApplicationAdminPermission(String actions) {
		super(actions);
	}

	/**
	 * Constructs an ApplicationAdminPermission. The name is the unique
	 * identifier of the target application. If the name is null then the
	 * constructed permission is granted for all targets.
	 * 
	 * @param pid -
	 *            unique identifier of the application, it may be null. The
	 *            value null indicates "all application".
	 * @param actions -
	 *            comma-separated list of the desired actions granted on the
	 *            applications. It must not be null. The order of the actions in
	 *            the list is not significant.
	 * 
	 * @exception NullPointerException
	 *                is thrown if the actions parameter is null
	 * 
	 * @modelguid {BE4BF220-EBCF-4303-A5DD-39CFDFE660BA}
	 */
	public ApplicationAdminPermission(String pid, String actions) {
		super(pid, actions);
	}
}