package com.feedback;

public class InitChunkFeedback implements Feedback {

	private static final long serialVersionUID = 1L;
	private String ChunkHandle;
	
	public InitChunkFeedback(String ChunkHandle) {
		this.ChunkHandle = ChunkHandle;
	}
	
	public String ChunkHandle() {
		return ChunkHandle;
	}
}
