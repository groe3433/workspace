<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/inventory.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_returns.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/extn/scripts/nwcg_new_ajaxHelper.js"></script>
<script>
function populateSerialDetails(elem,xmlDoc) {
	var nodes=xmlDoc.getElementsByTagName("TagDetail");
	var Serialnodes = xmlDoc.getElementsByTagName("Serial"); 
	var curRow = elem.parentNode.parentNode;
	//SecondarySerial1
	var SecondarySerial1 ="" ;
	//SerialNo
	var SerialNo = "" ;
	if(nodes!=null && nodes.length > 0 ) {		
		if(Serialnodes!=null && Serialnodes.length > 0 ) {
			var serDetail = Serialnodes(0);
			SecondarySerial1 = serDetail.getAttribute("SecondarySerial1") ;
			SerialNo = serDetail.getAttribute("SerialNo") ;
		}
		var tagDetail = nodes(0);
		var LotAttribute1 = tagDetail.getAttribute("LotAttribute1") ;
		var LotAttribute2 = tagDetail.getAttribute("LotAttribute2") ;
		var LotAttribute3 = tagDetail.getAttribute("LotAttribute3") ;
		var ManufacturingDate = tagDetail.getAttribute("ManufacturingDate") ;
		var RevisionNo = tagDetail.getAttribute("RevisionNo") ;
		var LotNumber = tagDetail.getAttribute("LotNumber") ;
		if(ManufacturingDate.length == 10) {
			var strYear = ManufacturingDate.substring(0,4);
			var strMon = ManufacturingDate.substring(5,7);
			var strDate = ManufacturingDate.substring(8,10);
			ManufacturingDate = strMon + "/" + strDate + "/" + strYear ;
		}
		var InputList=curRow.getElementsByTagName("INPUT");
		for(i = 0 ; i < InputList.length ; i++) {
			//SecondarySerial1
			if(InputList[i].name.indexOf('/@SecondarySerial1') != -1) {			
				InputList[i].value = SecondarySerial1;
			}
			//SerialNo
			if(InputList[i].name.indexOf('/@SerialNo') != -1) {			
				InputList[i].value = SerialNo;
			}
			if(InputList[i].name.indexOf('/@LotAttribute3') != -1) {			
				InputList[i].value = LotAttribute3;
			}
			if(InputList[i].name.indexOf('/@LotAttribute1') != -1) {			
				InputList[i].value = LotAttribute1;
			}
			if(InputList[i].name.indexOf('/@LotAttribute2') != -1) {			
				InputList[i].value = LotAttribute2;
			}
			if(InputList[i].name.indexOf('/@RevisionNo') != -1) {			
				InputList[i].value = RevisionNo;
			}
			if(InputList[i].name.indexOf('/@ManufacturingDate') != -1) {			
				InputList[i].value = ManufacturingDate;
			}
		}
	}
}

function setSerialDetails(ele) {
	var returnArray = new Object();
	returnArray["xml:SerialNo"] = ele.value;
	return returnArray;
}

// START 03/03/2015 - Defect 1522 
// Trim the Serial Number of white spaces before attempting to validate
function trim (elem) {
	elem.value = elem.value.replace(/(^\s*)|(\s*$)/gi, "");
	return;
}

// Call getSerialList to ensure the serial number exists
function isSerialReal (elem) {
	// PI 1655 (05/05/2015) - Check if DocumentType is 0006 (C2C Transfer)
	var strDocumentType = "<%=request.getParameter("DocumentType")%>";
	if(strDocumentType == "0006") {
		fetchDataWithParams(elem, 'getSerialList_recordcount', checkSerialExists_forC2CTransfer, setSerialNumber(elem));
	}
	// PI 1803 (09/01/2015) - Check if DocumentType is 0005 (Inbound PO)
	if(strDocumentType == "0005") {
		fetchDataWithParams(elem, 'getSerialList_recordcount', checkSerialExists_forInboundPO, setSerialNumber(elem));
	}
}

// PI 1803 (09/01/2015) - validate if the serial number already exists during the receiving process of an Inbound PO
function checkSerialExists_forInboundPO(elem, xmlDoc) {
	var serialList = xmlDoc.getElementsByTagName("Serial");
	if(serialList != null && serialList.length > 0) {
		alert("Serial number already exists, when receiving an Inbound PO the serial number CANNOT already exist in the system :: " + elem.value);
		elem.value = "";
		elem.focus();
	} 
}

