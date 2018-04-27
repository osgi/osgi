package org.osgi.test.cases.cdi.tb12;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.osgi.service.cdi.annotations.BeanPropertyType;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

@ApplicationScoped
@Service
@EventAdminReceiver.Props(event_topics = "com/acme/foo")
public class EventAdminReceiver implements EventHandler {

	@BeanPropertyType
	@Retention(RUNTIME)
	@Target(TYPE)
	public @interface Props {
		String[] event_topics();
		String event_filter() default "";
	}

	@Inject
	javax.enterprise.event.Event<Event> eventAdminEvent;

	@Override
	public void handleEvent(Event event) {
		// fire the event into the CDI container asynchronously
		eventAdminEvent.fireAsync(event);
	}

}
