package com.nwcg.icbs.yantra.condition.issue;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGCheckOrderLineIdentifier implements YCPDynamicConditionEx {

	private Map props = new HashMap();
	public NWCGCheckOrderLineIdentifier() {
		super();
	}

	/*
	 * <OrderLine OrderNo="">
	 * <Extn EntnRequestNo=""/>
	 * </OrderLine>
	 * 
	 */
	/*
	 * (non-Javadoc)
	 * @see com.yantra.ycp.japi.YCPDynamicConditionEx#evaluateCondition(com.yantra.yfs.japi.YFSEnvironment, java.lang.String, java.util.Map, org.w3c.dom.Document)
	 */
	public boolean evaluateCondition(YFSEnvironment env, String name,
			Map dataMap, Document inDoc) {
		boolean rFlag = false;
		try{
			//do a get orderline
			Element inDocElm = inDoc.getDocumentElement();
			String identiFierPath = (String)props.get(NWCGConstants.KEY_ORDER_LINE_IDENTIFIER_XPATH);
			String identifier = XPathUtil.getString(inDocElm,identiFierPath);

			String incidentNoPath = (String)props.get(NWCGConstants.KEY_INCIDENT_NO_XPATH);
			String incidentNo = XPathUtil.getString(inDocElm,incidentNoPath);
			
			String incidentYrPath = (String) props.get(NWCGConstants.KEY_INCIDENT_YEAR_XPATH);
			String incidentYr = XPathUtil.getString(inDocElm, incidentYrPath);

			if(StringUtil.isEmpty(identifier) || StringUtil.isEmpty(incidentNo)){
				return rFlag;
			}
			//check if the orderline exists
			rFlag = CommonUtilities.checkOrderLineExistsForRequestNo(env,identifier,incidentNo, incidentYr);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return rFlag;
	}

	public void setProperties(Map props) {
		this.props = props;
	}
	


}
