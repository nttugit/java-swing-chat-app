import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import util.ChatPort;

public class ServerForm extends JFrame {

	private JPanel contentPane;

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerForm frame = new ServerForm();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ServerForm() {
		setTitle("GutGutChat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		setBounds(100, 100, 450, 230);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		// Title
		JLabel jlTitle = new JLabel("Chào mừng đến với GutGutChat!");
		jlTitle.setFont(new Font("Arial", Font.BOLD, 20));
		jlTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
		jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel jlTitle2 = new JLabel("Trước tiên, bạn cần chọn một port để khởi động");
		jlTitle2.setFont(new Font("Arial", Font.PLAIN, 15));
		jlTitle2.setAlignmentX(Component.CENTER_ALIGNMENT);

		// PORT
		JPanel jpPort = new JPanel();

		JLabel jlPort = new JLabel("PORT:  ");
		jlPort.setFont(new Font("Arial", Font.PLAIN, 20));

		jpPort.setBorder(new EmptyBorder(10, 0, 10, 0));

		JTextField jtfPort = new JTextField(5);
		jtfPort.setFont(new Font("Arial", Font.PLAIN, 20));
		jtfPort.setText("12345");

		jpPort.add(jlPort);
		jpPort.add(jtfPort);

		// Button Panel
		JPanel jpButtons = new JPanel();

		// Button start
		JButton jbStart = new JButton("Bắt đầu");
		jbStart.setFont(new Font("Arial", Font.PLAIN, 20));

		// Button stop
		JButton jbStop = new JButton("Dừng");
		jbStop.setFont(new Font("Arial", Font.PLAIN, 20));
		jbStop.setEnabled(false);

		jpButtons.add(jbStart);
		jpButtons.add(jbStop);
		
		jbStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int port = Integer.parseInt(jtfPort.getText().trim());
//					dispose();
				
					jbStop.setEnabled(true);
					jbStart.setEnabled(false);
					jlTitle2.setText("Server đang lắng nghe kết nối trên port " + port);
					ChatPort.savePort(port);
					
					// Khởi động chat server
					Server.startServer(port);
				} catch (Exception error) {
					error.printStackTrace();
				}

			}
		});


		jbStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int result = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn dừng Chat App Server?", "Xác nhận dừng Server", JOptionPane.OK_CANCEL_OPTION);

			        if (result == JOptionPane.OK_OPTION) {
			        	// Dừng server
			        	jbStop.setEnabled(false);
						jbStart.setEnabled(true);
						jlTitle2.setText("Trước tiên, bạn cần chọn một port để khởi động");

			            Server.stopServer();
			        } 
				} catch (Exception error) {
					error.printStackTrace();
				}

			}
		});
		
		contentPane.add(jlTitle);
		contentPane.add(jlTitle2);
		contentPane.add(jpPort);
		contentPane.add(jpButtons);
		setContentPane(contentPane);

	}

}
