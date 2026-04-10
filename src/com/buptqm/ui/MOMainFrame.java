package com.buptqm.ui;

import com.buptqm.model.Application;
import com.buptqm.model.Job;
import com.buptqm.model.User;
import com.buptqm.service.ApplicationService;
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

    // 主面板组件
    private JTabbedPane tabbedPane;
    private DefaultListModel<Job> myJobsListModel;
    private JList<Job> myJobsList;
    private JTextArea applicantsArea;
    private JButton acceptBtn;
    private JButton rejectBtn;
    private Job selectedJob;

    public MOMainFrame(User moUser) {
        this.currentMO = moUser;
        setTitle("MO Home - " + moUser.getRealName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 初始化主面板
        initUI();
        // 加载已发布的职位
        loadMyJobs();
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();

        // 标签页1：发布职位（保留你原来的功能）
        JPanel postJobPanel = createPostJobPanel();
        tabbedPane.addTab("Post Job", postJobPanel);

        // 标签页2：我的职位 + 申请者审核（新增核心功能）
        JPanel myJobsPanel = createMyJobsPanel();
        tabbedPane.addTab("My Jobs & Applicants", myJobsPanel);

        add(tabbedPane);
    }

    // --------------- 1. 保留原来的「发布职位」面板 ---------------
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

        // 发布按钮事件（完全复用你原来的逻辑）
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

    // --------------- 2. 新增「我的职位 + 申请者审核」面板 ---------------
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

        // 右侧：申请者列表 + 操作按钮
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        applicantsArea = new JTextArea(15, 30);
        applicantsArea.setEditable(false);
        applicantsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(new JScrollPane(applicantsArea), BorderLayout.CENTER);

        // 操作按钮面板（通过/拒绝）
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        acceptBtn = new JButton("Accept Selected Applicant");
        rejectBtn = new JButton("Reject Selected Applicant");
        acceptBtn.setEnabled(false);
        rejectBtn.setEnabled(false);

        btnPanel.add(acceptBtn);
        btnPanel.add(rejectBtn);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // 职位列表选择事件：点击职位，加载对应的申请者
        myJobsList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            selectedJob = myJobsList.getSelectedValue();
            if (selectedJob == null) {
                applicantsArea.setText("");
                acceptBtn.setEnabled(false);
                rejectBtn.setEnabled(false);
                return;
            }
            // 加载该职位的所有申请者
            loadApplicants(selectedJob);
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

    // --------------- 3. 加载MO自己发布的所有职位 ---------------
    private void loadMyJobs() {
        myJobsListModel.clear();
        List<Job> myJobs = jobService.getJobsByMoId(currentMO.getId());
        for (Job job : myJobs) {
            myJobsListModel.addElement(job);
        }
    }

    // --------------- 4. 加载选中职位的所有申请者 ---------------
    private void loadApplicants(Job job) {
        List<Application> apps = appService.getAppsByJobId(job.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("Applicants for Job: ").append(job.getTitle()).append("\n");
        sb.append("----------------------------------------\n");

        if (apps.isEmpty()) {
            sb.append("No applicants yet.");
        } else {
            for (int i = 0; i < apps.size(); i++) {
                Application app = apps.get(i);
                User ta = userService.getUserById(app.getTaId());
                sb.append(i+1).append(". TA Name: ").append(ta.getRealName())
                        .append(" | Status: ").append(app.getStatus())
                        .append(" | Apply Time: ").append(app.getApplyTime().toString().replace("T", " "))
                        .append("\n\n");
            }
        }

        applicantsArea.setText(sb.toString());
        acceptBtn.setEnabled(!apps.isEmpty());
        rejectBtn.setEnabled(!apps.isEmpty());
    }

    // --------------- 5. 更新申请状态（通过/拒绝） ---------------
    private void updateApplicationStatus(String newStatus) {
        if (selectedJob == null) return;

        List<Application> apps = appService.getAppsByJobId(selectedJob.getId());
        if (apps.isEmpty()) return;

        // 简单实现：选择列表中的第一个申请者进行操作（中期汇报足够用）
        // 期末可以升级为多选/单选框精准选择
        Application appToUpdate = apps.get(0);
        appToUpdate.setStatus(newStatus);
        appService.updateApp(appToUpdate);

        // 刷新申请者列表
        loadApplicants(selectedJob);
        JOptionPane.showMessageDialog(this, "Application status updated to: " + newStatus, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}