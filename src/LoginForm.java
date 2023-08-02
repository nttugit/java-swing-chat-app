import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {

    private JTextField nameTextField;
    private JButton submitButton;

    public LoginForm() {
        setTitle("Name Input Form");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        JLabel nameLabel = new JLabel("Enter your name:");
        nameTextField = new JTextField(20);
        submitButton = new JButton("Submit");

        // Set layout
        setLayout(new GridLayout(2, 1));

        // Add components to the frame
        JPanel inputPanel = new JPanel();
        inputPanel.add(nameLabel);
        inputPanel.add(nameTextField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);

        add(inputPanel);
        add(buttonPanel);

        // Add ActionListener to the submitButton
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = nameTextField.getText();
                openClientChatForm(userName);
            }
        });
    }

    private void openClientChatForm(String userName) {
        ClientChatForm newForm = new ClientChatForm(userName);
        newForm.setVisible(true);
        dispose(); // Close the current form
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
}

//class NewForm extends JFrame {
//    public NewForm(String userName) {
//        setTitle("Welcome!");
//        setSize(300, 100);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        JLabel welcomeLabel = new JLabel("Welcome, " + userName + "!");
//        add(welcomeLabel);
//    }
//}
