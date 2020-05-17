package cn.htz.myuploader.utils;


import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

/**
 * 文件操作工具类
 */
public class FileUtils {

    /**
     * 写入文件
     * @param target
     * @param src
     * @throws IOException
     */
    public static void write(String target, InputStream src) throws IOException {
        OutputStream os = new FileOutputStream(target);
        byte[] buf = new byte[1024];
        int len;
        while (-1 != (len = src.read(buf))) {
            os.write(buf,0,len);
        }
        os.flush();
        os.close();
    }

    /**
     * 写入文件
     * @param target
     * @param name
     * @param versionName
     * @param content
     * @throws IOException
     */
    public static void write(String target, String name, String versionName, String content) throws IOException {
        String result = ReadString(target);
        result = "v" + versionName + " (" + name + ")\n" + content + "\n\n" + result;

        write(target, new ByteArrayInputStream(result.getBytes()));
    }

    /**
     * 写入文件
     * @param target
     * @throws IOException
     */
    public static void write(String target, String name) throws IOException {
        String result = ReadString(target);
        result = result.replaceAll("apk/release/.+?\"><font", "apk/release/" + name + "\"><font");

        write(target, new ByteArrayInputStream(result.getBytes()));
    }

    //将file转化成string
    private static String ReadString(String filePath)throws IOException{
        //对一串字符进行操作
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        //缓冲区使用完必须关掉
        reader.close();
        return fileData.toString();
    }

    /**
     * 分块写入文件
     * @param target
     * @param targetSize
     * @param src
     * @param srcSize
     * @param chunks
     * @param chunk
     * @throws IOException
     */
    public static void writeWithBlok(String target, Long targetSize, InputStream src, Long srcSize, Integer chunks, Integer chunk) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(target,"rw");
        randomAccessFile.setLength(targetSize);
        if (chunk == chunks - 1 && chunk != 0) {
            randomAccessFile.seek(chunk * (targetSize - srcSize) / chunk);
        } else {
            randomAccessFile.seek(chunk * srcSize);
        }
        byte[] buf = new byte[1024];
        int len;
        while (-1 != (len = src.read(buf))) {
            randomAccessFile.write(buf,0,len);
        }
        randomAccessFile.close();
    }

    /**
     * 生成随机文件名
     * @return
     */
    public static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取文件扩展名
     * @param file
     * @return
     */
    public static String getExt(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
