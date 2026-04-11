package com.buptqm.ui;

import com.buptqm.model.User;
import com.buptqm.service.UserService;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminMainFrame extends JFrame {
    public AdminMainFrame(User user) {
        setTitle("Admin Home - " + user.getRealName());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 🔥 新增：顶部退出按钮面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getRealName() + " (Admin)", JLabel.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutBtn.addActionListener(e -> logout());

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);

        // 主内容区
        UserService service = new UserService();
        DefaultListModel<String> model = new DefaultListModel<>();
        List<User> list = service.getAllTAUsers(); // 假设你有这个方法，如果没有就用原来的
        for(User u : list) model.addElement(u.getId()+" | "+u.getRealName()+" | workload:"+u.getWorkload());

        JList<String> userList = new JList<>(model);
        userList.setFont(new Font("Arial", Font.PLAIN, 14));

        // 组装主界面
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(userList), BorderLayout.CENTER);
        add(mainPanel);
    }

    // 🔥 新增：退出登录方法
    private void logout() {
        this.dispose();
        new LoginFrame().setVisible(true);
    }
}