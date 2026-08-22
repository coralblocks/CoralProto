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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * This utility class provides some useful methods to perform common I/O operations. 
 */
public class IOUtils {
	
	private IOUtils() {
		
	}

	public static String readFile(String file) throws IOException {
		return readFile(file, StandardCharsets.UTF_8.name());
	}
	
	public static String readFile(String file, String charset) throws IOException {
		
		File f = new File(file);
		
		if (!f.exists()) return null;
		
		String resolvedCharset = charset != null ? charset : StandardCharsets.UTF_8.name();

		try (FileInputStream fis = new FileInputStream(f);
			 InputStreamReader reader = new InputStreamReader(fis, resolvedCharset);
			 BufferedReader br = new BufferedReader(reader)) {
			
			StringBuilder sb = new StringBuilder(1024);
			
			String line;
			
			while((line = br.readLine()) != null) {
				
				sb.append(line).append('\n');
			}
			
			return sb.toString();
		}
	}
}
