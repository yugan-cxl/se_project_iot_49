package com.buptqm.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {
    private static final String DATA_DIR = "data";

    // 初始化数据目录和文件
    public static void initFile(String fileName, String header) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            if (!file.exists()) {
                file.createNewFile();
                try (FileWriter fw = new FileWriter(file)) {
                    fw.write(header + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 读取CSV所有行（跳过表头）
    public static List<String> readAllLines(String fileName) {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(DATA_DIR, fileName);
            if (!file.exists()) return lines;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;
                lines.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    // 追加写入一行数据
    public static void appendLine(String fileName, String line) {
        try {
            File file = new File(DATA_DIR, fileName);
            FileWriter fw = new FileWriter(file, true);
            fw.write(line + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 覆盖写入所有数据
    public static void writeAllLines(String fileName, List<String> lines, String header) {
        try {
            File file = new File(DATA_DIR, fileName);
            FileWriter fw = new FileWriter(file);
            fw.write(header + "\n");
            for (String line : lines) {
                fw.write(line + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取下一个自增ID
    public static int getNextId(String fileName) {
        List<String> lines = readAllLines(fileName);
        if (lines.isEmpty()) return 1;
        int max = 0;
        for (String line : lines) {
            try {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                if (id > max) max = id;
            } catch (Exception ignored) {}
        }
        return max + 1;
    }
}