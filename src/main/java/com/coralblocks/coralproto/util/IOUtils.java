/* 
 * Copyright 2024 (c) CoralBlocks - http://www.coralblocks.com
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

/**
 * This utility class provides some useful methods to perform common I/O operations. 
 */
public class IOUtils {
	
	private IOUtils() {
		
	}

	public static String readFile(String file) throws IOException {
		return readFile(file, null);
	}
	
	public static String readFile(String file, String charset) throws IOException {
		
		File f = new File(file);
		
		if (!f.exists()) return null;
		
		FileInputStream fis = null;
		BufferedReader br = null;
		
		try {
			
			fis = new FileInputStream(f);
			
			if (charset != null) {
			
				br = new BufferedReader(new InputStreamReader(fis, charset));
				
			} else {
				
				br = new BufferedReader(new InputStreamReader(fis));
			}
			
			StringBuilder sb = new StringBuilder(1024);
			
			String line;
			
			while((line = br.readLine()) != null) {
				
				sb.append(line).append('\n');
			}
			
			return sb.toString();

		} finally {
			
			br.close();
			
		}
	}

}
