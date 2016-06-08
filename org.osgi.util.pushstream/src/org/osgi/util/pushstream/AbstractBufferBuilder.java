package org.osgi.util.pushstream;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

abstract class AbstractBufferBuilder<R, T, U extends BlockingQueue<PushEvent< ? extends T>>>
		implements BufferBuilder<R,T,U> {

	protected Executor				worker;
	protected int					concurrency;
	protected PushbackPolicy<T,U>	backPressure;
	protected QueuePolicy<T,U>		bufferingPolicy;
	protected U						buffer;

	@Override
	public BufferBuilder<R,T,U> withBuffer(U queue) {
		this.buffer = queue;
		return this;
	}

	@Override
	public BufferBuilder<R,T,U> withQueuePolicy(
			QueuePolicy<T,U> queuePolicy) {
		this.bufferingPolicy = queuePolicy;
		return this;
	}

	@Override
	public BufferBuilder<R,T,U> withQueuePolicy(
			QueuePolicyOption queuePolicyOption) {
		this.bufferingPolicy = queuePolicyOption.getPolicy();
		return this;
	}

	@Override
	public BufferBuilder<R,T,U> withPushbackPolicy(
			PushbackPolicy<T,U> pushbackPolicy) {
		this.backPressure = pushbackPolicy;
		return this;
	}

	@Override
	public BufferBuilder<R,T,U> withPushbackPolicy(
			PushbackPolicyOption pushbackPolicyOption, long time) {
		this.backPressure = pushbackPolicyOption.getPolicy(time);
		return this;
	}

	@Override
	public BufferBuilder<R,T,U> withParallelism(int parallelism) {
		this.concurrency = parallelism;
		return this;
	}

	@Override
	public BufferBuilder<R,T,U> withExecutor(Executor executor) {
		this.worker = executor;
		return this;
	}
}
