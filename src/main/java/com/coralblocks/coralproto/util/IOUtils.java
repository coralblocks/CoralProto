/*
* Copyright (c) CoralBlocks LLC (c) 2017
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
