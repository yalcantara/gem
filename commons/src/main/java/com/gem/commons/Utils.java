package com.gem.commons;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;

public class Utils {


	public static String strip(String str){
		if(str == null){
			return null;
		}

		var s = str.strip();

		if(s.isEmpty() || s.isBlank()){
			return null;
		}

		return s;
	}
	
	public static String getStackTrace(Throwable t) {
		if (t == null) {
			return "null";
		}
		
		// exception stack trace is mostly large.
		StringWriter sw = new StringWriter(512);
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		
		String ans = sw.toString();

		return ans;
	}

	private static String fill(int r) {
		if (r < 10) {
			return "0" + r;
		}

		return String.valueOf(r);
	}

	private static String millifill(int r) {
		if (r < 10) {
			return "00" + r;
		}

		if (r < 100) {
			return "0" + r;
		}
		return String.valueOf(r);
	}

	public static String toFlipedFormat(Instant date) {

		OffsetDateTime d = OffsetDateTime.ofInstant(date, ZoneOffset.UTC);
		
		return d.getYear() + "-" + fill(d.getMonth().getValue()) + "-" + fill(d.getDayOfMonth())
				+ " " + fill(d.getHour()) + ":" + fill(d.getMinute()) + ":" + fill(d.getSecond())
				+ "." + millifill(d.get(ChronoField.MILLI_OF_SECOND));
	}
}
