/*##########################################################################################
 *
 * Project Name          : Fanatics
 * Module                : OMS
 * Author                : Anmol Vats
 * Date                  : June 17, 2016 
 *
 * Description           : This service is responsible to allow advanced order search capability in Call center order search Portlet.
 *                      
 * Change Revision
 * -------------------------------------------------------------------------------------------
 * Date             Author                  Version#    Remarks/Description                      
 * -------------------------------------------------------------------------------------------
                                                                                           
#############################################################################################################*/
package com.fanatics.sterling.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANConstants;
import com.fanatics.sterling.util.StringUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class OrderSearchService implements YIFCustomApi {

	private static YFCLogCategory logger = YFCLogCategory
			.instance(YFCLogCategory.class);

	String sCustEmailId = "";
	String sCustPhoneNo = "";
	String sCustFirstName = "";
	String sCustLastName = "";
	String sCustZipCode = "";

	String sAddLine1 = "";
	String sAddLine2 = "";
	String sFirstName = "";
	String sZipcode = "";
	String sLastName = "";
	String sEmailID = "";
	String sIsBillingAddress = "";

	Document docgetPersonInfoListOp = null;
	List<String> perInfoKeyList = new ArrayList<String>();
	String sPerInfKey = "";
	String sTotalOrderList = "";
	Document docOutput = null;
	Document varDoc = null;

	public Document searchMethod(YFSEnvironment env, Document inXML)
			throws ParserConfigurationException {

		logger.info("searchMethod Begins");
		logger.info("Input Doc is >>>>>>>>>>>>>>>>> "
				+ XMLUtil.getXMLString(inXML));

		Element eleRoot = inXML.getDocumentElement();
		sCustEmailId = eleRoot.getAttribute(FANConstants.ATT_CUSTOMER_EMAIL_ID);
		sCustPhoneNo = eleRoot.getAttribute(FANConstants.ATT_CUSTOMER_PHONE_NO);
		sCustFirstName = eleRoot
				.getAttribute(FANConstants.ATT_CUSTOMER_FIRST_NAME);
		sCustLastName = eleRoot
				.getAttribute(FANConstants.ATT_CUSTOMER_LAST_NAME);
		sCustZipCode = eleRoot.getAttribute(FANConstants.ATT_CUSTOMER_ZIP_CODE);

		sAddLine1 = eleRoot.getAttribute(FANConstants.ATT_ADDRESS_LINE1);
		sAddLine2 = eleRoot.getAttribute(FANConstants.ATT_ADDRESS_LINE2);
		sFirstName = eleRoot.getAttribute(FANConstants.ATT_FirstName);
		sZipcode = eleRoot.getAttribute(FANConstants.ATT_ZIP_CODE);
		sLastName = eleRoot.getAttribute(FANConstants.ATT_LastName);
		sEmailID = eleRoot.getAttribute(FANConstants.ATT_EMAIL_ID);
		if(StringUtil.isNullOrEmpty(sEmailID)){
			if(!StringUtil.isNullOrEmpty(sCustEmailId)){
				sEmailID = sCustEmailId;
			}
			
		}
		sIsBillingAddress = eleRoot
				.getAttribute(FANConstants.A_IS_BILLING_ADDRESS);

		logger.info("CustomerEMailID is >>>>>>>>>>>>>>>>> " + sCustEmailId);
		logger.info("CustomerPhoneNo is >>>>>>>>>>>>>>>>> " + sCustPhoneNo);
		logger.info("CustomerFirstName is >>>>>>>>>>>>>>>>> " + sCustFirstName);
		logger.info("CustomerLastName is >>>>>>>>>>>>>>>>> " + sCustLastName);
		logger.info("CustomerZipCode is >>>>>>>>>>>>>>>>> " + sCustZipCode);

		logger.info("AddressLine1 is >>>>>>>>>>>>>>>>> " + sAddLine1);
		logger.info("AddressLine2 is >>>>>>>>>>>>>>>>> " + sAddLine2);
		logger.info("FirstName is >>>>>>>>>>>>>>>>> " + sFirstName);
		logger.info("ZipCode is >>>>>>>>>>>>>>>>> " + sZipcode);
		logger.info("LastName is >>>>>>>>>>>>>>>>> " + sLastName);
		logger.info("EmailID is >>>>>>>>>>>>>>>>> " + sEmailID);
		logger.info("IsBillingAddress is >>>>>>>>>>>>>>>>> "
				+ sIsBillingAddress);

		if (!StringUtil.isNullOrEmpty(sAddLine1)
				|| !StringUtil.isNullOrEmpty(sAddLine2)
				|| !StringUtil.isNullOrEmpty(sFirstName)
				|| !StringUtil.isNullOrEmpty(sZipcode)
				|| !StringUtil.isNullOrEmpty(sLastName)
				|| !StringUtil.isNullOrEmpty(sEmailID)
				|| !StringUtil.isNullOrEmpty(sCustPhoneNo)) {

			logger.info(" Inside the Address if block!!");

			Document docPersonInfo = SCXmlUtil
					.createDocument(FANConstants.A_PERSON_INFO);
			Element elePersonInfo = docPersonInfo.getDocumentElement();

			if (!StringUtil.isNullOrEmpty(sEmailID)) {
				elePersonInfo.setAttribute(FANConstants.A_EMAIL_ID, sEmailID);
				elePersonInfo.setAttribute(FANConstants.A_EMAIL_ID_QRY_TYPE,
						FANConstants.A_FLIKE);
			}

			if (!StringUtil.isNullOrEmpty(sAddLine1))
				elePersonInfo.setAttribute(FANConstants.ATT_ADDRESS_LINE1,
						sAddLine1);

			if (!StringUtil.isNullOrEmpty(sAddLine2))
				elePersonInfo.setAttribute(FANConstants.ATT_ADDRESS_LINE2,
						sAddLine2);

			if (!StringUtil.isNullOrEmpty(sFirstName))
				elePersonInfo.setAttribute(FANConstants.ATT_FirstName,
						sFirstName);

			if (!StringUtil.isNullOrEmpty(sZipcode))
				elePersonInfo.setAttribute(FANConstants.ATT_ZIP_CODE, sZipcode);

			if (!StringUtil.isNullOrEmpty(sLastName))
				elePersonInfo
						.setAttribute(FANConstants.ATT_LastName, sLastName);

			if (!StringUtil.isNullOrEmpty(sCustPhoneNo))
				elePersonInfo.setAttribute(FANConstants.ATT_DAY_PHONE,
						sCustPhoneNo);

			// Calling getPersonInfoList API
			logger.info(" getPersonInfoList Input XML >>>"
					+ XMLUtil.getXMLString(docPersonInfo));
			try {
				docgetPersonInfoListOp = CommonUtil.invokeAPI(env,
						FANConstants.API_GET_PERSON_INFO_LIST, docPersonInfo);
				logger.info(" getPersonInfoList Output XML >>>"
						+ XMLUtil.getXMLString(docgetPersonInfoListOp));
			} catch (Exception e) {
				logger.error("Exception while invoking API: " + e.getMessage()
						+ ", Cause: " + e.getCause());
			}

			if (!YFCCommon.isVoid(docgetPersonInfoListOp)) {
				Element eleGetPerInfoLstOp = docgetPersonInfoListOp
						.getDocumentElement();
				NodeList nlPersonInfoList = eleGetPerInfoLstOp
						.getElementsByTagName(FANConstants.A_PERSON_INFO);

				if (null != nlPersonInfoList) {
					for (int i = 0; i <= nlPersonInfoList.getLength(); i++) {
						Element elePerInfo = (Element) nlPersonInfoList.item(i);
						if (null != elePerInfo) {
							logger.info("Inside loop : PersonInfoKey -> "
									+ elePerInfo
											.getAttribute(FANConstants.ATT_PERSON_INFO_KEY));
							perInfoKeyList
									.add(elePerInfo
											.getAttribute(FANConstants.ATT_PERSON_INFO_KEY));
						}
					}
				}

			}

			// fetch the list of PersonInfoKeys and call getOrderList with
			// complex query

			if (perInfoKeyList.size() > 0) {
				Element eleComplexQuery = inXML
						.createElement(FANConstants.A_COMPLEX_QUERY);
				eleRoot.appendChild(eleComplexQuery);
				eleComplexQuery.setAttribute(FANConstants.A_OPERATOR,
						FANConstants.AA_OR);

				Element eleOr = inXML.createElement(FANConstants.A_OR);
				eleComplexQuery.appendChild(eleOr);

				Element eleExp = null;

				for (int j = 0; j < perInfoKeyList.size(); j++) {
					sPerInfKey = perInfoKeyList.get(j);
					if (!StringUtil.isNullOrEmpty(sPerInfKey)) {

						eleExp = inXML.createElement(FANConstants.A_EXP);
						eleOr.appendChild(eleExp);

						if (sIsBillingAddress.equalsIgnoreCase(FANConstants.Y)) {
							eleExp.setAttribute(FANConstants.A_NAME,
									FANConstants.ATT_BILL_TO_KEY);
						} else {
							eleExp.setAttribute(FANConstants.A_NAME,
									FANConstants.ATT_ShipToKey);
						}

						eleExp.setAttribute(FANConstants.A_VALUE, sPerInfKey);
						eleExp.setAttribute(FANConstants.A_QRYTYPE,
								FANConstants.A_EQUAL);

					}

				}
				// Removing DocumentType, DraftOrderFlag & EnterpriseCode before invoking getOrderList API.
				
				eleRoot.removeAttribute(FANConstants.ATT_DocumentType);
				eleRoot.removeAttribute(FANConstants.AT_DRAFT_ORDER_FLAG);
				eleRoot.removeAttribute(FANConstants.ATT_EnterpriseCode);
				
				// Calling getOrderList API with the Complex query
				logger.info("Input Doc to getOrderList with Complex query -> "
						+ XMLUtil.getXMLString(inXML));
				try {
					docOutput = CommonUtil
							.invokeAPI(
									env,
									"global/template/api/getOrderList_OrderSearchPortlet.xml",
									FANConstants.API_getOrderList, inXML);
					logger.info("Output XML >>>"
							+ XMLUtil.getXMLString(docOutput));

				} catch (Exception e) {
					logger.error("Exception while invoking API: "
							+ e.getMessage() + ", Cause: " + e.getCause());
				}

			}

		} else {
			logger.info(" Inside the last else block!!");
			logger.info(" getOrderList Input -> " + XMLUtil.getXMLString(inXML));
			try {
				docOutput = CommonUtil
						.invokeAPI(
								env,
								"global/template/api/getOrderList_OrderSearchPortlet.xml",
								FANConstants.API_getOrderList, inXML);
				logger.info("Output XML >>>" + XMLUtil.getXMLString(docOutput));
			} catch (Exception e) {
				logger.error("Exception while invoking API: " + e.getMessage()
						+ ", Cause: " + e.getCause());
			}
		}

		logger.info("Final Return Document -> "
				+ XMLUtil.getXMLString(docOutput));
		logger.info("searchMethod Ends");
		return docOutput;

	}

	public void setProperties(Properties arg0) throws Exception {

	}

}
