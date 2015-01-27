
package de.tfsw.accounting.io.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xmlExpenseType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="xmlExpenseType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OPEX"/>
 *     &lt;enumeration value="CAPEX"/>
 *     &lt;enumeration value="OTHER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "xmlExpenseType")
@XmlEnum
public enum XmlExpenseType {

    OPEX,
    CAPEX,
    OTHER;

    public String value() {
        return name();
    }

    public static XmlExpenseType fromValue(String v) {
        return valueOf(v);
    }

}
