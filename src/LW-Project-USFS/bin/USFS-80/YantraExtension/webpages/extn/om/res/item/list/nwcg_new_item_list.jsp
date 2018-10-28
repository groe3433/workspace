<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<!-- Changes----------------------->
<script language="javascript" src="/yantra/console/scripts/dm.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script>

//------------Step 2 Changes for making the MultiselectToSingleAPI work---------->
	function callyfcMultiSelectToSingleAPIOnAction()
	{
		// Added this to check Qty -- SGN 10/12/2006  
		var entities = "";
		var issueQty = "";
		var reservedQty = "";

		entities = document.all("EntityKey");
		//alert ("ENTITIES.LENGTH : " + entities.length);
		cnt = entities.length;
		if(typeof entities.length == 'undefined') {
			reservedQty = entities.yfcMultiSelectValue9;
			//alert("reservedQty="+reservedQty);
			issueQty = entities.yfcMultiSelectValue2;
			//alert("issueQty="+issueQty);
			if (document.containerform.EntityKey.checked) {
				if (issueQty == "") {
					alert("QTY for Issue needed for all Selected Items");
					return false;
				}
				if (parseInt(issueQty) == 0) {
					alert("QTY for Issue must be greater than zero for all Selected Items");
					return false;
				}
				if (parseInt(issueQty) > parseInt(reservedQty)) {
					alert("QTY for Issue cannot exceed Reserved Quantity for all Selected Items");
					return false;
				}
			}
		} else {
			for (i=0;i<cnt;i++) {
				//alert ("I VALUE : " + i);
				reservedQty = entities[i].yfcMultiSelectValue9;
				//alert("reservedQty="+reservedQty);
				issueQty = entities[i].yfcMultiSelectValue2;
				//alert("issueQty="+issueQty);

				// perform the below sanity checks
				if (document.containerform.EntityKey[i].checked) {
					if (issueQty == "") {
						alert("QTY for Issue needed for all Selected Items");
						return false;
					}
					if (parseInt(issueQty) == 0) {
						alert("QTY for Issue must be greater than zero for all Selected Items");
						return false;
					}
					if (parseInt(issueQty) > parseInt(reservedQty)) {
						alert("QTY for Issue cannot exceed Reserved Quantity for all Selected Items");
						return false;
					}
				}
			}// for loop
		}
		
	    yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue1','ItemKey', 'xml:/ItemList/Item', null); 
	    yfcMultiSelectToSingleAPIOnAction('EntityKey','yfcMultiSelectCounter', 'yfcMultiSelectValue2', 'SelectedQTY', 'xml:/ItemList/Item', null);
	    yfcMultiSelectToSingleAPIOnAction('EntityKey','yfcMultiSelectCounter', 'yfcMultiSelectValue3', 'ReservationID', 'xml:/ItemList/Item', null);
	    yfcMultiSelectToSingleAPIOnAction('EntityKey','yfcMultiSelectCounter', 'yfcMultiSelectValue4', 'ShipNode', 'xml:/ItemList/Item', null);
	    yfcMultiSelectToSingleAPIOnAction('EntityKey','yfcMultiSelectCounter', 'yfcMultiSelectValue5', 'ItemID', 'xml:/ItemList/Item', null);
	    yfcMultiSelectToSingleAPIOnAction('EntityKey','yfcMultiSelectCounter', 'yfcMultiSelectValue6', 'UnitOfMeasure', 'xml:/ItemList/Item', null);
	    yfcMultiSelectToSingleAPIOnAction('EntityKey','yfcMultiSelectCounter', 'yfcMultiSelectValue7', 'ProductClass', 'xml:/ItemList/Item', null);
	    yfcMultiSelectToSingleAPIOnAction('EntityKey','yfcMultiSelectCounter', 'yfcMultiSelectValue8', 'ShipDate', 'xml:/ItemList/Item', null);
	    return true;
	}

	function setSelectedQTY(elem,counter)
	{
		//alert("Elem="+elem.value+" Counter="+counter);
		if(elem.value == null){
			return false;
		}
		var entities = document.all("EntityKey");
		//alert("tyepof entities=["+typeof entities+"]");
		//alert("entities.length=["+entities.length+"]");

		if(typeof entities.length == 'undefined'){
			// only one check box and no array
			entities.yfcMultiSelectValue2 = elem.value;
		}else{// else it's an array of checkboxes
			entities[counter-1].yfcMultiSelectValue2 = elem.value;
		}//End IF
	}
