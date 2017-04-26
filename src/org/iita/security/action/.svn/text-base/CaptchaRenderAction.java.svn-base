/**
 * iita-common-web.struts Jan 29, 2010
 */
package org.iita.security.action;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.iita.struts.BaseAction;
import org.iita.struts.DownloadableStream;

import com.octo.captcha.service.image.ImageCaptchaService;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

/**
 * Action to render captcha image.
 * 
 * @author mobreza
 */
@SuppressWarnings("serial")
public class CaptchaRenderAction extends BaseAction implements DownloadableStream, CaptchaValidator {
	public static final String CAPTCHA_IMAGE_FORMAT = "png";

	private ImageCaptchaService captchaService;

	private ByteArrayInputStream outputStream;

	/**
	 * @param captchaService
	 * 
	 */
	public CaptchaRenderAction() {

	}

	/**
	 * @see org.iita.security.action.CaptchaValidator#setCaptchaService(com.octo.captcha.service.image.ImageCaptchaService)
	 */
	public void setCaptchaService(ImageCaptchaService captchaService) {
		this.captchaService = captchaService;
	}

	/**
	 * @see org.iita.struts.BaseAction#execute()
	 */
	@Override
	public String execute() {
		try {
			generateCaptcha();
			return Action.SUCCESS;
		} catch (IOException e) {
			LOG.error("Captcha generation error: " + e.getMessage());
			return Action.ERROR;
		}
	}

	private void generateCaptcha() throws IOException {
		ActionContext ac = ActionContext.getContext().getActionInvocation().getInvocationContext();
		HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
		HttpServletResponse response = (HttpServletResponse) ac.get(ServletActionContext.HTTP_RESPONSE);

		// the output stream to render the captcha image as jpeg into
		ByteArrayOutputStream binaryStream = new ByteArrayOutputStream();

		// get the session id that will identify the generated captcha.
		// the same id must be used to validate the response, the session id is a good candidate!

		String captchaId = request.getSession().getId();
		LOG.debug("Session ID: " + captchaId);
		BufferedImage challenge = captchaService.getImageChallengeForID(captchaId, request.getLocale());

		ImageIO.write(challenge, CAPTCHA_IMAGE_FORMAT, binaryStream);

		this.outputStream = new ByteArrayInputStream(binaryStream.toByteArray());

		// flush it in the response
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/" + CAPTCHA_IMAGE_FORMAT);
	}

	/**
	 * @see org.iita.struts.DownloadableStream#getDownloadFileName()
	 */
	@Override
	public String getDownloadFileName() {
		return "captcha.png";
	}

	/**
	 * @see org.iita.struts.DownloadableStream#getDownloadStream()
	 */
	@Override
	public InputStream getDownloadStream() {
		return this.outputStream;
	}

	/**
	 * @see org.iita.security.action.CaptchaValidator#isCaptchaValid(java.lang.String)
	 */
	public boolean isCaptchaValid() {
		ActionContext ac = ActionContext.getContext().getActionInvocation().getInvocationContext();
		HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
		String captchaId = request.getSession().getId();
		String captchaText = request.getParameter("jcaptcha");
		LOG.debug("Got jcaptcha: " + captchaText);
		return this.captchaService.validateResponseForID(captchaId, captchaText);
	}
}
