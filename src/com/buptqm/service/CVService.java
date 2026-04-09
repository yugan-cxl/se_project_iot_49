package com.buptqm.service;

import com.buptqm.dao.CVDAO;
import com.buptqm.model.CV;

public class CVService {
    private final CVDAO cvDAO = new CVDAO();

    public void uploadCV(CV cv) {
        cvDAO.addCV(cv);
    }

    public CV getCVByTaId(int taId) {
        return cvDAO.getCVByTaId(taId);
    }
}