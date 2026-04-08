package com.buptqm.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CV {
    private int id;
    private int taId;
    private String content;
    private LocalDateTime uploadTime;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CV() {}

    public CV(int id, int taId, String content, LocalDateTime uploadTime) {
        this.id = id;
        this.taId = taId;
        this.content = content;
        this.uploadTime = uploadTime;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTaId() { return taId; }
    public void setTaId(int taId) { this.taId = taId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getUploadTime() { return uploadTime; }
    public void setUploadTime(LocalDateTime uploadTime) { this.uploadTime = uploadTime; }

    public String toCSVString() {
        return String.join(",",
                String.valueOf(id),
                String.valueOf(taId),
                content.replace(",", "，"), // 处理内容中的逗号
                uploadTime.format(FORMATTER)
        );
    }

    public static CV fromCSVString(String csvLine) {
        String[] fields = csvLine.split(",", 4); // 限制分割，避免内容逗号影响
        CV cv = new CV();
        cv.setId(Integer.parseInt(fields[0]));
        cv.setTaId(Integer.parseInt(fields[1]));
        cv.setContent(fields[2]);
        cv.setUploadTime(LocalDateTime.parse(fields[3], FORMATTER));
        return cv;
    }

    public static String getCSVHeader() {
        return "id,taId,content,uploadTime";
    }
}