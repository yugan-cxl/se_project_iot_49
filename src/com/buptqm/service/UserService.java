package com.buptqm.service;

import com.buptqm.dao.UserDAO;
import com.buptqm.model.Role;
import com.buptqm.model.User;
//import com.buptqm.util.MD5Util;

import java.util.List;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    // 用户注册
    public boolean register(String username, String password, String realName, String email, Role role) {
        if (userDAO.getUserByUsername(username) != null) {
            return false; // 用户名已存在
        }
        //String encryptedPwd = MD5Util.encrypt(password);
        //User user = new User(0, username, encryptedPwd, realName, email, role, 0, "");
        String plainPwd = password;
        User user = new User(0, username, plainPwd, realName, email, role, 0, "");
        userDAO.addUser(user);
        return true;
    }

    // 用户登录
    public User login(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            return null;
        }
        String plainPwd = password;
        //String encryptedPwd = MD5Util.encrypt(password);
        if (plainPwd.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    // 更新用户技能和个人信息
    public void updateUserProfile(User user) {
        userDAO.updateUser(user);
    }

    // 根据ID查询用户
    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }

    // 获取所有TA用户
    public List<User> getAllTAUsers() {
        return userDAO.getAllTAUsers();
    }

    // 更新TA工作量
    public void updateTAWorkload(int taId, int addWorkload) {
        User ta = userDAO.getUserById(taId);
        if (ta != null && "TA".equals(ta.getRole().name())) {
            ta.setWorkload(ta.getWorkload() + addWorkload);
            userDAO.updateUser(ta);
        }
    }
}