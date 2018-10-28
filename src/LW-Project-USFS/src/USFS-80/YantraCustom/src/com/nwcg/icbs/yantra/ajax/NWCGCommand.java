package com.nwcg.icbs.yantra.ajax;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
/**
 * @author gacharya
 */

public class NWCGCommand {

	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger.getLogger(NWCGCommand.class
			.getName());	

	private String commandName;

	private String apiName;

	private String apiType;

	private Document inputTemplate;

	private Document apiOutputTemplate;
	
	private String apiOutputTemplatePath;

	public NWCGCommand() {
		super();
	}

	/*
	 * <NWCGCommand Name="getOrderDetails" APIName="getOrderDetails" APIType="API">
	 * 	<InputTemplate> 
	 *      <Order OrderHeaderKey="xml:/Order/@OrderHeaderKey"/>
	 * 	</InputTemplate> 
	 * 	<ApiOutputTemplate TemplatePath=""> 
	 * 		<Order OrderName="" OrderNo=""/>
	 * 	<ApiOutputTemplate> 
	 * </NWCGCommand>
	 * 
	 */

	public NWCGCommand(Document commandDoc) throws NWCGCommandException {
		this(commandDoc.getDocumentElement());
	}

	/*
	 * <NWCGCommand Name="getOrderDetails" APIName="getOrderDetails" APIType="API">
	 * 	<InputTemplate> 
	 *      <Order OrderHeaderKey="xml:/Order/@OrderHeaderKey"/>
	 * 	</InputTemplate> 
	 * 	<ApiOutputTemplate TemplatePath=""> 
	 * 		<Order OrderName="" OrderNo=""/>
	 * 	<ApiOutputTemplate> 
	 * </NWCGCommand>
	 * 
	 */
	public NWCGCommand(Element commandElement) throws NWCGCommandException{

		//set the values
		this.commandName = commandElement.getAttribute(NWCGCommandConstants.COMMAND_NAME_ATTR);
		this.apiName = commandElement.getAttribute(NWCGCommandConstants.COMMAND_API_NAME_ATTR);
		String apiType = commandElement.getAttribute(NWCGCommandConstants.COMMAND_API_TYPE_ATTR);
		//Check for API Type if its not API or FLOW throw and Exception
		//This is for developers who can't get their commands right :D 
		if(NWCGCommandConstants.API_TYPE_API.equals(apiType) || NWCGCommandConstants.API_TYPE_FLOW.equals(apiType) || NWCGCommandConstants.API_TYPE_CUSTOM_API.equals(apiType))
			this.apiType = apiType;
		else 
			throw new NWCGCommandException("NWCG_AJAX_COMMAND_003");
		
		this.inputTemplate = getInputTemplateDoc(commandElement);
		this.apiOutputTemplatePath = getApiOutputTemplatePath(commandElement);
		this.apiOutputTemplate = getApiOutputTemplateDoc(commandElement);
	}

	/**
	 * Get the Input template document
	 * @param commandElement
	 * @return
	 * @throws NWCGCommandException
	 */
	private Document getInputTemplateDoc(Element commandElement) throws NWCGCommandException {
		logger.verbose("Begin getInputTemplateDoc(Element)");
		Document returnDoc = null;
		Element inputTemplateElem = (Element)XMLUtil.getChildNodeByName(commandElement, NWCGCommandConstants.INPUT_TEMPLATE_ELEM);
		//If the input template is null we throw an exception
		//This is to for developers who forget to include required tags :D
		if(null ==inputTemplateElem){
			throw new NWCGCommandException("NWCG_AJAX_COMMAND_001");
		}
		NodeList nList = inputTemplateElem.getChildNodes();
		for(int i=0;i<nList.getLength();i++){
			Node n = nList.item(i);
			if(n instanceof Element){
				if(inputTemplateElem!=null){
					try {
						returnDoc = XMLUtil.createDocument(n);
					} catch (ParserConfigurationException e) {
						throw new NWCGCommandException(e);
					}
				}
				break;
			}
			
		}
			
		return returnDoc;
	}
	
