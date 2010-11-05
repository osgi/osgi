package info.dmtree;

public interface MountPoint {

	String[] getMountPath();
	
	void postEvent( String topic, String[] relativeNodes, String[] newRelativeNodes );
}