// Validate that the serial number exists, i.e. "f4srfgvse5" is not a valid serial number
function checkSerialExists_forC2CTransfer(elem, xmlDoc) {
	var serialList = xmlDoc.getElementsByTagName("Serial");
	// CR 1522 - this is to check if the trackable item is valid
	if(serialList != null && serialList.length == 0) {
		alert("Serial number does not exist in the system, for a Cache 2 Cache Transfer the serial number MUST be present in the system :: " + elem.value);
		elem.value = "";
		elem.focus();
	} else {
		// Call NWCGGetTrackableItemListService Service to ensure the serial number is on the Shipment being received AND that the serial number is not IN the KIT being Transferred. 
		fetchDataWithParams(elem, 'NWCGGetTrackableItemListService', checkSerialOnC2C, setSerialNumber(elem));
	}
}

// Set Serial Number for getSerialList API call
function setSerialNumber(elem) {
	var returnArray = new Object();	
	returnArray["xml:SerialNo"] = elem.value;
	return returnArray;
}

// Validate that the Serial Number is part of the Shipment, i.e. don't let them enter a random serial number
function checkSerialOnC2C(elem, xmlDoc) {
	var NWCGTrackableItemList = xmlDoc.getElementsByTagName("NWCGTrackableItem");
	if(NWCGTrackableItemList.length > 0) {	
		var ResultNode = NWCGTrackableItemList(0);
		var strLastDocumentNo = ResultNode.getAttribute("LastDocumentNo").replace(/(^\s*)|(\s*$)/gi, "");
		// PI 1655 (05/05/2015) - Added line to get SerialStatusDesc
		var strSerialStatusDesc = ResultNode.getAttribute("SerialStatusDesc").replace(/(^\s*)|(\s*$)/gi, "");
		// CR 1522 - Added line to get the Shipment number of the C2C Transfer. 
		var strOrderNo = "<%=request.getParameter("OrderNo")%>";
		// CR 1522 - this was because we want to restrict users from receiving items that NOT shipped on this C2C Transfer Shipment
		if(strOrderNo != strLastDocumentNo) {
			alert("!!!!! Serial number is not part of the shipment being received :: " + strOrderNo);
			elem.value = "";
			elem.focus();
		}
		// PI 1655 (05/05/2015) - Added if to check if the serial is actually "Transferred" on this shipment (Child components with "Transferred in Kit" cannot be "Transferred")
		else if(strSerialStatusDesc != "Transferred") {
			alert("!!!!! Serial Status Description should be [Transferred] :: " + strSerialStatusDesc);
			elem.value = "";
			elem.focus();						
		}
		// CR 1522 - if all checks pass, THEN ONLY populate the Serial Details. 
		else {
			fetchDataWithParams(elem, 'getSerialList', populateSerialDetails, setSerialNumber(elem));
		}
	}
}
//END 03/03/2015 - Defect 1522 
</script>
<%
	int tagtrack=0;
  	int NoSecSerials = 0;
  	String sNoSecSerials = request.getParameter("NumSecondarySerials");
  	if(!isVoid(sNoSecSerials)){
	 	NoSecSerials = (new Integer(sNoSecSerials)).intValue();
  	}	 
	String strUnitCost = request.getParameter("UnitCost");
	String receivingPrice = (equals("Y", request.getParameter("refurbTransfer")))? request.getParameter("receivingPrice") : strUnitCost;
%>
<table class="table" >
	<tbody>
    	<tr>
			<td width="10%" >&nbsp;</td>
			<td width="80%" style="border:1px solid black">
				<table class="table" editable="true" border="1" width="100%" cellspacing="0" GenerateID="true">
<%
					Map identifierAttrMap=null;
					Map descriptorAttrMap=null;
					Map extnIdentifierAttrMap=null;
					Map extnDescriptorAttrMap=null;
					String tagContainer  = request.getParameter("TagContainer");
					if (isVoid(tagContainer)) {
						tagContainer = "TagContainer";
					}
					String tagElement  = request.getParameter("TagElement");
					if (isVoid(tagElement)) {
						tagElement = "Tag";
					}
%> 
					<yfc:hasXMLNode binding="xml:ItemDetails:/Item/InventoryTagAttributes">
<%
					prepareTagDetails ((YFCElement) request.getAttribute(tagContainer),tagElement,(YFCElement) request.getAttribute("ItemDetails"));
					identifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"IdentifierAttributes");
					descriptorAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"DescriptorAttributes");
					extnIdentifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"ExtnIdentifierAttributes");
					extnDescriptorAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"ExtnDescriptorAttributes");
%>
	 				</yfc:hasXMLNode>
<%
					String modifiable = request.getParameter("Modifiable");
					boolean isModifiable = false;
					if (equals(modifiable,"true")) {
						isModifiable = true;
					}
%>
   					<thead> 
   						<tr>
