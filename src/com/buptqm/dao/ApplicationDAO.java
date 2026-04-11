package com.buptqm.dao;

import com.buptqm.model.Application;
import com.buptqm.util.CSVUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationDAO {
    private static final String FILE_NAME = "applications.csv";

    static {
        CSVUtil.initFile(FILE_NAME, Application.getCSVHeader());
    }

    // 新增申请
    public void addApplication(Application app) {
        int nextId = CSVUtil.getNextId(FILE_NAME);
        app.setId(nextId);
        CSVUtil.appendLine(FILE_NAME, app.toCSVString());
    }

    // 根据TA ID查询申请记录
    public List<Application> getApplicationsByTaId(int taId) {
        List<String> lines = CSVUtil.readAllLines(FILE_NAME);
        List<Application> apps = new ArrayList<>();
        for (String line : lines) {
            Application app = Application.fromCSVString(line);
            if (app.getTaId() == taId) {
                apps.add(app);
            }
        }
        return apps;
    }

    // 根据职位ID查询申请记录
    public List<Application> getApplicationsByJobId(int jobId) {
        List<String> lines = CSVUtil.readAllLines(FILE_NAME);
        List<Application> apps = new ArrayList<>();
        for (String line : lines) {
            Application app = Application.fromCSVString(line);
            if (app.getJobId() == jobId) {
                apps.add(app);
            }
        }
        return apps;
    }

    // 更新申请状态
    public void updateApplication(Application updatedApp) {
        List<String> lines = CSVUtil.readAllLines(FILE_NAME);
        List<String> newLines = lines.stream()
                .map(line -> {
                    Application app = Application.fromCSVString(line);
                    if (app.getId() == updatedApp.getId()) {
                        return updatedApp.toCSVString();
                    }
                    return line;
                })
                .collect(Collectors.toList());
        CSVUtil.writeAllLines(FILE_NAME, newLines, Application.getCSVHeader());
    }
}