package javax.microedition.midlet;

import org.osgi.impl.service.midletcontainer.*;

import java.util.*;

public abstract class MIDlet {

	protected MIDlet() 
	{
		MidletContainer.registerMidlet( this, new Container2Midlet( this ) );
	}

	protected abstract void startApp() throws MIDletStateChangeException;

	protected abstract void pauseApp();

	protected abstract void destroyApp(boolean flag)
			throws MIDletStateChangeException;

	public final void notifyDestroyed() {
		class DestroyerThread extends Thread {
			public void run() {
        
				try {
					midletHandle.destroy();
				}catch( Exception e ) {
					e.printStackTrace();
				}          
			};
		}
		
		DestroyerThread st = new DestroyerThread();
		st.start();
	}

	public final void notifyPaused() {
		class PauserThread extends Thread {
			public void run() {
        
				try {
					midletHandle.pause();
				}catch( Exception e ) {
					e.printStackTrace();
				}          
			};
		}
		
		PauserThread st = new PauserThread();
		st.start();
	}

	public final String getAppProperty(String key) {
		if( args == null )
		  return null;
    Object o =  args.get( key );
    if( o == null )
    	return null;
    return o.toString();
	}

	public final void resumeRequest() {
		class ResumerThread extends Thread {
			public void run() {
        
				try {
					midletHandle.resume();
				}catch( Exception e ) {
					e.printStackTrace();
				}          
			};
		}
		
		ResumerThread st = new ResumerThread();
		st.start();
	}

	public final boolean platformRequest(String URL)
			/*throws ConnectionNotFoundException*/ {
		System.out.println("-----------------");
		System.out.println("Platform request:");
		System.out.println("-----------------");
		System.out.println("Requesting the platform to load <" + URL + ">");
		return false;
	}

	public final int checkPermission(String permission) {
		return -1 /*state.checkPermission(permission)*/;
	}
	
	private MidletHandle midletHandle = null;
	private Map          args = null;
	
	private class Container2Midlet implements Container2MidletInterface {
		private MIDlet midlet;
		
		public Container2Midlet( MIDlet midlet ) {
			this.midlet = midlet;
		}
		
		public void setMidletHandle(MidletHandle midHnd) {
			midletHandle = midHnd;			
		}
		public void start(Map startArgs) throws MIDletStateChangeException {
			args = startArgs;
			startApp();
		}

		public void pause() {
			pauseApp();
		}

		public void resume() throws MIDletStateChangeException {
			startApp();
		}

		public void destroy(boolean immed) throws MIDletStateChangeException {
			destroyApp( immed );
			MidletContainer.unregisterMidlet( midlet );
		}		
	}
}
