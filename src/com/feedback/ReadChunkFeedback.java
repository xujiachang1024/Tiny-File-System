package com.feedback;

public class ReadChunkFeedback implements Feedback {

	private static final long serialVersionUID = 1L;
	private byte[] retrievedData;
	
	public ReadChunkFeedback(byte[] retrievedData) {
		this.retrievedData = retrievedData;
	}
	
	public byte[] retrivedData() {
		return retrievedData;
	}
}
