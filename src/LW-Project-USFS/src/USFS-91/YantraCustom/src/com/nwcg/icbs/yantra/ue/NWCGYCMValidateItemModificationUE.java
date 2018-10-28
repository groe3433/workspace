/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
 LIMITATION OF LIABILITY
 THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
 LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
 OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
 OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
 OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
 PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
 FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
 BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
 CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
 OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
 */

package com.nwcg.icbs.yantra.ue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.exception.common.NWCGException;

import java.lang.Double;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycm.japi.ue.YCMValidateItemModificationUE;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

/*
 * INXML NWCGYCMValidateItemModificationUE <Item Action="Delete" CallingOrganizationCode="DEFAULT"
 CanUseAsServiceTool="N" GlobalItemID=""
 InheritAttributesFromClassification="N" IsShippingCntr="N"
 ItemGroupCode="PROD" ItemID="a" ItemKey="20070110112752865596"
 OrganizationCode="DEFAULT" ResourceIDForMasterData="YCM00022"
 UnitOfMeasure="EA" YCMInventoryUOMType="QUANTITY">
 <PrimaryInformation CapacityPerOrderedQty="0.00"
 CapacityQuantityStrategy="" CapacityUOM=""
 ComputedUnitCost="0.00" CostCurrency="USD" CountryOfOrigin=""
 CreditWOReceipt="N" DefaultProductClass="" Description=""
 ExtendedDescription="" FixedCapacityQtyPerLine="0.00"
 FixedPricingQtyPerLine="0.00" InvoiceBasedOnActuals="N"
 InvolvesSegmentChange="N" IsHazmat="N" IsReturnService="N"
 IsStandaloneService="" ItemType="" KitCode=""
 ManufacturerItem="" ManufacturerItemDesc="" ManufacturerName=""
 MasterCatalogID="" MaxOrderQuantity="0.00"
 MinOrderQuantity="0.00" MinimumCapacityQuantity="0.00"
 NumSecondarySerials="0" OrderingQuantityStrategy="ENT"
 PricingQuantityConvFactor="0.00" PricingQuantityStrategy="IQTY"
 PricingUOM="EA" PricingUOMStrategy="INV" PrimarySupplier=""
 ProductLine="" RequiresProdAssociation="N" RunQuantity="0.00"
 SerializedFlag="N" ServiceTypeID="" ShortDescription="a"
 Status="2000" TaxableFlag="N" UnitCost="0.00" UnitHeight="0.00"
 UnitHeightUOM="" UnitLength="0.00" UnitLengthUOM=""
 UnitVolume="0.00" UnitVolumeUOM="" UnitWeight="0.00"
 UnitWeightUOM="" UnitWidth="0.00" UnitWidthUOM="" YCMIsPricingUOMInventoryUOMDifferent="N"/>
 <InventoryParameters ATPRule="" DefaultExpirationDays="0"
 InventoryMonitorRule="" IsSerialTracked="N"
 NodeLevelInventoryMonitorRule="" ProcessingTime="0"
 TagControlFlag="N" TimeSensitive="N"/>
 <ClassificationCodes CommodityCode="" ECCNNo="" HarmonizedCode=""
 HazmatClass="" NAICSCode="" NMFCClass="" NMFCCode=""
 OperationalConfigurationComplete="N" PickingType=""
 PostingClassification="" Schedule_B_Code="" StorageType=""
 TaxProductCode="" UNSPSC="" VelocityCode=""/>
 <ContainerInformation CapacityVolume="0.00" CapacityVolumeUom=""
 MaxCntrWeight="0.00" MaxCntrWeightUom="" VolumeAllowance="0.00"/>
 <Extn ExtnNatCriticalItem="N" ExtnOwnerUnitId=""
 ExtnPublishToRoss="N" ExtnRossResourceItem="N" PreferenceCriterion=""/>
 <ItemAliasList/>
 <ItemExclusionList/>
 <AdditionalAttributeList/>
 <LanguageDescriptionList/>
 <Components/>
 <AlternateUOMList/>
 <ItemInstructionList/>
 <ItemOptionList/>
 <ItemServiceAssocList/>
 <ItemServiceSkillList/>
 <ContainerBuyerAssociationList/>
 <ContainerItemAssociationList/>
 <CategoryList/>
 <InheritedAttributes/>
 <AssociationList/>
 </Item>
 */
