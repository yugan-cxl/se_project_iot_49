package com.buptqm.ui;

import com.buptqm.model.Role;
import com.buptqm.service.UserService;
import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private final UserService userService = new UserService();
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField realNameField;
    private JTextField emailField;
    private JComboBox<Role> roleBox;

    public RegisterFrame() {
        setTitle("Register");
        setSize(420, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(10,10));
        main.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel title = new JLabel("Register", JLabel.CENTER);
        title.setFont(new Font("宋体", Font.BOLD, 20));
        main.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(5,2,10,15));
        form.add(new JLabel("Username:"));
        usernameField = new JTextField();
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        form.add(passwordField);

        form.add(new JLabel("Real Name:"));
        realNameField = new JTextField();
        form.add(realNameField);

        form.add(new JLabel("Email:"));
        emailField = new JTextField();
        form.add(emailField);

        form.add(new JLabel("Role"));
        roleBox = new JComboBox<>(Role.values());
        form.add(roleBox);

        main.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton regBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");
        btnPanel.add(regBtn);
        btnPanel.add(backBtn);
        main.add(btnPanel, BorderLayout.SOUTH);

        regBtn.addActionListener(e -> {
            String name = usernameField.getText().trim();
            String pwd = new String(passwordField.getPassword()).trim();
            String real = realNameField.getText().trim();
            String email = emailField.getText().trim();
            Role role = (Role) roleBox.getSelectedItem();

            if(name.isEmpty() || pwd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty");
                return;
            }

            boolean ok = userService.register(name, pwd, real, email, role);
            if(ok) {
                JOptionPane.showMessageDialog(this, "Registration successful");
                new LoginFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists");
            }
        });

        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        add(main);
    }
}