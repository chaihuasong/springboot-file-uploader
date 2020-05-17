package cn.htz.myuploader.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UploadConfig {

    public static String apkPath;
    public static String path;

    @Value("${upload.apkPath}")
    public void setApkPath(String apkPath) {
        UploadConfig.apkPath = apkPath;
    }

    @Value("${upload.path}")
    public void setPath(String path) {
        UploadConfig.path = path;
    }
}