public class NWCGYCMValidateItemModificationUE implements
		YCMValidateItemModificationUE {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGYCMValidateItemModificationUE.class);

	public void validateItemModification(YFSEnvironment env, Document inDoc)
			throws YFSUserExitException {

		if (logger.isVerboseEnabled())
			logger.verbose("NWCGYCMValidateItemModificationUE inDoc is: "
					+ XMLUtil.getXMLString(inDoc));

		Element elemItem = inDoc.getDocumentElement();
		String strAction = elemItem.getAttribute("Action");

		if (logger.isVerboseEnabled())
			logger.verbose("action " + strAction + "   "
					+ strAction.toLowerCase().equals("delete"));

		if (strAction != null && strAction.toLowerCase().equals("delete")) {
			String strItemID = elemItem.getAttribute("ItemID");
			String strUOM = elemItem.getAttribute("UnitOfMeasure");
			String strOrgCode = elemItem.getAttribute("OrganizationCode");
			String strStatus = "";

			NodeList nlExtn = elemItem.getElementsByTagName("Extn");
			String strExtnPublishToRoss = "";

			if (nlExtn != null && nlExtn.getLength() >= 1) {
				Element eleExtn = (Element) nlExtn.item(0);
				strExtnPublishToRoss = eleExtn
						.getAttribute("ExtnPublishToRoss");
			}

			if (logger.isVerboseEnabled())
				logger.verbose("strExtnPublishToRoss is: "
						+ strExtnPublishToRoss);

			NodeList nlPI = elemItem.getElementsByTagName("PrimaryInformation");
			// if no PC assigned system cant check
			if (nlPI != null && nlPI.getLength() >= 1) {

				Element elemPrimaryInformation = (Element) nlPI.item(0);
				String strDefaultProductClass = elemPrimaryInformation
						.getAttribute("DefaultProductClass");
				strStatus = elemPrimaryInformation.getAttribute("Status");
				boolean bInventoryExists = true;
				if (strDefaultProductClass == null
						|| strDefaultProductClass.equals("")) {
					strDefaultProductClass = NWCGConstants.NWCG_ITEM_DEFAULT_PRODUCT_CLASS;
				}
				if (logger.isVerboseEnabled())
					logger.verbose("Invoking the inventoryExistsAtAnyNode");
				try {
					if (logger.isVerboseEnabled())
						logger.verbose("calling CommonUtilities.inventoryExistsAtAnyNode with params :: + "
								+ strItemID
								+ " "
								+ strUOM
								+ " "
								+ strDefaultProductClass + " " + strOrgCode);
					bInventoryExists = CommonUtilities
							.inventoryExistsAtAnyNode(strItemID, strUOM,
									strDefaultProductClass, strOrgCode, env);

					// determine change made

				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("!!!!! INXML NWCGYCMValidateItemModificationUE Got some exception while getting supply details ",
								e);
				}

				// Jeremy: check here for pending supply
				if (bInventoryExists == true) {
					throw new YFSUserExitException(
							"Can't delete the Item - Inventory Exists");
				}
				if (logger.isVerboseEnabled())
					logger.verbose("bInventoryExists ===> " + bInventoryExists);
			}

			if (logger.isVerboseEnabled())
				logger.verbose("strStatus is: " + strStatus
						+ " and strExtnPublishToRoss is: "
						+ strExtnPublishToRoss);

			if (strStatus.equals(NWCGConstants.NWCG_PUBLISHED_STATUS)
					&& strExtnPublishToRoss.equals(NWCGConstants.YES)) {

				if (logger.isVerboseEnabled())
					logger.verbose("Got inside the loop.");

				if (logger.isVerboseEnabled())
					logger.verbose("\nUser Action: Delete from item list.\n");

				try {
					CommonUtilities
							.invokeService(
									env,
									NWCGConstants.NWCG_PROCESS_DELETE_ITEM_CATALOG_SERVICE,
									inDoc);
				}

				catch (Exception e) {
					logger.error("!!!!! Caught an error on delete item.");
				}

			}// end if
			else {
				if (logger.isVerboseEnabled())
					logger.verbose("Did not send delete to ROSS since item is already removed from ROSS DB.");
			}

		}// end action = delete
		else {
			String strShortDescriptionNew = "";
			NodeList nlPI = elemItem.getElementsByTagName("PrimaryInformation");
			// if no PC assigned system cant check
			if (nlPI != null && nlPI.getLength() >= 1) {
				Element elemPrimaryInformation = (Element) nlPI.item(0);
				strShortDescriptionNew = elemPrimaryInformation
						.getAttribute("ShortDescription");

			}
			String strItemID = elemItem.getAttribute("ItemID");

			NodeList nlComponents = inDoc.getElementsByTagName("Component");

			// if (nlComponents.getLength() != 0){

			if (strShortDescriptionNew != "" && strShortDescriptionNew != null) {

				NWCGItemValidator.validateItemDescription(env,
						strShortDescriptionNew, strItemID);
			} else {
				if (logger.isVerboseEnabled())
					logger.verbose("Short desc is blank or null");
			}
			// }
			determineUserAction(env, inDoc);
			if (logger.isVerboseEnabled())
				logger.verbose("action is not delete !!!!!!!!!!!!!!!!!!!!!");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void determineUserAction(YFSEnvironment env, Document inputXMLDoc)
			throws YFSUserExitException {

		if (logger.isVerboseEnabled())
			logger.verbose("determineUserAction inputXMLDoc is: \n"
					+ XMLUtil.getXMLString(inputXMLDoc));

		String strAction = "";
		String strItemID = "";
		boolean isNoError = true;

		NodeList nlInputPrimaryInformation = inputXMLDoc
				.getElementsByTagName("PrimaryInformation");
		Element eleInputPrimaryInformation = (Element) nlInputPrimaryInformation
				.item(0);

		NodeList nlInputExtn = inputXMLDoc.getElementsByTagName("Extn");
		Element eleInputExtn = (Element) nlInputExtn.item(0);

		if (eleInputPrimaryInformation != null && eleInputExtn != null) {

			Element eleInputRoot = inputXMLDoc.getDocumentElement();

			Document inDoc = null;
			try {
				inDoc = XMLUtil.createDocument("Item");
			} catch (javax.xml.parsers.ParserConfigurationException e) {
				//logger.printStackTrace(e);
			}
			String strUnitOfMeasure = "";
			String strOrganizationCode = "";
			if (eleInputRoot != null) {
				if (logger.isVerboseEnabled())
					logger.verbose("eleInputRoot is not null, creating input to getItemDetails...");
				strItemID = eleInputRoot.getAttribute("ItemID");
				strUnitOfMeasure = eleInputRoot.getAttribute("UnitOfMeasure");
				strOrganizationCode = eleInputRoot
						.getAttribute("OrganizationCode");
			} else {
				throw new YFSUserExitException(
						"There was an error reading the item to be changed.");
			}
			Element rootElem = inDoc.getDocumentElement();
			rootElem.setAttribute("ItemID", strItemID);
			rootElem.setAttribute("UnitOfMeasure", strUnitOfMeasure);
			rootElem.setAttribute("OrganizationCode", strOrganizationCode);

			// TODO: call getItemDetails with created XML of itemid, uom, and
			// orgcode
			// if(returnedXML != null){

			Document outDoc = null;
			try {
				outDoc = CommonUtilities
						.invokeAPI(env, "getItemDetails", inDoc);
			} catch (Exception e) {
				//logger.printStackTrace(e);
			}

			if (logger.isVerboseEnabled())
				logger.verbose("getItemDetails outDoc is: \n"
						+ XMLUtil.getXMLString(outDoc));

			if (outDoc != null) {

				String strInInputStatus;
				String strInMaxOrderQuantity;
				String strInMinOrderQuantity;
				String strInExtnPublishToRoss;
				String strInExtnRossResourceItem;
				String strInExtnStandardPack;
				String strInShortDescription;

				String strRetInputStatus;
				String strRetMaxOrderQuantity;
				String strRetMinOrderQuantity;
				String strRetExtnPublishToRoss;
				String strRetExtnRossResourceItem;
				String strRetExtnStandardPack;
				String strRetShortDescription;
				String strRetAction;

				NodeList nlReturnedPrimaryInformation = outDoc
						.getElementsByTagName("PrimaryInformation");
				Element eleReturnedPrimaryInformation = (Element) nlReturnedPrimaryInformation
						.item(0);

				Element eleReturnedRootElement = outDoc.getDocumentElement();

				NodeList nlExtn = outDoc.getElementsByTagName("Extn");
				Element eleReturnedExtn = (Element) nlExtn.item(0);

				// Input XML Attributes

				strInInputStatus = eleInputPrimaryInformation
						.getAttribute("Status");
				strInMaxOrderQuantity = eleInputPrimaryInformation
						.getAttribute("MaxOrderQuantity");
				strInMinOrderQuantity = eleInputPrimaryInformation
						.getAttribute("MinOrderQuantity");
				strInExtnPublishToRoss = eleInputExtn
						.getAttribute("ExtnPublishToRoss");
				strInExtnRossResourceItem = eleInputExtn
						.getAttribute("ExtnRossResourceItem");
				strInExtnStandardPack = eleInputExtn
						.getAttribute("ExtnStandardPack");
				strInShortDescription = eleInputPrimaryInformation
						.getAttribute("ShortDescription");

				// Returned XML Attributes
				strRetAction = eleReturnedRootElement.getAttribute("Action");
				strRetInputStatus = eleReturnedPrimaryInformation
						.getAttribute("Status");
				strRetMaxOrderQuantity = eleReturnedPrimaryInformation
						.getAttribute("MaxOrderQuantity");
				strRetMinOrderQuantity = eleReturnedPrimaryInformation
						.getAttribute("MinOrderQuantity");
				strRetExtnPublishToRoss = eleReturnedExtn
						.getAttribute("ExtnPublishToRoss");
				strRetExtnRossResourceItem = eleReturnedExtn
						.getAttribute("ExtnRossResourceItem");
				strRetExtnStandardPack = eleReturnedExtn
						.getAttribute("ExtnStandardPack");
				strRetShortDescription = eleReturnedPrimaryInformation
						.getAttribute("ShortDescription");

				eleInputPrimaryInformation.setAttribute("ShortDescriptionOld",
						strRetShortDescription);

				if (logger.isVerboseEnabled())
					logger.verbose("################# Action is: "
							+ strRetAction);
				if (strRetAction.equals("")) {
					if (logger.isVerboseEnabled())
						logger.verbose("strRetAction is blank");
				}

				for (int x = 0; x < 1; x++) {

					if (!(strInInputStatus
							.equals(NWCGConstants.NWCG_UNPUBLISHED_STATUS) && strRetInputStatus
							.equals(NWCGConstants.NWCG_UNPUBLISHED_STATUS))) {

						if (strInInputStatus
								.equals(NWCGConstants.NWCG_PUBLISHED_STATUS)
								&& strRetInputStatus
										.equals(NWCGConstants.NWCG_UNPUBLISHED_STATUS)) {

							if (logger.isVerboseEnabled())
								logger.verbose("strInInputStatus.equals(3000) && strRetInputStatus.equals(2000)");

							if (logger.isVerboseEnabled())
								logger.verbose("Status: 2000 -> 3000");

							if (strInExtnPublishToRoss.equals("Y")) {
								if (logger.isVerboseEnabled())
									logger.verbose("strInExtnPublishToRoss.equals(Y)");

								if (logger.isVerboseEnabled())
									logger.verbose("and PublishToRoss N -> Y");

								if (strAction.equals("CREATE")
										|| strAction.equals("MODIFY")
										|| strAction.equals("")) {
									strAction = "CREATE";
								} else {
									isNoError = false;
									throw new YFSUserExitException(
											NWCGConstants.NWCG_MULTIPLE_STATUS_CHANGE_ERROR);
								}

							}

						}

						if (strInInputStatus
								.equals(NWCGConstants.NWCG_UNPUBLISHED_STATUS)
								&& strRetInputStatus
										.equals(NWCGConstants.NWCG_PUBLISHED_STATUS)) {

							if (logger.isVerboseEnabled())
								logger.verbose("Status: 3000 -> 2000");
							if (logger.isVerboseEnabled())
								logger.verbose("strAction is: " + strAction);
							if (strAction.equals("")
									|| strAction.equals("DELETE")) {

								if (logger.isVerboseEnabled())
									logger.verbose("strAction.equals('') || strAction.equals('DELETE')");

								if (strRetExtnPublishToRoss.equals("Y")) {

									if (logger.isVerboseEnabled())
										logger.verbose("Status: 3000 -> 2000 First delete...");
									strAction = "DELETE";
								}
							} else {
								isNoError = false;
								throw new YFSUserExitException(
										NWCGConstants.NWCG_MULTIPLE_STATUS_CHANGE_ERROR);
							}
						}

						if (strInExtnPublishToRoss.equals("Y")
								&& strRetExtnPublishToRoss.equals("N")) {

							if (logger.isVerboseEnabled())
								logger.verbose("strInExtnPublishToRoss.equals(Y) && strRetExtnPublishToRoss.equals(N)");

							if (!(strInInputStatus
									.equals(NWCGConstants.NWCG_UNPUBLISHED_STATUS))) {
								if (logger.isVerboseEnabled())
									logger.verbose("!(strInInputStatus.equals(2000)");

								if (logger.isVerboseEnabled())
									logger.verbose("strRetExtnPublishToRoss: N -> Y");
								if (strAction.equals("CREATE")
										|| strAction.equals("MODIFY")
										|| strAction.equals("")) {
									strAction = "CREATE";
								} else {
									// TODO: Throw custom error, can't make two
									// changes
									isNoError = false;
									throw new YFSUserExitException(
											NWCGConstants.NWCG_MULTIPLE_STATUS_CHANGE_ERROR);
								}
							}
						}

						if (strInExtnPublishToRoss.equals("N")
								&& strRetExtnPublishToRoss.equals("Y")) {

							if (logger.isVerboseEnabled())
								logger.verbose("strInExtnPublishToRoss.equals(N) && strRetExtnPublishToRoss.equals(Y)");

							if (logger.isVerboseEnabled())
								logger.verbose("strRetExtnPublishToRoss: Y -> N");

							if (strAction.equals("DELETE")
									|| strAction.equals("")) {
								if (logger.isVerboseEnabled())
									logger.verbose("strRetExtnPublishToRoss: Y -> N second delete...");
								strAction = "DELETE";
							} else {
								// TODO: Throw custom error, can't make two
								// changes
								isNoError = false;
								throw new YFSUserExitException(
										NWCGConstants.NWCG_MULTIPLE_STATUS_CHANGE_ERROR);
							}
						}

						if (strInExtnRossResourceItem.equals("Y")
								&& strRetExtnRossResourceItem.equals("N")) {

							if (logger.isVerboseEnabled())
								logger.verbose("strInExtnRossResourceItem.equals(Y) && strRetExtnRossResourceItem.equals(N)");

							if (logger.isVerboseEnabled())
								logger.verbose("strInExtnRossResourceItem: N -> Y");

							if ((strAction.equals("MODIFY") || strAction
									.equals(""))
									&& strInExtnPublishToRoss.equals("Y")
									&& strInInputStatus
											.equals(NWCGConstants.NWCG_PUBLISHED_STATUS)) {

								strAction = "MODIFY";

							} else if (strAction.equals("CREATE")) {
								if (logger.isVerboseEnabled())
									logger.verbose("Action is already 'CREATE', not sending 'MODIFY'");
							} else if (strAction.equals("DELETE")) {
								if (logger.isVerboseEnabled())
									logger.verbose("Action is already 'DELETE', not sending 'MODIFY'");
							} else {
								// TODO: Throw custom error, can't make two
								// changes
								isNoError = false;
								throw new YFSUserExitException(
										NWCGConstants.NWCG_MULTIPLE_STATUS_CHANGE_ERROR);
							}
						}

						if (strInExtnRossResourceItem.equals("N")
								&& strRetExtnRossResourceItem.equals("Y")) {

							if (logger.isVerboseEnabled())
								logger.verbose("strInExtnRossResourceItem.equals(N)	&& strRetExtnRossResourceItem.equals(Y)");

							if (logger.isVerboseEnabled())
								logger.verbose("strInExtnRossResourceItem: Y -> N");
							if ((strAction.equals("MODIFY") || strAction
									.equals(""))
									&& (strInExtnPublishToRoss.equals("Y") && strInInputStatus
											.equals(NWCGConstants.NWCG_PUBLISHED_STATUS))) {

								strAction = "MODIFY";

							}

							else if (strAction.equals("CREATE")) {
								if (logger.isVerboseEnabled())
									logger.verbose("Action is already 'CREATE', not sending 'MODIFY'");
							} else if (strAction.equals("DELETE")) {
								if (logger.isVerboseEnabled())
									logger.verbose("Action is already 'DELETE', not sending 'MODIFY'");
							} else {
								// TODO: Throw custom error, can't make two
								// changes
								isNoError = false;
								throw new YFSUserExitException(
										NWCGConstants.NWCG_MULTIPLE_STATUS_CHANGE_ERROR);
							}
						}

						if (!strRetShortDescription
								.equals(strInShortDescription)) {

							if (logger.isVerboseEnabled())
								logger.verbose("!strRetShortDescription.equals(strInShortDescription)");

							if (logger.isVerboseEnabled())
								logger.verbose("ShortDescription has been modified.");

							if ((strAction.equals("MODIFY") || strAction
									.equals(""))
									&& (strInExtnPublishToRoss.equals("Y") && strInInputStatus
											.equals(NWCGConstants.NWCG_PUBLISHED_STATUS))) {

								strAction = "MODIFY";

							}

							else if (strAction.equals("CREATE")) {
								if (logger.isVerboseEnabled())
									logger.verbose("Action is already 'CREATE', not sending 'MODIFY'");
							} else if (strAction.equals("DELETE")) {

								if (logger.isVerboseEnabled())
									logger.verbose("Action is already 'DELETE', not sending 'MODIFY'");
								throw new YFSUserExitException(
										NWCGConstants.NWCG_MULTIPLE_STATUS_CHANGE_ERROR);
							} else if (strAction.equals("")) {

							} else {
								// TODO: Throw custom error, can't make two
								// changes
								isNoError = false;
								throw new YFSUserExitException(
										NWCGConstants.NWCG_MULTIPLE_STATUS_CHANGE_ERROR);
							}
						}

						if (strInMaxOrderQuantity.equals(""))
							strInMaxOrderQuantity = "0";
						if (strRetMaxOrderQuantity.equals(""))
							strRetMaxOrderQuantity = "0";

						Double decInMaxOrderQuantity = Double
								.valueOf(strInMaxOrderQuantity);
						Double decRetMaxOrderQuantity = Double
								.valueOf(strRetMaxOrderQuantity);

						if (!decInMaxOrderQuantity
								.equals(decRetMaxOrderQuantity)) {

							if (logger.isVerboseEnabled())
								logger.verbose("decInMaxOrderQuantity != decRetMaxOrderQuantity");
							if (logger.isVerboseEnabled())
								logger.verbose("decRetMaxOrderQuantity: "
										+ decRetMaxOrderQuantity);
							if (logger.isVerboseEnabled())
								logger.verbose("decInMaxOrderQuantity: "
										+ decInMaxOrderQuantity);

							if ((strAction.equals("MODIFY") || strAction
									.equals(""))
									&& (strInExtnPublishToRoss.equals("Y") && strInInputStatus
											.equals(NWCGConstants.NWCG_PUBLISHED_STATUS))) {

								strAction = "MODIFY";

							}

							else if (strAction.equals("CREATE")) {
								if (logger.isVerboseEnabled())
									logger.verbose("Action is already 'CREATE', not sending 'MODIFY'");
							} else if (strAction.equals("DELETE")) {
								if (logger.isVerboseEnabled())
									logger.verbose("Action is already 'DELETE', not sending 'MODIFY'");
							} else if (strAction.equals("")) {

							} else {
								// TODO: Throw custom error, can't make two
								// changes
								isNoError = false;
								if (logger.isVerboseEnabled())
									logger.verbose("strAction is: " + strAction);
								throw new YFSUserExitException(
										NWCGConstants.NWCG_MULTIPLE_STATUS_CHANGE_ERROR);

							}

						}

						if (strInMinOrderQuantity.equals(""))
							strInMinOrderQuantity = "0";
						if (strRetMinOrderQuantity.equals(""))
							strRetMinOrderQuantity = "0";

						Double decInMinOrderQuantity = Double
								.valueOf(strInMinOrderQuantity);
						Double decRetMinOrderQuantity = Double
								.valueOf(strRetMinOrderQuantity);

						if (!decInMinOrderQuantity
								.equals(decRetMinOrderQuantity)) {

							if (logger.isVerboseEnabled())
								logger.verbose("decInMinOrderQuantity != decRetMinOrderQuantity");

							if ((strAction.equals("MODIFY") || strAction
									.equals(""))
									&& (strInExtnPublishToRoss.equals("Y") && strInInputStatus
											.equals(NWCGConstants.NWCG_PUBLISHED_STATUS))) {

								strAction = "MODIFY";

							}

							else if (strAction.equals("CREATE")) {
								if (logger.isVerboseEnabled())
									logger.verbose("Action is already 'CREATE', not sending 'MODIFY'");
							} else if (strAction.equals("DELETE")) {
								if (logger.isVerboseEnabled())
									logger.verbose("Action is already 'DELETE', not sending 'MODIFY'");
							} else if (strAction.equals("")) {

							} else {
								// TODO: Throw custom error, can't make two
								// changes
								isNoError = false;
								throw new YFSUserExitException(
										NWCGConstants.NWCG_MULTIPLE_STATUS_CHANGE_ERROR);

							}

						}

						if (!strInExtnStandardPack
								.equals(strRetExtnStandardPack)) {
							if (logger.isVerboseEnabled())
								logger.verbose("strInExtnStandardPack != strRetExtnStandardPack");
							if ((strAction.equals("MODIFY") || strAction
									.equals(""))
									&& (strInExtnPublishToRoss.equals("Y") && strInInputStatus
											.equals(NWCGConstants.NWCG_PUBLISHED_STATUS))) {

								strAction = "MODIFY";

							}

							else if (strAction.equals("CREATE")) {
								if (logger.isVerboseEnabled())
									logger.verbose("Action is already 'CREATE', not sending 'MODIFY'");
							} else if (strAction.equals("DELETE")) {
								if (logger.isVerboseEnabled())
									logger.verbose("Action is already 'DELETE', not sending 'MODIFY'");
							} else if (strAction.equals("")) {
								// Don't do anything. There is no need to send a
								// message to ROSS as it is not published. If we
								// don't have this check
								// ICBSR system will not allow to change the
								// standard pack for an item that is not
								// published to ROSS.
							} else {
								// TODO: Throw custom error, can't make two
								// changes
								isNoError = false;
								throw new YFSUserExitException(
										NWCGConstants.NWCG_MULTIPLE_STATUS_CHANGE_ERROR);

							}
						}

					}

				}

			}

			if (isNoError) {
				if (logger.isVerboseEnabled())
					logger.verbose("\n\n***********************************");
				if (logger.isVerboseEnabled())
					logger.verbose("User Action: " + strAction);
				if (logger.isVerboseEnabled())
					logger.verbose("************************************\n\n");

				if (logger.isVerboseEnabled())
					logger.verbose("XML being sent is...\n"
							+ XMLUtil.getXMLString(inputXMLDoc));
				if (logger.isVerboseEnabled())
					logger.verbose("Action is: " + strAction);

				if (strAction.equals("CREATE")) {
					try {
						CommonUtilities
								.invokeService(
										env,
										NWCGConstants.NWCG_PROCESS_CREATE_ITEM_CATALOG_SERVICE,
										inputXMLDoc);
					} catch (Exception e) {
						logger.error("!!!!! Caught an error on create item.", e);
					}
				}
				if (strAction.equals("MODIFY")) {
					try {

						CommonUtilities
								.invokeService(
										env,
										NWCGConstants.NWCG_PROCESS_MODIFY_ITEM_CATALOG_SERVICE,
										inputXMLDoc);
					} catch (Exception e) {
						logger.error("!!!!! Caught an error on modify item.", e);
					}
				}
				if (strAction.equals("DELETE")) {
					try {
						// if (!(checkPendingSupply(env, strItemID, true))){
						// throw new
						// YFSUserExitException(NWCGConstants.NWCG_CURRENT_PENDING_SUPPLY);
						// }
						// else{
						if (logger.isVerboseEnabled())
							logger.verbose("Calling invoke delete service...");
						if (logger.isVerboseEnabled())
							logger.verbose("Sending DELETE the following doc:");
						if (logger.isVerboseEnabled())
							logger.verbose(XMLUtil.getXMLString(inputXMLDoc));
						CommonUtilities
								.invokeService(
										env,
										NWCGConstants.NWCG_PROCESS_DELETE_ITEM_CATALOG_SERVICE,
										inputXMLDoc);
						// }
					} catch (Exception e) {
						logger.error("!!!!! There was an error while deleting the item.",
									e);
					}
				}
			}

			else {
				if (logger.isVerboseEnabled())
					logger.verbose(NWCGConstants.NWCG_MULTIPLE_STATUS_CHANGE_ERROR);
				throw new YFSUserExitException(
						NWCGConstants.NWCG_MULTIPLE_STATUS_CHANGE_ERROR);
			}
		}

		// Commented out because of conflicts with component items
		else {
			if (logger.isVerboseEnabled())
				logger.verbose("There is no Extn Element in the input document.");
			// throw new
			// YFSUserExitException("Component item added, or gift instructions.");

		}

	}

}
