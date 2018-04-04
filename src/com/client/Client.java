package com.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;

import com.chunkserver.ChunkServer;
import com.feedback.InitChunkFeedback;
import com.feedback.ReadChunkFeedback;
import com.feedback.WriteChunkFeedback;
import com.interfaces.ClientInterface;
import com.request.InitChunkRequest;
import com.request.ReadChunkRequest;
import com.request.WriteChunkRequest;

/**
 * implementation of interfaces at the client side
 * 
 * @author Shahram Ghandeharizadeh
 *
 */
public class Client extends Thread implements ClientInterface {

	private Socket clientSocket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	/**
	 * Initialize the client
	 */
	public Client() {
		// read selected port from file
		int port = 1024;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader("port.txt");
			br = new BufferedReader(fr);
			String line = br.readLine().trim();
			port = Integer.parseInt(line);
			this.log("Success: Client reads port " + port + " from file");
		} catch (IOException ioe)  {
			this.log("Error: Client fails to read selected port from file!");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (Exception e) {}
			}
//			this.log("Success: Client closes the file");
		}
		
		// start client socket
		try {
			clientSocket = new Socket("localhost", port);
//			this.log("Success: Client creates Socket at " + clientSocket.getInetAddress());
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ois = new ObjectInputStream(clientSocket.getInputStream());
			this.log("Success: Client connects to ChunkServer at " + clientSocket.getInetAddress());
		} catch (IOException ioe) {
			this.log("Error: Client fails to create client socket!");
		}
	}
	
	public void log(String message) {
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter("clientLog.txt", true);
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

	/**
	 * Create a chunk at the chunk server from the client side.
	 */
	public String initializeChunk() {
		this.log("Message: Client invokes initializeChunk at " + clientSocket.getInetAddress());
		try {
			oos.writeObject(new InitChunkRequest());
			oos.flush();
			this.log("Success: Client sends InitChunkRequest from " + clientSocket.getInetAddress());
			{
//				this.log("Client is waiting for InitChunkFeedback...");
				Object obj = ois.readObject();
				if (obj instanceof InitChunkFeedback) {
					InitChunkFeedback icf = (InitChunkFeedback)obj;
					this.log("Success: Client receives InitChunkFeedback at " + clientSocket.getInetAddress());
					return icf.ChunkHandle();	
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		return null;
	}

	/**
	 * Write a chunk at the chunk server from the client side.
	 */
	public boolean putChunk(String ChunkHandle, byte[] payload, int offset) {
		if (offset + payload.length > ChunkServer.ChunkSize) {
			this.log("The chunk write should be within the range of the file, invalide chunk write!");
			return false;
		}
		try {
			oos.writeObject(new WriteChunkRequest(ChunkHandle, payload, offset));
			oos.flush();
			this.log("Success: Client sends WriteChunkRequest from " + clientSocket.getInetAddress());
			{
				Object obj = ois.readObject();
				if (obj instanceof WriteChunkFeedback) {
					WriteChunkFeedback wcf = (WriteChunkFeedback)obj;
					this.log("Success: Client receives WriteChunkFeedback at " + clientSocket.getInetAddress());
					return wcf.writeSuccess();
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		return false;
	}

	/**
	 * Read a chunk at the chunk server from the client side.
	 */
	public byte[] getChunk(String ChunkHandle, int offset, int NumberOfBytes) {
		if (NumberOfBytes + offset > ChunkServer.ChunkSize) {
			this.log("The chunk read should be within the range of the file, invalide chunk read!");
			return null;
		}
		try {
			oos.writeObject(new ReadChunkRequest(ChunkHandle, offset, NumberOfBytes));
			oos.flush();
			this.log("Success: Client sends ReadChunkRequest from " + clientSocket.getInetAddress());
			{
				Object obj = ois.readObject();
				if (obj instanceof ReadChunkFeedback) {
					ReadChunkFeedback rcf = (ReadChunkFeedback)obj;
					this.log("Success: Client receives ReadChunkFeedback at " + clientSocket.getInetAddress());
					return rcf.retrivedData();
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			// TODO Auto-generated catch block
			cnfe.printStackTrace();
		}
		return null;
	}

	public static void main(String [] args) {
		Client client = new Client();
	}
}
