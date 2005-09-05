package javax.microedition.midlet;

import org.osgi.impl.service.midletcontainer.*;

import java.security.*;
import java.util.*;

public abstract class MIDlet {

	protected MIDlet() 
	{
		MidletHandle.registerMidlet( this, new Container2Midlet( this ) );
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
		public void start(final Map startArgs) throws MIDletStateChangeException {
			try {
			  AccessController.doPrivileged(new PrivilegedExceptionAction() {
				  public Object run() throws Exception {			
					  args = startArgs;
					  startApp();
					  return null;
				  }
			  });
			}catch(PrivilegedActionException e ) {
				throw (MIDletStateChangeException)e.getException(); 
			}
		}

		public void pause() {
		  AccessController.doPrivileged(new PrivilegedAction() {
			  public Object run() {			
     			pauseApp();
				  return null;
			  }
		  });
		}

		public void resume() throws MIDletStateChangeException {
			try {
			  AccessController.doPrivileged(new PrivilegedExceptionAction() {
				  public Object run() throws Exception {			
					  startApp();
					  return null;
				  }
			  });
			}catch(PrivilegedActionException e ) {
				throw (MIDletStateChangeException)e.getException(); 
			}
		}

		public void destroy(final boolean immed) throws MIDletStateChangeException {
			try {
			  AccessController.doPrivileged(new PrivilegedExceptionAction() {
				  public Object run() throws Exception {			
						destroyApp( immed );
					  return null;
				  }
			  });
			}catch(PrivilegedActionException e ) {
				throw (MIDletStateChangeException)e.getException(); 
			}
			MidletHandle.unregisterMidlet( midlet );
		}		
	}
}
