package kid1999.upload.utils;

import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import kid1999.upload.config.fastDfsConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author kid1999
 * @desc:
 * @auther: kid1999
 * @date: 2019/12/19 19:25
 **/

@Slf4j
@Component
public class FastDFSClientUtils {
    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private fastDfsConfig appConfig;   // 项目参数配置

    /**
     * 上传文件
     * @param file 文件对象
     * @return 文件访问地址
     * @throws IOException
     */
    public String uploadFile(MultipartFile file) throws IOException {
        StorePath storePath = storageClient.uploadFile(file.getInputStream(),file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()),null);
        return getResAccessUrl(storePath);
    }

    /**
     * 查询文件信息
     * @param fileUrl
     * @return  FileInfo 远程文件信息
     */
    public FileInfo getFileInfo(String fileUrl){
        if (StringUtils.isEmpty(fileUrl)) {
            return null;
        }
        StorePath storePath = StorePath.parseFromUrl(fileUrl);
        return storageClient.queryFileInfo(storePath.getGroup(), storePath.getPath());
    }



    /**
     * 删除文件
     * @param fileUrl 文件访问地址
     * @return
     */
    public void deleteFile(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            return;
        }
        try {
            StorePath storePath = StorePath.parseFromUrl(fileUrl);
            storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
        } catch (FdfsUnsupportStorePathException e) {
            log.warn(e.getMessage());
        }
    }

    /**
     * 下载文件
     * @param fileUrl  文件访问地址
     * @return
     */
    public byte[] downloadFile(String fileUrl){
        if(StringUtils.isEmpty(fileUrl)){
            return null;
        }
        try {
            StorePath storePath = StorePath.parseFromUrl(fileUrl);
            DownloadByteArray callback = new DownloadByteArray();
            byte[] content = storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), callback);
            return content;
        }catch (Exception e){
            log.warn(e.getMessage());
        }
        return null;
    }

    /**
     * 更新文件（复写）
     * @param file
     * @param oldFileUrl    原文件
     * @return  文件路径
     * @throws IOException
     */
    public String updateFile(MultipartFile file,String oldFileUrl) throws IOException {
        deleteFile(oldFileUrl);
        StorePath storePath = storageClient.uploadFile(file.getInputStream(),file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()),null);
        return getResAccessUrl(storePath);
    }

    /**
     * 封装完整URL地址
     * @param storePath
     */
    private String getResAccessUrl(StorePath storePath) {
        String fileUrl = "http://" + appConfig.getResHost()
                + ":" + appConfig.getStoragePort() + "/" + storePath.getFullPath();
        return fileUrl;
    }

}
