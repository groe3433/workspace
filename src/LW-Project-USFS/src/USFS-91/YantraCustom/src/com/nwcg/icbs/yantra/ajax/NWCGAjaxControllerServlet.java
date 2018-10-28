/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
 LIMITATION OF LIABILITY
 THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
 LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
 OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
 OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
 OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
 PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
 FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
 BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
 CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
 OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
 */

package com.nwcg.icbs.yantra.ajax;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.agents.NWCGQueueMonitoringAgent;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGAgentLogger;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfc.ui.backend.servlets.BaseServlet;
import com.yantra.yfc.ui.backend.util.APIManager;
import com.yantra.yfc.ui.backend.util.UIApplication;
import com.yantra.yfc.log.YFCLogCategory;

public class NWCGAjaxControllerServlet extends BaseServlet {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGAjaxControllerServlet.class);
	
	private static final long serialVersionUID = 964959869143203767L;

	public void init() throws ServletException {
		// Initialize the command cache
		try {
			NWCGCommandCache.getInstance().loadCommands();
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e);
			throw new ServletException(e);
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * The method processes the input request from the calls the command
	 * processor to process the ajax commands
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String commandName = request.getParameter(NWCGCommandConstants.COMMAND);
			// String requestId = request.getParameter(CommandConstants.REQUEST_ID);

			// check if its dev mode and reload the commands file
			if (CommonUtilities.isDevelopmentMode())
				NWCGCommandCache.getInstance().loadCommands();
			// get the command
			NWCGCommand command = NWCGCommandCache.getInstance().getCommand(commandName);
			Document returnDoc = NWCGCommandProcessor.getInstance().executeCommand(command, request);
			// set the request id for identification on the client this will also be used for caching returnDoc.getDocumentElement().setAttribute("RequestId",requestId);
			String returnXML = XMLUtil.extractStringFromDocument(returnDoc);
			// set the response
			response.setContentType(NWCGCommandConstants.RESPONSE_CONTENT_TYPE);
			response.setHeader(NWCGCommandConstants.CACHE_CONTROL, NWCGCommandConstants.NO_CACHE);
			response.getWriter().write(returnXML);
			response.flushBuffer();
		} catch (Exception ep) {
			logger.error("!!!!! Caught General Exception :: " + ep);
		}
	}

	protected void login(HttpServletRequest request) {
		com.yantra.yfc.util.YFCLocale locale = UIApplication.getInstance("YFSSYS00004").getLocale(request.getSession());
		if (request.getAttribute(NWCGCommandConstants.YFS_ENVIRONMENT) == null) {
			Object env = APIManager.getInstance().login(request, locale);
			request.setAttribute(NWCGCommandConstants.YFS_ENVIRONMENT, env);
		}
	}

	protected void logout(HttpServletRequest request) {
		APIManager.getInstance().logout(request.getAttribute(NWCGCommandConstants.YFS_ENVIRONMENT), request);
		request.removeAttribute(NWCGCommandConstants.YFS_ENVIRONMENT);
	}
}