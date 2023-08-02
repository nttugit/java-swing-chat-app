import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	final static String FILE_DIR = "D:\\";
	private static final int PORT = 12345;
	private static Set<PrintWriter> clientWriters = new HashSet<>();

	public static void main(String[] args) throws IOException {
		System.out.println("Chat Server is running on port " + PORT);
		ServerSocket serverSocket = new ServerSocket(PORT);

		while (true) {
			new ClientHandler(serverSocket.accept()).start();
		}
	}

	private static class ClientHandler extends Thread {
		private Socket socket;
		private PrintWriter writer;

		public ClientHandler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				InputStream inputStream = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

				OutputStream outputStream = socket.getOutputStream();
				writer = new PrintWriter(outputStream, true);
				synchronized (clientWriters) {
					clientWriters.add(writer);
				}

				String message;
				while ((message = reader.readLine()) != null) {
					System.out.println("Message: " + message);
					if (message.startsWith("FILE:")) {
						String fileName = message.substring(5); // Remove the "FILE:" prefix
						receiveFile(fileName, inputStream);
						broadcast("File received: " + fileName);
					} else {
						System.out.println("Received: " + message);
						broadcast(message);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (writer != null) {
					synchronized (clientWriters) {
						clientWriters.remove(writer);
					}
				}
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// Add a method to receive and save files
		private void receiveFile(String fileName, InputStream inputStream) {
			try {

				System.out.println("File name: " + fileName);
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
						new FileOutputStream(FILE_DIR + fileName));
				byte[] buffer = new byte[8192];
				int bytesRead;

				while ((bytesRead = inputStream.read(buffer)) != -1) {
					bufferedOutputStream.write(buffer, 0, bytesRead);
				}

				bufferedOutputStream.close();
				System.out.println("File received and saved: " + fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void broadcast(String message) {
			synchronized (clientWriters) {
				for (PrintWriter clientWriter : clientWriters) {
					clientWriter.println(message);
				}
			}
		}
	}
}
