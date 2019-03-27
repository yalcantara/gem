package com.gem.config.ws.services;

import javax.ws.rs.BadRequestException;

import static com.gem.commons.Checker.checkParamNotNull;

public class Verifier {

	private static void checkChars(String name, String str) {
		
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			
			if (((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.'
					|| c == '-') == false) {
				throw new BadRequestException("Invalid " + name
						+ ". Only letter, digit, dot(.) and dash(-) characters are allowed.");
			}
		}
		
	}

	// Check some basis:
	// 1 - Start with letter or digit.
	// 2 - Allows only: letters, digits, dots(.) and dashes(-).
	// 3 - The dot(.) and dash(-) characters can not be sequential.
	// 4 - It must end with a letter of digit.
	public static String checkId(String name, String id) {
		checkParamNotNull("name", name);
		checkParamNotNull("id", id);
		id = id.toLowerCase().strip();

		// 1 - Start with [a-z] or [0-9]
		char firstChar = id.charAt(0);
		if (((firstChar >= 'a' && firstChar <= 'z')
				|| (firstChar >= '0' && firstChar <= '9')) == false) {
			throw new BadRequestException(
					"Invalid " + name + ". It must start with a letter or digit.");
		}

		// 2 - Allows only: letters, digits, dots(.) and dashes(-).
		checkChars("name", id);

		// 3 - The dot(.) and dash(-) characters can not be sequential.
		for (int i = 0; i < id.length(); i++) {
			char crt = id.charAt(i);
			if (i > 0) {
				char prev = id.charAt(i - 1);

				if (crt == '.' || crt == '-') {
					if (prev == '.' || prev == '-') {
						throw new BadRequestException("Invalid " + name
								+ ". It can not have two consecutive special characters ('.' or '-').");
					}
				}
			}
		}

		// 4 - ending with [a-z] or [0-9]
		char lastChar = id.charAt(id.length() - 1);
		if (((lastChar >= 'a' && lastChar <= 'z')
				|| (lastChar >= '0' && lastChar <= '9')) == false) {
			throw new BadRequestException(
					"Invalid " + name + ". It must end with a letter or digit.");
		}
		
		return id;
	}
}
