package com.chunkserver;

import com.interfaces.ChunkServerInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * implementation of interfaces at the chunkserver side
 * 
 * @author Shahram Ghandeharizadeh
 *
 */

public class ChunkServer implements ChunkServerInterface {
//	final static String filePath = "C:\\Users\\jiachanx\\Documents\\TinyFS-2\\csci485Disk\\"; // or C:\\newfile.txt
	final static String filePath = "C:\\Users\\shahram\\Documents\\TinyFS-2\\csci485Disk\\";
	public static long counter;

	/**
	 * Initialize the chunk server
	 */
	public ChunkServer() {
		System.out.println("Constructor of ChunkServer is invoked");

		// find/make the directory
		File directory = new File(filePath);
		if (!directory.exists()) {
			directory.getParentFile().mkdirs();
			directory.mkdir();
		}
//		System.out.println("Directory exists: " + directory.exists());

		// list all the files under the directory
		File[] files = directory.listFiles();
//		System.out.println("Number of files under directory: " + files.length);

		// retrieve the counter under the directory
		if (files == null || files.length == 0) {
			counter = 0;
		}
		else {
			long[] counters = new long[files.length];
			for (int i = 0; i < counters.length; i++) {
				counters[i] = Long.valueOf(files[i].getName());
			}
			Arrays.sort(counters);
			counter = counters[counters.length - 1];
		}
	}

	/**
	 * Each chunk corresponds to a file. Return the chunk handle of the last chunk
	 * in the file.
	 */
	public String initializeChunk() {
		System.out.println("createChunk invoked");
		counter++;
		return String.valueOf(counter);
	}

	/**
	 * Write the byte array to the chunk at the specified offset The byte array size
	 * should be no greater than 4KB
	 */
	public boolean putChunk(String ChunkHandle, byte[] payload, int offset) {
		System.out.println("writeChunk invoked");
		try {
			String absolutePath = filePath + ChunkHandle;
			File file = new File(absolutePath);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.write(payload, offset, payload.length);
			raf.close();
			return true;
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return false;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;
		} 
	}

	/**
	 * read the chunk at the specific offset
	 */
	public byte[] getChunk(String ChunkHandle, int offset, int NumberOfBytes) {
		System.out.println("readChunk invoked");
		try {
			String absolutePath = filePath + ChunkHandle;
			File file = new File(absolutePath);
			if (!file.exists()) {
				return null;
			}

			byte[] retrievedData = new byte[NumberOfBytes];
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.read(retrievedData, offset, NumberOfBytes);
			raf.close();
			return retrievedData;
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return null;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}

}
