package kid1999.upload.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import kid1999.upload.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;

/**
 * @desc:
 * @auther: kid1999
 * @date: 2019/12/21 12:20
 **/
@Slf4j
@Controller
@RequestMapping("/api/kaptcha")
public class KaptchaController {
    @Autowired
    DefaultKaptcha defaultKaptcha;

    /**
     * 生成验证码
     * @throws Exception
     */
    @RequestMapping("/vrifyCode")
    public ModelAndView getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("生成验证码");
        HttpSession session = request.getSession();
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control","no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        // 获取KAPTCHA验证的随机文本
        String capText = defaultKaptcha.createText();
        // 将生成好的图片放入会话中
        session.setAttribute("vrifyCode", capText);
        // create the image with the text
        BufferedImage bi = defaultKaptcha.createImage(capText);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();//关闭
        }
        return null;
    }

    /**
     * 校对验证码
     * @return
     */
    public Result checkVerificationCode(String vrifyCode, HttpServletRequest request) {
        String verificationCodeIn = (String) request.getSession().getAttribute("vrifyCode");
        request.getSession().removeAttribute("vrifyCode");
        if (StringUtils.isEmpty(verificationCodeIn) || !verificationCodeIn.equals(vrifyCode)) {
            return Result.fail(400,"验证码错误，或已失效");
        }
        return Result.success("验证通过");
    }
}
