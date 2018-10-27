//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.common_types._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Structure containing an entity ID and System the ID is native to
 * 
 * <p>Java class for ExternalIDType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExternalIDType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EntityType" type="{http://nwcg.gov/services/ross/common_types/1.1}EntityTypeSimpleType"/>
 *         &lt;element name="ExternalEntityID" type="{http://nwcg.gov/services/ross/common_types/1.1}ExternalEntityIDSimpleType"/>
 *         &lt;element name="ApplicationSystem" type="{http://nwcg.gov/services/ross/common_types/1.1}ApplicationSystemType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalIDType", namespace = "http://nwcg.gov/services/ross/common_types/1.1", propOrder = {
    "entityType",
    "externalEntityID",
    "applicationSystem"
})
public class ExternalIDType {

    @XmlElement(name = "EntityType", required = true)
    protected EntityTypeSimpleType entityType;
    @XmlElement(name = "ExternalEntityID", required = true)
    protected String externalEntityID;
    @XmlElement(name = "ApplicationSystem", required = true)
    protected ApplicationSystemType applicationSystem;

    /**
     * Gets the value of the entityType property.
     * 
     * @return
     *     possible object is
     *     {@link EntityTypeSimpleType }
     *     
     */
    public EntityTypeSimpleType getEntityType() {
        return entityType;
    }

    /**
     * Sets the value of the entityType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityTypeSimpleType }
     *     
     */
    public void setEntityType(EntityTypeSimpleType value) {
        this.entityType = value;
    }

    public boolean isSetEntityType() {
        return (this.entityType!= null);
    }

    /**
     * Gets the value of the externalEntityID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalEntityID() {
        return externalEntityID;
    }

    /**
     * Sets the value of the externalEntityID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalEntityID(String value) {
        this.externalEntityID = value;
    }

    public boolean isSetExternalEntityID() {
        return (this.externalEntityID!= null);
    }

    /**
     * Gets the value of the applicationSystem property.
     * 
     * @return
     *     possible object is
     *     {@link ApplicationSystemType }
     *     
     */
    public ApplicationSystemType getApplicationSystem() {
        return applicationSystem;
    }

    /**
     * Sets the value of the applicationSystem property.
     * 
     * @param value
     *     allowed object is
     *     {@link ApplicationSystemType }
     *     
     */
    public void setApplicationSystem(ApplicationSystemType value) {
        this.applicationSystem = value;
    }

    public boolean isSetApplicationSystem() {
        return (this.applicationSystem!= null);
    }

}
