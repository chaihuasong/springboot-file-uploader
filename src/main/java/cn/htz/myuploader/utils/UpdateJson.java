package cn.htz.myuploader.utils;

import lombok.Data;

@Data
public class UpdateJson {
    private String versionNumber;
    private String versionName;
    private String name;
    private String md5;
    private String content;
    private long size;
}
