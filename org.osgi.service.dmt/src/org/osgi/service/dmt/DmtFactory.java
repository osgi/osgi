package org.osgi.service.dmt;

/** @modelguid {2E39F855-A94A-4318-BA0C-77CF42223701} */
public interface DmtFactory {
	/** @modelguid {F5475337-6239-4307-BD67-03627B711C18} */
	public static final int	LOCK_TYPE_SHARED	= 0;
	/** @modelguid {20A2CEF6-B767-4A7A-B5B6-A69D9D661414} */
	public static final int	LOCK_TYPE_EXCLUSIVE	= 1;
	/** @modelguid {F12E290C-C2F9-49EB-9A7B-1324F0B8BE10} */
	public static final int	LOCK_TYPE_AUTOMATIC	= 2;
	/** @modelguid {2BAB02B0-DFBD-4A6C-81A8-37D7FFEE9E13} */
	public static final int	LOCK_TYPE_ATOMIC	= 3;

	/** @modelguid {73442EB9-7066-4B28-A2E7-E9EC9F306FC2} */
	public DmtSession getTree(DmtPrincipal principal) throws DmtException;

	/** @modelguid {1B01B2AE-72AC-4097-B182-F8C235278B1C} */
	public DmtSession getTree(DmtPrincipal principal, String subtreeUri)
			throws DmtException;

	/** @modelguid {3470D158-BA2E-4782-9DD7-84F71F1A8A9A} */
	public DmtSession getTree(DmtPrincipal principal, String subtreeUri,
			int lockMode) throws DmtException;
}
