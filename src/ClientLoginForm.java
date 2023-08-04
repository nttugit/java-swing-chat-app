import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ClientLoginForm {
	private JFrame frame;
	private JPanel panel;
	private JPanel jpButtons;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JTextField displayNameField;
	private JButton registerButton;
	private JButton loginButton;

	private File userDataFile;

	public ClientLoginForm() {
		frame = new JFrame("GutGutChatApp - Đăng ký hoặc đăng nhập");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
//		frame.setSize(450, 450);
		panel = new JPanel();
		jpButtons = new JPanel();
		jpButtons.setBorder(new EmptyBorder(10, 0, 10, 0));
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

//		Font mainFont = new Font("Arial", Font.PLAIN, 20);

		// Title
		JLabel jlTitle = new JLabel("Chào mừng đến với GutGutChat!");
		jlTitle.setFont(new Font("Arial", Font.BOLD, 20));
		jlTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
		jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

		usernameField = new JTextField(20);
		passwordField = new JPasswordField(20);
		displayNameField = new JTextField(20);
		registerButton = new JButton("Đăng ký");
		loginButton = new JButton("Đăng nhập");

		userDataFile = new File("user_data.txt");

		panel.setLayout(new GridLayout(3, 2));
		panel.setBorder(new EmptyBorder(10, 20, 10, 20));

		// Username
		JLabel jlUsername = new JLabel("Username (*):");
//		jlUsername.setFont(mainFont);
		panel.add(jlUsername);
		panel.add(usernameField);
		// Password
		JLabel jlPassword = new JLabel("Password (*):");
//		jlPassword.setFont(mainFont);
		panel.add(jlPassword);
		panel.add(passwordField);

		JLabel jlDisplayName = new JLabel("Tên hiển thị:");
//		jlDisplayName.setFont(mainFont);
		panel.add(jlDisplayName);
		panel.add(displayNameField);

		jpButtons.add(registerButton);
		jpButtons.add(loginButton);

		frame.getContentPane().add(jlTitle);
		frame.getContentPane().add(panel);
		frame.getContentPane().add(jpButtons);

		frame.pack();
		frame.setVisible(true);

		// Đăng ký
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = usernameField.getText();
				String password = new String(passwordField.getPassword());
				String displayName = displayNameField.getText();

				if (!username.isEmpty() && !password.isEmpty()) {
					if (!isUsernameTaken(username)) {
						try (FileWriter writer = new FileWriter(userDataFile, true)) {
							writer.write(username + ";;" + password + ";;" + displayName + "\n");
							writer.flush();
							JOptionPane.showMessageDialog(frame, "Đăng ký thành công!");
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(frame, "Username đã tồn tại. Vui lòng chọn một username khác.");
					}
				} else {
					JOptionPane.showMessageDialog(frame, "Vui lòng nhập đầy đủ thông tin.");
				}

			}
		});

		// Đăng nhập
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = usernameField.getText();
				String password = new String(passwordField.getPassword());

				try (BufferedReader reader = new BufferedReader(new FileReader(userDataFile))) {
					String line;
					boolean found = false;
					String displayName = "";
					while ((line = reader.readLine()) != null) {
						String[] userData = line.split(";;");
						if (userData.length >= 2 && userData[0].equals(username) && userData[1].equals(password)) {
							JOptionPane.showMessageDialog(frame, "Đăng nhập thành công! Chat chit thôi nào!!");
							found = true;
							if(userData[2] != "") {
								displayName = userData[2];
							}else {
								displayName = userData[0];
							}
							
							break;
						}
					}

					if (!found) {
						JOptionPane.showMessageDialog(frame, "Tên đăng nhập hoặc mật khẩu không đúng. Vui lòng thử lại.");
					}else {
						// Đăng nhập thành công thì mở form Chat
						openClientChatForm(displayName);
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	private boolean isUsernameTaken(String username) {
		try (BufferedReader reader = new BufferedReader(new FileReader(userDataFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] userData = line.split(";;");
				if (userData.length >= 1 && userData[0].equals(username)) {
					return true;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	
	private void openClientChatForm(String userName) {
		ClientChatForm clientChatForm = new ClientChatForm(userName);
		clientChatForm.setVisible(true);
		frame.dispose(); // Close the current form
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ClientLoginForm();
			}
		});
	}
}
