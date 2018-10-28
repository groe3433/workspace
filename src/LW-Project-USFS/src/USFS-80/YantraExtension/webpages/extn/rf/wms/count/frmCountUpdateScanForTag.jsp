<%@ include file="/yfc/rfutil.jspf" %>
<%
	String errorDesc = null ;
	String errorField = null ;
	String entitybase= getParameter("entitybase") ;
	String formName= "/frmCountSKUTagEntry";	
	String forwardPage= "";
	String sSerialTracked = "";

    YFCElement tagElem =null;
    YFCElement invElem =null;
    YFCElement bcTagDtlElem= null; 
	YFCElement countResult=null;
	YFCElement tagAttrs=null;
	YFCElement extnTagAttrs=null;
	boolean bHasExtn = false;
	
	try{
		bcTagDtlElem= getStoredElement(getTempQ(),"BC_TagDetail", String.valueOf("1")); 
		countResult= (YFCElement)((getTempQ()).getElementsByTagName("RecordCountResult")).item(0);
		//get tag info from tempq
		tagAttrs=getStoredElement(getTempQ(),"InventoryTagAttributes", "1");
		extnTagAttrs=getStoredElement(getTempQ(),"InventoryTagExtnAttributes", "1");
		tagElem= getStoredElement(getTempQ(),"TagAttributes",String.valueOf("1")); 
		invElem= getStoredElement(getTempQ(),"Inventory",String.valueOf("1"));
		sSerialTracked = invElem.getAttribute("SerialTracked");
	}catch(Exception e){
		errorDesc="Mobile_Session_Error";
	}

	if(countResult==null){
		errorDesc="Mobile_Count_Result_Not_Found";
	}else{
		request.setAttribute("RecordCountResult", countResult);
	
		YFCDocument ydoc = getForm(formName) ;
		HashMap attrMap= new HashMap();
		attrMap.putAll(tagAttrs.getAttributes());
		if(!isVoid(extnTagAttrs)){
			bHasExtn = true;
			attrMap.putAll(extnTagAttrs.getAttributes());
		}
		String currentAttr=null;
		String currentAttrValue=null;
	
		int i=0;		
		if((attrMap!=null)&&(!attrMap.isEmpty())) {
			for (Iterator k = attrMap.keySet().iterator(); k.hasNext();) {
				currentAttr = (String) k.next();
				currentAttrValue = (String) attrMap.get(currentAttr);
				if("02".equals(currentAttrValue.trim()) && !isTagAlreadyScanned(bcTagDtlElem,tagElem,currentAttr)){
					YFCElement Elem=getField(ydoc, "txtTagNumber");
					if(Elem!=null){
						Elem.setAttribute("type", "text") ;
						Elem.setAttribute("subtype", "Text") ;
						String opBinding="xml:/TagAttributes/@" + currentAttr;
						if(bHasExtn && extnTagAttrs.hasAttribute(currentAttr)){
							opBinding="xml:/TagAttributes/Extn/@" + currentAttr;
						}
						Elem.setAttribute("outputbinding", opBinding );
						Elem.setAttribute("tag","binding=" + opBinding );
					}
					Elem =getField(ydoc, "lblTagNumber");
					if(Elem!=null){
						Elem.setAttribute("type", "text");
						Elem.setAttribute("subtype", "Label");
						Elem.setAttribute("value", currentAttr);
					}
					try{
						//put scanned attr value in tempq
						deleteAllFromTempQ("ScannedTagAttribute");
						addToTempQ("ScannedTagAttribute", currentAttr , false);
					}catch(Exception e){}
					i++;
					break;
				}
			}

			if(i>0){
				if(bHasExtn && extnTagAttrs.hasAttribute(currentAttr)){
					extnTagAttrs.removeAttribute(currentAttr);
				}else{
					tagAttrs.removeAttribute(currentAttr);
				}
				/* ---- Top of CR 492 -- 
				   ---- if an item is SerialTracked, skip LotNumber entry screen
				   ---- lot number field will be populated from serialNo later on
				   ---- */
				if(equals("Y",sSerialTracked)){
					forwardPage=checkExtension(entitybase + "/frmCountUpdateCheckForTagAttributes.jsp");
					%>
					<jsp:forward page='<%=forwardPage%>' ></jsp:forward>
					<%
				}else{
					out.println(sendForm(ydoc, "txtTagNumber", true));
				}
				/* ---- Bottom of CR 492 -- commenting out ---- */
			}else{
				deleteAllFromTempQ("ScannedTagAttribute");
				deleteAllFromTempQ("InventoryTagAttributes");
				deleteAllFromTempQ("InventoryTagExtnAttributes");

				forwardPage=checkExtension(entitybase + "/frmCountUpdateCheckForTagAttributes.jsp");
				%>
				<jsp:forward page='<%=forwardPage%>' ></jsp:forward>
				<%
			}
		}else{
			forwardPage=checkExtension(entitybase + "/frmCountUpdateCheckForTagAttributes.jsp");
			%>
			<jsp:forward page='<%=forwardPage%>' ></jsp:forward>
			<%
		}
	}

	if(errorDesc != null) {
		String errorXML=getErrorXML(errorDesc,errorField);
		%>
		<%=errorXML%>
	<%}%>

<%! 
private boolean isTagAlreadyScanned(YFCElement tagElem, YFCElement tagInfoElem, String sTagAttribute){
	if(tagElem == null){
		return false;
	}
	if(!isVoid(tagElem.getAttribute(sTagAttribute))) {
		if(tagInfoElem != null){
			tagInfoElem.setAttribute(sTagAttribute, tagElem.getAttribute(sTagAttribute));
		}
		return true;
	}
	YFCElement tagExtnElem = tagElem.getChildElement("Extn");
	if(tagExtnElem == null){
		return false;
	}
	if(!isVoid(tagExtnElem.getAttribute(sTagAttribute))){
		if(tagInfoElem != null ){
			tagInfoElem.getChildElement("Extn", true).setAttribute(sTagAttribute, tagExtnElem.getAttribute(sTagAttribute));
		}
		return true;
	}
	return false;
}
%>



