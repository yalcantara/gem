package com.gem.commons;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * An object that represents a Date without time or zone information, but always assumes UTC locale. This class is perfect
 * for storing date objects in a database that requires standard UTC time.
 */
public class FieldDate implements Serializable {

	private final int year;
	private final int month;
	private final int day;

	public static FieldDate now() {
		return new FieldDate(Instant.now().atOffset(ZoneOffset.UTC));
	}

	public static FieldDate of(int year, int month, int day){
		return new FieldDate(year, month, day);
	}


	private FieldDate(int year, int month, int day){
		this(OffsetDateTime.of(year, month, day, 0, 0, 0,0, ZoneOffset.UTC));
	}

	private FieldDate(OffsetDateTime d) {
		this.year = d.getYear();
		this.month = d.getMonth().getValue();
		this.day = d.getDayOfMonth();
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


	public OffsetDateTime toOffsetDateTime(){
		return OffsetDateTime.of(year, month, day, 0, 0, 0, 0, ZoneOffset.UTC);
	}

	public FieldDate minus(long amount, ChronoUnit unit) {
		var diff = toOffsetDateTime().minus(amount, unit);
		return new FieldDate(diff);
	}

	public Json toJson(){
		var ans = new Json();
		ans.put("year", year);
		ans.put("month", month);
		ans.put("day", day);

		return ans;
	}

	public Instant toInstant(){
		return toOffsetDateTime().toInstant();
	}

	public Date toDate(){
		return Date.from(toInstant());
	}

	@Override
	public String toString() {
		return "FieldDate{" +
				"year=" + year +
				", month=" + month +
				", day=" + day +
				'}';
	}
}
