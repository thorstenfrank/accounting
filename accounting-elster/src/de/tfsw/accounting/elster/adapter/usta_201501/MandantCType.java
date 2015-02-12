
package de.tfsw.accounting.elster.adapter.usta_201501;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MandantCType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MandantCType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.elster.de/2002/XMLSchema}NameSType" minOccurs="0"/>
 *         &lt;element name="Vorname" type="{http://www.elster.de/2002/XMLSchema}VornameSType" minOccurs="0"/>
 *         &lt;element name="MandantenNr" type="{http://www.elster.de/2002/XMLSchema}MandantenNrSType" minOccurs="0"/>
 *         &lt;element name="Bearbeiterkennzeichen" type="{http://www.elster.de/2002/XMLSchema}BearbeiterkennzeichenSType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MandantCType", propOrder = {
    "name",
    "vorname",
    "mandantenNr",
    "bearbeiterkennzeichen"
})
public class MandantCType {

    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Vorname")
    protected String vorname;
    @XmlElement(name = "MandantenNr")
    protected String mandantenNr;
    @XmlElement(name = "Bearbeiterkennzeichen")
    protected String bearbeiterkennzeichen;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the vorname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVorname() {
        return vorname;
    }

    /**
     * Sets the value of the vorname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVorname(String value) {
        this.vorname = value;
    }

    /**
     * Gets the value of the mandantenNr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMandantenNr() {
        return mandantenNr;
    }

    /**
     * Sets the value of the mandantenNr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMandantenNr(String value) {
        this.mandantenNr = value;
    }

    /**
     * Gets the value of the bearbeiterkennzeichen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBearbeiterkennzeichen() {
        return bearbeiterkennzeichen;
    }

    /**
     * Sets the value of the bearbeiterkennzeichen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBearbeiterkennzeichen(String value) {
        this.bearbeiterkennzeichen = value;
    }

}
