package com.chunkserver;

import com.interfaces.ChunkServerInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

/**
 * implementation of interfaces at the chunkserver side
 * 
 * @author Shahram Ghandeharizadeh
 *
 */

public class ChunkServer extends Thread implements ChunkServerInterface {
	final static String filePath = "C:\\Users\\jiachanx\\Documents\\TinyFS-2\\csci485Disk\\"; // or C:\\newfile.txt
//	final static String filePath = "C:\\Users\\shahram\\Documents\\TinyFS-2\\csci485Disk\\";
	public static long counter;
	private ServerSocket serverSocket;
	private Vector<ServerThread> serverThreads;

	/**
	 * Initialize the chunk server
	 */
	public ChunkServer() {
		this.log("Constructor of ChunkServer is invoked");

		// find/make the directory
		File directory = new File(filePath);
		if (!directory.exists()) {
			directory.getParentFile().mkdirs();
			directory.mkdir();
		}
//		this.log("Directory exists: " + directory.exists());

		// list all the files under the directory
		File[] files = directory.listFiles();
//		this.log("Number of files under directory: " + files.length);

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
		
		// start server
		int port = 1024;
		boolean portOccupied = true;
		Random random = new Random();
		while (portOccupied) {
			port = random.nextInt(48151 - 1024) + 1024;
			try {
				serverSocket = new ServerSocket(port);
				portOccupied = false;
			} catch (IOException ioe) {
				this.log("Warning: ChunkServer finds port " + port + " occupied, trying a new port...");
			}
		}
		
		// write selected port into file
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter("port.txt");
			pw = new PrintWriter(fw);
			pw.println(port);
			pw.flush();
			this.log("Success: ChunkServer writes port " + port + " into file");
		} catch (IOException ioe) {
			this.log("Error: ChunkServer fails to write selected port into file!");
		} finally {
			if (pw != null) {
				try {
					pw.close();
				} catch (Exception e) {}
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (Exception e) {}
			}
		}
		
		serverThreads = new Vector<ServerThread>();
		this.start();
		this.log("Success: ChunkServer is constructed and listening...");
	}
	
	public void log(String message) {
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter("serverLog.txt", true);
			pw = new PrintWriter(fw);
			pw.println((new Timestamp(System.currentTimeMillis())) + " | " + message);
			pw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}
	
	public void removeServerThread(ServerThread serverThread) {
		serverThreads.remove(serverThread);
		this.log("Message: ChunkServer removes 1 ServerThread");
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
//				this.log("Success: ChunkServer accepts Client " + clientSocket.getInetAddress());
				ServerThread serverThread = new ServerThread(this, clientSocket);
				serverThreads.add(serverThread);
				this.log("Success: ChunkServer connects to Client " + clientSocket.getInetAddress());
			} catch (IOException ioe) {
				this.log("Error: ChunkServer fails to connect to Client!");
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * Each chunk corresponds to a file. Return the chunk handle of the last chunk
	 * in the file.
	 */
	public String initializeChunk() {
		this.log("createChunk invoked");
		counter++;
		return String.valueOf(counter);
	}

	/**
	 * Write the byte array to the chunk at the specified offset The byte array size
	 * should be no greater than 4KB
	 */
	public boolean putChunk(String ChunkHandle, byte[] payload, int offset) {
		this.log("writeChunk invoked");
		try {
			String absolutePath = filePath + ChunkHandle;
			File file = new File(absolutePath);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(offset);
			raf.write(payload, 0, payload.length);
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
		this.log("readChunk invoked");
		try {
			String absolutePath = filePath + ChunkHandle;
			File file = new File(absolutePath);
			if (!file.exists()) {
				return null;
			}

			byte[] retrievedData = new byte[NumberOfBytes];
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(offset);
			raf.read(retrievedData, 0, NumberOfBytes);
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

	public static void main(String [] args) {
		new ChunkServer();
	}
}
