package com.buptqm.ui;

import com.buptqm.model.Application;
import com.buptqm.model.CV;
import com.buptqm.model.Job;
import com.buptqm.model.User;
import com.buptqm.service.ApplicationService;
import com.buptqm.service.CVService;
import com.buptqm.service.JobService;
import com.buptqm.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class TAMainFrame extends JFrame {
    private final User user;
    private final JobService jobService = new JobService();
    private final ApplicationService appService = new ApplicationService();
    private final CVService cvService = new CVService();
    private final UserService userService = new UserService(); // 用于查发布人姓名
    // 全局组件
    private DefaultListModel<String> applicationListModel;
    private JList<String> jobList;
    private JTextArea jobDetailArea;

    // CV模板组件（分栏填写）
    private JTextField telField;
    private JTextField nameField;
    private JTextField majorField;
    private JTextField emailField;
    private JTextArea educationArea;
    private JTextArea skillsArea;
    private JTextArea experienceArea;
    private JTextArea selfIntroArea;

    private List<Job> allOpenJobs;

    public TAMainFrame(User user) {
        this.user = user;
        setTitle("TA Home - " + user.getRealName());
        setSize(850, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 顶部退出按钮面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getRealName() + " (TA)", JLabel.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutBtn.addActionListener(e -> logout()); // 点击退出

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);

        JTabbedPane tab = new JTabbedPane();
        tab.add("Job List", jobPanel());
        tab.add("My Application", appPanel());
        tab.add("My CV", cvPanel());

        // 组装主界面
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tab, BorderLayout.CENTER);
        add(mainPanel);
    }

    // 退出登录方法
    private void logout() {
        this.dispose(); // 关闭当前窗口
        new LoginFrame().setVisible(true); // 打开登录窗口
    }

    // 职位列表
    private JPanel jobPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultListModel<String> jobListModel = new DefaultListModel<>();
        jobList = new JList<>(jobListModel);
        jobList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane jobListScroll = new JScrollPane(jobList);
        jobListScroll.setPreferredSize(new Dimension(250, 0));

        allOpenJobs = jobService.getOpenJobs();
        for (Job j : allOpenJobs) {
            jobListModel.addElement(j.getId() + " | " + j.getTitle());
        }

        jobDetailArea = new JTextArea();
        jobDetailArea.setEditable(false);
        jobDetailArea.setLineWrap(true);
        jobDetailArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane detailScroll = new JScrollPane(jobDetailArea);

        JButton applyBtn = new JButton("Apply for this Job");
        applyBtn.setPreferredSize(new Dimension(0, 40));
        applyBtn.setFont(new Font("Arial", Font.PLAIN, 16));

        jobList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int selectedIndex = jobList.getSelectedIndex();
            if (selectedIndex == -1) {
                jobDetailArea.setText("");
                return;
            }
            Job selectedJob = allOpenJobs.get(selectedIndex);
            showJobDetail(selectedJob);
        });

        applyBtn.addActionListener(e -> {
            int selectedIndex = jobList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Please select a job first!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Job selectedJob = allOpenJobs.get(selectedIndex);

            Application app = new Application();
            app.setJobId(selectedJob.getId());
            app.setTaId(user.getId());
            app.setStatus("PENDING");
            app.setApplyTime(LocalDateTime.now());
            appService.applyJob(app);

            refreshMyApplications();
            JOptionPane.showMessageDialog(this, "Application submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        mainPanel.add(jobListScroll, BorderLayout.WEST);
        mainPanel.add(detailScroll, BorderLayout.CENTER);
        mainPanel.add(applyBtn, BorderLayout.SOUTH);
        return mainPanel;
    }

    private void showJobDetail(Job job) {
        // 通过 moId 查发布人的真实姓名
        String publisherName = "Unknown";
        User publisher = userService.getUserById(job.getMoId());
        if (publisher != null) {
            publisherName = publisher.getRealName();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Job Detail ===\n\n");
        sb.append("Job ID: ").append(job.getId()).append("\n\n");
        sb.append("Title: ").append(job.getTitle()).append("\n\n");
        sb.append("Posted by: ").append(publisherName).append("\n\n"); //替换原来的 MO ID
        sb.append("Description:\n").append(job.getDescription()).append("\n\n");
        sb.append("Required Skills: ").append(job.getRequiredSkills()).append("\n\n");
        sb.append("Posted Time: ").append(job.getCreateTime().toString().replace("T", " "));
        jobDetailArea.setText(sb.toString());
    }

    // 我的申请
    private JPanel appPanel() {
        JPanel p = new JPanel(new BorderLayout());
        applicationListModel = new DefaultListModel<>();
        JList<String> list = new JList<>(applicationListModel);
        list.setFont(new Font("Arial", Font.PLAIN, 14));

        refreshMyApplications();
        p.add(new JScrollPane(list));
        return p;
    }

    public void refreshMyApplications() {
        applicationListModel.clear();
        List<Application> apps = appService.getAppsByTaId(user.getId());
        for (Application a : apps) {
            applicationListModel.addElement(
                    "Job ID: " + a.getJobId() +
                            " | Status: " + a.getStatus() +
                            " | Apply Time: " + a.getApplyTime().toString().replace("T", " ")
            );
        }
    }

    private JPanel cvPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 标题
        JLabel titleLabel = new JLabel("Teaching Assistant Application CV", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 内容面板
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ====================== 核心修复：基本信息栏 ======================
        // 第一行：Name | Major
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0; // 标签不拉伸
        contentPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1; // 输入框拉伸，权重1
        nameField = new JTextField();
        contentPanel.add(nameField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0; // 标签不拉伸
        contentPanel.add(new JLabel("Major:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1; // 输入框拉伸，权重1
        majorField = new JTextField();
        contentPanel.add(majorField, gbc);

        // 第二行：Email | Tel
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        contentPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        emailField = new JTextField();
        contentPanel.add(emailField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        contentPanel.add(new JLabel("Tel:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        telField = new JTextField();
        contentPanel.add(telField, gbc);

        // 重置全局设置，不影响下面的部分
        gbc.weightx = 1;
        gbc.gridx = 0;

        // 2. 教育背景
        addSectionLabel(contentPanel, gbc, "Education Background", 0, 2, 4);
        educationArea = createTextArea(3);
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.2;
        contentPanel.add(new JScrollPane(educationArea), gbc);

        // 3. 技能特长
        addSectionLabel(contentPanel, gbc, "Skills & Abilities", 0, 4, 4);
        skillsArea = createTextArea(3);
        gbc.gridy = 5;
        contentPanel.add(new JScrollPane(skillsArea), gbc);

        // 4. 相关经历
        addSectionLabel(contentPanel, gbc, "Relevant Experience", 0, 6, 4);
        experienceArea = createTextArea(4);
        gbc.gridy = 7;
        contentPanel.add(new JScrollPane(experienceArea), gbc);

        // 5. 自我评价
        addSectionLabel(contentPanel, gbc, "Self Introduction", 0, 8, 4);
        selfIntroArea = createTextArea(3);
        gbc.gridy = 9;
        contentPanel.add(new JScrollPane(selfIntroArea), gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // 保存按钮
        JButton saveBtn = new JButton("Save CV");
        saveBtn.setPreferredSize(new Dimension(0, 45));
        saveBtn.setFont(new Font("Arial", Font.BOLD, 16));
        saveBtn.addActionListener(e -> saveCV());
        mainPanel.add(saveBtn, BorderLayout.SOUTH);

        // 加载已保存的CV
        loadSavedCV();

        return mainPanel;
    }

    // 辅助方法：创建章节标题标签
    private void addSectionLabel(JPanel panel, GridBagConstraints gbc, String text, int x, int y, int width) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.weightx = 1;
        gbc.weighty = 0;
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        panel.add(label, gbc);
    }

    // 辅助方法：创建多行文本框
    private JTextArea createTextArea(int rows) {
        JTextArea area = new JTextArea(rows, 0);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    // 保存CV：把所有模板字段拼接成一个字符串存储
    // 保存CV：加Tel字段
    private void saveCV() {
        CV cv = new CV();
        cv.setTaId(user.getId());
        cv.setName(nameField.getText().trim());
        cv.setMajor(majorField.getText().trim());
        cv.setEmail(emailField.getText().trim());
        cv.setTel(telField.getText().trim());
        cv.setEducationBackground(educationArea.getText().trim());
        cv.setSkillsAbilities(skillsArea.getText().trim());
        cv.setRelevantExperience(experienceArea.getText().trim());
        cv.setSelfIntroduction(selfIntroArea.getText().trim());

        cvService.uploadCV(cv);
        JOptionPane.showMessageDialog(this, "CV saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    // 加载CV：加Tel字段
    private void loadSavedCV() {
        CV savedCV = cvService.getCVByTaId(user.getId());
        if (savedCV != null) {
            nameField.setText(savedCV.getName() != null ? savedCV.getName() : "");
            majorField.setText(savedCV.getMajor() != null ? savedCV.getMajor() : "");
            emailField.setText(savedCV.getEmail() != null ? savedCV.getEmail() : "");
            telField.setText(savedCV.getTel() != null ? savedCV.getTel() : "");
            educationArea.setText(savedCV.getEducationBackground() != null ? savedCV.getEducationBackground() : "");
            skillsArea.setText(savedCV.getSkillsAbilities() != null ? savedCV.getSkillsAbilities() : "");
            experienceArea.setText(savedCV.getRelevantExperience() != null ? savedCV.getRelevantExperience() : "");
            selfIntroArea.setText(savedCV.getSelfIntroduction() != null ? savedCV.getSelfIntroduction() : "");
        }
    }
}