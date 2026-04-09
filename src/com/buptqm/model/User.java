package com.buptqm.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String realName;
    private String email;
    private Role role;
    private int workload;
    private String skills;

    public User() {}

    public User(int id, String username, String password, String realName, String email, Role role, int workload, String skills) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.email = email;
        this.role = role;
        this.workload = workload;
        this.skills = skills;
    }

    // GET SET
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    //public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public int getWorkload() { return workload; }
    public void setWorkload(int workload) { this.workload = workload; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    // 转CSV
    public String toCSVString() {
        return id + "," +
                username + "," +
                password + "," +
                (realName == null ? "" : realName) + "," +
                (email == null ? "" : email) + "," +
                (role == null ? "TA" : role.name()) + "," +
                workload + "," +
                (skills == null ? "" : skills);
    }

    // 从CSV解析（改进版）
    public static User fromCSVString(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            throw new IllegalArgumentException("CSV line cannot be null or empty");
        }

        String[] fields = csvLine.split(",", -1);

        try {
            User u = new User();
            u.setId(fields.length > 0 && !fields[0].isEmpty() ? Integer.parseInt(fields[0]) : 0);
            u.setUsername(fields.length > 1 ? fields[1] : "");
            u.setPassword(fields.length > 2 ? fields[2] : "");
            u.setRealName(fields.length > 3 ? fields[3] : "");
            u.setEmail(fields.length > 4 ? fields[4] : "");
            u.setRole(fields.length > 5 && !fields[5].isEmpty() ? Role.valueOf(fields[5]) : Role.TA);
            u.setWorkload(fields.length > 6 && !fields[6].isEmpty() ? Integer.parseInt(fields[6]) : 0);
            u.setSkills(fields.length > 7 ? fields[7] : "");
            return u;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to parse number in CSV line: " + csvLine, e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role value in CSV line: " + csvLine, e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error parsing CSV line: " + csvLine, e);
        }
    }

    public static String getCSVHeader() {
        return "id,username,password,realName,email,role,workload,skills";
    }
}