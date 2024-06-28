/*************************************************************************
 > File Name: RKRecoverySystem.java
 > Author: jkand.huang
 > Mail: jkand.huang@rock-chips.com
 > Created Time: Wed 02 Nov 2016 03:10:47 PM CST
 test
 ************************************************************************/
package com.bll.lnkstudy.utils;

import android.content.Context;
import android.os.RecoverySystem;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class HtRecoverySystem {

    private static final String TAG = "HtRecoverySystem";
    private static File RECOVERY_DIR = new File("/cache/recovery");
    private static File UPDATE_FLAG_FILE = new File(RECOVERY_DIR, "last_flag");

    public static void verifyPackage(File imagePath) throws GeneralSecurityException, IOException {
        RecoverySystem.verifyPackage(new File(String.valueOf(imagePath)), null, null);
    }

    public static void installPackage(Context context, File packageFile) throws IOException {
        String filename = packageFile.getCanonicalPath();
        writeFlagCommand(filename);
        RecoverySystem.installPackage(context, packageFile);
    }

    public static String readFlagCommand() {
        if (UPDATE_FLAG_FILE.exists()) {
            char[] buf = new char[128];
            int readCount = 0;
            try {
                FileReader reader = new FileReader(UPDATE_FLAG_FILE);
                readCount = reader.read(buf, 0, buf.length);
            } catch (IOException e) {
            } finally {
                UPDATE_FLAG_FILE.delete();

            }

            StringBuilder sBuilder = new StringBuilder();
            for (int i = 0; i < readCount; i++) {
                if (buf[i] == 0) {
                    break;
                }
                sBuilder.append(buf[i]);
            }
            return sBuilder.toString();
        } else {
            return null;
        }
    }

    public static void writeFlagCommand(String path) throws IOException {
        RECOVERY_DIR.mkdirs();
        UPDATE_FLAG_FILE.delete();
        try (FileWriter writer = new FileWriter(UPDATE_FLAG_FILE)) {
            writer.write("updating$path=" + path);
        }
    }

}
