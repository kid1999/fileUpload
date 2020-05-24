package kid1999.upload;

import kid1999.upload.utils.MailUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author kid1999
 * @title: MailTest
 * @date 2019/12/22 15:43
 */
@EnableAsync
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailTest {

	@Autowired
	private MailUtil mailUtil;

	@Autowired
	private TemplateEngine templateEngine;

	private String receptionMailAddr = "kid.1447250889@live.com";


	@Test
	public void contextLoads() {
		mailUtil.sendTextMail(receptionMailAddr,"this is title","this is infomation");
	}

	@Test
	public void testHtmlMail(){
		String content="<html>\n" +
				"<body>\n" +
				"    <h3>hello world ! 这是一封html邮件!</h3>\n" +
				"</body>\n" +
				"</html>";
		mailUtil.sendHtmlMail(receptionMailAddr,"test simple mail",content);
		System.out.println("发送成功");
	}

	@Test
	public void sendAttachmentsMail() {
		String filePath="C:\\\\Users\\\\Administrator\\\\Desktop\\\\java并发学习.txt";
		mailUtil.sendAttachmentsMail(receptionMailAddr, "主题：带附件的邮件", "有附件，请查收！", filePath);
	}


	@Test
	public void sendInlineResourceMail() {
		String rscId = "neo006";
		String content="<html><body>这是有图片的邮件：<img src=\'cid:" + rscId + "\' ></body></html>";
		String imgPath = "C:\\\\Users\\\\Administrator\\\\Desktop\\\\testMail.png";

		mailUtil.sendInlineResourceMail(receptionMailAddr, "主题：这是有图片的邮件", content, imgPath, rscId);
	}


//	@Test
//	public void sendTemplateMail() {
//		//创建邮件正文
//		Context context = new Context();
//		context.setVariable("id", "006");
//
//		// 传递 emailTemplate.html 模板需要的值，并将模板转换为 String
//		String emailContent = templateEngine.process("emailTemplate", context);
//
//		mailUtil.sendHtmlMail(receptionMailAddr,"主题：这是模板邮件",emailContent);
//	}

}
