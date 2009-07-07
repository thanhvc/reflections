package org.reflections.adapters;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * A very naive implementation of parallel task computation using a thread pool.
 * 
 * @author justinsb
 * 
 */
public final class SimpleForkJoiner implements ForkJoiner {
	private final ExecutorService executorService;

	public static SimpleForkJoiner buildDefault() {
		// TODO: Base off number of CPUs
		int fixedThreadCount = Runtime.getRuntime().availableProcessors();

		ExecutorService executorService = Executors.newFixedThreadPool(fixedThreadCount);
		return new SimpleForkJoiner(executorService);
	}

	public SimpleForkJoiner(ExecutorService executorService) {
		super();
		this.executorService = executorService;
	}

	public <K, V> List<V> transform(Iterable<K> source, final Function<K, V> function, Class<K> keyClass) throws InterruptedException, ExecutionException {
		return transform(source.iterator(), function, keyClass);
	}

	public <K, V> List<V> transform(Iterator<K> source, final Function<K, V> function, Class<K> keyClass) throws InterruptedException, ExecutionException {
		List<V> destination = Lists.newArrayList();
		return transform(source, function, destination);
	}

	public <K, V, CollectionType extends Collection<V>> CollectionType transform(Iterator<K> source, final Function<K, V> function, CollectionType destination) throws InterruptedException, ExecutionException {
		final List<Future<V>> futures = Lists.newArrayList();

		while (source.hasNext()) {
			final K nextKey = source.next();
			Future<V> future = executorService.submit(new Callable<V>() {
				public V call() throws Exception {
					return function.apply(nextKey);
				}
			});

			futures.add(future);
		}

		for (Future<V> future : futures) {
			V result = future.get();
			if (destination != null) {
				destination.add(result);
			}
		}

		return destination;
	}

}
