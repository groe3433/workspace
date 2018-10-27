//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.common_types._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Structure containing the person resource fitness level and expiration
 * 
 * <p>Java class for PersonResourceFitnessType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonResourceFitnessType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FitnessCode" type="{http://nwcg.gov/services/ross/common_types/1.1}PersonResourceFitnessCodeSimpleType"/>
 *         &lt;element name="FitnessExpirationDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonResourceFitnessType", namespace = "http://nwcg.gov/services/ross/common_types/1.1", propOrder = {
    "fitnessCode",
    "fitnessExpirationDate"
})
public class PersonResourceFitnessType {

    @XmlElement(name = "FitnessCode", required = true)
    protected PersonResourceFitnessCodeSimpleType fitnessCode;
    @XmlElement(name = "FitnessExpirationDate")
    protected XMLGregorianCalendar fitnessExpirationDate;

    /**
     * Gets the value of the fitnessCode property.
     * 
     * @return
     *     possible object is
     *     {@link PersonResourceFitnessCodeSimpleType }
     *     
     */
    public PersonResourceFitnessCodeSimpleType getFitnessCode() {
        return fitnessCode;
    }

    /**
     * Sets the value of the fitnessCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonResourceFitnessCodeSimpleType }
     *     
     */
    public void setFitnessCode(PersonResourceFitnessCodeSimpleType value) {
        this.fitnessCode = value;
    }

    public boolean isSetFitnessCode() {
        return (this.fitnessCode!= null);
    }

    /**
     * Gets the value of the fitnessExpirationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFitnessExpirationDate() {
        return fitnessExpirationDate;
    }

    /**
     * Sets the value of the fitnessExpirationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFitnessExpirationDate(XMLGregorianCalendar value) {
        this.fitnessExpirationDate = value;
    }

    public boolean isSetFitnessExpirationDate() {
        return (this.fitnessExpirationDate!= null);
    }

}
