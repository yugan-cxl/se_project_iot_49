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

public class MOMainFrame extends JFrame {
    private final User currentMO;
    private final JobService jobService = new JobService();
    private final ApplicationService appService = new ApplicationService();
    private final UserService userService = new UserService();
    private final CVService cvService = new CVService(); //CV服务

    // 主面板组件
    private JTabbedPane tabbedPane;
    private DefaultListModel<Job> myJobsListModel;
    private JList<Job> myJobsList;
    private DefaultListModel<Application> applicantsListModel;
    private JList<Application> applicantsList;
    private JButton acceptBtn;
    private JButton rejectBtn;
    private Job selectedJob;

    public MOMainFrame(User moUser) {
        this.currentMO = moUser;
        setTitle("MO Home - " + moUser.getRealName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 顶部退出按钮面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel welcomeLabel = new JLabel("Welcome, " + moUser.getRealName() + " (MO)", JLabel.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutBtn.addActionListener(e -> logout());

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);

        initUI();
        loadMyJobs();

        // 组装主界面
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    // 退出登录方法
    private void logout() {
        this.dispose();
        new LoginFrame().setVisible(true);
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();

        // 标签页1：发布职位
        JPanel postJobPanel = createPostJobPanel();
        tabbedPane.addTab("Post Job", postJobPanel);

        // 标签页2：我的职位 + 申请者审核
        JPanel myJobsPanel = createMyJobsPanel();
        tabbedPane.addTab("My Jobs & Applicants", myJobsPanel);
    }

    // 发布职位面板
    private JPanel createPostJobPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        JTextField titleField = new JTextField(30);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        JTextArea descArea = new JTextArea(5, 30);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        gbc.gridx = 1;
        panel.add(descScroll, gbc);

        // Required Skills
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Required Skills:"), gbc);
        JTextField skillsField = new JTextField(30);
        gbc.gridx = 1;
        panel.add(skillsField, gbc);

        // Post Button
        JButton postBtn = new JButton("Post");
        postBtn.setPreferredSize(new Dimension(200, 50));
        postBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(postBtn, gbc);

        // 发布按钮事件
        postBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();
            String skills = skillsField.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Job newJob = new Job();
            newJob.setTitle(title);
            newJob.setDescription(desc);
            newJob.setRequiredSkills(skills);
            newJob.setMoId(currentMO.getId());
            newJob.setStatus("OPEN");
            newJob.setCreateTime(LocalDateTime.now());

            jobService.publishJob(newJob);
            JOptionPane.showMessageDialog(this, "Job posted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // 发布后刷新职位列表
            titleField.setText("");
            descArea.setText("");
            skillsField.setText("");
            loadMyJobs();
        });

        return panel;
    }

