package org.osgi.impl.service.upnp.cp.ssdp;

import java.util.Enumeration;

// This class checks device expiration times  
public class DeviceExpirationThread extends Thread {
	private SSDPComponent	ssdpcomp;
	private boolean			flag	= true;

	// This constructor constructs DeviceExpirationThread.
	DeviceExpirationThread(SSDPComponent comp) {
		ssdpcomp = comp;
	}

	// This method contineously checks all devices expiration times
	public void run() {
		while (flag) {
			try {
				Enumeration enum = ssdpcomp.devExpTimes.keys();
				while (enum.hasMoreElements()) {
					String uuid = (String) enum.nextElement();
					Long expTime = (Long) ssdpcomp.devExpTimes.get(uuid);
					if (expTime.longValue() <= System.currentTimeMillis()) {
						ssdpcomp.removeDevice(uuid);
					}
				}
				Thread.sleep(5000);//### added so this thread does not consume
				// all time - pkr
			}
			catch (Exception e) {
			}
		}
	}

	// This method kills DeviceExpirationThread.
	public void killDeviceExpirationThread() {
		flag = false;
		interrupt();
	}
}
