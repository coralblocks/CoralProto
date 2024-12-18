/* 
 * Copyright 2015-2024 (c) CoralBlocks LLC - http://www.coralblocks.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.coralblocks.coralproto.util;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A perl-like regex support for Java.
 */
public class RegexUtils {

	private static final String TOKEN1 = "A1cxxZ";

	private static final String TOKEN2 = "Bbsd423xx12asdf44xT";

	private static final char DEFAULT_ESCAPE_CHAR = '\\';

	private final String s;

	private final String escPattern;

	private final boolean caseInsensitive;

	private final boolean global;

	private final boolean substitute;

	private final String toSub;

	/**
	 * Creates a new regex expression.
	 * 
	 * @param s The string to be matched or substituted
	 * @param pattern The regex pattern
	 * @param escape The escape char to use
	 */
	private RegexUtils(final String s, String pattern, final char escape) {

		this.s = s;

		this.substitute = pattern.startsWith("s/");

		this.global = pattern.endsWith("/gi") || pattern.endsWith("/ig") || pattern.endsWith("/g");

		this.caseInsensitive = pattern.endsWith("/gi") || pattern.endsWith("/ig") || pattern.endsWith("/i");

		pattern = removeSlashes(pattern);

		if (!substitute) {

			if (escape != DEFAULT_ESCAPE_CHAR) {

				this.escPattern = changeEscapeChar(pattern, escape);

			}
			else {

				this.escPattern = pattern;
			}

			this.toSub = null;

		}
		else {

			String token = null;

			if (!pattern.contains(TOKEN1)) {

				token = TOKEN1;

			}
			else if (!pattern.contains(TOKEN2)) {

				token = TOKEN2;

			}
			else {

				throw new RuntimeException("Cannot use mentawai Regex with this pattern!");
			}

			// this is to allow '/' both in pattern and sub expression...
			// Necessary so that split returns only 2 items...
			// '/' must be escaped!
			final String newPattern = pattern.replaceAll("\\" + escape + "/", token);

			boolean changed = false;

			if (!newPattern.equals(pattern)) {

				pattern = newPattern;

				changed = true;
			}

			String[] parts = pattern.split("/");

			if (parts.length == 1 && pattern.endsWith("/")) {

				final String save = parts[0];

				parts = new String[2];
				parts[0] = save;
				parts[1] = "";

			}
			else if (parts.length != 2) {

				throw new IllegalArgumentException("Bad substitute pattern: " + pattern);
			}

			if (changed) {

				this.toSub = parts[1].replaceAll(token, "/");

			}
			else {

				this.toSub = parts[1];
			}

			if (escape != DEFAULT_ESCAPE_CHAR) {

				if (changed) {

					this.escPattern = changeEscapeChar(parts[0], escape).replaceAll(token, "\\/");

				}
				else {

					this.escPattern = changeEscapeChar(parts[0], escape);

				}

			}
			else {

				if (changed) {

					this.escPattern = parts[0].replaceAll(token, "\\/");

				}
				else {

					this.escPattern = parts[0];

				}
			}
		}

	}

	/**
	 * Creates a new regex expression.
	 * 
	 * @param s The string to be matched or substituted
	 * @param pattern The regex pattern
	 */
	private RegexUtils(final String s, final String pattern) {

		this(s, pattern, DEFAULT_ESCAPE_CHAR);

	}

	private static String removeSlashes(String s) {

		if (s.startsWith("s/")) {

			s = s.substring(2, s.length());

		}
		else if (s.startsWith("/")) {

			s = s.substring(1, s.length());
		}

		if (s.endsWith("/gi") || s.endsWith("/ig")) {

			s = s.substring(0, s.length() - 3);

		}
		else if (s.endsWith("/g") || s.endsWith("/i")) {

			s = s.substring(0, s.length() - 2);

		}
		else if (s.endsWith("/")) {

			s = s.substring(0, s.length() - 1);
		}

		return s;
	}

	private static String changeEscapeChar(final String s, final char esc) {

		return s.replace(esc, '\\');
	}

	/**
	 * Substitute and return the new string.
	 * 
	 * @return The new string, in other words, the result of this regex substitution
	 */
	public String substitute() {

		Pattern p = null;

		if (caseInsensitive) {

			p = Pattern.compile(escPattern, Pattern.CASE_INSENSITIVE);

		}
		else {

			p = Pattern.compile(escPattern);
		}

		final Matcher m = p.matcher(s);

		if (global) {

			return m.replaceAll(toSub);

		}
		else {

			return m.replaceFirst(toSub);
		}

	}

