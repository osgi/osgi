/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Jan 21, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 * Mar 02, 2005  Andre Assad
 * 11            Implement DMT Use Cases 
 * ===========   ==============================================================
 * Mar 04, 2005  Alexandre Santos
 * 23            Updates due to changes in the DmtAcl API
 * ===========   ==============================================================
 * Aug 25, 2005  Luiz Felipe Guimaraes
 * 173           [MEGTCK][DMT] Changes on interface names and plugins
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc;
import org.osgi.test.cases.dmt.tc1.tbc.Acl.AclConstants;
import org.osgi.test.cases.dmt.tc1.tbc.Acl.AddPermission;
import org.osgi.test.cases.dmt.tc1.tbc.Acl.DeletePermission;
import org.osgi.test.cases.dmt.tc1.tbc.Acl.Hashcode;
import org.osgi.test.cases.dmt.tc1.tbc.Acl.IsPermitted;
import org.osgi.test.cases.dmt.tc1.tbc.Acl.SetPermission;
import org.osgi.test.cases.dmt.tc1.tbc.AlertItem.AlertItem;
import org.osgi.test.cases.dmt.tc1.tbc.AlertItem.ToString;
import org.osgi.test.cases.dmt.tc1.tbc.AlertPermission.AlertPermission;
import org.osgi.test.cases.dmt.tc1.tbc.DmtData.DmtData;
import org.osgi.test.cases.dmt.tc1.tbc.DmtData.DmtDataConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtData.Equals;
import org.osgi.test.cases.dmt.tc1.tbc.DmtData.TestDmtDataExceptions;
import org.osgi.test.cases.dmt.tc1.tbc.DmtException.DmtExceptionConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtException.PrintStackTrace;
import org.osgi.test.cases.dmt.tc1.tbc.DmtPrincipalPermission.DmtPrincipalPermission;
import org.osgi.test.cases.dmt.tc1.tbc.DmtServiceFactory.GetDmtAdmin;
import org.osgi.test.cases.dmt.tc1.tbc.DmtServiceFactory.GetNotificationService;
import org.osgi.test.cases.util.DefaultTestBundleControl;

public class DmtTestControl extends DefaultTestBundleControl {
	
    

	//Acl test cases
	public void testAcl() {
		new org.osgi.test.cases.dmt.tc1.tbc.Acl.Acl(this).run();
	}

	public void testAclAddPermission() {
		new AddPermission(this).run();
	}

	public void testAclDeletePermission() {
		new DeletePermission(this).run();
	}

	public void testAclConstants() {
		new AclConstants(this).run();
	}

	public void testAclIsPermitted() {
		new IsPermitted(this).run();
	}

	public void testAclSetPermission() {
		new SetPermission(this).run();
	}
	
	public void testAclToString() {
		new org.osgi.test.cases.dmt.tc1.tbc.Acl.ToString(this).run();
	}
	
	public void testAclEquals() {
		new org.osgi.test.cases.dmt.tc1.tbc.Acl.Equals(this).run();
	}

	public void testAclHashcode() {
		new Hashcode(this).run();
	}
	
	
	//DmtData Test Cases
	public void testDmtDataConstant() {
		new DmtDataConstants(this).run();
	}

	public void testDmtData()  {
		new DmtData(this).run();
	}

	public void testDmtDataEquals() {
		new Equals(this).run();
	}
	public void testDmtDataExceptions() {
		new TestDmtDataExceptions(this).run();
	}
	//DmtException test cases

	public void testDmtException(){
		new org.osgi.test.cases.dmt.tc1.tbc.DmtException.DmtException(this).run();
	}
	
	public void testDmtExceptionPrintStackTrace(){
		new PrintStackTrace(this).run();
	}
	
	public void testDmtExceptionConstants(){
		new DmtExceptionConstants(this).run();
	}

	//DmtPrincipalPermission test case

	public void testDmtPrincipalPermission(){
		new DmtPrincipalPermission(this).run();
	}

	public void testDmtPrincipalPermissionEquals(){
		new org.osgi.test.cases.dmt.tc1.tbc.DmtPrincipalPermission.Equals(this).run();
	}

	public void testDmtPrincipalPermissionImplies(){
		new org.osgi.test.cases.dmt.tc1.tbc.DmtPrincipalPermission.Implies(this).run();
	}

	public void testDmtPrincipalPermissionHashCode(){
		new org.osgi.test.cases.dmt.tc1.tbc.DmtPrincipalPermission.HashCode(this).run();
	}

	//DmtPermission test cases
	public void testDmtPermission() {
		new org.osgi.test.cases.dmt.tc1.tbc.DmtPermission.DmtPermission(this).run();
	}
	
	public void testDmtPermissionConstants() {
		new org.osgi.test.cases.dmt.tc1.tbc.DmtPermission.DmtPermissionConstants(this).run();
	}
	
	public void testDmtPermissionEquals() {
		new org.osgi.test.cases.dmt.tc1.tbc.DmtPermission.Equals(this).run();
	}
	
	public void testDmtPermissionHashCode() {
		new org.osgi.test.cases.dmt.tc1.tbc.DmtPermission.HashCode(this).run();
	}	
	
	public void testDmtPermissionImplies() {
		new org.osgi.test.cases.dmt.tc1.tbc.DmtPermission.Implies(this).run();
	}	
	//AlertItem
	public void testAlertItem() {
		new AlertItem(this).run();
	}
	
	public void testAlertItemToString() {
		new ToString(this).run();
	}
	
	//AlertPermission
	public void testAlertPermission() {
		new AlertPermission(this).run();
	}
	
	public void testAlertPermissionEquals() {
		new org.osgi.test.cases.dmt.tc1.tbc.AlertPermission.Equals(this).run();
	}	

	public void testAlertPermissionHashCode() {
		new org.osgi.test.cases.dmt.tc1.tbc.AlertPermission.HashCode(this).run();
	}	

	public void testAlertPermissionImplies() {
		new org.osgi.test.cases.dmt.tc1.tbc.AlertPermission.Implies(this).run();
	}	

	//DmtServiceFactory
	public void testDmtServiceFactoryGetDmtAdmin() {
		new GetDmtAdmin(this).run();
	}
	
	public void testDmtServiceFactoryGetNotificationService() {
		new GetNotificationService(this).run();
	}
	public void failUnexpectedException(Exception exception) {
		fail("Unexpected Exception: " + exception.getClass().getName() + " [Message: " + exception.getMessage() +"]");
	}
	
	public void failExpectedOtherException(Class expected,Exception found) {
		fail("Expected " + expected.getName()+ " but was " + found.getClass().getName());
	}
	
}
