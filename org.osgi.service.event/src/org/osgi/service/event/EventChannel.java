package org.osgi.service.event;

/** @modelguid {C3C3506F-55A8-41CC-8F13-EBE7A4E9228F} */
public interface EventChannel {
	/** @modelguid {E5125D87-5B75-41DC-AA5E-C7BCE72E4859} */
	public void postEvent(ChannelEvent event);

	/** @modelguid {88BDEA87-C195-441B-8DD6-961F18749E50} */
	public void sendEvent(ChannelEvent event);
}
