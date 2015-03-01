package de.tfsw.accounting.elster.adapter.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for usta_AnmeldungssteuernCType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="usta_AnmeldungssteuernCType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DatenLieferant" type="{http://www.elster.de/2002/XMLSchema}usta_DatenlieferantCType"/>
 *         &lt;element name="Erstellungsdatum" type="{http://www.elster.de/2002/XMLSchema}usta_MyDateSType"/>
 *         &lt;element name="Steuerfall" type="{http://www.elster.de/2002/XMLSchema}usta_SteuerfallCType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="201501"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="art" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="UStVA"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "usta_AnmeldungssteuernCType", propOrder = {
    "datenLieferant",
    "erstellungsdatum",
    "steuerfall"
})
@XmlRootElement(name = "Anmeldungssteuern")
public class UstaAnmeldungssteuernCType {

    @XmlElement(name = "DatenLieferant", required = true)
    protected UstaDatenlieferantCType datenLieferant;
    @XmlElement(name = "Erstellungsdatum", required = true)
    protected String erstellungsdatum;
    @XmlElement(name = "Steuerfall", required = true)
    protected UstaSteuerfallCType steuerfall;
    @XmlAttribute(name = "version", required = true)
    protected String version;
    @XmlAttribute(name = "art", required = true)
    protected String art;

    /**
     * Gets the value of the datenLieferant property.
     * 
     * @return
     *     possible object is
     *     {@link UstaDatenlieferantCType }
     *     
     */
    public UstaDatenlieferantCType getDatenLieferant() {
        return datenLieferant;
    }

    /**
     * Sets the value of the datenLieferant property.
     * 
     * @param value
     *     allowed object is
     *     {@link UstaDatenlieferantCType }
     *     
     */
    public void setDatenLieferant(UstaDatenlieferantCType value) {
        this.datenLieferant = value;
    }

    /**
     * Gets the value of the erstellungsdatum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErstellungsdatum() {
        return erstellungsdatum;
    }

    /**
     * Sets the value of the erstellungsdatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErstellungsdatum(String value) {
        this.erstellungsdatum = value;
    }

    /**
     * Gets the value of the steuerfall property.
     * 
     * @return
     *     possible object is
     *     {@link UstaSteuerfallCType }
     *     
     */
    public UstaSteuerfallCType getSteuerfall() {
        return steuerfall;
    }

    /**
     * Sets the value of the steuerfall property.
     * 
     * @param value
     *     allowed object is
     *     {@link UstaSteuerfallCType }
     *     
     */
    public void setSteuerfall(UstaSteuerfallCType value) {
        this.steuerfall = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the art property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArt() {
        return art;
    }

    /**
     * Sets the value of the art property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArt(String value) {
        this.art = value;
    }

}
