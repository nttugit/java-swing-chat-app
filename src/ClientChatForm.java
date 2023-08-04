import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.NumberFormat.Style;
import java.util.ArrayList;

import model.ChatFile;
import model.ChatMessage;
import util.RandomIDGenerator;

public class ClientChatForm {
	// Socket & send file
	private static final String SERVER_IP = "127.0.0.1"; // Change to the server IP
	private static final int SERVER_PORT = 12345; // Change to the server port
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;

	// Lưu tạm các file để có thể tải xuống
	static private ArrayList<ChatFile> listFiles = new ArrayList<>();
	File fileToSend = null;

	// UI
	private JFrame frame;

	// Panel chứa các panel con là message (text, file)
	private JPanel jpMessage;

	private JButton jbSendFile;
	private JButton jbChooseFile;
	private JLabel jlFileName;

	private JTextArea jtaInputField;
	private JButton jbSendMsg;

	// User
	private String userChatName;

	public ClientChatForm(String userChatName) {
		this.userChatName = userChatName;
		setupGUI();
		setupNetworking();
	}

	private void setupGUI() {
		// FRAME
		frame = new JFrame("GutGut Chat");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 800);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		// JPanel để chứa message
		jpMessage = new JPanel();
		jpMessage.setLayout(new BoxLayout(jpMessage, BoxLayout.Y_AXIS));
		jpMessage.setBorder(new EmptyBorder(10, 10, 10, 10));
		Dimension preferredSize = new Dimension(300, 350);
		jpMessage.setFont(new Font("Arial", Font.PLAIN, 15));
		jpMessage.setPreferredSize(preferredSize);

		JScrollPane jscrollPane = new JScrollPane(jpMessage);
		jscrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		// Chứa chức năng input (text field, choose file,...)
		JPanel jpInput = new JPanel();
		jpInput.setLayout(new BoxLayout(jpInput, BoxLayout.Y_AXIS));
//		jpInput.setBorder(new EmptyBorder(0, 0, 10, 0));

		jtaInputField = new JTextArea(1, 40);
		jtaInputField.setFont(new Font("Arial", Font.PLAIN, 15));
		jtaInputField.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Panel chứa các nút về file
		JPanel jpFileInput = new JPanel();

		jbChooseFile = new JButton("Chọn file");
		jbSendFile = new JButton("Gửi file");
		jbSendFile.setEnabled(false);

		jlFileName = new JLabel("Chưa chọn file");
		jbSendMsg = new JButton("Gửi");
		jbSendMsg.setBorder(new EmptyBorder(10, 10, 10, 10));

		jpFileInput.add(jbChooseFile);
		jpFileInput.add(jbSendFile);
		jpFileInput.add(jlFileName);

		jpInput.add(jpFileInput);

		//
		JPanel jpMessageInput = new JPanel();
		jpMessageInput.add(jtaInputField);
		jpMessageInput.add(jbSendMsg);

		jpInput.add(jpMessageInput);

		// Add các thành phần chính vào
		frame.getContentPane().add(jscrollPane);
		frame.getContentPane().add(jpInput);

		// ======================= EVENTS =============================

