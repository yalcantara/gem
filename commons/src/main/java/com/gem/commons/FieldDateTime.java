package com.gem.commons;

import java.io.Serializable;
import java.sql.Time;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * An object that represents a Date with time, but not the zone information as it always assumes UTC time zone. This
 * class is perfect for storing date and time objects in a database that requires standard UTC time.
 */
public class FieldDateTime implements Serializable {


	private final int year;
	private final int month;
	private final int day;

	private final int hour;
	private final int minute;
	private final int second;
	private final int milli;


	public static void main(String[] args) {
		System.out.println(Instant.now().toEpochMilli());
		System.out.println(new Date().getTime());
	}

	public static FieldDateTime now(){
		OffsetDateTime d = Instant.now().atOffset(ZoneOffset.UTC);
		return new FieldDateTime(d);
	}

	private FieldDateTime(int year, int month, int day, int hour, int minute, int seconds, int millis){
		this(OffsetDateTime.of(year, month, day, hour, minute, seconds, (int)TimeUnit.MILLISECONDS.toNanos(millis), ZoneOffset.UTC));
	}

	private FieldDateTime(OffsetDateTime d){

		this.year = d.getYear();
		this.month =  d.getMonth().getValue();
		this.day = d.getDayOfMonth();


		this.hour = d.getHour();
		this.minute = d.getMinute();
		this.second = d.getSecond();
		this.milli = (int)TimeUnit.NANOSECONDS.toMillis(d.getNano());
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getSecond() {
		return second;
	}

	public int getMilli() {
		return milli;
	}


	public FieldDate toFieldDate(){
		return FieldDate.of(year, month, day);
	}


	public OffsetDateTime toOffsetDateTime(){
		return OffsetDateTime.of(year, month, day, hour, minute, second, (int)TimeUnit.MILLISECONDS.toNanos(milli), ZoneOffset.UTC);
	}

	public Instant toInstant(){
		return toOffsetDateTime().toInstant();
	}

	public Date toDate(){
		return Date.from(toInstant());
	}


	public FieldDateTime minus(long amount, ChronoUnit unit) {
		var diff = toOffsetDateTime().minus(amount, unit);
		return new FieldDateTime(diff);
	}

	@Override
	public String toString() {
		return "FieldDateTime{" +
				"year=" + year +
				", month=" + month +
				", day=" + day +
				", hour=" + hour +
				", minute=" + minute +
				", second=" + second +
				", milli=" + milli +
				'}';
	}
}
