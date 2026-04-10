package com.buptqm.dao;

import com.buptqm.model.User;
import com.buptqm.util.CSVUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDAO {
    private static final String FILE_NAME = "users.csv";

    static {
        CSVUtil.initFile(FILE_NAME, User.getCSVHeader());
    }

    // 新增用户
    public void addUser(User user) {
        int nextId = CSVUtil.getNextId(FILE_NAME);
        user.setId(nextId);
        CSVUtil.appendLine(FILE_NAME, user.toCSVString());
    }

    // 根据用户名查询用户
    public User getUserByUsername(String username) {
        List<String> lines = CSVUtil.readAllLines(FILE_NAME);
        for (String line : lines) {
            User user = User.fromCSVString(line);
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    // 根据ID查询用户
    public User getUserById(int id) {
        List<String> lines = CSVUtil.readAllLines(FILE_NAME);
        for (String line : lines) {
            User user = User.fromCSVString(line);
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    // 查询所有TA用户
    public List<User> getAllTAUsers() {
        List<String> lines = CSVUtil.readAllLines(FILE_NAME);
        List<User> users = new ArrayList<>();
        for (String line : lines) {
            User user = User.fromCSVString(line);
            if ("TA".equals(user.getRole().name())) {
                users.add(user);
            }
        }
        return users;
    }

    // 更新用户信息
    public void updateUser(User updatedUser) {
        List<String> lines = CSVUtil.readAllLines(FILE_NAME);
        List<String> newLines = lines.stream()
                .map(line -> {
                    User user = User.fromCSVString(line);
                    if (user.getId() == updatedUser.getId()) {
                        return updatedUser.toCSVString();
                    }
                    return line;
                })
                .collect(Collectors.toList());
        CSVUtil.writeAllLines(FILE_NAME, newLines, User.getCSVHeader());
    }
}