package org.osgi.service.distributedeventing;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.util.pushstream.PushEventConsumer;

/**
 * An event consumer.
 * 
 * An event consumer will be called with events coming from the relevant topics
 *
 * If this consumer returns a negative value then no further events will be sent
 * to it, see {@link PushEventConsumer#ABORT}. Values that are larger than 0
 * should be treated as a request to delay the next events with those number of
 * milliseconds.
 * 
 * If the consumer is unable to keep pace with the rate of event arrival then
 * the DistributedEventAdmin service will buffer the events until they can be
 * processed. If the Distributed Event Admin buffer grows too large then the
 * consumer will be closed, blacklisted and not called again.
 * 
 * @param <T> The payload type
 */
@ConsumerType
@FunctionalInterface
public interface DistributableEventConsumer<T> extends PushEventConsumer<DistributableEvent<T>> {

	/**
	 * The service property name defining the topics which this PushEventSource
	 * publishes to
	 */
	public static final String CONSUMED_EVENT_TOPICS = org.osgi.service.event.EventConstants.EVENT_TOPIC;
	
	/**
	 * The service property name defining the type of the event which this
	 * PushEventSource produces
	 */
	public static final String	EVENT_TYPE				= "event.type";
	
}
