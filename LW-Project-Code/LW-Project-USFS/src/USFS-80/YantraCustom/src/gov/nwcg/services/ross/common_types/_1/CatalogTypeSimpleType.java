//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.common_types._1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CatalogTypeSimpleType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CatalogTypeSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NWCG"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
@XmlType(name = "CatalogTypeSimpleType", namespace = "http://nwcg.gov/services/ross/common_types/1.1")
public enum CatalogTypeSimpleType {

    NWCG;

    public String value() {
        return name();
    }

    public static CatalogTypeSimpleType fromValue(String v) {
        return valueOf(v);
    }

}
