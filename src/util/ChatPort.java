package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

public class ChatPort {
	final static File PORT_FILE = new File("port.txt");

	// Chỉ để lưu port vào file (để user biết mà truy cập)
	public static void savePort(int port) {
		try (FileWriter writer = new FileWriter(PORT_FILE, false)) {
			writer.write(port + "");
			writer.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static int getPort() {
		int port = 12345; // mặc định
		try (BufferedReader reader = new BufferedReader(new FileReader(PORT_FILE))) {
			port = Integer.parseInt(reader.readLine());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return port;
	}
}
