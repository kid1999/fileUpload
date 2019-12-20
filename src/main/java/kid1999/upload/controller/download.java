package kid1999.upload.controller;

import kid1999.upload.dto.ZipModel;
import kid1999.upload.utils.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

@RestController
@Slf4j
public class download {

	@Autowired
	private ZipUtil zipUtil;

	@PostMapping("download")
	void download(HttpServletRequest request,
									 HttpServletResponse response) {
		try {
			if (request.getParameterValues("filenames") == null) {
				response.sendRedirect(request.getHeader("REFERER"));
			} else {
				List<ZipModel> zipModelList = new ArrayList<>();
				String[] filenames = request.getParameterValues("filenames");
				for (int i = 0; i < filenames.length; i++) {
					String[] files = filenames[i].split(" ");
					zipModelList.add(new ZipModel(files[0], files[1]));
				}
				//todo:设置打包后的文件名
				String fileName = "File.zip";
				//todo:临时文件目录,用于存储打包的下载文件
				String globalUploadPath = request.getSession().getServletContext().getRealPath("/");
				String outFilePath = globalUploadPath + File.separator + fileName;
				File file = new File(outFilePath);
				//文件输出流 压缩流
				ZipOutputStream toClient = new ZipOutputStream(new FileOutputStream(file));
				//todo:调用通用方法下载fastfds文件，打包成zip文件
				zipUtil.zipFile(zipModelList, toClient);
				toClient.close();
				response.setHeader("content-disposition", "attachment;fileName=" + fileName);
				//todo:将zip文件下载下来
				zipUtil.downloadZip(file, response);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
