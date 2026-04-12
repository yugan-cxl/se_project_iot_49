package com.buptqm.service;

import com.buptqm.dao.JobDAO;
import com.buptqm.model.Job;
import java.util.List;

public class JobService {
    private final JobDAO jobDAO = new JobDAO();

    public void publishJob(Job job) {
        jobDAO.addJob(job);
    }

    public List<Job> getOpenJobs() {
        return jobDAO.getAllOpenJobs();
    }

    public List<Job> getJobsByMoId(int moId) {
        return jobDAO.getJobsByMoId(moId);
    }

    public Job getJobById(int id) {
        return jobDAO.getJobById(id);
    }

    public void updateJob(Job job) {
        jobDAO.updateJob(job);
    }
}