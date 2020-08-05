package org.osgi.test.cases.pushstream.junit;

import java.io.Closeable;

import org.osgi.util.pushstream.PushEvent;
import org.osgi.util.pushstream.PushEventConsumer;
import org.osgi.util.pushstream.PushEventSource;

import junit.framework.TestCase;


/**
 * Section 706.3 The Push Stream
 */
@SuppressWarnings({
		"rawtypes", "unchecked"
})
public abstract class PushStreamComplianceTest extends TestCase {

	static class ExtGeneratorStatus {
		public final long		bp;
		public final PushEvent	event;
		public final Throwable	failure;

		ExtGeneratorStatus(long backpressure, PushEvent last,
				Throwable failure) {
			this.bp = backpressure;
			this.event = last;
			this.failure = failure;
		}

		@Override
		public String toString() {
			// pretty printing
			StringBuilder builder = new StringBuilder();
			builder.append("--------------------------\n");
			builder.append("back pressure : ");
			builder.append(bp);
			builder.append("\n");
			builder.append("failure : ");
			builder.append(
					failure != null ? failure.getClass().getName() : "null");
			builder.append(failure != null ? " [" : "");
			builder.append(failure != null ? failure.getMessage() : "");
			builder.append(failure != null ? "]" : "");
			builder.append("\n");
			builder.append("Event : ");
			builder.append(event != null ? event.getType().name() : "null");
			builder.append(event != null ? " [Failure : " : "");
			builder.append(event != null
					? (event.getType().equals(PushEvent.EventType.ERROR)
							? event.getFailure() : "null")
					: "");
			builder.append(event != null ? "]" : "");
			builder.append(event != null ? " [Data : " : "");
			builder.append(event != null
					? (event.getType().equals(PushEvent.EventType.DATA)
							? event.getData() : "null")
					: "");
			builder.append(event != null ? "]" : "");
			builder.append(event != null ? " [Terminal : " : "");
			builder.append(event != null ? event.isTerminal() : "");
			builder.append(event != null ? "]" : "");
			builder.append("\n");
			builder.append("--------------------------\n");
			return builder.toString();
		}
	}

	static class ExtGenerator implements PushEventSource<Integer> {

		int count = 10;
		boolean				closeCalled	= false;
		long				minBackPressure		= 0;
		long				maxBackPressure		= 0;
		boolean				fixedBackPressure	= true;
		long				start				= 0;
		long				stop				= 0;
		ExtGeneratorStatus	status		= null;
		Thread				thread;

		public ExtGenerator(int i) {
			this.count = i;
		}

		public ExtGeneratorStatus status() {
			return status;
		}

		public void status(ExtGeneratorStatus status) {

			if (this.status == null) {
				this.minBackPressure = status.bp;
				this.maxBackPressure = status.bp;
			} else {
				this.fixedBackPressure = (this.fixedBackPressure
						&& status.bp == this.status.bp);

				this.minBackPressure = (status.bp < this.minBackPressure)
						? status.bp : this.minBackPressure;
				this.maxBackPressure = (status.bp > this.maxBackPressure)
						? status.bp : this.maxBackPressure;
			}

			// System.out.println(status.bp);
			this.status = status;
		}

		public boolean fixedBackPressure() {
			return this.fixedBackPressure;
		}

		public long maxBackPressure() {
			return this.maxBackPressure;
		}

		public long minBackPressure() {
			return this.minBackPressure;
		}

		public Thread getExecutionThread() {
			return this.thread;
		}

		public boolean closeCalled() {
			return closeCalled;
		}

		@Override
		public Closeable open(PushEventConsumer< ? super Integer> l) {
			thread = new Thread("generator") {

				Throwable	failure	= null;
				PushEvent	event	= null;
				long		bp		= 0;

				// keep a trace of last thrown exception, propagated event
				// and received back pressure value
				void pushStatus() {
					status(new ExtGeneratorStatus(bp, event, failure));
				}

				@Override
				public void run() {

					boolean closed = false;
					start = System.currentTimeMillis();

					try {
						try {
							for (int i = 0; i < count && !Thread.currentThread()
									.isInterrupted(); i++) {

								event = PushEvent.data(i);
								bp = l.accept(event);

								if (bp < 0)
									return;

								pushStatus();

								if (bp > 0) {
									Thread.sleep(bp);
								}
							}
						} catch (InterruptedException e) {

							Thread.interrupted();
							// if close method called just send a close event
							// otherwise handle it as any other exception
							if (!closeCalled) {
								failure = e;
								event = PushEvent.error(e);
								l.accept(event);
								pushStatus();
								closed = true;
							}
						} catch (Exception e) {

							failure = e;
							event = PushEvent.error(e);
							l.accept(event);
							pushStatus();
							closed = true;

						} finally {
							if (!closed) {
								event = PushEvent.close();
								l.accept(event);
								pushStatus();
							}
							stop = System.currentTimeMillis();
						}
					} catch (Exception e) {
						e.printStackTrace();
						stop = System.currentTimeMillis();
					}
				}
			};
			thread.start();
			return () -> {
				closeCalled = true;
				thread.interrupt();
			};
		}
	}
}