		jbSendMsg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(jtaInputField.getText());
				jtaInputField.setText("");
			}
		});

		// Chọn file
		jbChooseFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setDialogTitle("Chọn file để gửi");

				if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					fileToSend = jFileChooser.getSelectedFile();
					jlFileName.setText("File đã chọn: " + fileToSend.getName());
				}
				if (fileToSend != null) {
					jbSendFile.setEnabled(true);
				} else {
					jbSendFile.setEnabled(false);
				}
			}
		});

		// Gửi file
		jbSendFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileToSend == null) {
					jlFileName.setText("Chưa chọn file");
				} else {
					try {
						FileInputStream fis = new FileInputStream(fileToSend.getAbsolutePath());
						DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

						String fileName = fileToSend.getName();
						byte[] fileNameBytes = fileName.getBytes();

//						String senderAndFileName = userChatName + ": " + fileName;
//						byte[] senderAndFileNameBytes = senderAndFileName.getBytes();

						byte[] fileContentBytes = new byte[(int) fileToSend.length()];
						fis.read(fileContentBytes);

						String fileID = RandomIDGenerator.generateRandomID();
						byte[] fileIdBytes = fileID.getBytes();

						String fileExtension = getFileExtension(fileName);
						byte[] fileExtensionBytes = fileExtension.getBytes();

						String fileSender = userChatName;
						byte[] fileSenderBytes = fileSender.getBytes();

						// Không thể add vào danh sách file lúc này, bởi vì chỉ có người gửi có data này
						// (vì lưu trên memory)
						// Giờ phải gửi qua socket, lát nữa broadcast cho tất cả users

//						ChatFile sentFile = new ChatFile(fileId, fileName, fileContentBytes, getFileExtension(fileName),
//								userChatName);
//						listFiles.add(sentFile);
//						fileId++;

//						System.out.println("Next file id: " + fileId);

						// Gửi tên file và nội dung file
						// Lưu ý: gửi một kích thước của file để server biết

//						dos.write(sentFile);
//						dos.writeInt(fileNameBytes.length);
//						dos.write(fileNameBytes);

						dos.writeInt(fileNameBytes.length);
						dos.write(fileNameBytes);

						dos.writeInt(fileContentBytes.length);
						dos.write(fileContentBytes);

						dos.writeInt(fileIdBytes.length);
						dos.write(fileIdBytes);

						dos.writeInt(fileExtensionBytes.length);
						dos.write(fileExtensionBytes);

						dos.writeInt(fileSenderBytes.length);
						dos.write(fileSenderBytes);

					} catch (IOException error) {
						// TODO Auto-generated catch block
						error.printStackTrace();
					}
				}

			}
		});

		frame.pack();
