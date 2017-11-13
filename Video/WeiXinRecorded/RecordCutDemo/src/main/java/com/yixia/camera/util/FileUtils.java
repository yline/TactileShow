package com.yixia.camera.util;

import java.io.File;

public class FileUtils {
    public static boolean deleteFile(File f) {
        if (f != null && f.exists() && !f.isDirectory()) {
            return f.delete();
        }
        return false;
    }

    public static void deleteDir(File f) {
        if (f != null && f.exists() && f.isDirectory()) {
            for (File file : f.listFiles()) {
                if (file.isDirectory()) {
                    deleteDir(file);
                }
                file.delete();
            }
            f.delete();
        }
    }
}
