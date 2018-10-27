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
import gov.nwcg.services.ross.common_types._1.CompositeIncidentRadioFrequencyKeyType;
import gov.nwcg.services.ross.common_types._1.ResponseStatusType;


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
 *         &lt;element name="ResponseStatus" type="{http://nwcg.gov/services/ross/common_types/1.1}ResponseStatusType"/>
 *         &lt;element name="IncidentRadioFrequencyKey" type="{http://nwcg.gov/services/ross/common_types/1.1}CompositeIncidentRadioFrequencyKeyType" maxOccurs="unbounded" minOccurs="0"/>
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
    "responseStatus",
    "incidentRadioFrequencyKey"
})
@XmlRootElement(name = "ReplaceIncidentRadioFrequenciesResp")
public class ReplaceIncidentRadioFrequenciesResp {

    @XmlElement(name = "ResponseStatus", required = true)
    protected ResponseStatusType responseStatus;
    @XmlElement(name = "IncidentRadioFrequencyKey")
    protected List<CompositeIncidentRadioFrequencyKeyType> incidentRadioFrequencyKey;

    /**
     * Gets the value of the responseStatus property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseStatusType }
     *     
     */
    public ResponseStatusType getResponseStatus() {
        return responseStatus;
    }

    /**
     * Sets the value of the responseStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseStatusType }
     *     
     */
    public void setResponseStatus(ResponseStatusType value) {
        this.responseStatus = value;
    }

    public boolean isSetResponseStatus() {
        return (this.responseStatus!= null);
    }

    /**
     * Gets the value of the incidentRadioFrequencyKey property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the incidentRadioFrequencyKey property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncidentRadioFrequencyKey().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CompositeIncidentRadioFrequencyKeyType }
     * 
     * 
     */
    public List<CompositeIncidentRadioFrequencyKeyType> getIncidentRadioFrequencyKey() {
        if (incidentRadioFrequencyKey == null) {
            incidentRadioFrequencyKey = new ArrayList<CompositeIncidentRadioFrequencyKeyType>();
        }
        return this.incidentRadioFrequencyKey;
    }

    public boolean isSetIncidentRadioFrequencyKey() {
        return ((this.incidentRadioFrequencyKey!= null)&&(!this.incidentRadioFrequencyKey.isEmpty()));
    }

    public void unsetIncidentRadioFrequencyKey() {
        this.incidentRadioFrequencyKey = null;
    }

}
