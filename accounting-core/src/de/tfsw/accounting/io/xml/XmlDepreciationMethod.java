
package de.tfsw.accounting.io.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xmlDepreciationMethod.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="xmlDepreciationMethod">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="STRAIGHTLINE"/>
 *     &lt;enumeration value="FULL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "xmlDepreciationMethod")
@XmlEnum
public enum XmlDepreciationMethod {

    STRAIGHTLINE,
    FULL;

    public String value() {
        return name();
    }

    public static XmlDepreciationMethod fromValue(String v) {
        return valueOf(v);
    }

}
