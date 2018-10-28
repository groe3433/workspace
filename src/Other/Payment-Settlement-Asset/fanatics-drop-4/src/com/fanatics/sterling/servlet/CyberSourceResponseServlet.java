package com.fanatics.sterling.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yantra.yfc.log.YFCLogCategory;


public class CyberSourceResponseServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	private final static YFCLogCategory logger = YFCLogCategory.instance(YFCLogCategory.class);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CyberSourceResponseServlet() 
    {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		
		logger.info("*********************** CyberSourceResponseServlet doGet **********************************");
				
				response.setContentType("text/html");
			    PrintWriter out = response.getWriter();

			    out.println("<HTML>");
			    out.println("<HEAD><TITLE>CyberSource Test</TITLE></HEAD>");
			    out.println("<BODY><H1>Test Successful</H1></BODY></HTML>");				
			
		logger.info("*********************** exit CyberSourceResponseServlet doGet **********************************");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		logger.info("*********************** CyberSourceResponseServlet doPost **********************************");

		String restrictedCardNo = request.getParameter("req_card_number");
	    String paymentToken = request.getParameter("payment_token");
	    String decision = request.getParameter("decision");
	    
	    String result = "decision: " + decision + " req_card_number:" + restrictedCardNo + " payment_token: " + paymentToken;
		
		logger.info("RESULT POST --> " + result);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("</head>");
		out.println("<div id=\"testFrameContent\" style=\"border:1px;\">");
		out.println(result);
		out.println("</div");
		out.println("<body>");
		out.write("<div id=\"decision\">" + decision + "</div>");
		out.write("<div id=\"token\">"+paymentToken+"</div>");
		out.write("<div id=\"cardNo\">"+restrictedCardNo+"</div>");
		out.println("</body>");
		out.println("</html>");

		out.close();
		
		logger.info("*********************** exit CyberSourceResponseServlet doPost **********************************");

	}

}
