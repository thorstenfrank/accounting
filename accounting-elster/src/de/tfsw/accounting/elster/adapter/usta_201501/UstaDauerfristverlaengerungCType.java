
package de.tfsw.accounting.elster.adapter.usta_201501;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for usta_DauerfristverlaengerungCType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="usta_DauerfristverlaengerungCType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Jahr" type="{http://www.elster.de/2002/XMLSchema}usta_JahrSType"/>
 *         &lt;element name="Steuernummer" type="{http://www.elster.de/2002/XMLSchema}allg_StNrSType"/>
 *         &lt;element name="Kz09" type="{http://www.elster.de/2002/XMLSchema}usta_Kz09SType"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "usta_DauerfristverlaengerungCType", propOrder = {

})
public class UstaDauerfristverlaengerungCType {

    @XmlElement(name = "Jahr", required = true)
    protected String jahr;
    @XmlElement(name = "Steuernummer", required = true)
    protected String steuernummer;
    @XmlElement(name = "Kz09", required = true)
    protected String kz09;

    /**
     * Gets the value of the jahr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJahr() {
        return jahr;
    }

    /**
     * Sets the value of the jahr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJahr(String value) {
        this.jahr = value;
    }

    /**
     * Gets the value of the steuernummer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSteuernummer() {
        return steuernummer;
    }

    /**
     * Sets the value of the steuernummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSteuernummer(String value) {
        this.steuernummer = value;
    }

    /**
     * Gets the value of the kz09 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz09() {
        return kz09;
    }

    /**
     * Sets the value of the kz09 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz09(String value) {
        this.kz09 = value;
    }

}
