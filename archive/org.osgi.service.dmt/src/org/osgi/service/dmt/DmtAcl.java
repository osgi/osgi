package org.osgi.service.dmt;

import java.util.Hashtable;

/** @modelguid {4E8A8013-DE89-4CBF-A66A-492EC76D4ECA} */
public interface DmtAcl {
	/** @modelguid {6EB1BEF6-A702-464E-91B4-DF2C78ED9EB9} */
	public static final int	GET		= 1;
	/** @modelguid {4A8135DB-AE82-4493-ACB3-CD51FE040D30} */
	public static final int	ADD		= 2;
	/** @modelguid {7FDF1D3B-3CCD-4704-BDE8-9A865C09E85B} */
	public static final int	REPLACE	= 4;
	/** @modelguid {EE22CA81-8538-4C38-9CD4-F4CD606DE1E8} */
	public static final int	DELETE	= 8;
	/** @modelguid {90C3C4EC-6533-43FD-8554-247E8D7745E6} */
	public static final int	EXEC	= 16;

	/** @modelguid {ECD0B090-097E-4A63-A830-132FA2C5C5B6} */
	public void addPermission(DmtPrincipal principal, int permissions);

	/** @modelguid {DA1467A5-B959-4A50-B2E2-ADBE2767C9E5} */
	public void deletePermission(DmtPrincipal principal, int permissions);

	/** @modelguid {E8BD211E-75C3-4A4C-B18B-00C63B4D8AB2} */
	public int getPermissions(DmtPrincipal principal);

	/** @modelguid {8E431463-7C84-41F1-B12F-7EE5E58B2C08} */
	public boolean isPermitted(DmtPrincipal principal, int permissions);

	/** @modelguid {9C2B145A-B17C-4D21-8317-E0A26B29F847} */
	public void setPermission(DmtPrincipal principal, int permissions);

	/** @modelguid {CC0B6245-D2C8-41FF-9C80-2974268ADEE3} */
	public Hashtable getPrincipals();

	/** @modelguid {51C74187-64FA-47EB-B205-1B74B62FA99A} */
	public String toString();
}
