package org.osgi.test.cases.webservice.junit;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import org.junit.jupiter.api.Assertions;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.webservice.runtime.WebserviceServiceRuntime;
import org.osgi.service.webservice.runtime.dto.RuntimeDTO;
import org.osgi.util.tracker.ServiceTracker;

public class WebServiceRuntimeTracker {

	private final ServiceTracker<WebserviceServiceRuntime, WebserviceServiceRuntime> tracker;
	
	private final Semaphore updateNotifier = new Semaphore(0);
	
	private long updateCount = Long.MIN_VALUE;
	
	private boolean hasChanged = false;
	
	/**
	 * Used to synchronise when we need to deal with 
	 * updateCount and updateNotifier at the same time
	 */
	private final Object lock = new Object();
	
	public WebServiceRuntimeTracker(BundleContext ctx, ServiceReference<WebserviceServiceRuntime> ref) {
		
		tracker = new ServiceTracker<WebserviceServiceRuntime, WebserviceServiceRuntime>(ctx, ref, null) {

			@Override
			public WebserviceServiceRuntime addingService(ServiceReference<WebserviceServiceRuntime> reference) {
				Object count = ref.getProperty(Constants.SERVICE_CHANGECOUNT);
				
				if(count instanceof Long) {
					synchronized (lock) {
						hasChanged = true;
						updateCount = (Long) count; 
						updateNotifier.release();
					}
				}
				return ctx.getService(ref);
			}

			@Override
			public void modifiedService(ServiceReference<WebserviceServiceRuntime> reference,
					WebserviceServiceRuntime service) {
				synchronized (lock) {
					Object update = ref.getProperty(Constants.SERVICE_CHANGECOUNT);
					if(update instanceof Long) {
						long newCount = (Long) update;
						// The count must increase
						if(newCount > updateCount) {
							hasChanged = true;
							updateCount = newCount;
							updateNotifier.release();
						}
					}
				}
			}
			
		};
		tracker.open();
	}
	
	public WebserviceServiceRuntime getServiceRuntime() {
		return tracker.getService();
	}
	
	public long getCurrentChangeCount() {
		synchronized (lock) {
			return updateCount;
		}
	}
	
	public boolean hasChanged() {
		synchronized (lock) {
			return hasChanged;
		}
	}
	
	public void waitForQuiet(long time, long maxTime, TimeUnit unit) throws InterruptedException {
		long start = System.nanoTime();
		
		for(;;) {
			synchronized (lock) {
				updateNotifier.drainPermits();
			}
			if(updateNotifier.tryAcquire(time, unit)) {
				long elapsed = unit.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
				if(elapsed > maxTime) {
					Assertions.fail("The WebserviceServiceRuntime did not settle within " + maxTime + " " + unit);
				}
			} else {
				break;
			}
		}
	}
	
	public <T> T waitForChange(Callable<T> action) throws Exception {
		return waitForChange(action, (x,y) -> true);
	}

	public <T> T waitForChange(Callable<T> action, BiPredicate<T,RuntimeDTO> test) throws Exception {
		synchronized (lock) {
			updateNotifier.drainPermits();
		}
		T t = action.call();
		for(;;) {
			updateNotifier.acquire();
			if(test.test(t, getServiceRuntime().getRuntimeDTO())) {
				break;
			}
		}
		return t;
	}
	
	public <T> T waitForChange(Callable<T> action, long maxTime, TimeUnit unit) throws Exception {
		return waitForChange(action, (x,y) -> true, maxTime, unit);
	}

	public <T> T waitForChange(Callable<T> action, BiPredicate<T,RuntimeDTO> test,
 			long maxTime, TimeUnit unit) throws Exception {
		return waitForChange(action, (x,y) -> true,
				(x,y) -> "The WebserviceServiceRuntime did not update within " + maxTime + " " + unit,
				maxTime, unit);
	}
	public <T> T waitForChange(Callable<T> action, BiPredicate<T,RuntimeDTO> test,
			BiFunction<T, RuntimeDTO, String> errorMessage, long maxTime, TimeUnit unit) throws Exception {
		synchronized (lock) {
			updateNotifier.drainPermits();
		}
		T t = action.call();
		long start = System.nanoTime();
		long remaining = unit.toNanos(maxTime);
		for(;;) {
			if(!updateNotifier.tryAcquire(remaining, TimeUnit.NANOSECONDS)) {
				Assertions.fail();
			} else {
				if(test.test(t, getServiceRuntime().getRuntimeDTO())) {
					break;
				} else {
					remaining = unit.toNanos(maxTime) - (System.nanoTime() - start);
				}
			}
		}
		return t;
	}

	public <T> T waitForNoChange(Callable<T> action,
			long maxTime, TimeUnit unit) throws Exception {
		return waitForNoChange(action, (x,y) -> false, maxTime, unit);
	}
	
	public <T> T waitForNoChange(Callable<T> action, BiPredicate<T,RuntimeDTO> test,
			long maxTime, TimeUnit unit) throws Exception {
		synchronized (lock) {
			updateNotifier.drainPermits();
		}
		T t = action.call();
		long start = System.nanoTime();
		long remaining = unit.toNanos(maxTime);
		for(;;) {
			if(!updateNotifier.tryAcquire(remaining, TimeUnit.NANOSECONDS)) {
				break;
			} else {
				if(test.test(t, getServiceRuntime().getRuntimeDTO())) {
					remaining = unit.toNanos(maxTime) - (System.nanoTime() - start);
				} else {
					Assertions.fail("The WebserviceServiceRuntime unexpectedly changed within " + maxTime + " " + unit);
				}
			}
		}
		return t;
	}
	
	public void close() {
		tracker.close();
	}
}
