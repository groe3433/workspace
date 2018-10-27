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
import gov.nwcg.services.ross.common_types._1.IncidentReturnType;
import gov.nwcg.services.ross.common_types._1.MessageOriginatorType;
import gov.nwcg.services.ross.common_types._1.ResourceRequestCreateType;
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
 *         &lt;element name="Incident" type="{http://nwcg.gov/services/ross/common_types/1.1}IncidentReturnType"/>
 *         &lt;element name="Request" type="{http://nwcg.gov/services/ross/common_types/1.1}ResourceRequestCreateType" maxOccurs="unbounded"/>
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
    "incident",
    "request",
    "placeToUnitID"
})
@XmlRootElement(name = "PlaceResourceRequestExternalReq")
public class PlaceResourceRequestExternalReq {

    @XmlElement(name = "MessageOriginator", required = true)
    protected MessageOriginatorType messageOriginator;
    @XmlElement(name = "Incident", required = true)
    protected IncidentReturnType incident;
    @XmlElement(name = "Request", required = true)
    protected List<ResourceRequestCreateType> request;
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
     * Gets the value of the incident property.
     * 
     * @return
     *     possible object is
     *     {@link IncidentReturnType }
     *     
     */
    public IncidentReturnType getIncident() {
        return incident;
    }

    /**
     * Sets the value of the incident property.
     * 
     * @param value
     *     allowed object is
     *     {@link IncidentReturnType }
     *     
     */
    public void setIncident(IncidentReturnType value) {
        this.incident = value;
    }

    public boolean isSetIncident() {
        return (this.incident!= null);
    }

    /**
     * Gets the value of the request property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the request property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResourceRequestCreateType }
     * 
     * 
     */
    public List<ResourceRequestCreateType> getRequest() {
        if (request == null) {
            request = new ArrayList<ResourceRequestCreateType>();
        }
        return this.request;
    }

    public boolean isSetRequest() {
        return ((this.request!= null)&&(!this.request.isEmpty()));
    }

    public void unsetRequest() {
        this.request = null;
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