	/**
	 * Tell whether there is a match for this regex.
	 * 
	 * @return true if we have a match
	 */
	public boolean matches() {

		Pattern p = null;

		if (caseInsensitive) {

			p = Pattern.compile(escPattern, Pattern.CASE_INSENSITIVE);

		}
		else {

			p = Pattern.compile(escPattern);
		}

		final Matcher m = p.matcher(s);

		return m.find();
	}

	/**
	 * Match and return what was matched. That's for when you use regular expressions with parenthesis.
	 * 
	 * @return What was matched or an empty array if nothing was matched
	 */
	public String[] match() {

		Pattern p = null;

		if (caseInsensitive) {

			p = Pattern.compile(escPattern, Pattern.CASE_INSENSITIVE);

		}
		else {

			p = Pattern.compile(escPattern);
		}

		final Matcher m = p.matcher(s);

		final List<String> res = new LinkedList<String>();

		while(m.find()) {

			final int x = m.groupCount();

			for (int i = 0; i < x; i++) {

				res.add(m.group(i + 1));
			}

			if (!global) {
				break; // just once...
			}
		}

		if (res.size() == 0) {
			return null;
		}

		return res.toArray(new String[res.size()]);

	}

	/**
	 * Fast way to match.
	 * 
	 * @param s The string to match
	 * @param pattern The regex pattern to match
	 * @return What was matched, if anything.
	 */
	public static String[] match(final String s, final String pattern) {

		return match(s, pattern, DEFAULT_ESCAPE_CHAR);
	}

	/**
	 * Fast way to match.
	 * 
	 * @param s The string to match
	 * @param pattern The regex pattern to match
	 * @return true if it was matched
	 */
	public static boolean matches(final String s, final String pattern) {

		return matches(s, pattern, DEFAULT_ESCAPE_CHAR);
	}

	/**
	 * Fast way to match.
	 * 
	 * @param s The string to match
	 * @param pattern The regex pattern to match
	 * @param escapeChar the escape char to use
	 * @return What was matched, if anything.
	 */
	public static String[] match(final String s, final String pattern, final char escapeChar) {

		final RegexUtils r = new RegexUtils(s, pattern, escapeChar);

		return r.match();
	}

	/**
	 * Fast way to match.
	 * 
	 * @param s The string to match
	 * @param pattern The regex pattern to match
	 * @param escapeChar the escape char to use
	 * @return true if it was matched
	 */
	public static boolean matches(final String s, final String pattern, final char escapeChar) {

		final RegexUtils r = new RegexUtils(s, pattern, escapeChar);

		return r.matches();
	}

	/**
	 * Fast way to substitute.
	 * 
	 * @param s The string to substitute
	 * @param pattern the pattern to match
	 * @return the new string
	 */
	public static String sub(final String s, final String pattern) {

		return sub(s, pattern, DEFAULT_ESCAPE_CHAR);
	}

	/**
	 * Fast way to substitute.
	 * 
	 * @param s The string to substitute
	 * @param pattern the pattern to match
	 * @param escapeChar the scape char to use
	 * @return the new string
	 */
	public static String sub(final String s, final String pattern, final char escapeChar) {

		final RegexUtils r = new RegexUtils(s, pattern, escapeChar);

		return r.substitute();
	}

	/**
	 * Escape a forward slash with the escape char provided. Great for directories for example.
	 * 
	 * @param s The string to change
	 * @param escapeChar the escape char to use
	 * @return the new string with the slashed escaped with the given escape char
	 */
	public static String escapeSlash(final String s, final String escapeChar) {

		return s.replaceAll("/", escapeChar + "/");
	}
	
	public static void main(String[] args) {
		
		{
			String s = "aaa bbb \"ccc ddd\" eee";
			
			String[] m = RegexUtils.match(s, "/(\".+\"|\\w+)/g");
			
			for(String a : m) {
				System.out.println(a);
			}
		}
		
		String s = "asdfasfasf \"sequence\":212, blah blah blah";
		
		String[] m = RegexUtils.match(s, "/\\\"sequence\\\"\\:(\\d+)/");
		
		for(String a : m) {
			System.out.println(a);
		}
	}
}
