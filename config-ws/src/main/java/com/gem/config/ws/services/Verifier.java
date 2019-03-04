package com.gem.config.ws.services;

import static com.gem.commons.Checker.checkParamNotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.BadRequestException;

public class Verifier {
	
	// Check some basis:
	// 1 - Start with letter or digit.
	// 2 - Allows '.' and '-' after the first character
	// But it needs extra checking because 'p..p' can be allowed.
	private static final Pattern REGEX = Pattern.compile("[a-z0-9]{1,1}[a-z\\.0-9-]{0,20}");
	
	public static String pack(String str) {
		checkParamNotNull("str", str);

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
					|| c == '.' || c == '-') {
				sb.append(Character.toLowerCase(c));
			}
		}

		return sb.toString();
	}

	public static void checkId(String name, String id) {
		checkParamNotNull("id", id);

		Matcher m = REGEX.matcher(name);
		
		if (m.find()) {
			String found = m.group();
			
			if (found.equals(id)) {
				throw new BadRequestException("Invalid " + name
						+ ". It must start with a letter or digit, and only allows '.' and '-' as special characters.");
			}
		}

		for (int i = 0; i < name.length(); i++) {
			char crt = name.charAt(i);
			if (i > 0) {
				char prev = name.charAt(i - 1);
				
				if (crt == '.' || crt == '-') {
					if (prev == '.' || prev == '-') {
						throw new BadRequestException("Invalid " + name
								+ ". It can not have two consecutive special characters ('.' or '-').");
					}
				}
			}
		}
	}
}
