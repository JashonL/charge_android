package com.growatt.chargingpile.util;


import java.io.File;
import java.io.FilenameFilter;

public class FileUtils {

    public static boolean hasSdcard() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }


    public static void delete(String path, FilenameFilter filenameFilter) {
        File file=new File(path);
        File[] files = file.listFiles(filenameFilter);
        if (files!=null&&files.length>0){
            for (File f:
                    files) {
                f.delete();
            }
        }
    }



    /*
     * Java文件操作 获取不带扩展名的文件名
     *
     *  Created on: 2011-8-2
     *      Author: blueeagle
     */
    public static String getFileNameWithoutExtension(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }


}
