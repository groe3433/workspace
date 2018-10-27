//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.common_types._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Structure containing location data in Township Range Section (TRS) format
 * 
 * <p>Java class for TownshipRangeSectionCoordinateType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TownshipRangeSectionCoordinateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="State">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="2"/>
 *               &lt;enumeration value="AK"/>
 *               &lt;enumeration value="AL"/>
 *               &lt;enumeration value="AR"/>
 *               &lt;enumeration value="AZ"/>
 *               &lt;enumeration value="CA"/>
 *               &lt;enumeration value="CO"/>
 *               &lt;enumeration value="FL"/>
 *               &lt;enumeration value="IA"/>
 *               &lt;enumeration value="ID"/>
 *               &lt;enumeration value="IN"/>
 *               &lt;enumeration value="IL"/>
 *               &lt;enumeration value="KS"/>
 *               &lt;enumeration value="LA"/>
 *               &lt;enumeration value="MI"/>
 *               &lt;enumeration value="MN"/>
 *               &lt;enumeration value="MO"/>
 *               &lt;enumeration value="MS"/>
 *               &lt;enumeration value="MT"/>
 *               &lt;enumeration value="ND"/>
 *               &lt;enumeration value="NE"/>
 *               &lt;enumeration value="NM"/>
 *               &lt;enumeration value="NV"/>
 *               &lt;enumeration value="OK"/>
 *               &lt;enumeration value="OR"/>
 *               &lt;enumeration value="SD"/>
 *               &lt;enumeration value="UT"/>
 *               &lt;enumeration value="WA"/>
 *               &lt;enumeration value="WI"/>
 *               &lt;enumeration value="WY"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="MeridianName" type="{http://nwcg.gov/services/ross/common_types/1.1}MeridianSimpleType"/>
 *         &lt;element name="Township">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="6"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TownshipDirection">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="North"/>
 *               &lt;enumeration value="South"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Range">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="6"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="RangeDirection">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="East"/>
 *               &lt;enumeration value="West"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Section">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *               &lt;minInclusive value="1"/>
 *               &lt;maxInclusive value="36"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="QuarterName" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="5"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TownshipRangeSectionCoordinateType", namespace = "http://nwcg.gov/services/ross/common_types/1.1", propOrder = {
    "state",
    "meridianName",
    "township",
    "townshipDirection",
    "range",
    "rangeDirection",
    "section",
    "quarterName"
})
public class TownshipRangeSectionCoordinateType {

    @XmlElement(name = "State", required = true)
    protected String state;
    @XmlElement(name = "MeridianName", required = true)
    protected MeridianSimpleType meridianName;
    @XmlElement(name = "Township", required = true)
    protected String township;
    @XmlElement(name = "TownshipDirection", required = true)
    protected String townshipDirection;
    @XmlElement(name = "Range", required = true)
    protected String range;
    @XmlElement(name = "RangeDirection", required = true)
    protected String rangeDirection;
    @XmlElement(name = "Section")
    protected int section;
    @XmlElement(name = "QuarterName")
    protected String quarterName;

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
    }

    public boolean isSetState() {
        return (this.state!= null);
    }

    /**
     * Gets the value of the meridianName property.
     * 
     * @return
     *     possible object is
     *     {@link MeridianSimpleType }
     *     
     */
    public MeridianSimpleType getMeridianName() {
        return meridianName;
    }

    /**
     * Sets the value of the meridianName property.
     * 
     * @param value
     *     allowed object is
     *     {@link MeridianSimpleType }
     *     
     */
    public void setMeridianName(MeridianSimpleType value) {
        this.meridianName = value;
    }

    public boolean isSetMeridianName() {
        return (this.meridianName!= null);
    }

    /**
     * Gets the value of the township property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTownship() {
        return township;
    }

    /**
     * Sets the value of the township property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTownship(String value) {
        this.township = value;
    }

    public boolean isSetTownship() {
        return (this.township!= null);
    }

    /**
     * Gets the value of the townshipDirection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTownshipDirection() {
        return townshipDirection;
    }

    /**
     * Sets the value of the townshipDirection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTownshipDirection(String value) {
        this.townshipDirection = value;
    }

    public boolean isSetTownshipDirection() {
        return (this.townshipDirection!= null);
    }

    /**
     * Gets the value of the range property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRange() {
        return range;
    }

    /**
     * Sets the value of the range property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRange(String value) {
        this.range = value;
    }

    public boolean isSetRange() {
        return (this.range!= null);
    }

    /**
     * Gets the value of the rangeDirection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRangeDirection() {
        return rangeDirection;
    }

    /**
     * Sets the value of the rangeDirection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRangeDirection(String value) {
        this.rangeDirection = value;
    }

    public boolean isSetRangeDirection() {
        return (this.rangeDirection!= null);
    }

    /**
     * Gets the value of the section property.
     * 
     */
    public int getSection() {
        return section;
    }

    /**
     * Sets the value of the section property.
     * 
     */
    public void setSection(int value) {
        this.section = value;
    }

    public boolean isSetSection() {
        return true;
    }

    /**
     * Gets the value of the quarterName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuarterName() {
        return quarterName;
    }

    /**
     * Sets the value of the quarterName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuarterName(String value) {
        this.quarterName = value;
    }

    public boolean isSetQuarterName() {
        return (this.quarterName!= null);
    }

}
