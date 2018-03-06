package com.chunkserver;

import com.interfaces.ChunkServerInterface;

import java.io.File;
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
	final static String filePath = "C:\\Users\\shahram\\Documents\\TinyFS-2\\csci485Disk\\"; // or C:\\newfile.txt
	public static long counter;

	/**
	 * Initialize the chunk server
	 */
	public ChunkServer() {
		System.out.println(
				"Constructor of ChunkServer is invoked:  Part 1 of TinyFS must implement the body of this method.");

		// find/make the directory
		File directory = new File(filePath);
		if (!directory.exists()) {
			directory.mkdir();
		}

		// list all the files under the directory
		File[] files = directory.listFiles();

		// retrieve the counter under the directory
		if (files.length == 0) {
			counter = 0;
		}
		else {
			long[] counters = new long[files.length];
			for (int i = 0; i < counters.length; i++) {
				if (!files[i].getName().equals(".DS_Store")) {
					counters[i] = Long.valueOf(files[i].getName());
				}
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
		System.out.println("createChunk invoked:  Part 1 of TinyFS must implement the body of this method.");
		counter++;
		return String.valueOf(counter);
	}

	/**
	 * Write the byte array to the chunk at the specified offset The byte array size
	 * should be no greater than 4KB
	 */
	public boolean putChunk(String ChunkHandle, byte[] payload, int offset) {
		System.out.println("writeChunk invoked:  Part 1 of TinyFS must implement the body of this method.");
		try {
			RandomAccessFile raf = new RandomAccessFile(filePath + ChunkHandle, "rw");
			raf.seek(offset);
			raf.write(payload, 0, payload.length);
			raf.close();
			return true;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;
		}
	}

	/**
	 * read the chunk at the specific offset
	 */
	public byte[] getChunk(String ChunkHandle, int offset, int NumberOfBytes) {
		System.out.println("readChunk invoked:  Part 1 of TinyFS must implement the body of this method.");
		try {
			boolean fileExists = (new File(filePath + ChunkHandle)).exists();
			if (!fileExists) {
				return null;
			}

			byte[] retrievedData = new byte[NumberOfBytes];
			RandomAccessFile raf = new RandomAccessFile(filePath + ChunkHandle, "rw");
			raf.seek(offset);
			raf.read(retrievedData, 0, NumberOfBytes);
			raf.close();
			return retrievedData;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}

}
