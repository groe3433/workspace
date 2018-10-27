package com.nwcg.icbs.yantra.condition.cachetocache;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGTransferLineFullUTF implements YCPDynamicConditionEx
{
	private Map map = null;

	public boolean evaluateCondition(YFSEnvironment env, String name, Map mapData, Document doc) 
	{
		//System.out.println("NWCGTransferLineFullUTF:evaluateCondition:Input XML\n" + XMLUtil.getXMLString(doc));

		String orderLineKey = null;
		Document outputXMLDoc = null;
		boolean result = false;

		String bTemplate = "<OrderLine><Extn/></OrderLine>";

		if (mapData != null && mapData.size() > 0) {
			orderLineKey = mapData.get(NWCGConstants.ORDER_LINE_KEY).toString();
		}

		try {
			if (orderLineKey != null && !orderLineKey.equals(NWCGConstants.EMPTY_STRING)) {
				if (bTemplate != null && !bTemplate.equals(NWCGConstants.EMPTY_STRING)) {
					env.setApiTemplate(NWCGConstants.API_GET_ORDER_LINE_DETAILS, XMLUtil.getDocument(bTemplate));
				}

				outputXMLDoc = getOrderLineDetail(env, mapData, doc);

				String orderedQty = outputXMLDoc.getDocumentElement().getAttribute(NWCGConstants.ORDERED_QTY);
				Element olExtn = (Element)XMLUtil.getChildNodeByName(outputXMLDoc.getDocumentElement(), NWCGConstants.EXTN_ELEMENT);
				String extnUTFQty = olExtn.getAttribute(NWCGConstants.EXTN_UTF_QTY);

				result = (Float.parseFloat(extnUTFQty) > 0 && Float.parseFloat(orderedQty)== 0)? true : false;
			}
		} 
		catch (Exception ep){
			result = false;
		}

		return result;
	}

	private Document getOrderLineDetail(YFSEnvironment env, Map mapData, Document doc) throws Exception 
	{
		Document inputXMLDoc = null;
		Document outputXMLDoc = null;
		String inputXMLString = null;

		String orderLineKey = (mapData != null && mapData.size() > 0)? mapData.get(NWCGConstants.ORDER_LINE_KEY).toString() : null;
		if (orderLineKey == null || orderLineKey.length() == 0) 
		{
			throw new RuntimeException(ResourceUtil.resolveMsgCode("USFS_COMMON_CONDITION_XPATH6"));
		} 
		else 
		{
			inputXMLString = "<OrderLineDetail OrderLineKey='" + orderLineKey + "' />";
			inputXMLDoc = XMLUtil.getDocument(inputXMLString);
			outputXMLDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_GET_ORDER_LINE_DETAILS, inputXMLDoc);
			env.clearApiTemplate(NWCGConstants.API_GET_ORDER_LINE_DETAILS);
		}
		return outputXMLDoc;
	}

	public void setProperties(Map map) {
		this.map = map;
	}
}