//        frame.setVisible(true);

	}

	private static String getFileExtension(String fileName) {
		// not work with .tar.gz (something like that)
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(i + 1);
		} else {
			return "No extension found.";
		}
	}

	private void setupNetworking() {
		try {
			socket = new Socket(SERVER_IP, SERVER_PORT);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			new IncomingReader().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(String message) {
		try {
			ChatMessage chatMessage = new ChatMessage(this.userChatName, message);

			// Gửi số -1 trước để phân biệt đây là text mesage
			int isMessage = -1;
			dos.writeInt(isMessage);
			dos.writeUTF(chatMessage.toString());
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

	private class IncomingReader extends Thread {
		public void run() {
			try {
				Integer isMessage;
				while ((isMessage = dis.readInt()) != null) {
					// Thay vì append vào JTextArea, mình sẽ add vào JPanel (mỗi message/file là một
					// JPanel con)
					JPanel jpFileRow = new JPanel();
					jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.Y_AXIS));

					if (isMessage == -1) {
						String message = dis.readUTF();

						JLabel jlMsg = new JLabel(message);
						jpFileRow.add(jlMsg);
						jpMessage.add(jpFileRow);
					} else { // Nếu là file

						// File name
						int fileNameLength = isMessage;
						byte[] fileNameBytes = new byte[fileNameLength];
						dis.readFully(fileNameBytes, 0, fileNameLength);
						String fileName = new String(fileNameBytes);

						int fileContentLength = dis.readInt();
						if (fileContentLength > 0) {

							// File content
							byte[] fileContentBytes = new byte[fileContentLength];
							dis.readFully(fileContentBytes, 0, fileContentLength);

							// File ID
							int fileIdLength = dis.readInt();
							byte[] fileIdBytes = new byte[fileIdLength];
							dis.readFully(fileIdBytes, 0, fileIdLength);
							String fileId = new String(fileIdBytes);

							// File extension
							int fileExtensionLength = dis.readInt();
							byte[] fileExtensionBytes = new byte[fileExtensionLength];
							dis.readFully(fileExtensionBytes, 0, fileExtensionLength);
							String fileExtension = new String(fileExtensionBytes);

							// File sender
							int fileSenderLength = dis.readInt();
							byte[] fileSenderBytes = new byte[fileSenderLength];
							dis.readFully(fileSenderBytes, 0, fileSenderLength);
							String fileSender = new String(fileSenderBytes);

							ChatFile chatFile = new ChatFile(fileId, fileName, fileContentBytes, fileExtension,
									fileSender);
							System.out.println("Nhận được file từ broad cast" + chatFile);

							//
							listFiles.add(chatFile);

							// Xử lý khi nhận File, show UI, lưu vào danh sách File (để download được)
							// Thiếu người gửi here
							JLabel jlFileName = new JLabel(fileSender + ": " + fileName);
							jpFileRow.add(jlFileName);

							jpFileRow.setBackground(Color.LIGHT_GRAY);

							// Event để hiển thị preview file hoặc thông báo download
							jpFileRow.addMouseListener(showDownloaderListener());
							jpFileRow.setName(fileId);
							jpMessage.add(jpFileRow);
//							jpFileRow

						}
					}

					frame.validate();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static MouseListener showDownloaderListener() {
		return new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				JPanel jPanel = (JPanel) e.getSource();
				String fileId = jPanel.getName();

				System.out.println("File id: " + fileId);

				// Nếu click đúng fileId trong danh sách file đã lưu
				for (ChatFile chatFile : listFiles) {
//					System.out.println(chatFile);
					if (chatFile.getId().equals(fileId)) {
//						System.out.println("file Id: " + fileId);
//						System.out.println(chatFile.getId());
//
//						// Hiển thị nút Frame thông báo download
						JFrame jfPreview = createFrame(chatFile.getName(), chatFile.getData(),
								chatFile.getFileExtension());
						jfPreview.setVisible(true);
					}
				}
			}

		};
	}

	private static JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {
		JFrame jfFilePreview = new JFrame("Preview file");
		jfFilePreview.setSize(600, 200);
//		jfFilePreview.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel jpFilePreview = new JPanel();
		jpFilePreview.setLayout(new BoxLayout(jpFilePreview, BoxLayout.Y_AXIS));

		JLabel jlPrompt = new JLabel("Bạn có chắc chắn muốn download file " + fileName + "?");
		jlPrompt.setFont(new Font("Arial", Font.BOLD, 20));
		jlPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));
		jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton jbYes = new JButton("Có");
//		jbYes.setPreferredSize(new Dimension(150, 75));
		jbYes.setFont(new Font("Arial", Font.BOLD, 20));

		JButton jbNo = new JButton("Huỷ bỏ");
//		jbNo.setPreferredSize(new Dimension(150, 75));
		jbNo.setFont(new Font("Arial", Font.BOLD, 20));

		JPanel jpButtons = new JPanel();
		jpButtons.setBorder(new EmptyBorder(20, 0, 10, 0));
		jpButtons.add(jbYes);
		jpButtons.add(jbNo);

		// Xử lý hiển thị (mà thôi)
//		if (fileExtension.equalsIgnoreCase("txt")) {
//			jlFileContent.setText("<html>" + new String(fileData) + "</html>");
//		} else {
//			jlFileContent.setIcon(new ImageIcon(fileData));
//		}

		// Xử lý lưu file
		jbYes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returnValue = fc.showSaveDialog(fc);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();

					String savedFilePath = file.getAbsolutePath();

					System.out.println("Đường dẫn đã lưu file: " + savedFilePath);

					FileOutputStream fos;
					try {
						fos = new FileOutputStream(file);
						fos.write(fileData);
						fos.close();
						jfFilePreview.dispose();

						fos.close();
					} catch (IOException error) {
						// TODO Auto-generated catch block
						error.printStackTrace();
					}

					jfFilePreview.dispose();
					System.out.println("savedFilePath: " + savedFilePath);
				}
			}
		});

		jbNo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jfFilePreview.dispose();
			}
		});

		jpFilePreview.add(jlPrompt);
//		jPanel.add(jlFileContent);
		jpFilePreview.add(jpButtons);

		jfFilePreview.add(jpFilePreview);

		return jfFilePreview;
	}
//	end createFrame

//	private static void appendColoredText(String text, Color bgColor) {
//		JTextPane textPane = new JTextPane();
//        StyledDocument doc = textPane.getStyledDocument();
//        javax.swing.text.Style style = textPane.addStyle("coloredStyle", null);
//
//        StyleConstants.setBackground(style, bgColor);
//
//        try {
//            doc.insertString(doc.getLength(), text, style);
//        } catch (BadLocationException e) {
//            e.printStackTrace();
//        }
//    }

	public void setVisible(boolean isVisible) {
		this.frame.setVisible(isVisible);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String userChatName = "ClientXXX";
				ClientChatForm newForm = new ClientChatForm(userChatName);
				newForm.setVisible(true);
			}
		});
	}
}
