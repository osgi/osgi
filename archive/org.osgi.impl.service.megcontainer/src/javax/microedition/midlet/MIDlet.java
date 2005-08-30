package javax.microedition.midlet;

import org.osgi.service.application.midlet.*;
import java.util.*;

public abstract class MIDlet {

	protected MIDlet() {}

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
	private Hashtable    args = null;
	
	private void reflectionMethod(String command, Object object) throws Exception {
		if( command.equals("MidletHandle"))
			midletHandle = (MidletHandle) object;
		else if( command.equals("Start")) {
			args = new Hashtable( (Hashtable)object );
			startApp();
		}
		else if( command.equals("Pause") )
			pauseApp();
		else if( command.equals("Resume") )
			startApp();
		else if( command.equals("Destroy") )
			destroyApp( ((Boolean)object).booleanValue() );
	}
}
