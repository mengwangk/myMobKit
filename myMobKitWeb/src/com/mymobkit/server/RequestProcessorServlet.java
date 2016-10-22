package com.mymobkit.server;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.mymobkit.client.service.RequestProcessor;

@SuppressWarnings("serial")
@Singleton
@Slf4j
public final class RequestProcessorServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String requestType = req.getParameter("request");
		if (StringUtils.isNotBlank(requestType)) {
			res.getWriter().print(RequestProcessor.process(req, requestType));
		} else {
			log.warn("Unknown request");
		}
	}
}