package org.osgi.service.dmt;

/** @modelguid {3EA53CF4-6C88-4395-A106-F0C2E891BBA3} */
public interface DmtExecPlugin {
	/** @modelguid {504A9717-718B-4920-8001-E7CA873C41CF} */
	public String execute(String nodeUri, String data) throws DmtException;
}
