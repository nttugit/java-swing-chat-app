package model;

import java.util.Arrays;

public class ChatFile {

	private String id;
	private String name;
	private byte[] data;
	private String fileExtension;
	private String sender;

	public ChatFile(String id, String name, byte[] data, String fileExtension, String sender) {
		super();
		this.id = id;
		this.name = name;
		this.data = data;
		this.fileExtension = fileExtension;
		this.setSender(sender);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	@Override
	public String toString() {
		return "ChatFile [id=" + id + ", name=" + name + ", data=" + Arrays.toString(data) + ", fileExtension="
				+ fileExtension + ", sender=" + sender + "]";
	}



}
