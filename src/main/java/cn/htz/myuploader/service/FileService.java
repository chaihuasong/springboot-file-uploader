package cn.htz.myuploader.service;

import cn.htz.myuploader.config.UploadConfig;
import cn.htz.myuploader.dao.FileDao;
import cn.htz.myuploader.model.File;
import cn.htz.myuploader.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
}