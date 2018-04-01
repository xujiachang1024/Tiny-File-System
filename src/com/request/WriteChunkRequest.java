package com.request;

public class WriteChunkRequest implements Request {

	private static final long serialVersionUID = 1L;
	private String ChunkHandle;
	private byte[] payload;
	private int offset;
	
	public WriteChunkRequest(String ChunkHandle, byte[] payload, int offset) {
		this.ChunkHandle = ChunkHandle;
		this.payload = payload;
		this.offset = offset;
	}
	
	public String ChunkHandle() {
		return ChunkHandle;
	}
	
	public byte[] payload() {
		return payload;
	}
	
	public int offset() {
		return offset;
	}
}
