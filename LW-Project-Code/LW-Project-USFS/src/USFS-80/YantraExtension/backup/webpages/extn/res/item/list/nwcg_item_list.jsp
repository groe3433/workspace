<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<!-- Changes----------------------->
<script language="javascript" src="/yantra/console/scripts/dm.js"></script>
<script language="javascript" src="/yantra/console/scripts/modificationreason.js"></script>
<script>

//------------Step 2 Changes for making the MultiselectToSingleAPI work---------->
	function callyfcMultiSelectToSingleAPIOnAction()
	{
	   //alert('*** reached here***');
	   yfcMultiSelectToSingleAPIOnAction('EntityKey', 'yfcMultiSelectCounter', 'yfcMultiSelectValue1','ItemKey', 'xml:/ItemList/Item', null); 
	   yfcMultiSelectToSingleAPIOnAction('EntityKey','yfcMultiSelectCounter', 'yfcMultiSelectValue2', 'selectedQTY', 'xml:/ItemList/Item', null);
	   return yfcMultiSelectToSingleAPIOnAction('EntityKey','yfcMultiSelectCounter', 'yfcMultiSelectValue3', 'reservationID', 'xml:/ItemList/Item', null);
	   //return true;
	}

	function setSelectedQTY(elem,counter){
		//alert("Elem:"+elem.value+"counter"+counter);
		if(elem.value ==null){
			return;
		}
		var entities = document.all("EntityKey");
		//alert("tyepof"+typeof entities);
		//alert("entities.length :-"+entities.length)
		// only one check box and no array
		if(entities.length =="undefined"){
			//Do Nothing
			//entities.yfcMultiSelectValue2 =  elem.value;
			//entities[counter-1].yfcMultiSelectValue2 =  elem.value;
		}else{// else its an array of checkboxes
			//entities[counter-1].yfcMultiSelectValue2 =  elem.value;
			entities.yfcMultiSelectValue2 =  elem.value;
		}//End IF

		if(entities.length >1){
			entities[counter-1].yfcMultiSelectValue2 =  elem.value;
		}//End IF
	}
</script>

<%
YFCElement myElement = getRequestDOM();
//System.out.println("test2 "+myElement.toString());
%>
<!-- ----------------------->
<table class="table" width="100%" editable="false">
<thead>
   <tr> 
        <td class="lookupiconheader" sortable="no"><br /></td>
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
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ItemList/Item/@QTY")%>">
            <yfc:i18n>QTY For Issue Creation</yfc:i18n>
        </td>
       
   </tr>
</thead>

<!-- Working :- The entity key is formed and gets assigned to the checkbox. The we implement the multiselect to single api to send all the selectd checkbox data as hidden data in the next page-->

<tbody>
    <yfc:loopXML name="ItemList" binding="xml:/ItemList/@Item" id="item"> 
    <tr> 
       	 <yfc:makeXMLInput name="itemKey">
          <yfc:makeXMLKey binding="xml:/Item/@ItemKey" value="xml:item:/Item/@ItemKey" />
		  </yfc:makeXMLInput>
		 <!------------Step 1 Changes for making the MultiselectToSingleAPI work---------->
			<td class="checkboxcolumn">
                    <input type="checkbox" value='<%=getParameter("itemKey")%>' name="EntityKey" yfcMultiSelectCounter='<%=itemCounter%>' 
					yfcMultiSelectValue1='<%=resolveValue("xml:item:/Item/@ItemKey")%>' yfcMultiSelectValue2=''
					yfcMultiSelectValue3='<%=resolveValue("xml:item:/Item/@ReservationID")%>'/>
			</td>
 		 <!------------ ---------->
		 <!-- OLD REMOVE COMMENT 	
         <td class="checkboxcolumn">                     
          <input type="checkbox" value='<%=getParameter("orderKey")%>' name="EntityKey" />
         </td>
		 -->
		 
        <td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/@ItemID"/></td>
		<td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/@UnitOfMeasure"/></td>
		<td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/@ProductClass"/></td>
		<td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/@Description"/></td>
        <td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/@QTY"/></td>
		<td class="tablecolumn"><yfc:getXMLValue name="item" binding="xml:/Item/@shipNode"/></td>
		<td class="tablecolumn"><input type="text" name="selectQty" size="2" value="" onblur="setSelectedQTY(this,<%=itemCounter%>)"/></td>
  </tr>
    </yfc:loopXML> 
</tbody>
</table>