	/**
	 * This method gets the api output template from the command xml
	 * @param commandElement
	 * @return
	 * @throws NWCGCommandException
	 */
	private Document getApiOutputTemplateDoc(Element commandElement) throws NWCGCommandException{
		Document returnDoc = null;
		Element apiOutputTemplateElem = (Element)XMLUtil.getChildNodeByName(commandElement, NWCGCommandConstants.API_OUTPUT_TEMPLATE);
		//If the output template is null we throw an exception
		//This is to for developers who forget to include required tags :D
		if(null == apiOutputTemplateElem){
			throw new NWCGCommandException("NWCG_AJAX_COMMAND_002");
		}
		NodeList nList = apiOutputTemplateElem.getChildNodes();
		for(int i=0;i<nList.getLength();i++){
			Node n = nList.item(i);
			if(n instanceof Element){
				if(apiOutputTemplateElem!=null){
					try {
						returnDoc = XMLUtil.createDocument(n);
					} catch (ParserConfigurationException e) {
						throw new NWCGCommandException(e);
					}
				}
			}
		}
		return returnDoc;
	}

	/**
	 * Get the template path 
	 * @param commandElement
	 * @return
	 * @throws NWCGCommandException
	 */
	private String getApiOutputTemplatePath(Element commandElement) throws NWCGCommandException{
		Element apiOutputTemplateElem = (Element)XMLUtil.getChildNodeByName(commandElement, NWCGCommandConstants.API_OUTPUT_TEMPLATE);
		//If the output template is null we throw an exception
		//This is to for developers who forget to include required tags :D
		if(null == apiOutputTemplateElem){
			throw new NWCGCommandException("NWCG_AJAX_COMMAND_002");
		}
		return apiOutputTemplateElem.getAttribute(NWCGCommandConstants.API_OUTPUT_TEMPLATE_PATH_ATTR);
	}
	
	/**
	 * @return Returns the apiName.
	 */
	public String getApiName() {
		return apiName;
	}

	/**
	 * @param apiName
	 *            The apiName to set.
	 */
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	/**
	 * @return Returns the apiOutputTemplate.
	 */
	public Document getApiOutputTemplate() {
		return apiOutputTemplate;
	}

	/**
	 * @param apiOutputTemplate
	 *            The apiOutputTemplate to set.
	 */
	public void setApiOutputTemplate(Document apiOutputTemplate) {
		this.apiOutputTemplate = apiOutputTemplate;
	}

	/**
	 * @return Returns the commandName.
	 */
	public String getCommandName() {
		return commandName;
	}

	/**
	 * @param commandName
	 *            The commandName to set.
	 */
	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	/**
	 * @return Returns the inputTemplate.
	 */
	public Document getInputTemplate() {
		return inputTemplate;
	}

	/**
	 * @param inputTemplate
	 *            The inputTemplate to set.
	 */
	public void setInputTemplate(Document inputTemplate) {
		this.inputTemplate = inputTemplate;
	}

	/**
	 * @return Returns the apiType.
	 */
	public String getApiType() {
		return apiType;
	}

	/**
	 * @param serviceName
	 *            The apiType to set.
	 */
	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

	/**
	 * @return Returns the apiType.
	 */
	public String getApiOutputTemplatePath() {
		return apiOutputTemplatePath;
	}

	/**
	 * @param serviceName
	 *            The apiType to set.
	 */
	public void setApiOutputTemplatePath(String path) {
		this.apiOutputTemplatePath = path;
	}
	
	
	/*
	 * 
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "{NWCGCommandName:"+commandName+",ApiName:"+apiName+",ApiType:"+apiType+
				"\nInputTemplate:"+XMLUtil.getXMLString(inputTemplate)+
				"\nApiOutputTemplate:"+(apiOutputTemplate==null?"":XMLUtil.getXMLString(apiOutputTemplate))+"}";
	}
	
}
