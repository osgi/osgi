package org.osgi.util.pushstream;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

class PushStreamBuilderImpl<T, U extends BlockingQueue<PushEvent< ? extends T>>>
		extends AbstractBufferBuilder<PushStream<T>,T,U>
		implements PushStreamBuilder<T,U> {

	private final PushStreamProvider		psp;
	private final PushEventSource<T>		eventSource;
	private final Executor					previousExecutor;
	private final ScheduledExecutorService	previousScheduler;

	private boolean							unbuffered;

	PushStreamBuilderImpl(PushStreamProvider psp, Executor defaultExecutor,
			ScheduledExecutorService defaultScheduler, PushEventSource<T> eventSource) {
		this.psp = psp;
		this.previousExecutor = defaultExecutor;
		this.previousScheduler = defaultScheduler;
		this.eventSource = eventSource;
		this.worker = defaultExecutor;
	}

	@Override
	public PushStreamBuilder<T,U> withBuffer(U queue) {
		unbuffered = false;
		return (PushStreamBuilder<T,U>) super.withBuffer(queue);
	}

	@Override
	public PushStreamBuilder<T,U> withQueuePolicy(
			QueuePolicy<T,U> queuePolicy) {
		unbuffered = false;
		return (PushStreamBuilder<T,U>) super.withQueuePolicy(queuePolicy);
	}

	@Override
	public PushStreamBuilder<T,U> withQueuePolicy(
			QueuePolicyOption queuePolicyOption) {
		unbuffered = false;
		return (PushStreamBuilder<T,U>) super.withQueuePolicy(
				queuePolicyOption);
	}

	@Override
	public PushStreamBuilder<T,U> withPushbackPolicy(
			PushbackPolicy<T,U> pushbackPolicy) {
		unbuffered = false;
		return (PushStreamBuilder<T,U>) super.withPushbackPolicy(
				pushbackPolicy);
	}

	@Override
	public PushStreamBuilder<T,U> withPushbackPolicy(
			PushbackPolicyOption pushbackPolicyOption, long time) {
		unbuffered = false;
		return (PushStreamBuilder<T,U>) super.withPushbackPolicy(
				pushbackPolicyOption, time);
	}

	@Override
	public PushStreamBuilder<T,U> withParallelism(int parallelism) {
		unbuffered = false;
		return (PushStreamBuilder<T,U>) super.withParallelism(parallelism);
	}

	@Override
	public PushStreamBuilder<T,U> withExecutor(Executor executor) {
		return (PushStreamBuilder<T,U>) super.withExecutor(executor);
	}

	@Override
	public PushStreamBuilder<T,U> withScheduler(
			ScheduledExecutorService scheduler) {
		return (PushStreamBuilder<T,U>) super.withScheduler(scheduler);
	}

	@Override
	public PushStreamBuilder<T,U> unbuffered() {
		unbuffered = true;
		return this;
	}

	@Override
	public PushStream<T> build() {
		Executor workerToUse = worker == null ? previousExecutor : worker;
		ScheduledExecutorService timerToUse = timer == null ? previousScheduler
				: timer;

		if (unbuffered) {
			return psp.createUnbufferedStream(eventSource, workerToUse,
					timerToUse);
		} else {
			return psp.createStream(eventSource, concurrency, workerToUse,
					timerToUse, buffer,
					bufferingPolicy, backPressure);
		}
	}
}
