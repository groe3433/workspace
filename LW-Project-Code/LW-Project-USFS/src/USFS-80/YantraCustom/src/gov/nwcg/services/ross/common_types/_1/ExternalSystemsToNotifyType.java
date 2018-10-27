//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.common_types._1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Structure containing the external systems to notify
 * 
 * <p>Java class for ExternalSystemsToNotifyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExternalSystemsToNotifyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExternalSystem" type="{http://nwcg.gov/services/ross/common_types/1.1}ExternalSystemToNotifyType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalSystemsToNotifyType", namespace = "http://nwcg.gov/services/ross/common_types/1.1", propOrder = {
    "externalSystem"
})
public class ExternalSystemsToNotifyType {

    @XmlElement(name = "ExternalSystem", required = true)
    protected List<ExternalSystemToNotifyType> externalSystem;

    /**
     * Gets the value of the externalSystem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the externalSystem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExternalSystem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExternalSystemToNotifyType }
     * 
     * 
     */
    public List<ExternalSystemToNotifyType> getExternalSystem() {
        if (externalSystem == null) {
            externalSystem = new ArrayList<ExternalSystemToNotifyType>();
        }
        return this.externalSystem;
    }

    public boolean isSetExternalSystem() {
        return ((this.externalSystem!= null)&&(!this.externalSystem.isEmpty()));
    }

    public void unsetExternalSystem() {
        this.externalSystem = null;
    }

}
