package com.buptqm.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class CV {
    private int id;
    private int taId;
    private String name;
    private String email;
    private String major;
    private String tel;
    private String educationBackground;
    private String skillsAbilities;
    private String relevantExperience;
    private String selfIntroduction;

    public CV() {}

    public CV(int id, int taId, String name, String email, String major, String tel,
              String educationBackground, String skillsAbilities,
              String relevantExperience, String selfIntroduction) {
        this.id = id;
        this.taId = taId;
        this.name = name;
        this.email = email;
        this.major = major;
        this.tel = tel;
        this.educationBackground = educationBackground;
        this.skillsAbilities = skillsAbilities;
        this.relevantExperience = relevantExperience;
        this.selfIntroduction = selfIntroduction;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTaId() { return taId; }
    public void setTaId(int taId) { this.taId = taId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public String getEducationBackground() { return educationBackground; }
    public void setEducationBackground(String educationBackground) { this.educationBackground = educationBackground; }

    public String getSkillsAbilities() { return skillsAbilities; }
    public void setSkillsAbilities(String skillsAbilities) { this.skillsAbilities = skillsAbilities; }

    public String getRelevantExperience() { return relevantExperience; }
    public void setRelevantExperience(String relevantExperience) { this.relevantExperience = relevantExperience; }

    public String getSelfIntroduction() { return selfIntroduction; }
    public void setSelfIntroduction(String selfIntroduction) { this.selfIntroduction = selfIntroduction; }


    private static String escapeCsv(String value) {
        if (value == null) return "";
        boolean needQuotes = value.contains(",") || value.contains("\n") || value.contains("\"");
        if (needQuotes) {
            value = value.replace("\"", "\"\""); // 双引号转义
            return "\"" + value + "\"";
        }
        return value;
    }

    private static String unescapeCsv(String value) {
        if (value == null || value.isEmpty()) return "";
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }

    // ========== 保存到 CSV 的行 ==========
    public String toCSVString() {
        return id + "," +
                escapeCsv(String.valueOf(taId)) + "," +
                escapeCsv(name) + "," +
                escapeCsv(email) + "," +
                escapeCsv(major) + "," +
                escapeCsv(tel) + "," +
                escapeCsv(educationBackground) + "," +
                escapeCsv(skillsAbilities) + "," +
                escapeCsv(relevantExperience) + "," +
                escapeCsv(selfIntroduction);
    }

    // ========== 从 CSV 行解析 ==========
    public static CV fromCSVString(String csvLine) {
        // 手动解析，支持引号内的逗号
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < csvLine.length(); i++) {
            char c = csvLine.charAt(i);
            if (c == '"') {
                // 处理转义的双引号 ""
                if (inQuotes && i + 1 < csvLine.length() && csvLine.charAt(i + 1) == '"') {
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

        // CV 应有 10 个字段 (id, taId, name, email, major, tel, educationBackground, skillsAbilities, relevantExperience, selfIntroduction)
        if (parts.size() < 10) {
            System.err.println("CV.fromCSVString: 字段数不足，实际=" + parts.size() + "，行内容=" + csvLine);
            return null;
        }

        try {
            CV cv = new CV();
            cv.setId(Integer.parseInt(parts.get(0).trim()));
            cv.setTaId(Integer.parseInt(parts.get(1).trim()));
            cv.setName(unescapeCsv(parts.get(2).trim()));
            cv.setEmail(unescapeCsv(parts.get(3).trim()));
            cv.setMajor(unescapeCsv(parts.get(4).trim()));
            cv.setTel(unescapeCsv(parts.get(5).trim()));
            cv.setEducationBackground(unescapeCsv(parts.get(6).trim()));
            cv.setSkillsAbilities(unescapeCsv(parts.get(7).trim()));
            cv.setRelevantExperience(unescapeCsv(parts.get(8).trim()));
            cv.setSelfIntroduction(unescapeCsv(parts.get(9).trim()));
            return cv;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // CSV 表头
    public static String getCSVHeader() {
        return "id,taId,name,email,major,tel,educationBackground,skillsAbilities,relevantExperience,selfIntroduction";
    }
}