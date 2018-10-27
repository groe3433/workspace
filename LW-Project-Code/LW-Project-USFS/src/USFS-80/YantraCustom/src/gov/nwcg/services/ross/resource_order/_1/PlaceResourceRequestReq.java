//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.resource_order._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import gov.nwcg.services.ross.common_types._1.CompositeResourceRequestKeyType;
import gov.nwcg.services.ross.common_types._1.MessageOriginatorType;
import gov.nwcg.services.ross.common_types._1.UnitIDType;


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
 *         &lt;element name="PlaceToUnitID" type="{http://nwcg.gov/services/ross/common_types/1.1}UnitIDType"/>
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
    "placeToUnitID"
})
@XmlRootElement(name = "PlaceResourceRequestReq")
public class PlaceResourceRequestReq {

    @XmlElement(name = "MessageOriginator", required = true)
    protected MessageOriginatorType messageOriginator;
    @XmlElement(name = "RequestKey", required = true)
    protected CompositeResourceRequestKeyType requestKey;
    @XmlElement(name = "PlaceToUnitID", required = true)
    protected UnitIDType placeToUnitID;

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
     * Gets the value of the placeToUnitID property.
     * 
     * @return
     *     possible object is
     *     {@link UnitIDType }
     *     
     */
    public UnitIDType getPlaceToUnitID() {
        return placeToUnitID;
    }

    /**
     * Sets the value of the placeToUnitID property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnitIDType }
     *     
     */
    public void setPlaceToUnitID(UnitIDType value) {
        this.placeToUnitID = value;
    }

    public boolean isSetPlaceToUnitID() {
        return (this.placeToUnitID!= null);
    }

}
