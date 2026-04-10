package com.buptqm.ui;

import com.buptqm.model.User;
import com.buptqm.service.UserService;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminMainFrame extends JFrame {
    public AdminMainFrame(User user) {
        setTitle("Admin Panel");
        setSize(500,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        UserService service = new UserService();
        DefaultListModel<String> model = new DefaultListModel<>();
        List<User> list = service.getAllTAUsers();
        for(User u : list) model.addElement(u.getId()+" | "+u.getRealName()+" | workload:"+u.getWorkload());

        add(new JScrollPane(new JList<>(model)));
    }
}