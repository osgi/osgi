package org.osgi.test.cases.transaction.control.jdbc.control;

import java.util.List;
import java.util.Map;

import javax.transaction.xa.XAResource;

import org.osgi.service.transaction.control.LocalResource;
import org.osgi.service.transaction.control.TransactionControl;

public interface TestTransactionControl extends TransactionControl {

	public List<LocalResource> getEnlistedLocalResources();

	public Map<XAResource,String> getEnlistedXAResources();

	public List<Throwable> finish(boolean rollback);

}
