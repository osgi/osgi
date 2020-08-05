
package org.osgi.impl.service.zigbee.event;

import java.util.LinkedList;

import org.osgi.service.zigbee.ZCLCommandResponse;
import org.osgi.service.zigbee.ZCLCommandResponseStream;
import org.osgi.util.function.Predicate;

public class ZCLCommandResponseStreamImpl implements ZCLCommandResponseStream {

	private final Object	lock	= new Object();

	private boolean			closed;

	private boolean			dequeuing;

	private LinkedList<ZCLCommandResponse>			buffer;

	private Predicate< ? super ZCLCommandResponse>	handler;

	public void handleResponse(ZCLCommandResponse response) {

		Predicate< ? super ZCLCommandResponse> handlerToUse;
		synchronized (lock) {
			if (closed) {
				// no need to deliver this event
				return;
			}

			if (this.handler == null) {
				if (buffer == null) {
					buffer = new LinkedList<>();
				}
				buffer.add(response);
				return;
			} else {
				if (buffer != null) {
					buffer.add(response);
					return;
				} else {
					// Wait for the final event to dequeue
					while (dequeuing) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							throw new RuntimeException("Unable to deliver ZCLCommand Response", e);
						}
					}

					if (closed) {
						return;
					}
					dequeuing = true;
					handlerToUse = this.handler;
				}
			}
		}

		boolean close = response.isEnd() || !handlerToUse.test(response);
		synchronized (lock) {
			if (close) {
				close();
			}
			dequeuing = false;
			lock.notifyAll();
		}
	}

	private void processBuffer() {
		for (;;) {
			Predicate< ? super ZCLCommandResponse> handlerToUse;
			ZCLCommandResponse response;
			synchronized (lock) {
				if (closed) {
					// no need to deliver this event
					return;
				}
				if (buffer != null) {
					dequeuing = true;
					response = buffer.removeFirst();
					handlerToUse = handler;
					if (buffer.isEmpty()) {
						buffer = null;
					}
				} else {
					dequeuing = false;
					lock.notifyAll();
					return;
				}
			}

			boolean close = response.isEnd() || !handlerToUse.test(response);
			synchronized (lock) {
				if (close) {
					close();
					dequeuing = false;
					lock.notifyAll();
				}
			}
		}
	}

	@Override
	public void close() {
		synchronized (lock) {
			if (closed) {
				// no work to do
				return;
			}
			closed = true;
			handler = null;
			buffer = null;
			dequeuing = false;
			lock.notifyAll();
		}
	}

	@Override
	public void forEach(
			@SuppressWarnings("hiding") Predicate< ? super ZCLCommandResponse> handler)
			throws IllegalStateException {
		if (handler == null) {
			throw new IllegalArgumentException("The handler function must not be null");
		}

		synchronized (lock) {
			if (closed) {
				throw new IllegalStateException("This MultiResponse is closed");
			}
			if (this.handler != null) {
				throw new IllegalStateException("There is already a handler registered");
			}
			this.handler = handler;
		}
		processBuffer();
	}

}
