package com.gem.commons;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static com.gem.commons.Checker.checkParamNotNull;

public class Props {


	public static Props fromClassPathResource(String path){
		Resource resource = new ClassPathResource(path);
		Properties p;
		try {
			p = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return fromFile(path);
	}

	public static Props fromFile(String path){
		Properties p = new Properties();
		FileInputStream fis;
		try {
			File f = new File(path);
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		try {
			p.load(fis);
		} catch (IOException e){
			throw new RuntimeException(e);
		}finally {
			Utils.close(fis);
		}

		return fromProperties(p);
	}

	public static Props fromProperties(Properties p) {

		Properties ans = new Properties();
		for (Map.Entry e : p.entrySet()) {

			Object k = e.getKey();
			Object v = e.getValue();

			if (v instanceof String) {
				v = ((String) v).strip();
			}

			ans.put(k, v);
		}

		return new Props(p);
	}


	private final Properties props;

	public Props(Properties props) {
		checkParamNotNull("props", props);
		this.props = new Properties(props);
	}

	public void check(String key) {
		checkParamNotNull("key", key);
		String val = get(key);
		if (val == null) {
			throw new ConfigurationException("The property '" + key + "' is not defined.");
		}
	}

	public String get(String key) {
		checkParamNotNull("key", key);
		return props.getProperty(key);
	}

	public String checkAndGet(String key) {
		check(key);
		return get(key);
	}

	public int checkAndGetInt(String key){
		String val = checkAndGet(key);
		return convert(key, val);
	}

	public String get(String key, String defaultValue) {
		String val = props.getProperty(key);
		if (val == null) {
			return defaultValue;
		}

		return val;
	}

	public Integer getInteger(String key, Integer defaultValue) {
		String val = props.getProperty(key);
		if (val == null) {
			return defaultValue;
		}

		return convert(key, val);
	}

	private static Integer convert(String key, String val){
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException ex) {
			throw new ConfigurationException(
					"Could not parse property '" + val + "' with key '" + key + "' to an integer."
					, ex);
		}
	}
}
