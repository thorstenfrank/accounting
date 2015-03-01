package de.tfsw.accounting.elster.adapter.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for usta_SteuerfallCType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="usta_SteuerfallCType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Berater" type="{http://www.elster.de/2002/XMLSchema}AdresseCType" minOccurs="0"/>
 *         &lt;element name="Mandant" type="{http://www.elster.de/2002/XMLSchema}MandantCType" minOccurs="0"/>
 *         &lt;element name="Unternehmer" type="{http://www.elster.de/2002/XMLSchema}AdresseCType" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="Umsatzsteuervoranmeldung" type="{http://www.elster.de/2002/XMLSchema}usta_UmsatzsteuervoranmeldungCType"/>
 *           &lt;element name="Dauerfristverlaengerung" type="{http://www.elster.de/2002/XMLSchema}usta_DauerfristverlaengerungCType"/>
 *           &lt;element name="Umsatzsteuersondervorauszahlung" type="{http://www.elster.de/2002/XMLSchema}usta_UmsatzsteuersondervorauszahlungCType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "usta_SteuerfallCType", propOrder = {
    "berater",
    "mandant",
    "unternehmer",
    "umsatzsteuervoranmeldung",
    "dauerfristverlaengerung",
    "umsatzsteuersondervorauszahlung"
})
public class UstaSteuerfallCType {

    @XmlElement(name = "Berater")
    protected AdresseCType berater;
    @XmlElement(name = "Mandant")
    protected MandantCType mandant;
    @XmlElement(name = "Unternehmer")
    protected AdresseCType unternehmer;
    @XmlElement(name = "Umsatzsteuervoranmeldung")
    protected UstaUmsatzsteuervoranmeldungCType umsatzsteuervoranmeldung;
    @XmlElement(name = "Dauerfristverlaengerung")
    protected UstaDauerfristverlaengerungCType dauerfristverlaengerung;
    @XmlElement(name = "Umsatzsteuersondervorauszahlung")
    protected UstaUmsatzsteuersondervorauszahlungCType umsatzsteuersondervorauszahlung;

    /**
     * Gets the value of the berater property.
     * 
     * @return
     *     possible object is
     *     {@link AdresseCType }
     *     
     */
    public AdresseCType getBerater() {
        return berater;
    }

    /**
     * Sets the value of the berater property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdresseCType }
     *     
     */
    public void setBerater(AdresseCType value) {
        this.berater = value;
    }

    /**
     * Gets the value of the mandant property.
     * 
     * @return
     *     possible object is
     *     {@link MandantCType }
     *     
     */
    public MandantCType getMandant() {
        return mandant;
    }

    /**
     * Sets the value of the mandant property.
     * 
     * @param value
     *     allowed object is
     *     {@link MandantCType }
     *     
     */
    public void setMandant(MandantCType value) {
        this.mandant = value;
    }

    /**
     * Gets the value of the unternehmer property.
     * 
     * @return
     *     possible object is
     *     {@link AdresseCType }
     *     
     */
    public AdresseCType getUnternehmer() {
        return unternehmer;
    }

    /**
     * Sets the value of the unternehmer property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdresseCType }
     *     
     */
    public void setUnternehmer(AdresseCType value) {
        this.unternehmer = value;
    }

    /**
     * Gets the value of the umsatzsteuervoranmeldung property.
     * 
     * @return
     *     possible object is
     *     {@link UstaUmsatzsteuervoranmeldungCType }
     *     
     */
    public UstaUmsatzsteuervoranmeldungCType getUmsatzsteuervoranmeldung() {
        return umsatzsteuervoranmeldung;
    }

    /**
     * Sets the value of the umsatzsteuervoranmeldung property.
     * 
     * @param value
     *     allowed object is
     *     {@link UstaUmsatzsteuervoranmeldungCType }
     *     
     */
    public void setUmsatzsteuervoranmeldung(UstaUmsatzsteuervoranmeldungCType value) {
        this.umsatzsteuervoranmeldung = value;
    }

    /**
     * Gets the value of the dauerfristverlaengerung property.
     * 
     * @return
     *     possible object is
     *     {@link UstaDauerfristverlaengerungCType }
     *     
     */
    public UstaDauerfristverlaengerungCType getDauerfristverlaengerung() {
        return dauerfristverlaengerung;
    }

    /**
     * Sets the value of the dauerfristverlaengerung property.
     * 
     * @param value
     *     allowed object is
     *     {@link UstaDauerfristverlaengerungCType }
     *     
     */
    public void setDauerfristverlaengerung(UstaDauerfristverlaengerungCType value) {
        this.dauerfristverlaengerung = value;
    }

    /**
     * Gets the value of the umsatzsteuersondervorauszahlung property.
     * 
     * @return
     *     possible object is
     *     {@link UstaUmsatzsteuersondervorauszahlungCType }
     *     
     */
    public UstaUmsatzsteuersondervorauszahlungCType getUmsatzsteuersondervorauszahlung() {
        return umsatzsteuersondervorauszahlung;
    }

    /**
     * Sets the value of the umsatzsteuersondervorauszahlung property.
     * 
     * @param value
     *     allowed object is
     *     {@link UstaUmsatzsteuersondervorauszahlungCType }
     *     
     */
    public void setUmsatzsteuersondervorauszahlung(UstaUmsatzsteuersondervorauszahlungCType value) {
        this.umsatzsteuersondervorauszahlung = value;
    }

}
