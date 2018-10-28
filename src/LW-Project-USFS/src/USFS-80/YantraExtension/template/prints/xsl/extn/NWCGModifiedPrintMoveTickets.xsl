<?xml version = "1.0" encoding = "UTF-8"?>
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
<xsl:output indent="yes"/>
<xsl:template name="makePrintDocument">
	<xsl:param name="activityGroupId"/>
	<PrintDocument>
		<xsl:attribute name="BeforeChildrenPrintDocumentId">NWCG_ITEM_PICK_BATCHSHEET</xsl:attribute>
		<xsl:attribute name="DataElementPath">xml:/Batch</xsl:attribute>
		<PrinterPreference>
			<xsl:attribute name="UserId"><xsl:text>xml:/Batch/@Modifyuserid</xsl:text></xsl:attribute>
			<xsl:attribute name="UsergroupId"/>
			<xsl:attribute name="WorkStationId"/>
			<xsl:attribute name="OrganizationCode"><xsl:text>xml:/Batch/@OrganizationCode</xsl:text></xsl:attribute>
		</PrinterPreference>
		<LabelPreference>
			<xsl:attribute name="Node">xml:/Batch/@Node</xsl:attribute>
			<xsl:attribute name="EquipmentType">xml:/Batch/@EquipmentType</xsl:attribute>
		</LabelPreference>
		<KeyAttributes>
			<KeyAttribute>
				<xsl:attribute name="Name"><xsl:text>BatchNo</xsl:text></xsl:attribute>
			</KeyAttribute>	
		</KeyAttributes>
		<InputData>
			<xsl:attribute name="APIName">
				<xsl:text>createBatchForReferences</xsl:text>
			</xsl:attribute>
			<Batch>
				<xsl:attribute name="ActivityGroupId">
					 <xsl:value-of select="$activityGroupId"/>
				</xsl:attribute>
				<xsl:attribute name="OrganizationCode">
				  <xsl:value-of select="@Node"/>
				</xsl:attribute>
				<TaskReferences>
						<xsl:attribute name="MoveRequestNo">
						  <xsl:value-of select="@MoveRequestNo"/>
						</xsl:attribute>
					<xsl:attribute name="MoveRequestKey">
					  <xsl:value-of select="@MoveRequestKey"/>
					</xsl:attribute>
				</TaskReferences>
			</Batch>
			<Template>
			 <BatchList>
				<Batch BatchKey=""/>
			 </BatchList>	
			</Template>
			<InputData>
				<xsl:attribute name="FlowName">
					<xsl:text>NWCGGetBatchDetails</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="ParentDataElement">
					<xsl:text>Batch</xsl:text>
				</xsl:attribute>
				<Batch>
					<xsl:attribute name="BatchKey"><xsl:text>@BatchKey</xsl:text></xsl:attribute> 
				</Batch>
				 <SortAttributes>
						<xsl:attribute name="ListElement">BatchTasks</xsl:attribute>
						<xsl:attribute name="SortDirection">ASC</xsl:attribute>
						
							<Attribute>
								<xsl:attribute name="Name">SourceSortSequence</xsl:attribute>

								<xsl:attribute name="isNumeric">Y</xsl:attribute>
							</Attribute>	
				 </SortAttributes>
			<Template>
			 <Api>
				<xsl:attribute name="Name"><xsl:text>getCustomBatchDetails</xsl:text></xsl:attribute>	
			  <Template>
				<Batch ActivityGroupId="" BatchKey="" ReceiptNo="" BatchNo="" BatchStatusDesc="" CountRequestNo="" EquipmentType="" MoveRequestNo="" Node="" OrganizationCode="" ShipmentNo="" Status="" TaskType="" WaveNo="" Modifyuserid="" WorkOrderNo="">
				<EquipmentType>
					<EquipmentTypeDetails>
						<EquipmentTypeDetail/>
					</EquipmentTypeDetails>
				</EquipmentType>
				<BatchLocations>
					<BatchLocation CartLocationId="" SlotNumber="" ShipmentKey="" ShipmentContainerKey="">
						<Container ShipmentContainerKey="" ContainerNo="">
							<Corrugation ItemID=""/>
						</Container>
					</BatchLocation>
				</BatchLocations>
				<Tasks>
					<Task SourceLocationId="" SourceZoneId="" SourceSortSequence="" TargetZoneId="" TargetLocationId=""  TargetSortSequence="" OrganizationCode="" TaskStatus="" TargetLPNNo="">
						<Inventory SourceCaseId=""  SourcePalletId="" TargetCaseId="" TargetPalletId=""  ItemId="" UnitOfMeasure="" ProductClass="" Quantity="" TagNumber="" SerialNo="">
							<TagAttributes/>
							<Item ItemID="" UnitOfMeasure="">
								<PrimaryInformation Description=""/>
							</Item>
						</Inventory>
						<TaskReferences ReceiptNo="" WaveNo="" BatchNo="" MoveRequestNo="" ShipmentNo="" CountRequestNo="" ShipmentContainerKey="" ShipmentKey=""/>
						<BatchLocation/>
						<Shipment ShipmentKey="" ShipmentSortLocationId=""/>
					</Task>
				</Tasks>
				</Batch>
				</Template>
			 </Api>	
			</Template>
		</InputData>
	  </InputData>
	</PrintDocument>
</xsl:template>
<xsl:template match="MoveRequest">
		<PrintDocuments>
		<xsl:attribute name="FlushToPrinter"><xsl:text>Y</xsl:text></xsl:attribute>
		<xsl:attribute name="PrintAfresh"><xsl:text>Y</xsl:text></xsl:attribute>
		<xsl:attribute name="PrintName"><xsl:text>NWCGItemPickBatchSheet</xsl:text></xsl:attribute>
	       <xsl:call-template name="makePrintDocument">
		    <xsl:with-param name="activityGroupId" select="'PUTAWAY'"/>
	       </xsl:call-template>
	       <xsl:call-template name="makePrintDocument">
		    <xsl:with-param name="activityGroupId" select="'RETRIEVAL'"/>
	       </xsl:call-template>  
	       <xsl:call-template name="makePrintDocument">
		    <xsl:with-param name="activityGroupId" select="'REPLENISHMENT'"/>
	       </xsl:call-template>  
		</PrintDocuments>
</xsl:template>
</xsl:stylesheet>