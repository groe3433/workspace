//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.resource_order._1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import gov.nwcg.services.ross.common_types._1.AddressType;
import gov.nwcg.services.ross.common_types._1.CompositeResourceRequestKeyType;
import gov.nwcg.services.ross.common_types._1.ConsolidationDetailType;
import gov.nwcg.services.ross.common_types._1.FillDetailType;
import gov.nwcg.services.ross.common_types._1.MessageOriginatorType;
import gov.nwcg.services.ross.common_types._1.ShippingInstructionsCreateType;
import gov.nwcg.services.ross.common_types._1.WillPickUpInformationType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MessageOriginator" type="{http://nwcg.gov/services/ross/common_types/1.1}MessageOriginatorType"/>
 *         &lt;element name="RequestKey" type="{http://nwcg.gov/services/ross/common_types/1.1}CompositeResourceRequestKeyType"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="FillDetail" type="{http://nwcg.gov/services/ross/common_types/1.1}FillDetailType" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="ConsolidationDetail" type="{http://nwcg.gov/services/ross/common_types/1.1}ConsolidationDetailType" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="ShippingAddress" type="{http://nwcg.gov/services/ross/common_types/1.1}AddressType"/>
 *           &lt;element name="ShippingInstructions" type="{http://nwcg.gov/services/ross/common_types/1.1}ShippingInstructionsCreateType"/>
 *           &lt;element name="WillPickUpInfo" type="{http://nwcg.gov/services/ross/common_types/1.1}WillPickUpInformationType"/>
 *         &lt;/choice>
 *         &lt;element name="ShippingContactName" type="{http://nwcg.gov/services/ross/common_types/1.1}ShippingContactNameSimpleType"/>
 *         &lt;element name="ShippingContactPhone" type="{http://nwcg.gov/services/ross/common_types/1.1}ShippingContactPhoneSimpleType"/>
 *         &lt;element name="SpecialNeeds" type="{http://nwcg.gov/services/ross/common_types/1.1}SpecialNeedsSimpleType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "messageOriginator",
    "requestKey",
    "fillDetail",
    "consolidationDetail",
    "shippingAddress",
    "shippingInstructions",
    "willPickUpInfo",
    "shippingContactName",
    "shippingContactPhone",
    "specialNeeds"
})
@XmlRootElement(name = "UpdateNFESResourceRequestReq")
public class UpdateNFESResourceRequestReq {

    @XmlElement(name = "MessageOriginator", required = true)
    protected MessageOriginatorType messageOriginator;
    @XmlElement(name = "RequestKey", required = true)
    protected CompositeResourceRequestKeyType requestKey;
    @XmlElement(name = "FillDetail")
    protected List<FillDetailType> fillDetail;
    @XmlElement(name = "ConsolidationDetail")
    protected ConsolidationDetailType consolidationDetail;
    @XmlElement(name = "ShippingAddress")
    protected AddressType shippingAddress;
    @XmlElement(name = "ShippingInstructions")
    protected ShippingInstructionsCreateType shippingInstructions;
    @XmlElement(name = "WillPickUpInfo")
    protected WillPickUpInformationType willPickUpInfo;
    @XmlElement(name = "ShippingContactName", required = true)
    protected String shippingContactName;
    @XmlElement(name = "ShippingContactPhone", required = true)
    protected String shippingContactPhone;
    @XmlElement(name = "SpecialNeeds")
    protected String specialNeeds;

    /**
     * Gets the value of the messageOriginator property.
     * 
     * @return
     *     possible object is
     *     {@link MessageOriginatorType }
     *     
     */
    public MessageOriginatorType getMessageOriginator() {
        return messageOriginator;
    }

    /**
     * Sets the value of the messageOriginator property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageOriginatorType }
     *     
     */
    public void setMessageOriginator(MessageOriginatorType value) {
        this.messageOriginator = value;
    }

    public boolean isSetMessageOriginator() {
        return (this.messageOriginator!= null);
    }

