
package org.osgi.service.application;

import org.osgi.service.event.*;

/**
 * A MEG Application. a.k.a Meglet
 */ 

public abstract class MEGApplication implements Application, ChannelListener {

	private MEGApplicationContext appContext;

	public MEGApplication(MEGApplicationContext context){
		appContext = context;
	}

 	public abstract void startApplication() throws Exception;

	public abstract void stopApplication() throws Exception;

	public abstract void suspendApplication() throws Exception;

	public abstract void resumeApplication() throws Exception; 

	public abstract void channelEvent(ChannelEvent event); //??? 
}
