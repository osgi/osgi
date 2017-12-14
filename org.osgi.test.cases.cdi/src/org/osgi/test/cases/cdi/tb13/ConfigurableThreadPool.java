package org.osgi.test.cases.cdi.tb13;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.Produces;

import org.osgi.service.cdi.annotations.Configuration;

public class ConfigurableThreadPool {

	@interface PoolConfig {
		String pool_name();

		int min_threads() default 2;

		int max_threads() default 10;

		long keep_alive_timeout() default 10;
	}

	@Produces
	Executor createPool(@Configuration PoolConfig poolConfig) {

		ThreadPoolExecutor executor = new ThreadPoolExecutor(poolConfig.min_threads(), poolConfig.max_threads(),
				poolConfig.keep_alive_timeout(), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
				threadFactory(poolConfig.pool_name()));

		executor.prestartCoreThread();

		return executor;
	}

	ThreadFactory threadFactory(String poolName) {
		return Executors.defaultThreadFactory();
	}

}
