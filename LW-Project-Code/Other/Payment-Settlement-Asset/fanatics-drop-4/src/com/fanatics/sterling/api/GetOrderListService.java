/*##########################################################################################
 *
 * Project Name          : Fanatics
 * Module                : OMS
 * Author                : Anmol Vats
 * Date                  : June 16, 2016 
 *
 * Description           : This service is responsible to allow capability in Call center order portlet to search by billing Email address.
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

public class GetOrderListService implements YIFCustomApi {

	private static YFCLogCategory logger = YFCLogCategory
			.instance(YFCLogCategory.class);

	String sCustEmailId = "";
	String sCustPhoneNo = "";
	String sOrderNo = "";
	String sCustOrderNo = "";

	Document docgetPersonInfoListOp = null;
	List<String> perInfoKeyList = new ArrayList<String>();
	String sPerInfKey = "";
	String sTotalOrderList = "";
	Document docOutput = null;
	Document varDoc = null;

	public Document callApi(YFSEnvironment env, Document inXML)
			throws ParserConfigurationException {
		logger.info("callApi Begins");
		logger.info("Input Doc is >>>>>>>>>>>>>>>>> "
				+ XMLUtil.getXMLString(inXML));

		Element eleRoot = inXML.getDocumentElement();
		sCustEmailId = eleRoot.getAttribute(FANConstants.ATT_CUSTOMER_EMAIL_ID);
		sCustPhoneNo = eleRoot.getAttribute(FANConstants.ATT_CUSTOMER_PHONE_NO);
		sOrderNo = eleRoot.getAttribute(FANConstants.ORDER_NO);
		sCustOrderNo = eleRoot.getAttribute(FANConstants.ATT_CUSTOMER_ORDER_NO);

		logger.info("CustomerEMailID is >>>>>>>>>>>>>>>>> " + sCustEmailId);
		logger.info("CustomerPhoneNo is >>>>>>>>>>>>>>>>> " + sCustPhoneNo);
		logger.info("OrderNo is >>>>>>>>>>>>>>>>> " + sOrderNo);
		logger.info("CustomerOrderNo is >>>>>>>>>>>>>>>>> " + sCustOrderNo);

		if ((!StringUtil.isNullOrEmpty(sCustEmailId) || !StringUtil
				.isNullOrEmpty(sCustPhoneNo))
				&& (StringUtil.isNullOrEmpty(sOrderNo) && StringUtil
						.isNullOrEmpty(sCustOrderNo))) {

			Document orderDoc = SCXmlUtil.createDocument(FANConstants.EL_ORDER);
			Element eleOrderDoc = orderDoc.getDocumentElement();

			Document docPersonInfo = SCXmlUtil
					.createDocument(FANConstants.A_PERSON_INFO);
			Element elePersonInfo = docPersonInfo.getDocumentElement();

			if (!StringUtil.isNullOrEmpty(sCustEmailId))
				elePersonInfo.setAttribute(FANConstants.A_EMAIL_ID,
						sCustEmailId);

			if (!StringUtil.isNullOrEmpty(sCustPhoneNo))
				elePersonInfo.setAttribute(FANConstants.ATT_DAY_PHONE,
						sCustPhoneNo);

			elePersonInfo.setAttribute(FANConstants.A_EMAIL_ID_QRY_TYPE,
					FANConstants.A_FLIKE);

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
			// Complex Query

			if (perInfoKeyList.size() > 0) {

				Element eleComplexQuery = orderDoc
						.createElement(FANConstants.A_COMPLEX_QUERY);
				eleOrderDoc.appendChild(eleComplexQuery);
				eleComplexQuery.setAttribute(FANConstants.A_OPERATOR,
						FANConstants.AA_OR);

				Element eleOr = orderDoc.createElement(FANConstants.A_OR);
				eleComplexQuery.appendChild(eleOr);

				Element eleExp = null;

				for (int j = 0; j < perInfoKeyList.size(); j++) {
					sPerInfKey = perInfoKeyList.get(j);
					if (!StringUtil.isNullOrEmpty(sPerInfKey)) {

						eleExp = orderDoc.createElement(FANConstants.A_EXP);
						eleOr.appendChild(eleExp);
						eleExp.setAttribute(FANConstants.A_NAME,
								FANConstants.ATT_BILL_TO_KEY);

						eleExp.setAttribute(FANConstants.A_VALUE, sPerInfKey);
						eleExp.setAttribute(FANConstants.A_QRYTYPE,
								FANConstants.A_EQUAL);

					}

				}

				// Calling getOrderList API with the Complex query
				logger.info("Input Doc to getOrderList with Complex query -> "
						+ XMLUtil.getXMLString(orderDoc));
				try {
					docOutput = CommonUtil
							.invokeAPI(
									env,
									"global/template/api/getOrderList_OrderPortlet.xml",
									FANConstants.API_getOrderList, orderDoc);
					logger.info("Output XML >>>"
							+ XMLUtil.getXMLString(docOutput));

				} catch (Exception e) {
					logger.error("Exception while invoking API: "
							+ e.getMessage() + ", Cause: " + e.getCause());
				}

			}

		} else {
			logger.info("Inside else block!!");
			logger.info(" getOrderList Input -> " + XMLUtil.getXMLString(inXML));
			try {
				docOutput = CommonUtil.invokeAPI(env,
						"global/template/api/getOrderList_OrderPortlet.xml",
						FANConstants.API_getOrderList, inXML);
				logger.info("Output XML >>>" + XMLUtil.getXMLString(docOutput));
			} catch (Exception e) {
				logger.error("Exception while invoking API: " + e.getMessage()
						+ ", Cause: " + e.getCause());
			}
		}
		logger.info("Final Return Document -> "
				+ XMLUtil.getXMLString(docOutput));
		logger.info("callApi Ends");
		return docOutput;
	}

	public void setProperties(Properties arg0) throws Exception {

	}

}
