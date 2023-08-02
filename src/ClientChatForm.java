import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import model.ChatMessage;

public class ClientChatForm {
	// Socket
	private static final String SERVER_IP = "127.0.0.1"; // Change to the server IP
	private static final int SERVER_PORT = 12345; // Change to the server port
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	
	// UI
	private JFrame frame;
	private JTextArea messageArea;
	private JTextField inputField;
	private JButton sendFileButton;
	
	// Thông tin cá nhân
	// Todo: lát tách ra model
	private String userChatName;

	public ClientChatForm(String userChatName) {
		setupGUI();
		setupNetworking();
		this.userChatName = userChatName;
	}

	private void setupGUI() {
		frame = new JFrame("GutGut Chat");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
//		int width = 800;
//        int height = 600;
//        frame.setSize(width, height);
		
		messageArea = new JTextArea(10, 30);
		messageArea.setEditable(false);
		JScrollPane jscrollPane = new JScrollPane(messageArea);
		frame.getContentPane().add(jscrollPane, BorderLayout.CENTER);
		
		inputField = new JTextField(30);
		frame.getContentPane().add(inputField, BorderLayout.SOUTH);
		sendFileButton= new JButton("Send file");
		frame.getContentPane().add(sendFileButton, BorderLayout.NORTH);
		
		inputField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage(inputField.getText());
				inputField.setText("");
			}
		});

		frame.pack();
//        frame.setVisible(true);
		
		sendFileButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        JFileChooser fileChooser = new JFileChooser();
		        int result = fileChooser.showOpenDialog(frame);

		        if (result == JFileChooser.APPROVE_OPTION) {
		            File selectedFile = fileChooser.getSelectedFile();
		            sendFile(selectedFile);
		        }
		    }
		});
	
		
	}

	private void setupNetworking() {
		try {
			socket = new Socket(SERVER_IP, SERVER_PORT);
			InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
			reader = new BufferedReader(streamReader);
			
//			  ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//	            Message msg1 = new Message("Client4", "Greeting", "Hello Server, I'm a new client");
//	            oos.writeObject(msg1);
			
			writer = new PrintWriter(socket.getOutputStream(), true);
			new IncomingReader().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(String message) {
		
		ChatMessage chatMessage = new ChatMessage(this.userChatName, message);
//		System.out.println("Sent: " + ChatMessage.toString());
		writer.println(chatMessage.toString());
	}

	private void sendFile(File file) {
	    try {
	        OutputStream outputStream = socket.getOutputStream();
	        FileInputStream fileInputStream = new FileInputStream(file);
	        byte[] buffer = new byte[8192];
	        int bytesRead;

	        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
	            outputStream.write(buffer, 0, bytesRead);
	        }

	        fileInputStream.close();
	        outputStream.flush();
	        
	        ChatMessage chatMsg = new ChatMessage(this.userChatName, "File đính kèm: " + file.getName() + "\n");
	        messageArea.append(chatMsg.toString());
	    } catch (IOException ex) {
	        ex.printStackTrace();	        
	        messageArea.append("Error sending file: " + ex.getMessage() + "\n");
	    }
	}
	
	private class IncomingReader extends Thread {
		public void run() {
			// Xử lý message, thêm phần tên người gửi
			
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					// 
					System.out.println("Received: " + message);
//					messageArea.append(userChatName + ": " + message + "\n");
					
					messageArea.append(message + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	public void setVisible(boolean isVisible) {
		this.frame.setVisible(isVisible);
	}

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//            	String userChatName = "ClientXXX";
//            	ClientChatForm newForm = new ClientChatForm(userChatName);
//                newForm.setVisible(true);
//            }
//        });
//    }
}
