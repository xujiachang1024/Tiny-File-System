package com.request;

public class ReadChunkRequest implements Request {
	
	private static final long serialVersionUID = 1L;
	private String ChunkHandle;
	private int offset;
	private int NumberOfBytes;
	
	public ReadChunkRequest(String ChunkHandle, int offset, int NumberOfBytes) {
		this.ChunkHandle = ChunkHandle;
		this.offset = offset;
		this.NumberOfBytes = NumberOfBytes;
	}
	
	public String ChunkHandle() {
		return ChunkHandle;
	}
	
	public int offset() {
		return offset;
	}
	
	public int NumberOfBytes() {
		return NumberOfBytes;
	}
}
