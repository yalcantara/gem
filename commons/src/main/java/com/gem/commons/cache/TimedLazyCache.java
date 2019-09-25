package com.gem.commons.cache;

import com.gem.commons.Lazy;
import com.gem.commons.Retriever;
import com.gem.commons.TimedLazy;
import com.gem.commons.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.gem.commons.Checker.*;
import static java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import static java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class TimedLazyCache<K extends Serializable, V> {

	private static final Logger log = LoggerFactory.getLogger(TimedLazyCache.class);

	//Max liveliness of a week (7  days)
	public static final int MAX_LIVELINESS = (int) TimeUnit.DAYS.toMillis(7);


	public static void main(String[] args) {

		Map<String, Integer> map = new LinkedHashMap<>();
		map.put("pop", 1);
		KeyRetriever<String, Integer> ret = (k) -> {
			Utils.sleep(10, TimeUnit.SECONDS);
			return map.get(k);
		};

		TimedLazyCache<String, Integer> cache = new TimedLazyCache<>(ret, 5, 2, TimeUnit.SECONDS);

		log.info("" + cache.get("pop"));
		log.info("" + cache.get("pop"));
		Utils.sleep(1, TimeUnit.SECONDS);
		log.info("" + cache.get("pop"));
		log.info("" + cache.get("pop"));
		Utils.sleep(1, TimeUnit.SECONDS);
		log.info("" + cache.get("pop"));
		log.info("" + cache.get("pop"));
	}

	private final ConcurrentHashMap<K, TimedLazy<V>> map;

	private final KeyRetriever<K, V> ret;
	private final int max;

	private final int duration;
	private final TimeUnit unit;
	private final int liveliness;

	private final Timer timer;

	public TimedLazyCache(KeyRetriever<K, V> ret, int max, int duration, TimeUnit unit) {
		checkParamNotNull("ret", ret);
		checkParamIsPositive("max", max);
		checkParamIsPositive("duration", duration);
		checkParamNotNull("unit", unit);

		long liveliness = unit.toMillis(duration);

		if (liveliness > MAX_LIVELINESS) {
			throw new IllegalArgumentException("The total time for " + unit + "(" + duration +
					")" +
					" " +
					"is higher than 7 days.");
		}

		this.ret = ret;
		this.max = max;
		this.duration = duration;
		this.unit = unit;
		this.liveliness = (int) liveliness;
		map = new ConcurrentHashMap<>();

		timer = new Timer(this.liveliness, (e) -> timerTrigger(e));
	}

	private void timerTrigger(ActionEvent e) {

		if (map.size() > max) {

			//TODO find a way to remove just some items if needed.
			//If the size is 33 and max 30, then just the 3 oldest.
			map.entrySet().removeIf(entry -> entry.getValue().isDead());
		}
	}

	public V get(K key) {
		Retriever<V> r = () -> ret.retrieve(key);

		TimedLazy<V> lazy = TimedLazy.wrap(r, duration, unit);
		TimedLazy<V> crt = map.putIfAbsent(key, lazy);
		if (crt == null) {
			return lazy.get();
		}

		return crt.get();
	}

	public int size() {
		return map.size();
	}

	public int getMax() {
		return max;
	}

	public int getLiveliness() {
		return liveliness;
	}
}
