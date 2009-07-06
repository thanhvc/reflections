package org.reflections.adapters;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public interface ForkJoiner {
	public <K, V> List<V> transform(Iterable<K> source, final Function<K, V> function, Class<K> keyClass) throws InterruptedException, ExecutionException;
	public <K, V> List<V> transform(Iterator<K> source, final Function<K, V> function, Class<K> keyClass) throws InterruptedException, ExecutionException;
}