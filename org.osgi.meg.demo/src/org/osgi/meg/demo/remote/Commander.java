package org.osgi.meg.demo.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.osgi.service.dmt.DmtException;


public class Commander implements RemoteReceiver {
    
    private RMServer      rms;
    private String        result;
    private ServerTestGUI gui;
    
    public Commander(RMServer rms, ServerTestGUI gui) {
        this.rms = rms;
        this.gui = gui;
    }

    public void destroy() {
    	try {
            command("close");
		} catch (CommanderException e) {
            System.err.println("Error occured during Commander.destroy():");
			e.printStackTrace();
		}
        rms.stop();
    }
    
    public synchronized String command(String cmd) throws CommanderException {
    	rms.setCommand(cmd);
        try {
			wait();
		} catch (InterruptedException e) {
            return "error: " + e.getMessage();
		}
        String exStr = DmtException.class.getName() + ": ";
        if (result.startsWith(exStr)) {
            int exStrL = exStr.length();
            int index = result.indexOf(':', exStrL);
            String code = result.substring(exStrL, index);
            throw new CommanderException(code, result);
        }
        return result;
    }
    
	public void onAlert(String alert) {
        if (null == alert)
            return;
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = new BufferedReader(new StringReader(alert));
        try {
			String line = reader.readLine();
            while (null != line) {
                // TODO
                if (line.length() > 200)
                    line = line.substring(0, 29) + "...";
                sb.append(line + "\n");
                line = reader.readLine();
            }
		} catch (IOException e) {
			// TODO
		} 
		gui.alert(sb.toString());
	}

	public void onResult(String result) {
		this.result = result;
        synchronized (this) {
        	notify();
        }
    }

}
