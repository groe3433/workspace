<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document    getBatchSheetData.xsl
    Created on  October 7, 2003
    Author      vinayb 
    Description
        This XSL transforms the XML into unique groups of Location Item combination.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
    <xsl:key name="distinct-shipment" match="TaskReferences" use="@ShipmentContainerKey"/>
    <xsl:key name="distinct-batchtask" match="BatchTask" use="concat(@SourceLocationId,Inventory/@SourceCaseId, Inventory/@SourcePalletId,Inventory/@ItemId,Inventory/@UnitOfMeasure,Inventory/@ProductClass,@OrganizationCode,@TargetLocationId,@TargetCaseId,@TargetPalletId)"/>
    <xsl:key name="distinct-location" match="BatchTask" use="@SourceLocationId"/>
    <xsl:key name="distinct-batchtask2" match="BatchTask" use="concat(Inventory/@ItemId,Inventory/@UnitOfMeasure,Inventory/@ProductClass,@OrganizationCode)"/>
    <xsl:key name="identical-item" match="BatchTask" use="concat(@SourceLocationId,Inventory/@SourceCaseId,Inventory/@SourcePalletId,Inventory/@ItemId,Inventory/@UnitOfMeasure,
		@TargetCaseId,@TargetPalletId,@TargetLocationId)"  />

	<xsl:output indent="yes"/>
    <!-- template rule matching source root element -->
    <xsl:template match="/">
        <Batch>
	    <xsl:for-each select="Batch/@*">
		<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
	    </xsl:for-each> 
	    <xsl:attribute name="LabelFormatId">
	       <xsl:if test="count(Batch/BatchLocations/BatchLocation) &gt; 1" >CART_MANIFEST_BATCHSHEET</xsl:if>
	       <xsl:if test="count(Batch/BatchLocations/BatchLocation) &lt;= 1" >ITEM_PICK_BATCHSHEET</xsl:if>
            </xsl:attribute>
           <xsl:copy-of select="Batch/EquipmentType"/>      
           <xsl:copy-of select="Batch/BatchLocations"/> 
	   <xsl:copy-of select="Batch/CartLocations"/>  
	   <References>
	   <xsl:if test="Batch/@WaveNo != &quot;&quot;">
	      <Reference>
		<xsl:attribute name="ReferenceName"><xsl:text>Wave #:</xsl:text></xsl:attribute>
		<xsl:attribute name="ReferenceNo"><xsl:value-of select="Batch/@WaveNo"/></xsl:attribute>
	      </Reference>
	   </xsl:if>
	   <xsl:if test="Batch/@ShipmentNo != &quot;&quot;">
	      <Reference>
		<xsl:attribute name="ReferenceName"><xsl:text>Shipment #:</xsl:text></xsl:attribute>
		<xsl:attribute name="ReferenceNo"><xsl:value-of select="Batch/@ShipmentNo"/></xsl:attribute>
	      </Reference>
	   </xsl:if>
	   <xsl:if test="Batch/@ReceiptNo != &quot;&quot;">
	      <Reference>
		<xsl:attribute name="ReferenceName"><xsl:text>Receipt #:</xsl:text></xsl:attribute>
		<xsl:attribute name="ReferenceNo"><xsl:value-of select="Batch/@ReceiptNo"/></xsl:attribute>
	      </Reference>
           </xsl:if>
	   <xsl:if test="Batch/@MoveRequestNo != &quot;&quot;">
	      <Reference>
		<xsl:attribute name="ReferenceName"><xsl:text>Request # :</xsl:text></xsl:attribute>
		<xsl:attribute name="ReferenceNo"><xsl:value-of select="Batch/@MoveRequestNo"/></xsl:attribute>
	     </Reference>
	   </xsl:if>
	   <xsl:if test="Batch/@DepositedLPN != &quot;&quot;">
	      <Reference>
		<xsl:attribute name="ReferenceName"><xsl:text>PALLET ID :</xsl:text></xsl:attribute>
		<xsl:attribute name="ReferenceNo"><xsl:value-of select="Batch/@DepositedLPN"/></xsl:attribute>
	     </Reference>
	   </xsl:if>
	   </References>

	   <Tasks>
	      <xsl:apply-templates select="Batch/Tasks/Task">
	      </xsl:apply-templates>
	   </Tasks>


        <xsl:variable name="unique-batch-container"  select="//Task/TaskReferences[generate-id()=generate-id(key('distinct-shipment',@ShipmentContainerKey))]" />

	   <BatchContainers>
		<xsl:for-each select="$unique-batch-container">
			<BatchContainer>
				<xsl:attribute name="ShipmentContainerKey"><xsl:value-of select="@ShipmentContainerKey"/></xsl:attribute>
			</BatchContainer>
		</xsl:for-each>
	   </BatchContainers>

        <xsl:variable name="unique-tasks"  select="//BatchTask[generate-id()=generate-id(key('distinct-batchtask',concat(@SourceLocationId,Inventory/@SourceCaseId, Inventory/@SourcePalletId,Inventory/@ItemId,Inventory/@UnitOfMeasure,Inventory/@ProductClass,@OrganizationCode,@TargetLocationId,@TargetCaseId,@TargetPalletId)))]"/>

        <BatchTasks>
		<xsl:attribute name="TotalLocations"><xsl:value-of select="count(//BatchTask[generate-id()=generate-id(key('distinct-location',@SourceLocationId))])"/></xsl:attribute>
		 <xsl:attribute name="TotalItems"><xsl:value-of select="count(//BatchTask[generate-id()=generate-id(key('distinct-batchtask2',concat(Inventory/@ItemId,Inventory/@UnitOfMeasure,Inventory/@ProductClass,@OrganizationCode)))])"/></xsl:attribute>
        <xsl:for-each select="$unique-tasks">
            <BatchTask>
	         <xsl:attribute name="SourceSortSequence"><xsl:value-of select="@SourceSortSequence"/></xsl:attribute> 
                <xsl:attribute name="SourceLocationId"><xsl:value-of select="@SourceLocationId"/></xsl:attribute> 
		<xsl:attribute name="SerialNo"><xsl:value-of select="Inventory/@SerialNo"/></xsl:attribute>
				<xsl:choose>
					<xsl:when test="/Batch/@ActivityGroupId = &quot;OUTBOUND_PICKING&quot;">
		                <xsl:attribute name="TargetLocationId"><xsl:value-of select="Shipment/@ShipmentSortLocationId"/></xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
		                <xsl:attribute name="TargetLocationId"><xsl:value-of select="@TargetLocationId"/></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:choose>
					<xsl:when test="Inventory/@SourceCaseId != &quot;&quot;">
		                <xsl:attribute name="SourceLPNNo"><xsl:value-of select="Inventory/@SourceCaseId"/></xsl:attribute>
					</xsl:when>
					<xsl:when test="Inventory/@SourcePalletId != &quot;&quot;">
		                <xsl:attribute name="SourceLPNNo"><xsl:value-of select="Inventory/@SourcePalletId"/></xsl:attribute>
					</xsl:when>
				</xsl:choose>
				<xsl:variable name="itemdetails" select="concat(current()/@SourceLocationId,current()/Inventory/@SourceCaseId,current()/Inventory/@SourcePalletId,current()/Inventory/@ItemId,current()/Inventory/@UnitOfMeasure,
		current()/@TargetCaseId,current()/@TargetPalletId,current()/@TargetLocationId)"/>
               <xsl:attribute name="TotalQty"><xsl:value-of select="sum(key('identical-item',$itemdetails)/Inventory/@Quantity)"/></xsl:attribute>             
	        <xsl:choose>
		<xsl:when test="not(Inventory/Item)">
		     <Inventory>
			 <xsl:for-each select="Inventory/@*">
		         <xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
		        </xsl:for-each>
			<Item>
			    <xsl:attribute name="ItemID"/>
   			    <xsl:attribute name="UnitOfMeasure"/>
			 <PrimaryInformation>
			    <xsl:attribute name="Description"/>
			 </PrimaryInformation>	
			</Item>
		     </Inventory>
	        </xsl:when>
		<xsl:otherwise>
		    <xsl:copy-of select="Inventory"/>
		</xsl:otherwise>
		</xsl:choose>
		<TagReferences>
			<xsl:if test="Inventory/TagAttributes/@LotNumber != &quot;&quot;">
				<xsl:attribute name="LotNoReference"><xsl:text>Lot No:</xsl:text></xsl:attribute>
				<xsl:attribute name="LotNumber"><xsl:value-of select="Inventory/TagAttributes/@LotNumber"/></xsl:attribute> 
			</xsl:if>
			<xsl:if test="Inventory/TagAttributes/@BatchNo != &quot;&quot;">
				<xsl:attribute name="BatchNoReference"><xsl:text>Batch No:</xsl:text></xsl:attribute>
				<xsl:attribute name="TagBatchNo"><xsl:value-of select="Inventory/TagAttributes/@BatchNo"/></xsl:attribute> 
			</xsl:if>
			<xsl:if test="Inventory/TagAttributes/@RevisionNo != &quot;&quot;">
				<xsl:attribute name="RevisionNoReference"><xsl:text>Revision No:</xsl:text></xsl:attribute>
				<xsl:attribute name="RevisionNo"><xsl:value-of select="Inventory/TagAttributes/@RevisionNo"/></xsl:attribute> 	
			</xsl:if>
		</TagReferences>    
        
		<xsl:copy-of select="TaskReferences"/>
		
                <CartLocations>
                   <xsl:variable name="unique-cart-locns"  select="CartLocations/CartLocation[not(@CartLocationId=preceding-sibling::CartLocation/@CartLocationId)]"/>
                   <xsl:variable name="all-cart-locns"  select="CartLocations/CartLocation"/>
		    <xsl:for-each select="$unique-cart-locns">
                    <CartLocation>
		       <xsl:variable name="current-qty"><xsl:value-of select="sum($all-cart-locns[@CartLocationId=current()/@CartLocationId]/@CartQuantity)"/></xsl:variable>
                           <xsl:attribute name="CartLocationId"><xsl:value-of select="@CartLocationId"/></xsl:attribute> 
							<xsl:attribute name="ShipmentContainerKey"><xsl:value-of select="@ShipmentContainerKey"/></xsl:attribute>                                                                   
                           <xsl:attribute name="CartQuantity"><xsl:value-of select="$current-qty"/></xsl:attribute>
		    </CartLocation>    
                     </xsl:for-each>
                </CartLocations>
             </BatchTask>   
        </xsl:for-each>
        </BatchTasks>
      </Batch>  
    </xsl:template>

<xsl:template match="Task">
<Task>
    <xsl:for-each select="@*">
	<xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
    </xsl:for-each> 
    <xsl:copy-of select="Inventory"/>
    <xsl:copy-of select="TaskReferences"/>
    <xsl:copy-of select="Shipment"/>
</Task>
</xsl:template>

</xsl:stylesheet> 