package com.buptqm.dao;

import com.buptqm.model.Job;
import com.buptqm.util.CSVUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JobDAO {
    private static final String FILE_NAME = "jobs.csv";

    static {
        CSVUtil.initFile(FILE_NAME, Job.getCSVHeader());
    }

    // 新增职位
    public void addJob(Job job) {
        int nextId = CSVUtil.getNextId(FILE_NAME);
        job.setId(nextId);
        CSVUtil.appendLine(FILE_NAME, job.toCSVString());
    }

    // 查询所有开放的职位
    public List<Job> getAllOpenJobs() {
        List<String> lines = CSVUtil.readAllLines(FILE_NAME);
        List<Job> jobs = new ArrayList<>();
        for (String line : lines) {
            Job job = Job.fromCSVString(line);
            if ("OPEN".equals(job.getStatus())) {
                jobs.add(job);
            }
        }
        return jobs;
    }

    // 根据MO ID查询发布的职位
    public List<Job> getJobsByMoId(int moId) {
        List<String> lines = CSVUtil.readAllLines(FILE_NAME);
        List<Job> jobs = new ArrayList<>();
        for (String line : lines) {
            Job job = Job.fromCSVString(line);
            if (job.getMoId() == moId) {
                jobs.add(job);
            }
        }
        return jobs;
    }

    // 根据ID查询职位
    public Job getJobById(int id) {
        List<String> lines = CSVUtil.readAllLines(FILE_NAME);
        for (String line : lines) {
            Job job = Job.fromCSVString(line);
            if (job.getId() == id) {
                return job;
            }
        }
        return null;
    }

    // 更新职位信息
    public void updateJob(Job updatedJob) {
        List<String> lines = CSVUtil.readAllLines(FILE_NAME);
        List<String> newLines = lines.stream()
                .map(line -> {
                    Job job = Job.fromCSVString(line);
                    if (job.getId() == updatedJob.getId()) {
                        return updatedJob.toCSVString();
                    }
                    return line;
                })
                .collect(Collectors.toList());
        CSVUtil.writeAllLines(FILE_NAME, newLines, Job.getCSVHeader());
    }
}