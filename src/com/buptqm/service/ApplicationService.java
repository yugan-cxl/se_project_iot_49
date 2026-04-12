package com.buptqm.service;

import com.buptqm.dao.ApplicationDAO;
import com.buptqm.model.Application;
import java.util.List;

public class ApplicationService {
    private final ApplicationDAO appDAO = new ApplicationDAO();

    public void applyJob(Application app) {
        appDAO.addApplication(app);
    }

    public List<Application> getAppsByTaId(int taId) {
        return appDAO.getApplicationsByTaId(taId);
    }

    public List<Application> getAppsByJobId(int jobId) {
        return appDAO.getApplicationsByJobId(jobId);
    }

    public void updateApp(Application app) {
        appDAO.updateApplication(app);
    }
}