<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/inventory.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript">
yfcDoNotPromptForChanges(true); 
</script>
<%int tagtrack=0;%>
<%  
    String modifyView = resolveValue("xml:/WorkOrder/@WorkOrderKey");
    modifyView = modifyView == null ? "" : modifyView;
%>
<table class="table" >
	<tbody>
		<tr>
			<td width="10%" >
			&nbsp;
			</td>
			<td width="80%" style="border:1px solid black">
				<table class="table" editable="true" width="100%" cellspacing="0">
					<%Map identifierAttrMap=null;
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
					}%>  
					<yfc:hasXMLNode binding="xml:ComponentTagDetails:/Item/InventoryTagAttributes">
						<%prepareTagDetails ((YFCElement) request.getAttribute(tagContainer),tagElement,(YFCElement) request.getAttribute("ComponentTagDetails"));
						identifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ComponentTagDetails"),"IdentifierAttributes");
						descriptorAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ComponentTagDetails"),"DescriptorAttributes");
						extnIdentifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ComponentTagDetails"),"ExtnIdentifierAttributes");
						extnDescriptorAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ComponentTagDetails"),"ExtnDescriptorAttributes");%>
					</yfc:hasXMLNode>

					<%String modifiable = request.getParameter("Modifiable");
					boolean isModifiable = false;
					if (equals(modifiable,"true")) {
						isModifiable = true;
					}%>
					<thead> 
						<tr>
							<td class="tablecolumnheader">&nbsp</td>
							<%int i = 0;
							while (i < 2){ 
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
													String currentAttrValue = (String) currentMap.get(currentAttr);%>
													<td class="tablecolumnheader"><yfc:i18n><%=currentAttr%></yfc:i18n></td>
												<%}
											}
										}
										j++;
									}%>
								<%}
								i++;
							}%>	
						</tr>
					</thead>
					<tbody>
						<%if (modifyView != ""){
							String totalbind=getParameter("TotalBinding");%>
							<yfc:loopXML binding="xml:/WorkOrder/WorkOrderComponents/@WorkOrderComponent" id="WorkOrderComponent">
								<%String sLineNo=getParameter("componentOptionSetBelongingToLine");
								Integer myInteger=new Integer(Integer.parseInt(sLineNo));
								if (equals(myInteger,WorkOrderComponentCounter)){%>
									<yfc:loopXML binding="xml:/WorkOrderComponent/@WorkOrderComponentTag" id="WorkOrderComponentTag">
										<tr>
											<td class="checkboxcolumn" ></td>
											<%i = 0;
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
																	String sbind="xml:/WorkOrderComponentTag/@"+currentAttr;
																	if(isExtn){
																		sbind="xml:/WorkOrderComponentTag/Extn/@"+currentAttr;
																	}
																	if(currentAttr.indexOf("ManufacturingDate") < 0){%>
																		<td class="tablecolumn">
																			<%=resolveValue(sbind)%>
																		</td>
																	<%}else{%>
																		<td nowrap="true" class="tablecolumn">
																			<input type="text" class="dateinput" <%=getTextOptions(sbind + "_YFCDATE") %> DISABLED/>
																		</td>
																	<%}
																}
															}
														}
														j++;
													}
												}
												i++;
											}%>
										</tr>
									</yfc:loopXML>
								<%}%> 
							</yfc:loopXML>  
						<%}else{%>
							<tr>
								<td class="checkboxcolumn" ></td>
								<%String binding = request.getParameter("TotalBinding");
								i = 0;
								while (i < 2) { 
									int j = 0;
									Map normalMap = null;
									Map extnMap = null;
									Map currentMap = null;
									String currentBinding = "" ;
									if (i == 0) {
										normalMap = identifierAttrMap;
										extnMap = extnIdentifierAttrMap;
									} else {
										normalMap = descriptorAttrMap;
										extnMap = extnDescriptorAttrMap;
									}		
									if ((normalMap != null) || (extnMap != null)) {
										while (j < 2) {
											boolean isExtn = false;
											if (j == 0) {
												currentMap = normalMap;
												currentBinding = binding ;
												isExtn = false;
											} else {
												currentMap = extnMap;
												currentBinding = binding + "/Extn" ;
												isExtn = true;
											}
											if (currentMap != null) {
												if (!currentMap.isEmpty()) {
													for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
														String currentAttr = (String) k.next();
														String currentAttrValue = (String) currentMap.get(currentAttr);
														if(currentAttr.indexOf("ManufacturingDate") < 0){%>
															<td nowrap="true" class="tablecolumn">
																<input type="text" class="unprotectedinput" <%=getTextOptions(currentBinding + "/@" +currentAttr) %>/>
															</td>
														<%}else{%>
															<td nowrap="true" class="tablecolumn">
																<input type="text" class="dateinput" <%=getTextOptions(currentBinding + "/@" +currentAttr+"_YFCDATE", currentBinding + "/@" +currentAttr+"_YFCDATE")%>/>
																<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
															</td>
														<%}
													}
												}
											}
											j++;
										}
									}
									i++;
								}%>
							</tr>
						<%}%>
					</tbody>
				</table>
			</td>
		</tr>
	</tbody>
</table>	