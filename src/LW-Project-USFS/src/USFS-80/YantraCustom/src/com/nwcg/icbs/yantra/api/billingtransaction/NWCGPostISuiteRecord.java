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

package com.nwcg.icbs.yantra.api.billingtransaction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGPostISuiteRecord implements YIFCustomApi,
		NWCGBillingTransRecordMutator {
	
	private static Logger logger = Logger
			.getLogger(NWCGProcessBillingTransConfirmShipment.class.getName());

	public void setProperties(Properties arg0) throws Exception {
	}

	public void throwAlert(YFSEnvironment env, StringBuffer Message)
			throws Exception {
		Message.append(" ExceptionType='BILLING_TRANS_CONFIRM_SHIPMENT'"
				+ " InboxType='" + NWCGConstants.NWCG_BILL_TRANS_INBOXTYPE
				+ "' QueueId='" + NWCGConstants.NWCG_BILL_TRANS_QUEUEID
				+ "' />");
		if (logger.isVerboseEnabled())
			logger.verbose("Throw Alert Method called with message:-"
					+ Message.toString());
		try {
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, "createException", inTemplate);
		} catch (Exception ex1) {
			throw new NWCGException(
					"NWCG_BILLING_TRANSACTION_ERROR_WHILE_LOGGING_ALERT");
		}
	}

	public Document PostRecord(YFSEnvironment env, Document inXML)
			throws Exception {
		Element elemRoot = null;
		String strYear = "";
		String strMonth = "";
		String strDay = "";
		String strHour = "";
		String strMinute = "";
		String strSecond = "";
		elemRoot = inXML.getDocumentElement();
		if (elemRoot != null) {
			String ShipmentKey = elemRoot.getAttribute("ShipmentKey");
			Document inputDoc = XMLUtil.newDocument();
			Document outputShipmentDetailsDoc = XMLUtil.newDocument();
			Element element_NWCGShipmentDetails = inputDoc
					.createElement("Shipment");
			inputDoc.appendChild(element_NWCGShipmentDetails);
			element_NWCGShipmentDetails
					.setAttribute("ShipmentKey", ShipmentKey);
			outputShipmentDetailsDoc = CommonUtilities.invokeAPI(env,
					"getShipmentDetails", "getShipmentDetails", inputDoc);
			Element outputDocElem = outputShipmentDetailsDoc
					.getDocumentElement();
			String ActualShipmentDate = outputDocElem
					.getAttribute("ActualShipmentDate");
			Element ExtnElm = (Element) outputDocElem.getElementsByTagName(
					"Extn").item(0);
			String ExtnIncidentNum = ExtnElm.getAttribute("ExtnIncidentNum");
			String strShipmentDetails = XMLUtil
					.getXMLString(outputShipmentDetailsDoc);
			//Prevent a StringIndexOutOfBoundsException
			if (!ActualShipmentDate.equals("") && ActualShipmentDate != null) {
				strYear = ActualShipmentDate.substring(0, 4);
				strMonth = ActualShipmentDate.substring(5, 7);
				strDay = ActualShipmentDate.substring(8, 10);
				strHour = ActualShipmentDate.substring(11, 13);
				strMinute = ActualShipmentDate.substring(14, 16);
				strSecond = ActualShipmentDate.substring(17, 19);
			}
			String ExtFilename = ExtnIncidentNum + "-" + strYear + ""
					+ strMonth + "" + strDay + "-" + strHour + "" + strMinute
					+ "" + strSecond + ".xml";
			//String OutFileName = "C:\\test" + "\\" + ExtFilename;
			String OutFileName = NWCGConstants.NWCG_BLM_ISUITE_DIR
					+ ExtFilename;
			PrintStream p;
			p = Set_OutFile(env, OutFileName);
			//print shipment details to file
			p.print(strShipmentDetails);
			p.println("");
			p.println("<!-- SHIPMENT DETAILS - EOF -->");
		} //END Root IF
		if (logger.isVerboseEnabled()) {
			logger.verbose("Entering NWCGPostISuiteRecord, Input document is:"
					+ XMLUtil.getXMLString(inXML));
		}
		return inXML;
	}

	public Document insertBillingTransRecord(YFSEnvironment env, Document doc)
			throws Exception {
		return PostRecord(env, doc);
	}

	public PrintStream Set_OutFile(YFSEnvironment env, String outfile)
			throws Exception {
		FileOutputStream out = null;
		PrintStream ps;
		try {
			out = new FileOutputStream(outfile, true);
		} catch (FileNotFoundException e) {
			//Jimmy - added during process to description
			e.printStackTrace();
			StringBuffer stbuf = new StringBuffer(
					"<Inbox ActiveFlag='' ApiName='NWCGPostISuiteRecord' "
							+ "DetailDescription='Error in Opening File : "
							+ outfile
							+ ", during the NWCGPostISuiteRecord process'");
			throwAlert(env, stbuf);
			if (logger.isVerboseEnabled())
				logger.verbose("NWCGPostISuiteRecord::Error in Opening File  "
						+ outfile);
		}
		ps = new PrintStream(out);
		return ps;
	}
}