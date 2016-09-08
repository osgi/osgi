package org.osgi.test.cases.pushstream.junit;

import junit.framework.TestCase;

/**
 * Section 706.3 The Push Stream
 */
public class ThePushStreamTest extends TestCase {
	
	/**
	 * 706.3 : The Push Stream
	 * <p/>
	 * Simple event passing can be achieved by connecting a Push Event Consumer
	 * directly to a Push Event Source.
	 */
	public void testConsumerDirectlyConnectedToSource() {

	}

	/**
	 * 706.3.1: Simple Pipelines
	 * <p/>
	 * A Push Stream can be created from a Push Event Source.
	 */
	public void testCreatePushStreamFromPushEventSource() {

	}

	/**
	 * 706.3.1: Simple Pipelines
	 * <p/>
	 * Once a Push Stream object has had an operation invoked on it then it may
	 * not have any other operations chained to it
	 */
	public void testUnableToChainNewOperationWhileIntermediateOperationRunning() {

	}

	/**
	 * 706.3.4.1 : Coalescing
	 * <p/>
	 * [Coalescence mechanism ] to coalesce registers a coalescing function
	 * which aggregates one or more data events into a single data event which
	 * will be passed to the next stage of stream
	 */
	public void testCoalesceUsingBuffer() {

	}

}