    /**
     * Gets the value of the requestKey property.
     * 
     * @return
     *     possible object is
     *     {@link CompositeResourceRequestKeyType }
     *     
     */
    public CompositeResourceRequestKeyType getRequestKey() {
        return requestKey;
    }

    /**
     * Sets the value of the requestKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompositeResourceRequestKeyType }
     *     
     */
    public void setRequestKey(CompositeResourceRequestKeyType value) {
        this.requestKey = value;
    }

    public boolean isSetRequestKey() {
        return (this.requestKey!= null);
    }

    /**
     * Gets the value of the fillDetail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fillDetail property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFillDetail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FillDetailType }
     * 
     * 
     */
    public List<FillDetailType> getFillDetail() {
        if (fillDetail == null) {
            fillDetail = new ArrayList<FillDetailType>();
        }
        return this.fillDetail;
    }

    public boolean isSetFillDetail() {
        return ((this.fillDetail!= null)&&(!this.fillDetail.isEmpty()));
    }

    public void unsetFillDetail() {
        this.fillDetail = null;
    }

    /**
     * Gets the value of the consolidationDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ConsolidationDetailType }
     *     
     */
    public ConsolidationDetailType getConsolidationDetail() {
        return consolidationDetail;
    }

    /**
     * Sets the value of the consolidationDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConsolidationDetailType }
     *     
     */
    public void setConsolidationDetail(ConsolidationDetailType value) {
        this.consolidationDetail = value;
    }

    public boolean isSetConsolidationDetail() {
        return (this.consolidationDetail!= null);
    }

    /**
     * Gets the value of the shippingAddress property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getShippingAddress() {
        return shippingAddress;
    }

    /**
     * Sets the value of the shippingAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setShippingAddress(AddressType value) {
        this.shippingAddress = value;
    }

    public boolean isSetShippingAddress() {
        return (this.shippingAddress!= null);
    }

    /**
     * Gets the value of the shippingInstructions property.
     * 
     * @return
     *     possible object is
     *     {@link ShippingInstructionsCreateType }
     *     
     */
    public ShippingInstructionsCreateType getShippingInstructions() {
        return shippingInstructions;
    }

    /**
     * Sets the value of the shippingInstructions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShippingInstructionsCreateType }
     *     
     */
    public void setShippingInstructions(ShippingInstructionsCreateType value) {
        this.shippingInstructions = value;
    }

    public boolean isSetShippingInstructions() {
        return (this.shippingInstructions!= null);
    }

    /**
     * Gets the value of the willPickUpInfo property.
     * 
     * @return
     *     possible object is
     *     {@link WillPickUpInformationType }
     *     
     */
    public WillPickUpInformationType getWillPickUpInfo() {
        return willPickUpInfo;
    }

    /**
     * Sets the value of the willPickUpInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link WillPickUpInformationType }
     *     
     */
    public void setWillPickUpInfo(WillPickUpInformationType value) {
        this.willPickUpInfo = value;
    }

    public boolean isSetWillPickUpInfo() {
        return (this.willPickUpInfo!= null);
    }

    /**
     * Gets the value of the shippingContactName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingContactName() {
        return shippingContactName;
    }

    /**
     * Sets the value of the shippingContactName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingContactName(String value) {
        this.shippingContactName = value;
    }

    public boolean isSetShippingContactName() {
        return (this.shippingContactName!= null);
    }

    /**
     * Gets the value of the shippingContactPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShippingContactPhone() {
        return shippingContactPhone;
    }

    /**
     * Sets the value of the shippingContactPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShippingContactPhone(String value) {
        this.shippingContactPhone = value;
    }

    public boolean isSetShippingContactPhone() {
        return (this.shippingContactPhone!= null);
    }

    /**
     * Gets the value of the specialNeeds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialNeeds() {
        return specialNeeds;
    }

    /**
     * Sets the value of the specialNeeds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialNeeds(String value) {
        this.specialNeeds = value;
    }

    public boolean isSetSpecialNeeds() {
        return (this.specialNeeds!= null);
    }

}
