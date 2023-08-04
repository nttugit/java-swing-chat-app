import java.awt.Component;
import java.awt.Font;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import model.ChatFile;

public class Server {
	private static Set<DataOutputStream> clientDOSs = new HashSet<>();

	public static void startServer(int port) throws IOException {
		System.out.println("GutGutChat SERVER IS LISTENING ON PORT " + port);
		ServerSocket serverSocket = new ServerSocket(port);
		
		// Luôn mở để lắng nghe client connect
		while (true) {
			new ClientHandler(serverSocket.accept()).start();
		}
	}


	private static class ClientHandler extends Thread {
		private Socket socket;
		DataOutputStream dos;

		public ClientHandler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());

				synchronized (clientDOSs) {
					clientDOSs.add(dos);
				}

				// isMessage giúp phân biệt đây là plain text
				Integer isMessage;
				while ((isMessage = dis.readInt()) != null) {
					if (isMessage == -1) {
						String message = dis.readUTF();

						if (message.contains("end")) {
							break;
						}

						// Broadcast plain text message here
						broadcastPlainText(message);
					} else {
						int fileNameLength = isMessage;
						byte[] fileNameBytes = new byte[fileNameLength];
						dis.readFully(fileNameBytes, 0, fileNameLength);
						
						int fileContentLength = dis.readInt();
						if (fileContentLength > 0) {
							byte[] fileContentBytes = new byte[fileContentLength];
							dis.readFully(fileContentBytes, 0, fileContentLength);
						
							// File ID
							int fileIdLength = dis.readInt();
							byte[] fileIdBytes = new byte[fileIdLength];
							dis.readFully(fileIdBytes, 0, fileIdLength);
							
							// File extension
							int fileExtensionLength = dis.readInt();
							byte[] fileExtensionBytes = new byte[fileExtensionLength];
							dis.readFully(fileExtensionBytes, 0, fileExtensionLength);
							
							
							// File sender
							int fileSenderLength = dis.readInt();
							byte[] fileSenderBytes = new byte[fileSenderLength];
							dis.readFully(fileSenderBytes, 0, fileSenderLength);
											
							// Broadcast file here
							broadcastFile(fileIdBytes, fileNameBytes, fileContentBytes, fileExtensionBytes, fileSenderBytes);
						}
					}

				}

				// Gửi đến các user khác (kể cả mình)
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (dos != null) {
					synchronized (clientDOSs) {
						clientDOSs.remove(dos);
					}
				}
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void broadcastPlainText(String message) {
			synchronized (clientDOSs) {
				for (DataOutputStream clientDOS : clientDOSs) {
					try {
						clientDOS.writeInt(-1);
						clientDOS.writeUTF(message);

						clientDOS.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private void broadcastFile(byte[] fileId, byte[] fileNameBytes, byte[] fileContentBytes, byte[] fileExtensionBytes, byte[] fileSenderBytes) {
			synchronized (clientDOSs) {
				for (DataOutputStream clientDOS : clientDOSs) {
					try {
						clientDOS.writeInt(fileNameBytes.length);
						clientDOS.write(fileNameBytes);

						clientDOS.writeInt(fileContentBytes.length);
						clientDOS.write(fileContentBytes);
						
						clientDOS.writeInt(fileId.length);
						clientDOS.write(fileId);
						
						clientDOS.writeInt(fileExtensionBytes.length);
						clientDOS.write(fileExtensionBytes);
						
						clientDOS.writeInt(fileSenderBytes.length);
						clientDOS.write(fileSenderBytes);
						
						clientDOS.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
}
