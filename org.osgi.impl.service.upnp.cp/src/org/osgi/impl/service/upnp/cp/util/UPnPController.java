package org.osgi.impl.service.upnp.cp.util;

public interface UPnPController {
	public void addDevice(String uuid, String descurl);

	public void removeDevice(String uuid);

	public String getProduct();

	public void registerDeviceListener(UPnPDeviceListener devlistener);

	public void unRegisterDeviceListener(UPnPDeviceListener devlistener);

	public Control getControl();

	public EventService getEventService();
}
