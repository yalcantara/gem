package com.gem.commons;

import java.io.Closeable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.gem.commons.Checker.checkParamNotEmpty;
import static com.gem.commons.Checker.checkParamNotNull;

public class Utils {

	private static Pattern MARKS_PATTERN = Pattern
			.compile("\\p{InCombiningDiacriticalMarks}+");


	public static void println(String str){
		System.out.println(str);
	}
	public static void println(double val){
		System.out.println(val);
	}

	public static void println(Json json){
		System.out.println(json);
	}


	public static boolean containsField(Class<?> clazz, String field){
		checkParamNotNull("clazz", clazz);
		checkParamNotEmpty("field", field);


		Field[] arr = clazz.getDeclaredFields();
		if(arr == null || arr.length == 0){
			return false;
		}

		for(Field f:arr){
			if(field.equals(f.getName())){
				return true;
			}
		}

		return false;
	}


	public static Class<?> getFieldType(Class<?> clazz, String field){
		checkParamNotNull("clazz", clazz);
		checkParamNotEmpty("field", field);

		Field[] arr = clazz.getDeclaredFields();
		if(arr == null || arr.length == 0){
			return null;
		}

		for(Field f:arr){
			if(field.equals(f.getName())){
				return f.getType();
			}
		}

		return null;
	}

	public static String steam(String str) {
		if (str == null) {
			return null;
		}

		str = str.strip();

		str = str.replace("\r\n", "").replace('\n', '\0').trim();
		if (str.isEmpty()) {
			return "";
		}

		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		str = MARKS_PATTERN.matcher(str).replaceAll("");
		str = str.toLowerCase();
		return str;
	}

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


	public static void sleep(int duration, TimeUnit unit){

		try {
			unit.sleep(duration);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toFlipedFormat(Instant date){
		OffsetDateTime d = OffsetDateTime.ofInstant(date, ZoneOffset.UTC);
		return toFlipedFormat(d);
	}


	public static String toFlipedFormat(FieldDate d){
		return toFlipedFormat(LocalDate.of(d.getYear(), d.getMonth(), d.getDay()));
	}

	public static String toFlipedFormat(OffsetDateTime d) {

		return d.getYear() + "-" + fill(d.getMonth().getValue()) + "-" + fill(d.getDayOfMonth())
				+ " " + fill(d.getHour()) + ":" + fill(d.getMinute()) + ":" + fill(d.getSecond())
				+ "." + millifill(d.get(ChronoField.MILLI_OF_SECOND));
	}


	public static String toFlipedFormat(LocalDate d) {
		return d.getYear() + "-" + fill(d.getMonth().getValue()) + "-" + fill(d.getDayOfMonth());
	}


	public static OffsetDateTime nowAtUTC(){
		return OffsetDateTime.now(ZoneOffset.UTC);
	}


	public static void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception ex) {
				// ignored
			}
		}
	}
}
