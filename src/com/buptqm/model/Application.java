package com.buptqm.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Application {
    private int id;
    private int jobId;
    private int taId;
    private int cvId;
    private String status; // PENDING/ACCEPTED/REJECTED
    private LocalDateTime applyTime;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Application() {}

    public Application(int id, int jobId, int taId, int cvId, String status, LocalDateTime applyTime) {
        this.id = id;
        this.jobId = jobId;
        this.taId = taId;
        this.cvId = cvId;
        this.status = status;
        this.applyTime = applyTime;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public int getTaId() { return taId; }
    public void setTaId(int taId) { this.taId = taId; }
    public int getCvId() { return cvId; }
    public void setCvId(int cvId) { this.cvId = cvId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getApplyTime() { return applyTime; }
    public void setApplyTime(LocalDateTime applyTime) { this.applyTime = applyTime; }

    public String toCSVString() {
        return String.join(",",
                String.valueOf(id),
                String.valueOf(jobId),
                String.valueOf(taId),
                String.valueOf(cvId),
                status,
                applyTime.format(FORMATTER)
        );
    }

    public static Application fromCSVString(String csvLine) {
        String[] fields = csvLine.split(",");
        Application app = new Application();
        app.setId(Integer.parseInt(fields[0]));
        app.setJobId(Integer.parseInt(fields[1]));
        app.setTaId(Integer.parseInt(fields[2]));
        app.setCvId(Integer.parseInt(fields[3]));
        app.setStatus(fields[4]);
        app.setApplyTime(LocalDateTime.parse(fields[5], FORMATTER));
        return app;
    }

    public static String getCSVHeader() {
        return "id,jobId,taId,cvId,status,applyTime";
    }
}