<% 							
							if(equals("Y",request.getParameter("SerialTracked"))|| equals("Y",request.getParameter("TimeSensitive"))|| (equals("Y",request.getParameter("Serialized")) && equals("omr",request.getParameter("applicationCode")))) {
%>
	   							<td class="tablecolumnheader">&nbsp;</td>
<%
							}
%>
<%
							int i = 0;
							while (i < 2) { 
								int j = 0;
								Map normalMap = null;
								Map extnMap = null;
								Map currentMap = null;
								if (i == 0) {
									normalMap = identifierAttrMap;
									extnMap = extnIdentifierAttrMap;
								} else {
									normalMap = descriptorAttrMap;
									extnMap = extnDescriptorAttrMap;
								}		
								if ((normalMap != null) || (extnMap != null)) {
									tagtrack=1;
%>					
<%
									while (j < 2) {
								    	boolean isExtn = false;
										if (j == 0) {
											currentMap = normalMap;
											isExtn = false;
										} else {
											currentMap = extnMap;
											isExtn = true;
										}
										if (currentMap != null) {
											if (!currentMap.isEmpty()) {
												for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
										        	String currentAttr = (String) k.next();
										        	String currentAttrValue = (String) currentMap.get(currentAttr);
%>
													<td class="tablecolumnheader"><yfc:i18n><%=currentAttr%></yfc:i18n></td>
<%
									        	}
									   	 	}
										}					        	
						        		j++;
						        	}
%>
<%
								}
								i++;
							}
%>				
<% 	
							if(equals("Y",request.getParameter("SerialTracked"))|| (equals("Y",request.getParameter("Serialized")) && equals("omr",request.getParameter("applicationCode")))) { 
%>
								<td sortable="no" class="tablecolumnheader"> 
									<yfc:i18n>Serial_#</yfc:i18n>
								</td>
<% 								
								for (int s=1; s <= NoSecSerials ; s++) {
					  				String serLabel= "Secondary_Serial_"+s; 
%>
									<td sortable="no" class="tablecolumnheader">
										<yfc:i18n><%=serLabel%></yfc:i18n>
									</td>
<%
								}
%>	
<%
							}
%>                
<% 	
							if(equals("Y",request.getParameter("TimeSensitive"))) {
%>
								<td sortable="no" class="tablecolumnheader"> 
									<yfc:i18n>Ship_By_Date</yfc:i18n>
								</td>
<%
							}
%> 
							<td sortable="no" class="tablecolumnheader">
								<yfc:i18n>Receiving_Price</yfc:i18n>
							</td>
						</tr>
					</thead>
					<tbody>
					</tbody>
<% 
					String availableToReceiveQty = request.getParameter("availableToReceiveQty");
					String numReceivableLines = request.getParameter("numReceivableLines");
					if(equals("Y",request.getParameter("SerialTracked"))|| equals("Y",request.getParameter("TimeSensitive"))|| (equals("Y",request.getParameter("Serialized")) && equals("omr",request.getParameter("applicationCode")))) {
%>
 						<tfoot>
	 						<tr style='display:none' TemplateRow="true">
								<td class="checkboxcolumn" ></td>
<%
					}
%> 
							<input type="hidden" id="AvailableToReceiveQty" value=<%=availableToReceiveQty%> />
							<input type="hidden" id="NumReceivableLines" value=<%=numReceivableLines%> />
