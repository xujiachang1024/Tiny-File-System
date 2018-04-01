package com.feedback;

public class WriteChunkFeedback implements Feedback {

	private static final long serialVersionUID = 1L;
	private boolean writeSuccess;
	
	public WriteChunkFeedback(boolean writeSuccess) {
		this.writeSuccess = writeSuccess;
	}
	
	public boolean writeSuccess() {
		return writeSuccess;
	}
}
