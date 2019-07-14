package by.plisunov.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import by.plisunov.scoreboard.controller.WebController;

@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {

	private static final Logger logger = LoggerFactory.getLogger(WebController.class);

	@Override
	public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			AccessDeniedException exception) throws IOException, ServletException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Access Denied");
		if (auth != null) {
			logger.info("User '" + auth.getName() + "' attempted to access the protected URL: "
					+ httpServletRequest.getRequestURI());
		}
		httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/403");

	}

}
