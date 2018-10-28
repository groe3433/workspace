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

package com.nwcg.icbs.yantra.ue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.api.returns.NWCGGenerateReceiptNo;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetCustomerIDUE;

/**
 * @author jvishwakarma This class stamps the Customer ID, which is based on the
 *         total number of records of the passed CustomerID value. The new value
 *         of customer id will be "Code Entered" followed by a number generated
 *         by this class, which is total number of existing records + 1, input
 *         xml to the getCustomerList <Customer CustomerID="<Entered Value>"
 *         CustomerIDQryType="FLIKE" /> template <CustomerList
 *         TotalNumberOfRecords=""/>
 * 
 */
public class NWCGGetCustomerIDUE implements YFSGetCustomerIDUE {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGGetCustomerIDUE.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yantra.yfs.japi.ue.YFSGetCustomerIDUE#getCustomerID(com.yantra.yfs
	 * .japi.YFSEnvironment, org.w3c.dom.Document)
	 */
	public String getCustomerID(YFSEnvironment arg0, Document arg1)
			throws YFSUserExitException {

		if (logger.isVerboseEnabled())
			logger.verbose("Input XML " + XMLUtil.getXMLString(arg1));

		Element root = arg1.getDocumentElement();
		NodeList nlExtn = root.getElementsByTagName("Extn");
		String strExtnCustomerType = "";
		// get the customer type 01 = NWCG 02 = Other customer, we need to
		// validate only for other customer
		for (int index = 0; index < nlExtn.getLength(); index++) {
			Element elemExtn = (Element) nlExtn.item(index);
			strExtnCustomerType = elemExtn.getAttribute("ExtnCustomerType");
		}
		// get the customer id
		String strCustomerID = root.getAttribute("CustomerID");

		// check if the customer is other then only process ... leave as it is
		// otherwise
		if (strExtnCustomerType.equals(NWCGConstants.NWCG_CUSTOMER_TYPE_OTHERS)) {
			try {
				// BEGIN CR 598/637 - ML
				String strStateCode = "";
				String strCountryCode = "";

				NodeList nBPI = root.getElementsByTagName("BillingPersonInfo");
				Element elemBPI = (Element) nBPI.item(0);
				strStateCode = elemBPI.getAttribute("State");
				strCountryCode = elemBPI.getAttribute("Country");

				NWCGGenerateReceiptNo recieptOne = new NWCGGenerateReceiptNo();
				if (strStateCode.equals("") || strStateCode.equals("NS")
						|| strStateCode.startsWith(" ")
						|| strStateCode.endsWith(" ")) {
					strCustomerID = recieptOne.GenerateSeqNo(arg0,
							strCountryCode,
							NWCGConstants.NWCG_NEW_CUSTOMER_IDENTIFIER);
					recieptOne.UpdateSeqNo(arg0, strCountryCode, strCustomerID,
							NWCGConstants.NWCG_NEW_CUSTOMER_IDENTIFIER);
				} else {
					strCustomerID = recieptOne.GenerateSeqNo(arg0,
							strStateCode,
							NWCGConstants.NWCG_NEW_CUSTOMER_IDENTIFIER);
					recieptOne.UpdateSeqNo(arg0, strStateCode, strCustomerID,
							NWCGConstants.NWCG_NEW_CUSTOMER_IDENTIFIER);
				}

				// return this customer id
				return (strCustomerID);
				// END CR 598/637 - ML

				// create an input document
				// Document doc = XMLUtil.createDocument("Customer");
				// Element elem = doc.getDocumentElement();
				// elem.setAttribute("CustomerID",strCustomerID);
				// elem.setAttribute("CustomerIDQryType","FLIKE");

				// create a template
				// Document docTemplate =
				// XMLUtil.createDocument("CustomerList");
				// docTemplate.getDocumentElement().setAttribute("TotalNumberOfRecords","");

				// if(log.isVerboseEnabled()) log.verbose("input document "+
				// XMLUtil.getXMLString(doc));

				// get customer count
				// Document docReturn =
				// CommonUtilities.invokeAPI(arg0,docTemplate,"getCustomerList",doc);
				// if(log.isVerboseEnabled()) log.verbose("returned document "+
				// XMLUtil.getXMLString(docReturn));
				// get total number of records
				// String strTotalNumberOfRecords =
				// StringUtil.nonNull(docReturn.getDocumentElement().getAttribute("TotalNumberOfRecords"));
				// to be on safer side
				// if(strTotalNumberOfRecords.equals(""))
				// {
				// strTotalNumberOfRecords = "0";
				// }
				// try
				// {
				// int iTotalNumberOfRecords =
				// Integer.parseInt(strTotalNumberOfRecords);
				// increase the counter
				// iTotalNumberOfRecords++ ;
				// append leading zero's
				// strCustomerID = strCustomerID +
				// StringUtil.prepadStringWithZeros(""+iTotalNumberOfRecords,6);
				// if(log.isVerboseEnabled()) log.verbose("returning " +
				// strCustomerID);

				// Create New CustID based upon NWCG_SEQUENCE
				// NWCGGenerateReceiptNo recieptOne = new
				// NWCGGenerateReceiptNo();
				// strCustomerID =
				// recieptOne.GenerateSeqNo(arg0,strStateCode,NWCGConstants.NWCG_NEW_CUSTOMER_IDENTIFIER);
				// return this customer id
				// return (strCustomerID);
				// }
				// catch(NumberFormatException e)
				// {
				// if(log.isVerboseEnabled())
				// log.verbose("NumberFormatException "+ e);
				// logger.printStackTrace(e);
				// throw new YFSUserExitException("NumberFormatException " +
				// e.getMessage());
				// }

			}
			/*
			 * catch (ParserConfigurationException e) { logger.printStackTrace(e);
			 * if(log.isVerboseEnabled())
			 * log.verbose("ParserConfigurationException "+ e); throw new
			 * YFSUserExitException("ParserConfigurationException " +
			 * e.getMessage()); }
			 */
			catch (Exception e) {
				logger.error("!!!!! Exception " ,e);
				throw new YFSUserExitException("Exception " + e.getMessage());
			}
		}
		return strCustomerID;
	}
}
