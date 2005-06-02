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
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 27/04/2005   Leonardo Barros
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 * 23/05/2005   Alexandre Santos
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tb1;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Dictionary;
import java.util.Map;

import org.osgi.meglet.Meglet;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.test.cases.application.tbc.MegletInterface;

public class TestMeglet extends Meglet implements MegletInterface
{
	private boolean started = false;
	private boolean stopped = false;
	private boolean suspended = false;
	private boolean resumed = false;
	
	public TestMeglet()
	{
		super();		
	}
	
	public void start( Map args ) throws Exception
	{
		started = true;
		//getComponentContext().getBundleContext().registerService(MegletInterface.class.getName(), this, null);
	}
	
	public void stop() throws Exception
	{
		stopped = true;
	}
	
	public void suspend( OutputStream stateStorage ) throws Exception
	{
		suspended = true;
	}
	
	public void resume( InputStream stateStorage ) throws Exception
	{
		resumed = true;
	}
	
	
	public void handleEvent(Event event)
	{
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public boolean isStopped() {		
		return stopped;
	}
	
	public boolean isSuspended() {
		return suspended;
	}
	
	public boolean isResumed() {
		return resumed;
	}
	
	public ComponentContext getCompContext() {
		return this.getComponentContext();
	}
	
	public String getInstID(){
		return this.getInstanceID();
	}
	
	public Dictionary getProps() {
	    return this.getProperties();	
	}
}