package org.osgi.test.shared;

import java.util.*;
import java.io.*;

public interface IRun {
	void linkClosed() throws Exception;
	void push( String bundle, Object msg ); 		
	void sendLog( String bundle, Log log )  throws IOException; 		
	void setTargetProperties( Dictionary properties ) throws IOException;
	void stopped( String bundle ); 
}

