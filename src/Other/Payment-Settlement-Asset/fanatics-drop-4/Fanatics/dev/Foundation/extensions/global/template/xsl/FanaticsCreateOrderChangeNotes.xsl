<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"

	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
			<xsl:param name="BilltoNote">updated billing address</xsl:param>
			<xsl:param name="ShiptoNote">updated Shipping address</xsl:param>
			<xsl:param name="RevisedShipDateNote">revised ship by date</xsl:param>
			<xsl:param name="WaivedMLRFeeNote">waived MRL fee</xsl:param>
			<xsl:param name="ApplyHoldNote1">put order on</xsl:param>
			<xsl:param name="ApplyHoldBuyersRemorse">Order is in buyer's remorse hold</xsl:param>
			<xsl:param name="ApplyHoldPendingReview">Order is in pending review hold</xsl:param>
			<xsl:param name="ApplyHoldAwaitReview">put order on await review hold</xsl:param>
			<xsl:param name="ApplyHoldPendingEvaluation">put order in Pending Evaluation hold</xsl:param>
			<xsl:param name="HoldNote">hold,</xsl:param>
			<xsl:param name="ResolveAwaitCallNote">removed Await call hold</xsl:param>
			<xsl:param name="ResolveHoldBuyersRemorse">removed buyer's remorse hold</xsl:param>
			<xsl:param name="AwaitCallNote">put order on Await call hold</xsl:param>
			<xsl:param name="ResolveHoldAwaitReview">removed Await Review Hold</xsl:param>
			<xsl:param name="ResolveHoldPendingReview">removed pending review hold</xsl:param>
			<xsl:param name="CancelOrderNote">cancelled order</xsl:param> 
			<xsl:param name="ChangeShippingMethod">updated shipping method</xsl:param>
			<xsl:param name="ApproveOrderNote">Approved Order</xsl:param> 
			<xsl:param name="RejectOrderNote">Rejected Order</xsl:param> 
			<xsl:param name="PackSLipNote">updated the pack slip comments</xsl:param> 
			<xsl:param name="FreezeNote">froze the order</xsl:param>
	
<xsl:template match="/">
	<Order>
		<xsl:copy-of select="@*|Order/@*" />
		<xsl:copy-of select="/Order/*[not(self::Notes)]" />
		<xsl:call-template name="addNotes"/>
	</Order>
    
</xsl:template>

