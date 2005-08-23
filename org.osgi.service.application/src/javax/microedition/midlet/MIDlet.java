package javax.microedition.midlet;

public abstract class MIDlet {

	protected MIDlet() {}

	protected abstract void startApp() throws MIDletStateChangeException;

	protected abstract void pauseApp();

	protected abstract void destroyApp(boolean flag)
			throws MIDletStateChangeException;

	public final void notifyDestroyed() {
//		state.notifyDestroyed();
	}

	public final void notifyPaused() {
//		state.notifyPaused();
	}

	public final String getAppProperty(String key) {
		return null;
//		return state.getMIDletSuite().getProperty(key);
	}

	public final void resumeRequest() {
//		state.resumeRequest();
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
		return 1 /*state.checkPermission(permission)*/;
	}
}