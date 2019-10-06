package com.gem.commons;

import java.sql.Time;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.gem.commons.Checker.*;

public final class TimedLazy<T> {


	private static final int SECOND = 1000;
	public static final int MIN_LIVELINESS = 1 * SECOND;
	public static final int MAX_LIVELINESS = (int) TimeUnit.DAYS.toMillis(7);

	public static <T> TimedLazy<T> wrap(Retriever<T> ret, int duration, TimeUnit unit) {
		checkParamNotNull("unit", unit);
		return new TimedLazy<>(ret, duration, unit);
	}

	public static <T> TimedLazy<T> wrap(Retriever<T> ret, Duration duration) {
		checkParamNotNull("ret", ret);
		checkParamNotNull("duration", duration);
		return new TimedLazy<>(ret, duration);
	}

	public static <T> TimedLazy<T> wrap(Retriever<T> ret, long liveliness) {
		checkParamNotNull("ret", ret);
		checkParamNotNull("liveliness", liveliness);
		return new TimedLazy<>(ret, liveliness);
	}


	private final Lazy<T> lazy;
	private final int liveliness;

	private final Object lock = new Object();


	private TimedLazy(Retriever<T> ret, Duration duration){
		this(ret, duration.toMillis());
	}

	private TimedLazy(Retriever<T> ret, long amount, TimeUnit unit){
		this(ret, unit.toMillis(amount));
	}

	private TimedLazy(Retriever<T> ret, long time) {
		checkParamNotNull("ret", ret);
		checkParamIsPositive("time", time);

		if (time < MIN_LIVELINESS) {
			throw new IllegalArgumentException("The minimum liveliness is 1000 milli seconds. Got duration: "
                    + time + ".");
		}

		if (time > MAX_LIVELINESS) {
			throw new IllegalArgumentException("The total time for " + time + " is higher than 7 days.");
		}

		lazy = Lazy.wrap(ret);
		liveliness = (int) time;
	}

	public Tuple<T, Long> peek() {
		return lazy.peek();
	}

	public boolean isDead() {
		return remaining() == 0;
	}

	public long remaining() {

		Long snapshot = lazy.getRetrievalDate();

		if (snapshot == null) {
			return -1;
		}

		var now = System.currentTimeMillis();
		var diff = now - snapshot;

		var remaining = liveliness - diff;

		if (remaining < 0) {
			return 0;
		}

		return remaining;
	}


	public T get() {

		synchronized (lock) {

			Tuple<T, Long> peek = lazy.peek();

			if (peek == null) {
				return lazy.get(true);
			}

			long snapshot = peek.second();

			var now = System.currentTimeMillis();
			var diff = now - snapshot;

			if (diff > liveliness) {
				return lazy.get(true);
			}

			return peek.first();
		}
	}


}
