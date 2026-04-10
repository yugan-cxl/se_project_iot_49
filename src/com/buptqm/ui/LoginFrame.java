package com.buptqm.ui;

import com.buptqm.model.User;
import com.buptqm.service.UserService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final UserService userService = new UserService();
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("BUPT International College Teaching Assistant Recruitment System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // 标题
        JLabel titleLabel = new JLabel("Teaching Assistant Recruitment System", JLabel.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 表单面板
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 20));
        formPanel.add(new JLabel("Username:："));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:："));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        // 登录按钮事件
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your username and password!", "Promt", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User user = userService.login(username, password);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Incorrect username or password!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 登录成功，跳转到对应角色界面
            JOptionPane.showMessageDialog(this, "Login successful!Welcome." + user.getRealName(), "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            switch (user.getRole()) {
                case TA:
                    new TAMainFrame(user).setVisible(true);
                    break;
                case MO:
                    new MOMainFrame(user).setVisible(true);
                    break;
                case ADMIN:
                    new AdminMainFrame(user).setVisible(true);
                    break;
            }
        });

        // 注册按钮事件
        registerBtn.addActionListener(e -> {
            this.dispose();
            new RegisterFrame().setVisible(true);
        });

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}