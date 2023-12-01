package com.bll.lnkstudy.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtils {
    /**
     * 读取文本内容String
     *
     * @param inputStream
     * @return 读取文本内容String
     */
    public static String readFileContent(InputStream inputStream) {

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(inputStreamReader);
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    /**
     * 将str转换为inputStream
     *
     * @param str
     * @return
     */
    public static InputStream str2InputStream(String str) {
        ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes());
        return is;
    }

    /**
     * 将inputStream转换为str
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String inputStream2Str(InputStream is) throws IOException {
        StringBuffer sb;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));

            sb = new StringBuffer();

            String data;
            while ((data = br.readLine()) != null) {
                sb.append(data);
            }
        } finally {
            br.close();
        }

        return sb.toString();
    }

    /**
     * 将file转换为inputStream
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream file2InputStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    /**
     * 将inputStream转化为file
     *
     * @param is
     * @param file 要输出的文件目录
     */
    public static void inputStream2File(InputStream is, File file) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int len = 0;
            byte[] buffer = new byte[8192];

            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } finally {
            os.close();
            is.close();
        }
    }

    /**
     *
     * 获取目录下文件名  不包含文件目录下的子文件目录
     * @Author：
     * @Description：获取某个目录下所有直接下级文件，不包括目录下的子目录的下的文件，所以不用递归获取
     * @Date：
     */
    public static List<String> getFilesName(String path) {
        List<String> files = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i].getName());
            }
        }
        return files;
    }

    /**
     * 获取目录下文件对象  不包含文件目录下的子文件目录
     * @param path
     * @return
     */
    public static List<File> getFiles(String path){
        List<File> files = new ArrayList<>();
        if(path.isEmpty()){
            return null;
        }
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList==null) return null;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i]);
            }
        }
//        //文件排序
        sortFiles(files);
        return files;
    }

    /**
     * 获取目录下文件夹
     * @param path
     * @return
     */
    public static List<File> getDirectorys(String path){
        List<File> files = new ArrayList<>();
        if(path==null||path.isEmpty()){
            return null;
        }
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList==null) return null;
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isDirectory()) {
                files.add(tempList[i]);
            }
        }
        return files;
    }


    /**
     * 获取目录下指定后缀文件对象  不包含文件目录下的子文件目录
     * @param path
     * @param suffix
     * @return
     */
    public static List<File> getFiles(String path,String suffix){
        List<File> files = new ArrayList<>();
        if("".equals(path)){
            return null;
        }
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList==null) return null;
        for (int i = 0; i < tempList.length; i++) {
            File childFile=tempList[i];
            if (childFile.isFile()&&childFile.getName().endsWith(suffix)) {
                files.add(tempList[i]);
            }
        }
        //文件排序
        sortFiles(files);
        return files;
    }

    /**
     * 删除指定文件夹里的 指定文件
     * @param path 文件夹路径
     * @param name 文件名
     */
    public static void deleteFile(String path,String name){
        List<File> files=getFiles(path);
        if (files!=null){
            for (int i = 0; i < files.size(); i++) {
                File file=files.get(i);
                if (getFileName(file.getName()).equals(name)){
                    deleteFile(file);
                }
            }
        }
    }

    public static void deleteFile(File file){
        if (file == null || !file.exists() )
            return;
        // 判断传递进来的是文件还是文件夹,如果是文件,直接删除,如果是文件夹,则判断文件夹里面有没有东西
        if (file.isDirectory()) {
            // 如果是目录,就删除目录下所有的文件和文件夹
            File[] files = file.listFiles();
            // 遍历目录下的文件和文件夹
            for (File f : files) {
                // 如果是文件,就删除
                if (f.isFile()) {
                    // 删除文件
                    f.delete();
                } else{
                    // 如果是文件夹,就递归调用文件夹的方法
                    deleteFile(f);
                }
            }
            file.delete();
        }
        else {
            file.delete();
        }
    }
    /**
     * 文件夹排序
     * @param files
     */
    public static void sortFiles(List<File> files) {
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs,File rhs) {
                //返回负数表示o1 小于o2，返回0 表示o1和o2相等，返回正数表示o1大于o2。
                boolean l1 = lhs.isDirectory();
                boolean l2 = rhs.isDirectory();
                if (l1 && !l2)
                    return -1;
                else if (!l1 && l2)
                    return 1;
                else {
                    return lhs.getName().compareTo(rhs.getName());
                }

//                if (lhs.lastModified() < rhs.lastModified()) {
//                    return -1;
//                } else if (lhs.lastModified() == rhs.lastModified()) {
//                    return 0;
//                } else {
//                    return 1;
//                }

            }
        });
    }

    /**
     * 复制文件到指定文件夹
     * @param oldPathName
     * @param newPathName
     * @return
     */
    public static boolean copyFile(String oldPathName, String newPathName) {
        try {
            File oldFile = new File(oldPathName);
            if (!oldFile.exists()) {
                Log.d("debug", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.d("debug", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.d("debug", "copyFile:  oldFile cannot read.");
                return false;
            }
            File newFile = new File(newPathName);
            if (!newFile.exists()){
                newFile.getParentFile().mkdirs();
                newFile.createNewFile();
            }
            FileInputStream fileInputStream = new FileInputStream(oldPathName);
            FileOutputStream fileOutputStream = new FileOutputStream(newPathName);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * name1.txt   -> name1
     * @param fileNameStr
     * @return
     */
    public static String getFileName(String fileNameStr){
        return fileNameStr.substring(0,fileNameStr.lastIndexOf("."));
    }

    public static boolean isExist(String path){
        return new File(path).exists();
    }

    /**
     * 判断文件夹是否存在内容
     * @param path
     * @return
     */
    public static boolean isExistContent(String path){
        List<File> files=getFiles(path);
        return files!=null&&files.size()>0;
    }
}
