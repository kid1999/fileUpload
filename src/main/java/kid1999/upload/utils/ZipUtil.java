package kid1999.upload.utils;

import kid1999.upload.dto.ZipModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @desc:
 * @auther: kid1999
 * @date: 2019/12/19 20:21
 **/
@Component
@Slf4j
public class ZipUtil {


    @Autowired
    private FastDFSClientUtils fastDFSClientUtils;

    /**
     * 压缩文件列表中的文件
     *
     * @param files
     * @param outputStream
     * @throws IOException
     */
    public void zipFile(List<ZipModel> files, ZipOutputStream outputStream){
        try {
            int size = files.size();
            //压缩列表中的文件
            for (int i = 0; i < size; i++) {
                ZipModel zipModel = files.get(i);
                zipFile(zipModel, outputStream);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 将文件写入到zip文件中
     *
     * @param zipModel
     * @param outputstream
     * @throws IOException
     */
    public  void zipFile(ZipModel zipModel, ZipOutputStream outputstream) throws IOException {
        if (zipModel != null && zipModel.getFilePath() != null && zipModel.getFileName() != null) {
            log.info(zipModel.getFileName() + ",被下载: " + zipModel.getFilePath());
            byte[] content = fastDFSClientUtils.downloadFile(zipModel.getFilePath());
            InputStream bInStream = new ByteArrayInputStream(content);
            ZipEntry entry = new ZipEntry(zipModel.getFileName());
            outputstream.putNextEntry(entry);
            final int MAX_BYTE = 10 * 1024 * 1024; //最大的流为10M
            long streamTotal = 0;      //接受流的容量
            int streamNum = 0;      //流需要分开的数量
            int leaveByte = 0;      //文件剩下的字符数
            byte[] inOutbyte;       //byte数组接受文件的数据

            streamTotal = bInStream.available();      //通过available方法取得流的最大字符数
            streamNum = (int) Math.floor(streamTotal / MAX_BYTE); //取得流文件需要分开的数量
            leaveByte = (int) streamTotal % MAX_BYTE;    //分开文件之后,剩余的数量

            if (streamNum > 0) {
                for (int j = 0; j < streamNum; ++j) {
                    inOutbyte = new byte[MAX_BYTE];
                    //读入流,保存在byte数组
                    bInStream.read(inOutbyte, 0, MAX_BYTE);
                    outputstream.write(inOutbyte, 0, MAX_BYTE); //写出流
                }
            }
            //写出剩下的流数据
            inOutbyte = new byte[leaveByte];
            bInStream.read(inOutbyte, 0, leaveByte);
            outputstream.write(inOutbyte);
            outputstream.closeEntry();
            bInStream.close(); //关闭
        }
    }

    /**
     * 下载打包的文件
     * @param file
     * @param response
     */
    public void downloadZip(File file, HttpServletResponse response) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            // 以流的形式下载文件。
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();

            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
            file.delete();  //将生成的服务器端文件删除
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