</script>

<%
YFCElement myElement = getRequestDOM();
//System.out.println("test2 "+myElement.toString());
//ystem.out.println("PRINTING INPUT XML FOR ITEM_LIST.JSP");
//System.out.println(getRequestDOM()); 
%>
<!-- ----------------------->
<table class="table" width="100%" editable="false">
<thead>
   <tr> 
        <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
		</td>
        
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/@ReservationID")%>">
            <yfc:i18n>Reservation_ID</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/@ItemID")%>">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/@UnitOfMeasure")%>">
            <yfc:i18n>UOM</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/@ProductClass")%>">
            <yfc:i18n>Product Class</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/@Description")%>">
            <yfc:i18n>Short Description</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/@QTY")%>">
            <yfc:i18n>Reserved Quantity</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/@shipNode")%>">
            <yfc:i18n>ShipNode</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/@shipDate")%>">
            <yfc:i18n>Ship Date</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/@QTY")%>">
            <yfc:i18n>QTY For Issue Creation</yfc:i18n>
        </td>
   </tr>
</thead>

<!-- Working :- The entity key is formed and gets assigned to the checkbox.
Then we implement the multiselect to single api to send all the selectd checkbox data
as hidden data in the next page -->

<tbody>
    <yfc:loopXML name="ItemList" binding="xml:/ItemList/@Item" id="item"> 
    <tr> 
       	<yfc:makeXMLInput name="itemKey">
			<yfc:makeXMLKey binding="xml:/Item/@ItemKey" value="xml:item:/Item/@ItemKey" />
		</yfc:makeXMLInput>
		<!------------Step 1 Changes for making the MultiselectToSingleAPI work---------->
		<td class="checkboxcolumn">
			<input type="checkbox" value='<%=getParameter("itemKey")%>' name="EntityKey" yfcMultiSelectCounter='<%=itemCounter%>' 
			yfcMultiSelectValue1='<%=resolveValue("xml:item:/Item/@ItemKey")%>' 
			yfcMultiSelectValue2=''
			yfcMultiSelectValue3='<%=resolveValue("xml:item:/Item/@ReservationID")%>'
			yfcMultiSelectValue4='<%=resolveValue("xml:item:/Item/@shipNode")%>'
			yfcMultiSelectValue5='<%=resolveValue("xml:item:/Item/@ItemID")%>'
			yfcMultiSelectValue6='<%=resolveValue("xml:item:/Item/@UnitOfMeasure")%>'
			yfcMultiSelectValue7='<%=resolveValue("xml:item:/Item/@ProductClass")%>'
			yfcMultiSelectValue8='<%=resolveValue("xml:item:/Item/@shipDate")%>'
			yfcMultiSelectValue9='<%=resolveValue("xml:item:/Item/@QTY")%>'/>
		</td>
 		 <!------------ ---------->
		 <!-- OLD REMOVE COMMENT 	
         <td class="checkboxcolumn">                     
          <input type="checkbox" value='<%=getParameter("orderKey")%>' name="EntityKey" />
         </td>
		 -->
		<td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/@ReservationID"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/@ItemID"/></td>
		<td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/@UnitOfMeasure"/></td>
		<td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/@ProductClass"/></td>
		<td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/@Description"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/@QTY"/></td>
		<td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/@shipNode"/></td>
		<td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/@shipDate"/></td>
		<td class="tablecolumn"><input type="text" name="selectQty" size="2" value="" onblur="setSelectedQTY(this,<%=itemCounter%>)"/></td>
  </tr>
    </yfc:loopXML> 
</tbody>
</table>
