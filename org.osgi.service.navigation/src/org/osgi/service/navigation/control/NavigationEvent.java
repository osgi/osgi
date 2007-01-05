package org.osgi.service.navigation.control;

public class NavigationEvent {
	final static int GUIDANCE_STARTED = 1;
	final static int GUIDANCE_STOPPED = 2;
	final static int DEVIATED_FROM_PATH = 4;
	final static int ADVICE_PREPARE = 8; // prepare to turn left
	final static int ADVICE_IMMINENT = 16; // turn left in 200m
	final static int ADVICE_NOW = 32; // turn left	
	final static int ADVICE_COMPLETED = 64; 
	final static int NEXT_SEGMENT = 128;
	
	Path	path;
	
	public NavigationEvent(int type, Path path ) {
		this.path = path;
	}
	
	
	public Path getPath() {
		return path;
	}
}
