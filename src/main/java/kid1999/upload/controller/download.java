package kid1999.upload.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import kid1999.upload.service.homeworkService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class download {

	@Value("${TMPFILEPATH}")
	private String TMPFILEPATH;

	@Autowired
	private homeworkService homeworkService;

	// 下载文件
	@PostMapping("download")
	ResponseEntity<byte[]> download(HttpServletRequest request,
									HttpServletResponse response) throws Exception {
		String userid = request.getParameter("studentid");
		if(request.getParameterValues("filenames") == null){
			response.sendRedirect(request.getHeader("REFERER"));
			return null;
		}else{
			String[] filenames = request.getParameterValues("filenames");
			int uid = Integer.parseInt(userid);
			String addr = homeworkService.findaddrBySid(uid);
			String tmpfile = Paths.get(TMPFILEPATH,userid).toString() + ".zip";    //缓存文件

			ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(tmpfile));  // 输出文件
			InputStream input = null;

			// 将选择文件一一写入
			for (String str : filenames) {
				input = new FileInputStream(new File(addr,str));
				zipOut.putNextEntry(new ZipEntry(str));
				byte[] buff = new byte[1024];
				while((input.read(buff)) != -1){
					zipOut.write(buff);
				}
				input.close();
			}
			zipOut.close();
			File file = new File(tmpfile);
			HttpHeaders headers = new HttpHeaders();
//		String filename = new String(tmpfile.getBytes("utf-8"),"iso-8859-1");   //中文文件名
			headers.setContentDispositionFormData("attachment", "result.zip");  //设置下载文件名
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			ResponseEntity<byte[]> res = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),headers, HttpStatus.CREATED);
			Files.deleteIfExists(Paths.get(tmpfile));   // 清除缓存
			return res;
		}
	}

}
