package com.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
			System.out.println("Success: Client reads port " + port + " from file");
		} catch (IOException ioe)  {
			System.out.println("Error: Client fails to read selected port from file!");
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
//			System.out.println("Success: Client closes the file");
		}
		
		// start client socket
		try {
			clientSocket = new Socket("localhost", port);
//			System.out.println("Success: Client creates Socket at " + clientSocket.getInetAddress());
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ois = new ObjectInputStream(clientSocket.getInputStream());
			System.out.println("Success: Client connects to ChunkServer at " + clientSocket.getInetAddress());
		} catch (IOException ioe) {
			System.out.println("Error: Client fails to create client socket!");
		}
	}

	/**
	 * Create a chunk at the chunk server from the client side.
	 */
	public String initializeChunk() {
		System.out.println("Message: Client invokes initializeChunk at " + clientSocket.getInetAddress());
		try {
			oos.writeObject(new InitChunkRequest());
			oos.flush();
			System.out.println("Success: Client sends InitChunkRequest from " + clientSocket.getInetAddress());
			while (true) {
//				System.out.println("Client is waiting for InitChunkFeedback...");
				Object obj = ois.readObject();
				if (obj instanceof InitChunkFeedback) {
					InitChunkFeedback icf = (InitChunkFeedback)obj;
					System.out.println("Success: Client receives InitChunkFeedback at " + clientSocket.getInetAddress());
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
			System.out.println("The chunk write should be within the range of the file, invalide chunk write!");
			return false;
		}
		try {
			oos.writeObject(new WriteChunkRequest(ChunkHandle, payload, offset));
			oos.flush();
			System.out.println("Success: Client sends WriteChunkRequest from " + clientSocket.getInetAddress());
			while (true) {
				Object obj = ois.readObject();
				if (obj instanceof WriteChunkFeedback) {
					WriteChunkFeedback wcf = (WriteChunkFeedback)obj;
					System.out.println("Success: Client receives WriteChunkFeedback at " + clientSocket.getInetAddress());
					return wcf.writeSuccess();
				}
			}
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			// TODO Auto-generated catch block
			cnfe.printStackTrace();
		}
		return false;
	}

	/**
	 * Read a chunk at the chunk server from the client side.
	 */
	public byte[] getChunk(String ChunkHandle, int offset, int NumberOfBytes) {
		if (NumberOfBytes + offset > ChunkServer.ChunkSize) {
			System.out.println("The chunk read should be within the range of the file, invalide chunk read!");
			return null;
		}
		try {
			oos.writeObject(new ReadChunkRequest(ChunkHandle, offset, NumberOfBytes));
			oos.flush();
			System.out.println("Success: Client sends ReadChunkRequest from " + clientSocket.getInetAddress());
			while (true) {
				// TODO: read read chunk feedback
				Object obj = ois.readObject();
				if (obj instanceof ReadChunkFeedback) {
					ReadChunkFeedback rcf = (ReadChunkFeedback)obj;
					System.out.println("Success: Client receives ReadChunkFeedback at " + clientSocket.getInetAddress());
					return rcf.retrivedData();
				}
			}
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
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
