package com.buptqm.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Job {
    private int id;
    private String title;
    private String description;
    private String requiredSkills; // 所需技能，逗号分隔
    private int moId; // 发布者MO的用户ID
    private String status; // OPEN/CLOSED
    private LocalDateTime createTime;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Job() {}

    public Job(int id, String title, String description, String requiredSkills, int moId, String status, LocalDateTime createTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.moId = moId;
        this.status = status;
        this.createTime = createTime;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }
    public int getMoId() { return moId; }
    public void setMoId(int moId) { this.moId = moId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public String toCSVString() {
        return String.join(",",
                String.valueOf(id),
                title,
                description,
                requiredSkills,
                String.valueOf(moId),
                status,
                createTime.format(FORMATTER)
        );
    }

    public static Job fromCSVString(String csvLine) {
        String[] fields = csvLine.split(",");
        Job job = new Job();
        job.setId(Integer.parseInt(fields[0]));
        job.setTitle(fields[1]);
        job.setDescription(fields[2]);
        job.setRequiredSkills(fields[3]);
        job.setMoId(Integer.parseInt(fields[4]));
        job.setStatus(fields[5]);
        job.setCreateTime(LocalDateTime.parse(fields[6], FORMATTER));
        return job;
    }

    public static String getCSVHeader() {
        return "id,title,description,requiredSkills,moId,status,createTime";
    }
}