package com.chunkserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.feedback.InitChunkFeedback;
import com.feedback.ReadChunkFeedback;
import com.feedback.WriteChunkFeedback;
import com.request.InitChunkRequest;
import com.request.ReadChunkRequest;
import com.request.WriteChunkRequest;

public class ServerThread extends Thread {
	
	private ChunkServer chunkServer;
	private Socket clientSocket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	public ServerThread(ChunkServer chunkServer, Socket clientSocket) {
		System.out.println("Constructor of ServerThread is invoked");
		try {
			this.chunkServer = chunkServer;
			this.clientSocket = clientSocket;
//			System.out.println("Message: ServerThread stores member variables");
			this.oos = new ObjectOutputStream(clientSocket.getOutputStream());
			this.ois = new ObjectInputStream(clientSocket.getInputStream());
//			System.out.println("Message: ServerThread creates ois & oos");
			this.start();
			System.out.println("Success: ServerThread is constructed and running at " + clientSocket.getInetAddress());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			while (true) {

				Object obj = ois.readObject();

				if (obj instanceof InitChunkRequest) {
					@SuppressWarnings("unused")
					InitChunkRequest icr = (InitChunkRequest)obj;
					System.out.println("Success: ChunkServer receives InitChunkRequest from Client " + clientSocket.getInetAddress());
					String ChunkHandle = chunkServer.initializeChunk();
					InitChunkFeedback icf = new InitChunkFeedback(ChunkHandle);
					oos.writeObject(icf);
					oos.flush();
					System.out.println("Success: ChunkServer sends InitChunkFeedback to Client " + clientSocket.getInetAddress());
				}
				
				if (obj instanceof ReadChunkRequest) {
					ReadChunkRequest rcr = (ReadChunkRequest)obj;
					System.out.println("Success: ChunkServer receives ReadChunkRequest from Client " + clientSocket.getInetAddress());
					byte[] retrievedData = chunkServer.getChunk(rcr.ChunkHandle(), rcr.offset(), rcr.NumberOfBytes());
					ReadChunkFeedback rcf = new ReadChunkFeedback(retrievedData);
					oos.writeObject(rcf);
					oos.flush();
					System.out.println("Success: ChunkServer sends ReadChunkFeedback to Client " + clientSocket.getInetAddress());
				}
				
				if (obj instanceof WriteChunkRequest) {
					WriteChunkRequest wcr = (WriteChunkRequest)obj;
					System.out.println("Success: ChunkServer receives WriteChunkRequest from Client " + clientSocket.getInetAddress());
					boolean writeSuccess = chunkServer.putChunk(wcr.ChunkHandle(), wcr.payload(), wcr.offset());
					WriteChunkFeedback wcf = new WriteChunkFeedback(writeSuccess);
					oos.writeObject(wcf);
					oos.flush();
					System.out.println("Success: ChunkServer sends WriteChunkFeedback to Client " + clientSocket.getInetAddress());
				}
			}
		} catch (SocketException se) {
			chunkServer.removeServerThread(this);
		} catch (ClassNotFoundException cnfe) {
			// TODO Auto-generated catch block
			cnfe.printStackTrace();
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
	}
}