    // 我的职位 + 申请者审核面板
    private JPanel createMyJobsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 左侧：我的职位列表
        myJobsListModel = new DefaultListModel<>();
        myJobsList = new JList<>(myJobsListModel);
        myJobsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Job) {
                    Job job = (Job) value;
                    setText(job.getId() + " | " + job.getTitle() + " (" + job.getStatus() + ")");
                }
                return this;
            }
        });
        myJobsList.setPreferredSize(new Dimension(300, 0));
        mainPanel.add(new JScrollPane(myJobsList), BorderLayout.WEST);

        // 右侧：申请者列表（可选择）
        applicantsListModel = new DefaultListModel<>();
        applicantsList = new JList<>(applicantsListModel);
        applicantsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Application) {
                    Application app = (Application) value;
                    User ta = userService.getUserById(app.getTaId());
                    setText((index+1) + ". TA Name: " + ta.getRealName() + " | Status: " + app.getStatus() + " | Apply Time: " + app.getApplyTime().toString().replace("T", " "));
                }
                return this;
            }
        });
        applicantsList.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(new JScrollPane(applicantsList), BorderLayout.CENTER);

        // 🔥 新增：双击申请者查看CV
        applicantsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // 双击事件
                    Application selectedApp = applicantsList.getSelectedValue();
                    if (selectedApp != null) {
                        showTACV(selectedApp.getTaId()); // 弹出TA的CV
                    }
                }
            }
        });

        // 操作按钮面板（通过/拒绝）
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        acceptBtn = new JButton("Accept Selected Applicant");
        rejectBtn = new JButton("Reject Selected Applicant");
        acceptBtn.setEnabled(false);
        rejectBtn.setEnabled(false);

        btnPanel.add(acceptBtn);
        btnPanel.add(rejectBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        // 职位列表选择事件：点击职位，加载对应的申请者
        myJobsList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            selectedJob = myJobsList.getSelectedValue();
            if (selectedJob == null) {
                applicantsListModel.clear();
                acceptBtn.setEnabled(false);
                rejectBtn.setEnabled(false);
                return;
            }
            loadApplicants(selectedJob);
        });

        // 申请者列表选择事件：选中申请者后，按钮可用
        applicantsList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            boolean hasSelection = applicantsList.getSelectedIndex() != -1;
            acceptBtn.setEnabled(hasSelection);
            rejectBtn.setEnabled(hasSelection);
        });

        // 通过按钮事件
        acceptBtn.addActionListener(e -> {
            updateApplicationStatus("ACCEPTED");
        });

        // 拒绝按钮事件
        rejectBtn.addActionListener(e -> {
            updateApplicationStatus("REJECTED");
        });

        return mainPanel;
    }

    // 弹出TA的CV详情窗口
    private void showTACV(int taId) {
        User ta = userService.getUserById(taId);
        if (ta == null) {
            JOptionPane.showMessageDialog(this, "TA not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CV cv = cvService.getCVByTaId(taId);
        if (cv == null || cv.getContent() == null || cv.getContent().isEmpty()) {
            JOptionPane.showMessageDialog(this, "This TA has not created a CV yet!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 拆分CV内容（和TA端用同一个分隔符！）
        String[] parts = cv.getContent().split("###", -1);

        // 创建CV详情窗口
        JFrame cvFrame = new JFrame("TA CV - " + ta.getRealName());
        cvFrame.setSize(700, 600);
        cvFrame.setLocationRelativeTo(null);
        cvFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 标题
        JLabel titleLabel = new JLabel("Teaching Assistant Application CV", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // 内容面板
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // 基本信息
        addSectionLabel(contentPanel, gbc, "Basic Information", 0, 0, 2);
        addInfoRow(contentPanel, gbc, "Name:", parts.length > 0 ? parts[0] : "", 0, 1);
        addInfoRow(contentPanel, gbc, "Major:", parts.length > 1 ? parts[1] : "", 2, 1);
        addInfoRow(contentPanel, gbc, "Email:", parts.length > 2 ? parts[2] : "", 0, 2);
        addInfoRow(contentPanel, gbc, "Tel:", parts.length > 3 ? parts[3] : "", 2, 2);

        // 教育背景
        addSectionLabel(contentPanel, gbc, "Education Background", 0, 3, 2);
        JTextArea eduArea = createInfoTextArea(parts.length > 4 ? parts[4] : "");
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.2;
        contentPanel.add(new JScrollPane(eduArea), gbc);

        // 技能特长
        addSectionLabel(contentPanel, gbc, "Skills & Abilities", 0, 5, 2);
        JTextArea skillArea = createInfoTextArea(parts.length > 5 ? parts[5] : "");
        gbc.gridx = 0; gbc.gridy = 6;
        contentPanel.add(new JScrollPane(skillArea), gbc);

        // 相关经历
        addSectionLabel(contentPanel, gbc, "Relevant Experience", 0, 7, 2);
        JTextArea expArea = createInfoTextArea(parts.length > 6 ? parts[6] : "");
        gbc.gridx = 0; gbc.gridy = 8;
        contentPanel.add(new JScrollPane(expArea), gbc);

        // 自我评价
        addSectionLabel(contentPanel, gbc, "Self Introduction", 0, 9, 2);
        JTextArea introArea = createInfoTextArea(parts.length > 7 ? parts[7] : "");
        gbc.gridx = 0; gbc.gridy = 10;
        contentPanel.add(new JScrollPane(introArea), gbc);

        panel.add(contentPanel, BorderLayout.CENTER);
        cvFrame.add(panel);
        cvFrame.setVisible(true);
    }

    // 辅助方法：添加信息行
    private void addInfoRow(JPanel panel, GridBagConstraints gbc, String label, String value, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lbl, gbc);

        gbc.gridx = x + 1;
        gbc.weightx = 1;
        JTextField field = new JTextField(value);
        field.setEditable(false); // 只读，MO只能看不能改
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(field, gbc);
    }

    // 辅助方法：创建只读文本区域
    private JTextArea createInfoTextArea(String content) {
        JTextArea area = new JTextArea(content);
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
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

    // 加载MO自己发布的所有职位
    private void loadMyJobs() {
        myJobsListModel.clear();
        List<Job> myJobs = jobService.getJobsByMoId(currentMO.getId());
        for (Job job : myJobs) {
            myJobsListModel.addElement(job);
        }
    }

    // 加载选中职位的所有申请者
    private void loadApplicants(Job job) {
        applicantsListModel.clear();
        List<Application> apps = appService.getAppsByJobId(job.getId());
        for (Application app : apps) {
            applicantsListModel.addElement(app);
        }

        boolean hasApps = !apps.isEmpty();
        acceptBtn.setEnabled(hasApps);
        rejectBtn.setEnabled(hasApps);
    }

    private void updateApplicationStatus(String newStatus) {
        if (selectedJob == null) return;

        Application selectedApp = applicantsList.getSelectedValue();
        if (selectedApp == null) {
            JOptionPane.showMessageDialog(this, "Please select an applicant first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 获取原来的状态
        String oldStatus = selectedApp.getStatus();
        int taId = selectedApp.getTaId();

        // ===================== 核心逻辑 =====================
        // 1. 从 PENDING/REJECTED → ACCEPTED：工作量 +1
        if ("ACCEPTED".equals(newStatus) && !"ACCEPTED".equals(oldStatus)) {
            userService.updateTAWorkload(taId, 1); 
        }

        // 2. 从 ACCEPTED → REJECTED：工作量 -1
        if ("REJECTED".equals(newStatus) && "ACCEPTED".equals(oldStatus)) {
            userService.updateTAWorkload(taId, -1); 
        }
        // ====================================================

        // 更新申请状态
        selectedApp.setStatus(newStatus);
        appService.updateApp(selectedApp);

        // 刷新列表
        loadApplicants(selectedJob);
        JOptionPane.showMessageDialog(this, "Status updated: " + newStatus, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}