<%
							String binding ="xml:/Receipt/ReceiptLines/ReceiptLine";
							String sOptionCounter = request.getParameter("optionSetBelongingToLine");
							i = 0;
							while (i < 2) { 
								int j = 0;
								Map normalMap = null;
								Map extnMap = null;
								Map currentMap = null;
								if (i == 0) {
									normalMap = identifierAttrMap;
									extnMap = extnIdentifierAttrMap;
								} else {
									normalMap = descriptorAttrMap;
									extnMap = extnDescriptorAttrMap;	
								}		
								if ((normalMap != null) || (extnMap != null)) {
%>
<%
									while (j < 2) {
										boolean isExtn = false;
										if (j == 0) {
											currentMap = normalMap;
											isExtn = false;
										} else {
											currentMap = extnMap;
											isExtn = true;
										}
										if (currentMap != null) {
											if (!currentMap.isEmpty()) {
												for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
													String currentAttr = (String) k.next();
													String currentAttrValue = (String) currentMap.get(currentAttr);
													String sTagSerialExtnBinding = "/@" ;
													if(isExtn) {
														sTagSerialExtnBinding = "/Extn/@" ;
													}
%>
													<td nowrap="true" class="tablecolumn">
<% 
														if(currentAttr=="RevisionNo") {
%>
															<input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_"+sOptionCounter+"/TagSerial_"+sTagSerialExtnBinding +currentAttr,"xml:/Receipt/ReceiptLines/ReceiptLine"+sTagSerialExtnBinding+currentAttr)%> onblur="javascript:if(this.value !='' && (this.value.length != 10 || this.value.indexOf('/') != 2)){alert('Please enter a valid date  in \'mm/dd/yyyy\' format');this.focus();}" />
<%
														} else if(currentAttr=="LotNumber") {
%>
															<!-- 03/03/2015 CHANGED onblur, it used to be :: "javascript:if(this.value !='') { fetchDataWithParams(this,'getSerialList',populateSerialDetails,setSerialDetails(this)); }" -->
															<input type="text" class="unprotectedinput" maxLength=20 size=20 onblur="trim(this); isSerialReal(this);" <%=getTextOptions(binding + "_"+sOptionCounter+"/TagSerial_"+sTagSerialExtnBinding +currentAttr,"xml:/Receipt/ReceiptLines/ReceiptLine"+sTagSerialExtnBinding+currentAttr) %> />
<%
														} else if(currentAttr=="LotAttribute2") {
%>
															<input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_"+sOptionCounter+"/TagSerial_"+sTagSerialExtnBinding +currentAttr,"xml:/Receipt/ReceiptLines/ReceiptLine"+sTagSerialExtnBinding+currentAttr, resolveValue("xml:/Receipt/@ReceivingNode"))%> />
<%													
														} else {
%>
															<input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_"+sOptionCounter+"/TagSerial_"+sTagSerialExtnBinding +currentAttr,"xml:/Receipt/ReceiptLines/ReceiptLine"+sTagSerialExtnBinding+currentAttr) %> />
<%
														}
														if(currentAttr=="ManufacturingDate" || currentAttr=="RevisionNo") {
%>
															<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
<%													
														}
%>
													</td>
<% 
												}
					    					}
										}
	        							j++;
	        						}
								}
								i++;
							}
%> 
    
<%
							if(equals("Y",request.getParameter("SerialTracked"))|| (equals("Y",request.getParameter("Serialized")) && equals("omr", request.getParameter("applicationCode")))) {
%>
	  	 						<td nowrap="true" class="tablecolumn">
	  	 							<!-- 03/03/2015 ADDED the ONBLUR to validate the Serial Number -->
          							<input type="text" class="unprotectedinput" onblur="trim(this); isSerialReal(this);" <%=getTextOptions(binding + "_" +sOptionCounter+"/TagSerial_"+"/@SerialNo")%>/>
       							</td>
<% 
								for (int s=1; s <= NoSecSerials; s++) {
%>
									<td>
										<input type="text" maxLength="20" class="unprotectedinput" <%=getTextOptions(binding + "_" +sOptionCounter+"/SerialDetail_"+"/@SecondarySerial"+s)%>/>
									</td>
<%
								}
%>
<%
							}
%>
<%
							if(equals("Y",request.getParameter("TimeSensitive"))) {
%>
					   			<td nowrap="true" class="tablecolumn">
				           			<input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_" +sOptionCounter+"/TagSerial_"+"/@ShipByDate")%>/>
									<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
				       			</td>
<%
							}
%>
	  						<td class="tablecolumn">
	  							<input type="text" class="unprotectedinput" <%=getTextOptions(binding+"_"+sOptionCounter+"/TagSerial_/Extn/@ExtnReceivingPrice",receivingPrice)%> style='width:100px'/>
  	 						</td>
						   <!--
						   <td nowrap="true" class="tablecolumn">
					      		<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@IsNewRow","Y")%>/> 
						      	<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@ItemID", request.getParameter("ItemID"))%>/>
								<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@ProductClass", request.getParameter("ProductClass"))%>/>
								<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@UnitOfMeasure", request.getParameter("UnitOfMeasure"))%>/>
								<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@InventoryStatus", request.getParameter("InventoryStatus"))%>/>
								<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@DispositionCode")%>/>
								<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@OrderLineKey", request.getParameter("OrderLineKey"))%>/>
								<input type="hidden" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine_"+sOptionCounter+"/@ShipmentLineKey", request.getParameter("ShipmentLineKey"))%>/>
							</td>
						 	-->
	 					</tr>
<% 
					if(equals("Y",request.getParameter("SerialTracked"))|| equals("Y",request.getParameter("TimeSensitive"))|| (equals("Y",request.getParameter("Serialized")) && equals("omr",request.getParameter("applicationCode")))) {
%>
	 						<tr>
						    	<td nowrap="true" colspan="15">
								
						    		<jsp:include page="/common/editabletbl.jsp" flush="true">
						    		</jsp:include>
									
						    	</td>
						    </tr>
						</tfoot>
<%
					}
%>
				</table>
			</td>
		</tr>
	</tbody>
</table>