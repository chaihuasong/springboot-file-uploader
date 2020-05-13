package cn.htz.myuploader.controller;

import cn.htz.myuploader.service.FileService;
import cn.htz.myuploader.utils.UpdateJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 大文件上传
 */
@RestController
@RequestMapping("/APKFile")
@CrossOrigin
@Api(tags = "APK信息相关接口", description = "提供APK信息相关的 Rest API")
public class APKInfoController {
    @Autowired
    private FileService fileService;


    /**
     * @param updateJson 文件参数信息
     * @throws IOException
     */
    @ApiOperation("APK信息修改接口")
    @PostMapping("/info")
    public void info(@RequestBody UpdateJson updateJson) throws IOException {
        fileService.update(updateJson);
    }
}
