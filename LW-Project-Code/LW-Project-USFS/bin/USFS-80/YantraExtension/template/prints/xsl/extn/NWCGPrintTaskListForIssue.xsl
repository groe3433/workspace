<?xml version = "1.0" encoding = "UTF-8"?>
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
	<xsl:template match="Print | Batch">
		<PrintDocuments>
		<xsl:attribute name="FlushToPrinter"><xsl:text>Y</xsl:text></xsl:attribute>
		<xsl:attribute name="PrintAfresh"><xsl:text>Y</xsl:text></xsl:attribute>
		<xsl:attribute name="PrintName"><xsl:text>NWCGIssueItemPickBatchSheet</xsl:text></xsl:attribute>
			<PrintDocument>
				<xsl:attribute name="BeforeChildrenPrintDocumentId">NWCG_ISSUE_ITEM_PICK_BATCHSHEET</xsl:attribute>
				<xsl:attribute name="DataElementPath">xml:/Batch</xsl:attribute>
				<xsl:choose>
					<xsl:when test="name()=&quot;Print&quot;">
						<xsl:copy-of select="PrinterPreference"/>
						<LabelPreference>
							<xsl:attribute name="EquipmentType">
								<xsl:text>xml:/Batch/@EquipmentType</xsl:text>
							</xsl:attribute>
							<xsl:copy-of select="LabelPreference/@*"/>
						</LabelPreference>
					</xsl:when>
					<xsl:when test="name()=&quot;Batch&quot;">
						<PrinterPreference>
							<xsl:attribute name="UserId"><xsl:text>xml:/Batch/@Modifyuserid</xsl:text></xsl:attribute>
							<xsl:attribute name="UsergroupId"/>
							<xsl:attribute name="WorkStationId"/>
							<xsl:attribute name="OrganizationCode"><xsl:text>xml:/Batch/@OrganizationCode</xsl:text></xsl:attribute>
						</PrinterPreference>
						<LabelPreference>
							<xsl:attribute name="Node"><xsl:text>xml:/Batch/@OrganizationCode</xsl:text></xsl:attribute>
							<xsl:attribute name="EquipmentType">xml:/Batch/@EquipmentType</xsl:attribute>
						</LabelPreference>
					</xsl:when>
				</xsl:choose>
				<KeyAttributes>
					<KeyAttribute>
						<xsl:attribute name="Name"><xsl:text>BatchNo</xsl:text></xsl:attribute>
					</KeyAttribute>	
				</KeyAttributes>
				<InputData>
					<xsl:attribute name="FlowName">
						<xsl:text>NWCGGetBatchDetailsForIssue</xsl:text>
					</xsl:attribute>
					<Batch>
					<xsl:choose>
						<xsl:when test="name()=&quot;Print&quot;">
							<xsl:copy-of select="Batch/@*" /> 
						</xsl:when>
						<xsl:when test="name()=&quot;Batch&quot;">
							<xsl:copy-of select="@*" /> 
						</xsl:when>
					</xsl:choose>	
					</Batch>
					<Template>
					 <Api>
						<xsl:attribute name="Name"><xsl:text>getCustomBatchDetailsForIssue</xsl:text></xsl:attribute>	
					  <Template>
						<Batch ActivityGroupId="" BatchKey="" ReceiptNo="" BatchNo="" BatchStatusDesc="" CountRequestNo="" EquipmentType="" MoveRequestNo="" Node="" OrganizationCode="" ShipmentNo="" Status="" TaskType="" WaveNo="" Modifyuserid="">
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
								<PredecessorTask TaskId="" TargetPalletId="" TargetCaseId="">
								  <Inventory SourceCaseId="" SourcePalletId="" /> 
								  </PredecessorTask>
							</Task>
						</Tasks>
						</Batch>
						</Template>
					 </Api>	
					</Template>
				</InputData>
				<xsl:if test="normalize-space(@PrintContainerLabel) = &quot;Y&quot;">
					<PrintDocuments>
						<PrintDocument>
							<xsl:attribute name="BeforeChildrenPrintDocumentId">
								<xsl:text>CONTAINER_LABEL</xsl:text> 
							</xsl:attribute>
							<xsl:attribute name="BeforeChildrenLabelFormatId">
								<xsl:text>xml:/Container/@LabelFormatId</xsl:text> 
							</xsl:attribute>
							<xsl:attribute name="DataElementPath">
								<xsl:text>xml:/Container</xsl:text> 
							</xsl:attribute>
							<PrinterPreference>
								<xsl:attribute name="UsergroupId" /> 
								<xsl:attribute name="UserId" /> 
								<xsl:attribute name="WorkStationId">
									<xsl:text>xml:/Container/@ContainerEquipment</xsl:text> 
								</xsl:attribute>
								<xsl:attribute name="OrganizationCode">
									<xsl:text>xml:/Container/Shipment/ShipNode/@ShipNode</xsl:text> 
								</xsl:attribute>
							</PrinterPreference>
							<LabelPreference>
								<xsl:attribute name="BuyerOrganizationCode">
									<xsl:text>xml:/Container/Shipment/@BuyerOrganizationCode</xsl:text> 
								</xsl:attribute>
							</LabelPreference>
							<InputData>
								<xsl:attribute name="FlowName">
									<xsl:text>GetShippingLabelData</xsl:text> 
								</xsl:attribute>
								<xsl:attribute name="ParentDataElement">
									<xsl:text>BatchContainer</xsl:text> 
								</xsl:attribute> 
								<Container>
									<xsl:attribute name="GenerateContainerScm">Y</xsl:attribute> 
									<xsl:attribute name="ShipmentContainerKey">
										<xsl:text>@ShipmentContainerKey</xsl:text> 
									</xsl:attribute>
								</Container>
								<Template>
									<Api Name="getShipmentContainerDetails">
										<Template>
											<Container ContainerEquipment="" ContainerNo="" ContainerScm="" ContainerType="" ShipmentContainerKey="" ShipmentKey="" TrackingNo="" Zone="">
												<BatchLocation BatchNo="" CartLocationId="" SlotNumber="" /> 
												<ContainerDetails TotalNumberOfRecords="">
													<ContainerDetail ItemID="" Quantity="">
														<ShipmentLine ActualQuantity="" CustomerPoNo="" DepartmentCode="" ShipmentLineKey="" MarkForKey="">
															<OrderLine OrderLineKey="">
																<Item ItemID="" CustomerItem="" /> 
															</OrderLine>
															<MarkForAddress Department="" /> 
														</ShipmentLine>
													</ContainerDetail>
												</ContainerDetails>
												<Shipment BuyerOrganizationCode="" ShipmentNo="" SCAC="" ProNo="" BolNo="" TrailerNo="">
													<ScacAndService CarrierType="" /> 
													<ToAddress AddressLine1="" AddressLine2="" FirstName="" MiddleName="" LastName="" City="" State="" Country="" ZipCode="" /> 
													<FromAddress AddressLine1="" AddressLine2="" FirstName="" MiddleName="" LastName="" City="" State="" Country="" ZipCode="" /> 
													<MarkForAddress Department="" /> 
													<ShipNode ShipNode="" NodeOrgCode="" /> 
												</Shipment>
											</Container>
										</Template>
									</Api>
								</Template>
							  </InputData>
						</PrintDocument>
					</PrintDocuments>
				</xsl:if>
			</PrintDocument>
		</PrintDocuments>
	</xsl:template>
</xsl:stylesheet>