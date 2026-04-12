package com.buptqm.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Job {
    private int id;
    private String title;
    private String description;
    private String requiredSkills;
    private int moId;
    private String status;
    private LocalDateTime createTime; // 你有这个字段

    // ====================== GETTER / SETTER ======================
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

    // ====================== CSV 转义工具 ======================
    public static String escapeCsv(String value) {
        if (value == null) return "";
        boolean needQuotes = value.contains(",") || value.contains("\n") || value.contains("\"");
        if (needQuotes) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }

    public static String unescapeCsv(String value) {
        if (value == null || value.isEmpty()) return "";
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }

    public String toCSVString() {
        return id + "," +
                escapeCsv(title) + "," +
                escapeCsv(description) + "," +
                escapeCsv(requiredSkills) + "," +
                moId + "," +
                status + "," +
                createTime; 
    }

    public static Job fromCSVString(String csvLine) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < csvLine.length(); i++) {
            char c = csvLine.charAt(i);
            if (c == '"') {
                if (inQuotes && i+1 < csvLine.length() && csvLine.charAt(i+1) == '"') {
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        parts.add(cur.toString());

        if (parts.size() < 7) return null; 

        try {
            Job job = new Job();
            job.setId(Integer.parseInt(parts.get(0).trim()));
            job.setTitle(unescapeCsv(parts.get(1).trim()));
            job.setDescription(unescapeCsv(parts.get(2).trim()));
            job.setRequiredSkills(unescapeCsv(parts.get(3).trim()));
            job.setMoId(Integer.parseInt(parts.get(4).trim()));
            job.setStatus(parts.get(5).trim());

            // 
            if (parts.get(6) != null && !parts.get(6).trim().isEmpty()) {
                job.setCreateTime(LocalDateTime.parse(parts.get(6).trim()));
            } else {
                job.setCreateTime(LocalDateTime.now());
            }

            return job;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 
    public static String getCSVHeader() {
        return "id,title,description,requiredSkills,moId,status,createTime";
    }
}