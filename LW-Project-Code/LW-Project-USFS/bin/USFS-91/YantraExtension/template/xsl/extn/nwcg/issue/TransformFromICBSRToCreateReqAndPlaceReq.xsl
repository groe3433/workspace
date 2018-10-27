<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:nwcg="http://nwcg.gov/services/ross/common_types/1.1" xmlns:ro="http://nwcg.gov/services/ross/resource_order/1.1" xmlns:IntDate="IntDate" >
    <xsl:output indent="yes"/>
    <xsl:template match="/">	
        <xsl:variable name="uppercase">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
        <xsl:variable name="lowercase">abcdefghijklmnopqrstuvwxyz</xsl:variable>
        <ro:CreateRequestAndPlaceReq>
            <xsl:attribute name="EntityName">ISSUE</xsl:attribute>
            <xsl:attribute name="EntityValue">
                <xsl:value-of select="/Order/@OrderNo"/>
            </xsl:attribute>
            <xsl:attribute name="EntityKey">
                <xsl:value-of select="/Order/@OrderHeaderKey"/>
            </xsl:attribute>
            <xsl:attribute name="messageName">CreateRequestAndPlaceReq</xsl:attribute>
            <ro:MessageOriginator>
                <SystemOfOrigin>
                    <SystemType>ICBS</SystemType>
                    <SystemID />
                </SystemOfOrigin>
            </ro:MessageOriginator>
            <ro:IncidentKey>
                <NaturalIncidentKey>
                    <HostID>
                        <UnitIDPrefix>
                            <xsl:call-template name="getUnitIDPrefix"/>
                        </UnitIDPrefix>
                        <UnitIDSuffix>
                            <xsl:call-template name="getUnitIDSuffix"/>
                        </UnitIDSuffix>
                    </HostID>
                    <SequenceNumber>
                        <xsl:call-template name="getSequenceNumber"/>
                    </SequenceNumber>
                    <YearCreated>
                        <xsl:value-of select="/Order/Extn/@ExtnIncidentYear"/>
                    </YearCreated>
                </NaturalIncidentKey>
            </ro:IncidentKey>
            <xsl:variable name="orderType" select="/Order/@OrderType"/>
            <xsl:variable name="rossFinancialCode" select="/Order/Extn/@ExtnROSSFinancialCode"/>
            <xsl:variable name="rossOwningAgency" select="/Order/Extn/@ExtnROSSOwningAgency"/>
            <xsl:variable name="rossFiscalYear" select="/Order/Extn/@ExtnROSSFiscalYear"/>
            <xsl:variable name="navFlag" select="/Order/Extn/@ExtnNavInfo"/>
            <xsl:comment>BEGIN Manish K ICBS-ROSS Issue #64</xsl:comment>
            <xsl:variable name="FsAcctCode" select="/Order/Extn/@ExtnFsAcctCode"/>
            <xsl:variable name="OverrideCode" select="/Order/Extn/@ExtnOverrideCode "/>
            <xsl:variable name="BlmAcctCode" select="/Order/Extn/@ExtnBlmAcctCode "/>
            <xsl:variable name="OtherAcctCode" select="/Order/Extn/@ExtnOtherAcctCode"/>
            <xsl:variable name="FiscalYear" select="substring(/Order/@Createts,1,4)"/>
            <xsl:comment>END Manish K ICBS-ROSS Issue #64</xsl:comment>
            <xsl:for-each select="/Order/OrderLines/OrderLine">
                <xsl:variable name="reqQty" select="substring-before(Extn/@ExtnOrigReqQty, '.')"/>
                <xsl:variable name="status" select="@MaxLineStatus"/>
                <xsl:variable name="reqNo" select="substring-after(Extn/@ExtnRequestNo, '-')"/>
                <xsl:if test="($reqQty != '0') and not($status = '9000' or $status = '9000.010' or $status = '9000.050')">
                    <xsl:if test="($orderType != 'Backordered' and $orderType != 'Forward Order') or (($orderType = 'Backordered' or $orderType = 'Forward Order') and not(contains($reqNo, '.')))">
                        <xsl:element name="ro:Request">
                            <RequestedCatalogItem>
                                <CatalogType>NWCG</CatalogType>
                                <CatalogName>Supply</CatalogName>
                                <CatalogItemDescription>
                                    <xsl:value-of select="Item/@ItemShortDesc"/>
                                </CatalogItemDescription>
                                <CatalogItemMnemonic></CatalogItemMnemonic>
                            </RequestedCatalogItem>
                            <NeedDateTime>
                                <xsl:value-of select="/Order/Extn/@ExtnReqDeliveryDate"/>
                            </NeedDateTime>
                            <SequenceNumber>
                                <xsl:value-of select="$reqNo"/>
                            </SequenceNumber>
                            <QuantityRequested>
                                <xsl:value-of select="substring-before(Extn/@ExtnOrigReqQty, '.')"/>
                            </QuantityRequested>
                            <xsl:comment>BEGIN Manish K ICBS-ROSS Issue #64</xsl:comment>
                            <xsl:choose>
                                <xsl:when test="($rossFinancialCode != '') and (string-length($rossFinancialCode) &gt; 2)">
                                    <FinancialCode>
                                        <Code>
                                            <xsl:value-of select="$rossFinancialCode"/>
                                        </Code>
                                        <OwningAgencyName>
                                            <xsl:value-of select="$rossOwningAgency"/>
                                        </OwningAgencyName>
                                        <FiscalYear>
                                            <xsl:value-of select="$rossFiscalYear"/>
                                        </FiscalYear>
                                    </FinancialCode>
                                </xsl:when>
                                <xsl:when test="($FsAcctCode != '') and (string-length($FsAcctCode) &gt; 2)">
                                    <FinancialCode>
                                        <Code>
                                            <xsl:value-of select="concat($FsAcctCode,' (', $OverrideCode,')')"/>
                                        </Code>
                                        <OwningAgencyName>ICBS</OwningAgencyName>
                                        <FiscalYear>
                                            <xsl:value-of select="$FiscalYear"/>
                                        </FiscalYear>
                                    </FinancialCode>
                                </xsl:when>
                                <xsl:when test="($BlmAcctCode != '') and (string-length($BlmAcctCode) &gt; 2)">
                                    <FinancialCode>
                                        <Code>
                                            <xsl:value-of select="$BlmAcctCode"/>
                                        </Code>
                                        <OwningAgencyName>ICBS</OwningAgencyName>
                                        <FiscalYear>
                                            <xsl:value-of select="$FiscalYear"/>
                                        </FiscalYear>
                                    </FinancialCode>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:if test="($OtherAcctCode != '') and (string-length($OtherAcctCode) &gt; 2)">
                                        <FinancialCode>
                                            <Code>
                                                <xsl:value-of select="$OtherAcctCode"/>
                                            </Code>
                                        <OwningAgencyName>ICBS</OwningAgencyName>
                                        <FiscalYear>
                                            <xsl:value-of select="$FiscalYear"/>
                                        </FiscalYear>
                                        </FinancialCode>
                                    </xsl:if>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:comment>END Manish K ICBS-ROSS Issue #64</xsl:comment>
                            <xsl:choose>
                                <xsl:when test="$navFlag = 'SHIP_ADDRESS'">
                                    <xsl:variable name="altEmailID" select="/Order/PersonInfoShipTo/@AlternateEmailID"/>
                                    <ShippingAddress>
                                        <Name>
                                            <xsl:value-of select="/Order/PersonInfoShipTo/@FirstName"/>
                                            <xsl:text> </xsl:text>
                                            <xsl:value-of select="/Order/PersonInfoShipTo/@LastName"/>
                                        </Name>
                                        <Type>Shipping</Type>
                                        <Line1>
                                            <xsl:value-of select="/Order/PersonInfoShipTo/@AddressLine1"/>
                                        </Line1>
                                        <Line2>
                                            <xsl:value-of select="/Order/PersonInfoShipTo/@AddressLine2"/>
                                        </Line2>
                                        <City>
                                            <xsl:value-of select="/Order/PersonInfoShipTo/@City"/>
                                        </City>
                                        <State>
                                            <xsl:value-of select="/Order/PersonInfoShipTo/@State"/>
                                        </State>
                                        <ZipCode>
                                            <xsl:value-of select="/Order/PersonInfoShipTo/@ZipCode"/>
                                        </ZipCode>
                                        <CountryCode>
                                            <xsl:value-of select="/Order/PersonInfoShipTo/@Country"/>
                                        </CountryCode>
                                        <xsl:if test="($altEmailID != '')">
                                            <UnitID>
                                                <UnitIDPrefix>
                                                    <xsl:value-of select="substring(/Order/PersonInfoShipTo/@AlternateEmailID, 1, 2)"/>
                                                </UnitIDPrefix>
                                                <UnitIDSuffix>
                                                    <xsl:value-of select="substring(/Order/PersonInfoShipTo/@AlternateEmailID, 3)"/>
                                                </UnitIDSuffix>
                                            </UnitID>
                                        </xsl:if>
                                    </ShippingAddress>
                                </xsl:when>
                                <xsl:when test="$navFlag = 'NAV_INST'">
                                    <ShippingInstructions>
                                        <ShippingInstructions>
                                            <xsl:value-of select="/Order/Extn/@ExtnShippingInstructions"/>
                                        </ShippingInstructions>
                                        <City>
                                            <xsl:value-of select="/Order/Extn/@ExtnShipInstrCity"/>
                                        </City>
                                        <State>
                                            <xsl:value-of select="/Order/Extn/@ExtnShipInstrState"/>
                                        </State>
                                    </ShippingInstructions>
                                </xsl:when>
                                <xsl:when test="$navFlag = 'WILL_PICK_UP'">
                                    <WillPickUpInfo>
                                        <PickUpContactName>
                                            <xsl:value-of select="/Order/Extn/@ExtnWillPickUpName"/>
                                        </PickUpContactName>
                                        <PickUpContactInfo>
                                            <xsl:value-of select="/Order/Extn/@ExtnWillPickUpInfo"/>
                                        </PickUpContactInfo>
                                        <PickUpDateTime>
                                            <xsl:value-of select="/Order/Extn/@ExtnReqDeliveryDate"/>
                                        </PickUpDateTime>
                                    </WillPickUpInfo>
                                </xsl:when>
                                <xsl:otherwise>
                                    <WillPickUpInfo>
                                        <PickUpContactName>
                                            <xsl:value-of select="$navFlag"/>
                                        </PickUpContactName>
                                    </WillPickUpInfo>
                                </xsl:otherwise>
                            </xsl:choose>
                            <ShippingContactName>
                                <xsl:value-of select="/Order/Extn/@ExtnShippingContactName"/>
                            </ShippingContactName>
                            <ShippingContactPhone>
                                <xsl:value-of select="/Order/Extn/@ExtnShippingContactPhone"/>
                            </ShippingContactPhone>
                            <xsl:variable name="icOrderType">
                                <xsl:value-of select="translate($orderType, $lowercase, $uppercase)"/>
                            </xsl:variable>
                            <xsl:if test="(($icOrderType = 'REPLACEMENT') or ($icOrderType = 'INCDT_REPLACEMENT'))">
                                <ReplacementInd>true</ReplacementInd>
                            </xsl:if>
                            <SpecialNeeds>
                                <xsl:value-of select="Instructions/Instruction/@InstructionText"/>
                            </SpecialNeeds>
                        </xsl:element>
                    </xsl:if>
                </xsl:if>
            </xsl:for-each>
            <ro:PlaceToUnitID>
                <UnitIDPrefix>
                    <xsl:value-of select="substring(/Order/@ShipNode, 1, 2)"/>
                </UnitIDPrefix>
                <UnitIDSuffix>
                    <xsl:value-of select="substring(/Order/@ShipNode, 3)"/>
                </UnitIDSuffix>
            </ro:PlaceToUnitID>
            <ro:CacheIssue>
                <CacheIssueNumber>
                    <xsl:value-of select="/Order/@OrderNo"/>
                </CacheIssueNumber>
                <CacheIssueCreateDateTime>
                    <xsl:value-of select="/Order/@Createts"/>
                </CacheIssueCreateDateTime>
            </ro:CacheIssue>
        </ro:CreateRequestAndPlaceReq>
    </xsl:template>
    <xsl:template name="getUnitIDPrefix">
        <xsl:value-of select="substring-before(/Order/Extn/@ExtnIncidentNo, '-')"/>
    </xsl:template>
    <xsl:template name="getUnitIDSuffix">
        <xsl:variable name="post" select="substring-after(/Order/Extn/@ExtnIncidentNo, '-')"/>
        <xsl:variable name="last" select="substring-before($post, '-')"/>
        <xsl:value-of select="$last"/>
    </xsl:template>
    <xsl:template name="getSequenceNumber">
        <xsl:variable name="post" select="substring-after(/Order/Extn/@ExtnIncidentNo, '-')"/>
        <xsl:variable name="last" select="substring-after($post, '-')"/>
        <xsl:value-of select="$last"/>
    </xsl:template>
</xsl:stylesheet>