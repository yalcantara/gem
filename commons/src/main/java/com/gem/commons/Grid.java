package com.gem.commons;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Grid {
	
	private static final int MAX_PRINT_ROWS = 200;
	private static final int MAX_PRINT_COLS = 50;
	
	private static final int MAX_DISPLAY_LENGTH = 60;

	private final int rows;
	private final int cols;
	
	private List<String> header;
	private List<List<String>> data;
	
	public Grid(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
	}
	
	public Grid(List<String> values) {
		this.rows = values.size();
		this.cols = 1;
		
		int i = 0;
		for (String v : values) {
			set(i, 0, v);
			i++;
		}
	}

	@SuppressWarnings("rawtypes")
	public static Grid wrap(List<Map> values) {
		
		Set<String> colNames = new LinkedHashSet<>();
		
		for (Map m : values) {
			
			for (Object k : m.keySet()) {
				String name = (k == null) ? null : k.toString();

				colNames.add(name);
			}
		}
		
		int rows = values.size();
		int cols = colNames.size();

		Grid g = new Grid(rows, cols);
		
		int c = 0;
		for (String col : colNames) {
			
			g.header(c, col);
			c++;
		}
		
		int i = 0;
		for (Map m : values) {
			
			int j = 0;
			for (String n : colNames) {

				if (m.containsKey(n)) {
					Object val = m.get(n);

					g.set(i, j, val);
				}
				j++;
			}
			i++;
		}
		
		return g;
	}

	public Grid(Set<String> values) {
		this.rows = values.size();
		this.cols = 1;
		
		int i = 0;
		for (String v : values) {
			set(i, 0, v);
			i++;
		}
	}

	public String header(int j) {
		checkColIndex(j);
		if (header == null) {
			return null;
		}
		
		String val = header.get(j);
		
		if (val == null) {
			return null;
		}

		return val;
	}
	
	public void header(int j, String val) {
		checkColIndex(j);
		if (header == null) {
			header = newArray(cols);
		}
		
		header.set(j, val);
	}

	public String get(int i, int j) {
		checkIndex(i, j);

		if (data == null) {
			return null;
		}
		
		List<String> list = data.get(i);
		
		if (list == null) {
			return null;
		}
		
		String val = list.get(j);
		
		return val;
	}

	public void set(int i, int j, Object val) {
		if (val == null) {
			set(i, j, (String) null);
			return;
		}
		
		if (val instanceof String) {
			set(i, j, (String) val);
			return;
		}
		
		if (val instanceof Number) {
			set(i, j, (Number) val);
			return;
		}
		
		String str = val.toString();
		
		set(i, j, str);
	}

	public void set(int i, int j, Number val) {
		if (val == null) {
			set(i, j, (String) null);
			return;
		}
		
		String fmt = format(val);
		set(i, j, fmt);
	}
	
	public void set(int i, int j, String val) {
		checkIndex(i, j);

		if (data == null) {
			data = newArray(rows);
		}
		
		List<String> list = data.get(i);
		
		if (list == null) {
			list = newArray(cols);
			data.set(i, list);
		}

		list.set(j, val);
	}

	private void checkIndex(int i, int j) {
		checkRowIndex(i);
		checkColIndex(j);
	}
	
	private void checkRowIndex(int i) {
		
		if (i < 0) {
			throw new IllegalArgumentException(
					"The row index can not be less than 0. Got: " + i + ".");
		}
		
		if (i >= rows) {
			throw new IndexOutOfBoundsException(
					"The row index is out of bounds. Expected a value less than " + rows
							+ ", but got: " + i + " instead.");
		}
	}
	
	private void checkColIndex(int j) {

		if (j < 0) {
			throw new IllegalArgumentException(
					"The col index can not be less than 0. Got: " + j + ".");
		}

		if (j >= cols) {
			throw new IndexOutOfBoundsException(
					"The column index is out of bounds. Expected a value less than " + cols
							+ ", but got: " + j + " instead.");
		}
	}

	public void print() {
		print(System.out, MAX_PRINT_ROWS, MAX_PRINT_COLS);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(1024);
		print(sb, MAX_PRINT_ROWS, MAX_PRINT_COLS);
		return sb.toString();
	}

	private String format(Number n) {
		if (n == null) {
			throw new NullPointerException("The 'n' parameter can not be null.");
		}
		
		if (n instanceof Double) {
			Double dval = (Double) n;
			if (dval.isNaN()) {
				return "(NaN)";
			}
		}
		
		if (n instanceof Float) {
			Float fval = (Float) n;
			if (fval.isNaN()) {
				return "(NaN)";
			}
			
			if (fval.isInfinite() && fval < 0) {
				return "(-Infinite)";
			}
		}
		
		DecimalFormat nf = new DecimalFormat();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);

		String str = nf.format(n);

		return str;
	}

	private String pack(String str) {
		if (str == null) {
			return "(null)";
		}

		if (str.length() > MAX_DISPLAY_LENGTH) {
			return str.substring(0, MAX_DISPLAY_LENGTH);
		}

		return str;
	}

	private void print(Appendable out, int maxRows, int maxCols) {
		int mr = (maxRows < rows) ? maxRows : rows;
		int mc = (maxCols < cols) ? maxCols : cols;

		try {
			if (mr < rows || mc < cols) {
				out.append("Grid " + rows + "x" + cols + "  (truncated)\n");
			} else {
				out.append("Grid " + rows + "x" + cols + "\n");
			}

			int[] maxLength = new int[mc];

			for (int j = 0; j < mc; j++) {

				String str = pack(header(j));

				maxLength[j] = Math.max(maxLength[j], str.length());
			}

			for (int j = 0; j < mc; j++) {
				for (int i = 0; i < mr; i++) {

					String str = pack(get(i, j));

					maxLength[j] = Math.max(maxLength[j], str.length());
				}
			}

			line(out, maxLength, mc);
			out.append('\n');
			// Headers
			// =================================================================
			for (int j = 0; j < mc; j++) {
				if (j == 0) {
					out.append("¦ ");
				} else {
					out.append(" ");
				}
				String h = pack(header(j));
				int leading = maxLength[j] - h.length();

				for (int s = 0; s < leading; s++) {
					out.append(" ");
				}

				out.append(h);
				out.append(" ¦");
			}
			// =================================================================
			out.append('\n');
			line(out, maxLength, mc);
			out.append('\n');
			for (int i = 0; i < mr; i++) {
				for (int j = 0; j < mc; j++) {
					if (j == 0) {
						out.append("¦ ");
					} else {
						out.append(" ");
					}
					String str = pack(get(i, j));
					int leading = maxLength[j] - str.length();

					for (int s = 0; s < leading; s++) {
						out.append(" ");
					}

					out.append(str);
					out.append(" ¦");
				}
				out.append("\n");
			}
			line(out, maxLength, mc);
			out.append("\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void line(Appendable out, int[] maxLength, int maxCols) throws IOException {

		for (int j = 0; j < maxCols; j++) {
			if (j == 0) {
				out.append("+-");
			} else {
				out.append("-");
			}

			for (int s = 0; s < maxLength[j]; s++) {
				out.append("-");
			}

			out.append("-+");
		}
	}
	
	private <T> ArrayList<T> newArray(int length) {
		ArrayList<T> arr = new ArrayList<>(length);
		
		for (int i = 0; i < length; i++) {
			arr.add(null);
		}
		
		return arr;
	}
}
