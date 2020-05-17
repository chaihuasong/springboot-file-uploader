package cn.htz.myuploader.service;

import cn.htz.myuploader.config.UploadConfig;
import cn.htz.myuploader.dao.FileDao;
import cn.htz.myuploader.model.File;
import cn.htz.myuploader.utils.FileUtils;
import cn.htz.myuploader.utils.UpdateJson;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import static cn.htz.myuploader.utils.UploadUtils.*;

/**
 * 文件上传服务
 */
@Service
public class FileService {

    @Autowired
    private FileDao fileDao;


    /**
     * 上传文件
     *
     * @param md5
     * @param file
     */
    public void upload(String name,
                       String md5,
                       MultipartFile file) throws IOException {
        String path = UploadConfig.path + file.getOriginalFilename();//generateFileName();
        FileUtils.write(path, file.getInputStream());
        fileDao.save(new File(name, md5, path, new Date(), FileUtils.getExt(file)));
    }

    /**
     * 分块上传文件
     *
     * @param md5
     * @param size
     * @param chunks
     * @param chunk
     * @param file
     * @throws IOException
     */
    public void uploadWithBlock(String name,
                                String md5,
                                Long size,
                                Integer chunks,
                                Integer chunk,
                                MultipartFile file) throws IOException {
        String fileName = getFileName(md5, chunks, name);
        FileUtils.writeWithBlok(UploadConfig.path + fileName, size, file.getInputStream(), file.getSize(), chunks, chunk);
        addChunk(md5, chunk);
        if (isUploaded(md5)) {
            removeKey(md5);
            fileDao.save(new File(name, md5, UploadConfig.path + fileName, new Date(), FileUtils.getExt(file)));
        }
    }

    /**
     * 检查Md5判断文件是否已上传
     * true:  未上传
     * false: 已上传
     *
     * @param md5
     * @return
     */
    public boolean checkMd5(String md5) {
        File file = new File();
        file.setMd5(md5);
        return fileDao.getByFile(file) == null;
    }

    public void update(UpdateJson updateJson) throws IOException {
        String path = UploadConfig.path + "UpdateManifest.json";
        JSONObject updateObject = new JSONObject();
        JSONObject patchObject = new JSONObject();
        JSONObject p1Object = new JSONObject();
        JSONObject v1Object = new JSONObject();
        v1Object.put("patchURL", "http://htzshanghai.top/resources/apk/update/v1/diff.patch");
        v1Object.put("tip", "patch v1");
        v1Object.put("hash", updateJson.getMd5());
        v1Object.put("size", updateJson.getSize());
        p1Object.put("v1", v1Object);
        patchObject.put("patchInfo", p1Object);

        updateObject.put("minVersion", "1");
        updateObject.put("minAllowPatchVersion", updateJson.getVersionNumber());
        updateObject.put("newVersion", updateJson.getVersionNumber());
        updateObject.put("newVersionName", updateJson.getVersionName());
        updateObject.put("tip", updateJson.getContent());
        updateObject.put("size", updateJson.getSize());
        updateObject.put("apkURL", "http://121.36.132.237/resources/apk/release/" + updateJson.getName());
        updateObject.put("hash", updateJson.getMd5());
        updateObject.put("patchInfo", patchObject);

        FileUtils.write(path, new ByteArrayInputStream(updateObject.toString().getBytes()));

        path = UploadConfig.path + "release_info.txt";

        FileUtils.write(path, updateJson.getName(), updateJson.getVersionName(), updateJson.getContent());

        path = "/var/www/html/redirect.html";
        FileUtils.write(path, updateJson.getName());
    }
}
