package com.buptqm.dao;

import com.buptqm.model.CV;
import com.buptqm.util.CSVUtil;

import java.util.List;

public class CVDAO {
    private static final String FILE_NAME = "cvs.csv";

    static {
        CSVUtil.initFile(FILE_NAME, CV.getCSVHeader());
    }

    // 新增简历
    public void addCV(CV cv) {
        int nextId = CSVUtil.getNextId(FILE_NAME);
        cv.setId(nextId);
        CSVUtil.appendLine(FILE_NAME, cv.toCSVString());
    }

    // 根据TA ID查询简历
    public CV getCVByTaId(int taId) {
        List<String> lines = CSVUtil.readAllLines(FILE_NAME);
        for (String line : lines) {
            CV cv = CV.fromCSVString(line);
            if (cv.getTaId() == taId) {
                return cv;
            }
        }
        return null;
    }

    // 根据ID查询简历
    public CV getCVById(int id) {
        List<String> lines = CSVUtil.readAllLines(FILE_NAME);
        for (String line : lines) {
            CV cv = CV.fromCSVString(line);
            if (cv.getId() == id) {
                return cv;
            }
        }
        return null;
    }
}