package com.nwcg.icbs.yantra.ajax;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.ui.backend.servlets.BaseServlet;
import com.yantra.yfc.ui.backend.util.APIManager;
import com.yantra.yfc.ui.backend.util.UIApplication;

/**
 * @author gacharya
 */
public class NWCGAjaxControllerServlet extends BaseServlet {
	
	private static final long serialVersionUID = 964959869143203767L;

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		// Initialize the command cache
		try {
			NWCGCommandCache.getInstance().loadCommands();
		} catch (Exception e) {
			throw new ServletException(e);
		}		
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger logger = YFCLogCategory.getLogger (NWCGAjaxControllerServlet.class.getName());
		logger.debug("Entering AjaxController.doGet()");
			processRequest(request,response);
		logger.debug("Exiting AjaxController.doGet()");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger logger = YFCLogCategory.getLogger (NWCGAjaxControllerServlet.class.getName());
		logger.debug("Entering AjaxController.doPost()");
			processRequest(request,response);
		logger.debug("Exiting AjaxController.doPost()");
	}
	
	/**
	 * The method processes the input request from the calls the command processor to process the ajax commands
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger logger = YFCLogCategory.getLogger (NWCGAjaxControllerServlet.class.getName());
		logger.debug("Entering AjaxController.processRequest()");
		
		try {
			// logger.debug("Then a dump");
			
			String commandName = request.getParameter(NWCGCommandConstants.COMMAND);
			//String requestId = request.getParameter(CommandConstants.REQUEST_ID);
			logger.debug("Command Name:"+commandName);
			// check if its dev mode and reload the commands file
			if(CommonUtilities.isDevelopmentMode())
				NWCGCommandCache.getInstance().loadCommands();
			
			//get the command
			NWCGCommand command = NWCGCommandCache.getInstance().getCommand(commandName);
			Document returnDoc = NWCGCommandProcessor.getInstance().executeCommand(command,request);
			// set the request id for identification on the client
			// this will also be used for caching
			// returnDoc.getDocumentElement().setAttribute("RequestId",requestId);
			
			String returnXML = XMLUtil.extractStringFromDocument(returnDoc);				
			// logger.debug("Return XML:"+returnXML);
			
			// set the response
            response.setContentType(NWCGCommandConstants.RESPONSE_CONTENT_TYPE);
            response.setHeader(NWCGCommandConstants.CACHE_CONTROL, NWCGCommandConstants.NO_CACHE);
            response.getWriter().write(returnXML);
            response.flushBuffer();
            
		}catch(Exception ep) {
			ep.printStackTrace();
		}
		logger.debug("Exiting AjaxController.processRequest()");		
	}
	
    protected void login(HttpServletRequest request)
    {
        com.yantra.yfc.util.YFCLocale locale = UIApplication.getInstance().getLocale(request.getSession());
        if(request.getAttribute(NWCGCommandConstants.YFS_ENVIRONMENT) == null)
        {
            Object env = APIManager.getInstance().login(request, locale);
            request.setAttribute(NWCGCommandConstants.YFS_ENVIRONMENT, env);
        }
    }    

    protected void logout(HttpServletRequest request)
    {
        APIManager.getInstance().logout(request.getAttribute(NWCGCommandConstants.YFS_ENVIRONMENT), request);
        request.removeAttribute(NWCGCommandConstants.YFS_ENVIRONMENT);
    }
}