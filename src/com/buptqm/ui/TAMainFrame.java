package com.buptqm.ui;

import com.buptqm.model.Application;
import com.buptqm.model.CV;
import com.buptqm.model.Job;
import com.buptqm.model.User;
import com.buptqm.service.ApplicationService;
import com.buptqm.service.CVService;
import com.buptqm.service.JobService;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class TAMainFrame extends JFrame {
    private final User user;
    private final JobService jobService = new JobService();
    private final ApplicationService appService = new ApplicationService();
    private final CVService cvService = new CVService();

    public TAMainFrame(User user) {
        this.user = user;
        setTitle("TA Home - " + user.getRealName());
        setSize(650, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tab = new JTabbedPane();
        tab.add("Job list", jobPanel());
        tab.add("My Application", appPanel());
        tab.add("My CV", cvPanel());
        add(tab);
    }

    private JPanel jobPanel() {
        JPanel p = new JPanel(new BorderLayout());
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        List<Job> jobs = jobService.getOpenJobs();

        for(Job j : jobs) model.addElement(j.getId() + " | " + j.getTitle() + " | " + j.getRequiredSkills());

        JButton applyBtn = new JButton("Apply for this Job");
        applyBtn.addActionListener(e -> {
            String sel = list.getSelectedValue();
            if(sel == null) return;
            int jobId = Integer.parseInt(sel.split("\\|")[0].trim());
            Application app = new Application();
            app.setJobId(jobId);
            app.setTaId(user.getId());
            app.setStatus("PENDING");
            app.setApplyTime(LocalDateTime.now());
            appService.applyJob(app);
            JOptionPane.showMessageDialog(this, "Application submitted successfully");
        });

        p.add(new JScrollPane(list), BorderLayout.CENTER);
        p.add(applyBtn, BorderLayout.SOUTH);
        return p;
    }

    private JPanel appPanel() {
        JPanel p = new JPanel(new BorderLayout());
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        List<Application> apps = appService.getAppsByTaId(user.getId());
        for(Application a : apps) model.addElement("Job:"+a.getJobId()+" Status:"+a.getStatus());
        p.add(new JScrollPane(list));
        return p;
    }

    private JPanel cvPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setLineWrap(true);
        JButton save = new JButton("Save CV");

        save.addActionListener(e -> {
            CV cv = new CV();
            cv.setTaId(user.getId());
            cv.setContent(area.getText());
            cv.setUploadTime(LocalDateTime.now());
            cvService.uploadCV(cv);
            JOptionPane.showMessageDialog(this, "Saved successfully");
        });

        p.add(new JScrollPane(area), BorderLayout.CENTER);
        p.add(save, BorderLayout.SOUTH);
        return p;
    }
}