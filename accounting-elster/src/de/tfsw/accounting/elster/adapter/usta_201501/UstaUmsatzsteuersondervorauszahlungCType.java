
package de.tfsw.accounting.elster.adapter.usta_201501;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for usta_UmsatzsteuersondervorauszahlungCType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="usta_UmsatzsteuersondervorauszahlungCType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Jahr" type="{http://www.elster.de/2002/XMLSchema}usta_JahrSType"/>
 *         &lt;element name="Steuernummer" type="{http://www.elster.de/2002/XMLSchema}allg_StNrSType"/>
 *         &lt;element name="Kz09" type="{http://www.elster.de/2002/XMLSchema}usta_Kz09SType"/>
 *         &lt;element name="Kz10" type="{http://www.elster.de/2002/XMLSchema}usta_Kz10SType" minOccurs="0"/>
 *         &lt;element name="Kz26" type="{http://www.elster.de/2002/XMLSchema}usta_Kz26SType" minOccurs="0"/>
 *         &lt;element name="Kz29" type="{http://www.elster.de/2002/XMLSchema}usta_Kz29SType" minOccurs="0"/>
 *         &lt;element name="Kz38" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZPositiveSType"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "usta_UmsatzsteuersondervorauszahlungCType", propOrder = {

})
public class UstaUmsatzsteuersondervorauszahlungCType {

    @XmlElement(name = "Jahr", required = true)
    protected String jahr;
    @XmlElement(name = "Steuernummer", required = true)
    protected String steuernummer;
    @XmlElement(name = "Kz09", required = true)
    protected String kz09;
    @XmlElement(name = "Kz10")
    protected String kz10;
    @XmlElement(name = "Kz26")
    protected String kz26;
    @XmlElement(name = "Kz29")
    protected String kz29;
    @XmlElement(name = "Kz38", required = true)
    protected String kz38;

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

    /**
     * Gets the value of the kz10 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz10() {
        return kz10;
    }

    /**
     * Sets the value of the kz10 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz10(String value) {
        this.kz10 = value;
    }

    /**
     * Gets the value of the kz26 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz26() {
        return kz26;
    }

    /**
     * Sets the value of the kz26 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz26(String value) {
        this.kz26 = value;
    }

    /**
     * Gets the value of the kz29 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz29() {
        return kz29;
    }

    /**
     * Sets the value of the kz29 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz29(String value) {
        this.kz29 = value;
    }

    /**
     * Gets the value of the kz38 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz38() {
        return kz38;
    }

    /**
     * Sets the value of the kz38 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz38(String value) {
        this.kz38 = value;
    }

}