<xsl:template name="addNotes">
 		<Notes>
			<xsl:copy-of select="@*|Order/Notes/Note" />		
 			<xsl:choose>
				<xsl:when test="((/Order/PersonInfoBillTo/@AddressLine1) or (/Order/PersonInfoBillTo/@AddressLine2) or (/Order/PersonInfoBillTo/@City) or (/Order/PersonInfoBillTo/@ZipCode) or (/Order/PersonInfoBillTo/@State)) and (/Order[@createNote=&quot;Y&quot;])">
						<Note>
							<xsl:attribute name="NoteText">
         					<xsl:value-of
								select="concat(/Order/@UserId, ' ', $BilltoNote ,' ',/Order/@CurrentDBDateTime)" />
  							</xsl:attribute>
						</Note>
				</xsl:when>
			</xsl:choose>				
			<xsl:choose>
				<xsl:when test="((/Order/PersonInfoShipTo/@AddressLine1) or (/Order/PersonInfoShipTo/@AddressLine2) or (/Order/PersonInfoBillTo/@City) or (/Order/PersonInfoBillTo/@ZipCode) or (/Order/PersonInfoBillTo/@State)) and (/Order[@createNote=&quot;Y&quot;])">
				
						<Note>
							<xsl:attribute name="NoteText">
         					<xsl:value-of
								select="concat(/Order/@UserId, ' ' , $ShiptoNote, ' ',/Order/@CurrentDBDateTime)" />
  							</xsl:attribute>
						</Note>
					
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="//Order/OrderLines/OrderLine/OrderDates/OrderDate[@DateTypeId=&quot;REVISED_SHIP_DATE&quot;]">
						<Note>
							<xsl:attribute name="NoteText">
         					<xsl:value-of
								select="concat(/Order/@UserId, ' ' , $RevisedShipDateNote, ' ',/Order/@CurrentDBDateTime)" />
  							</xsl:attribute>
						</Note>
				</xsl:when>
			</xsl:choose>			
			<xsl:choose>
				<xsl:when test="//Order/CustomAttributes[@IsMRLFeeWaived=&quot;N&quot;]">
						<Note>
							<xsl:attribute name="NoteText">
         					<xsl:value-of
								select="concat(/Order/@UserId, ' ' , $WaivedMLRFeeNote, ' ',/Order/@CurrentDBDateTime)" />
  							</xsl:attribute>
						</Note>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="//Order/OrderHoldTypes/OrderHoldType">				
					<xsl:for-each select="/Order/OrderHoldTypes/OrderHoldType[@Status=&quot;1100&quot;]">
						<xsl:if test="@HoldType='PENDINGEVALUATION'">
               				<Note>
								<xsl:attribute name="NoteText">
         							<xsl:value-of
										select="concat(/Order/@UserId, ' ' , $ApplyHoldPendingEvaluation, ' ' , /Order/@CurrentDBDateTime)" />
  									</xsl:attribute>
							</Note>
            			</xsl:if>
						<xsl:if test="@HoldType='BUYERSREMORSE'">
            				<Note>
								<xsl:attribute name="NoteText">
         							<xsl:value-of
										select="concat($ApplyHoldBuyersRemorse, ' ' , /Order/@CurrentDBDateTime)" />
  								</xsl:attribute>
							</Note>
         				</xsl:if>
         				<xsl:if test="@HoldType='AWAITCALL'">
            				<Note>
								<xsl:attribute name="NoteText">
         							<xsl:value-of
										select="concat(/Order/@UserId, ' ' ,$AwaitCallNote, ' ' , /Order/@CurrentDBDateTime)" />
  								</xsl:attribute>
							</Note>
         				</xsl:if>
         				<xsl:if test="@HoldType='PENDINGREVIEW'">
            				<Note>
								<xsl:attribute name="NoteText">
         							<xsl:value-of
										select="concat($ApplyHoldPendingReview, ' ' , /Order/@CurrentDBDateTime)" />
  								</xsl:attribute>
							</Note>
         				</xsl:if>
         				<xsl:if test="@HoldType='AWAITREVIEW'">
            				<Note>
								<xsl:attribute name="NoteText">
         							<xsl:value-of
										select="concat(/Order/@UserId, ' ' , $ApplyHoldAwaitReview, ' ' , /Order/@CurrentDBDateTime)" />
  								</xsl:attribute>
							</Note>
         				</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="/Order/OrderHoldTypes/OrderHoldType[@Status=&quot;1300&quot;]">
					<xsl:if test="@HoldType='PENDINGEVALUATION' and @FraudResponseRef='APPROVED'">
               				<Note>
								<xsl:attribute name="NoteText">
         							<xsl:value-of
										select="concat(/Order/@UserId, ' ' , $ApproveOrderNote, ' ' , /Order/@CurrentDBDateTime)" />
  									</xsl:attribute>
							</Note>
            			</xsl:if>
            			<xsl:if test="@HoldType='BUYERSREMORSE'">
            				<Note>
								<xsl:attribute name="NoteText">
         							<xsl:value-of
										select="concat(/Order/@UserId, ' ' , $ResolveHoldBuyersRemorse, ' ' , /Order/@CurrentDBDateTime)" />
  								</xsl:attribute>
							</Note>
         				</xsl:if>
         				<xsl:if test="@HoldType='AWAITCALL'">
            				<Note>
								<xsl:attribute name="NoteText">
         							<xsl:value-of
										select="concat(/Order/@UserId, ' ' ,$ResolveAwaitCallNote, ' ' , /Order/@CurrentDBDateTime)" />
  								</xsl:attribute>
							</Note>
         				</xsl:if>
         				<xsl:if test="@HoldType='AWAITREVIEW'">
            				<Note>
								<xsl:attribute name="NoteText">
         							<xsl:value-of
										select="concat(/Order/@UserId, ' ' , $ResolveHoldAwaitReview, ' ' , /Order/@CurrentDBDateTime)" />
  								</xsl:attribute>
							</Note>
         				</xsl:if>
         				<xsl:if test="@HoldType='PENDINGREVIEW'">
            				<Note>
								<xsl:attribute name="NoteText">
         							<xsl:value-of
										select="concat(/Order/@UserId, ' ' , $ResolveHoldPendingReview, ' ' , /Order/@CurrentDBDateTime)" />
  								</xsl:attribute>
							</Note>
         				</xsl:if>

					</xsl:for-each>					
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="(//Order/OrderLines/OrderLine/@CarrierServiceCode)">
				<xsl:for-each select="/Order/OrderLines/OrderLine/@CarrierServiceCode">
						<Note>
							<xsl:attribute name="NoteText">
         					<xsl:value-of
								select="concat(/Order/@UserId, ' ', $ChangeShippingMethod ,' ',/Order/@CurrentDBDateTime)" />
  							</xsl:attribute>
						</Note>
					</xsl:for-each>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="//Order/Instructions/Instruction[@InstructionType=&quot;Packslip&quot;]">
						<Note>
							<xsl:attribute name="NoteText">
         					<xsl:value-of
								select="concat(/Order/@UserId, ' ' , $PackSLipNote, ' ',/Order/@CurrentDBDateTime)" />
  							</xsl:attribute>
						</Note>
				</xsl:when>
			</xsl:choose>
			<xsl:choose>
				<xsl:when test="//Order/Instructions/Instruction[@InstructionType=&quot;Freeze&quot;]">
						<Note>
							<xsl:attribute name="NoteText">
         					<xsl:value-of
								select="concat(/Order/@UserId, ' ' , $FreezeNote, ' ',/Order/@CurrentDBDateTime)" />
  							</xsl:attribute>
						</Note>
				</xsl:when>
			</xsl:choose>
						<xsl:choose>
				<xsl:when test="//Order/CustomAttributes[@FraudStatus=&quot;2&quot;]">
						<Note>
							<xsl:attribute name="NoteText">
								<xsl:value-of
										select="concat(/Order/CustomAttributes/@FraudStatusUserID, ' ' , $RejectOrderNote, ' ' , /Order/@CurrentDBDateTime)" />
  							</xsl:attribute>
						</Note>
				</xsl:when>
			</xsl:choose>
		</Notes>
</xsl:template>	

</xsl:stylesheet>
