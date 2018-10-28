package com.nwcg.icbs.yantra.api.cachetocache;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGUpdateShipNodeAccountCodes implements YIFCustomApi {

	private Properties props;

	public NWCGUpdateShipNodeAccountCodes() {
		props = null;
	}

	public void setProperties(Properties props) throws Exception {
		this.props = props;
	}

	public Document modifyShipNodeAccountCodes(YFSEnvironment env, Document inXML) throws Exception {

		String orgCode = inXML.getDocumentElement().getAttribute("OrganizationCode");

		Document getOrganizationList_inXML = XMLUtil.createDocument("Organization");
		Element inDocRoot = getOrganizationList_inXML.getDocumentElement();
		inDocRoot.setAttribute("OrganizationCode", orgCode);

		Document getOrganizationList_outXML = null;
		
		System.out.println("@@@@@ getOrganizationList_inXML : " + XMLUtil.getXMLString(getOrganizationList_inXML));
		if (orgCode != null && (!orgCode.equals(""))) {
			getOrganizationList_outXML = CommonUtilities.invokeAPI(env, "getOrganizationList_ShipNode", "getOrganizationList", getOrganizationList_inXML);
		}
		System.out.println("@@@@@ getOrganizationList_outXML : " + XMLUtil.getXMLString(getOrganizationList_outXML));

		return getOrganizationList_outXML;
	}
}