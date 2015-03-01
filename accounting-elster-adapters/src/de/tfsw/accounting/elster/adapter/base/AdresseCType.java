package de.tfsw.accounting.elster.adapter.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AdresseCType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AdresseCType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Bezeichnung" type="{http://www.elster.de/2002/XMLSchema}BezeichnungSType" minOccurs="0"/>
 *         &lt;element name="Name" type="{http://www.elster.de/2002/XMLSchema}NameSType" minOccurs="0"/>
 *         &lt;element name="Vorname" type="{http://www.elster.de/2002/XMLSchema}VornameSType" minOccurs="0"/>
 *         &lt;element name="Namensvorsatz" type="{http://www.elster.de/2002/XMLSchema}NamenszusatzSType" minOccurs="0"/>
 *         &lt;element name="Namenszusatz" type="{http://www.elster.de/2002/XMLSchema}NamenszusatzSType" minOccurs="0"/>
 *         &lt;element name="Str" type="{http://www.elster.de/2002/XMLSchema}StrSType" minOccurs="0"/>
 *         &lt;element name="Hausnummer" type="{http://www.elster.de/2002/XMLSchema}HausnummerSType" minOccurs="0"/>
 *         &lt;element name="HNrZusatz" type="{http://www.elster.de/2002/XMLSchema}HNrZusatzSType" minOccurs="0"/>
 *         &lt;element name="AnschriftenZusatz" type="{http://www.elster.de/2002/XMLSchema}AnschriftenZusatzSType" minOccurs="0"/>
 *         &lt;element name="Ort" type="{http://www.elster.de/2002/XMLSchema}OrtSType" minOccurs="0"/>
 *         &lt;element name="PLZ" type="{http://www.elster.de/2002/XMLSchema}PLZSType" minOccurs="0"/>
 *         &lt;element name="AuslandsPLZ" type="{http://www.elster.de/2002/XMLSchema}AuslandsPLZSType" minOccurs="0"/>
 *         &lt;element name="Land" type="{http://www.elster.de/2002/XMLSchema}LandSType" minOccurs="0"/>
 *         &lt;element name="PostfachOrt" type="{http://www.elster.de/2002/XMLSchema}OrtSType" minOccurs="0"/>
 *         &lt;element name="Postfach" type="{http://www.elster.de/2002/XMLSchema}PostfachSType" minOccurs="0"/>
 *         &lt;element name="PostfachPLZ" type="{http://www.elster.de/2002/XMLSchema}PLZSType" minOccurs="0"/>
 *         &lt;element name="GKPLZ" type="{http://www.elster.de/2002/XMLSchema}PLZSType" minOccurs="0"/>
 *         &lt;element name="Telefon" type="{http://www.elster.de/2002/XMLSchema}TelefonSType" minOccurs="0"/>
 *         &lt;element name="Email" type="{http://www.elster.de/2002/XMLSchema}EmailSType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdresseCType", propOrder = {
    "bezeichnung",
    "name",
    "vorname",
    "namensvorsatz",
    "namenszusatz",
    "str",
    "hausnummer",
    "hNrZusatz",
    "anschriftenZusatz",
    "ort",
    "plz",
    "auslandsPLZ",
    "land",
    "postfachOrt",
    "postfach",
    "postfachPLZ",
    "gkplz",
    "telefon",
    "email"
})
public class AdresseCType {

    @XmlElement(name = "Bezeichnung")
    protected String bezeichnung;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Vorname")
    protected String vorname;
    @XmlElement(name = "Namensvorsatz")
    protected String namensvorsatz;
    @XmlElement(name = "Namenszusatz")
    protected String namenszusatz;
    @XmlElement(name = "Str")
    protected String str;
    @XmlElement(name = "Hausnummer")
    protected String hausnummer;
    @XmlElement(name = "HNrZusatz")
    protected String hNrZusatz;
    @XmlElement(name = "AnschriftenZusatz")
    protected String anschriftenZusatz;
    @XmlElement(name = "Ort")
    protected String ort;
    @XmlElement(name = "PLZ")
    protected String plz;
    @XmlElement(name = "AuslandsPLZ")
    protected String auslandsPLZ;
    @XmlElement(name = "Land")
    protected String land;
    @XmlElement(name = "PostfachOrt")
    protected String postfachOrt;
    @XmlElement(name = "Postfach")
    protected String postfach;
    @XmlElement(name = "PostfachPLZ")
    protected String postfachPLZ;
    @XmlElement(name = "GKPLZ")
    protected String gkplz;
    @XmlElement(name = "Telefon")
    protected String telefon;
    @XmlElement(name = "Email")
    protected String email;

    /**
     * Gets the value of the bezeichnung property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBezeichnung() {
        return bezeichnung;
    }

    /**
     * Sets the value of the bezeichnung property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBezeichnung(String value) {
        this.bezeichnung = value;
    }

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
     * Gets the value of the namensvorsatz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamensvorsatz() {
        return namensvorsatz;
    }

    /**
     * Sets the value of the namensvorsatz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamensvorsatz(String value) {
        this.namensvorsatz = value;
    }

    /**
     * Gets the value of the namenszusatz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamenszusatz() {
        return namenszusatz;
    }

    /**
     * Sets the value of the namenszusatz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamenszusatz(String value) {
        this.namenszusatz = value;
    }

    /**
     * Gets the value of the str property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStr() {
        return str;
    }

    /**
     * Sets the value of the str property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStr(String value) {
        this.str = value;
    }

    /**
     * Gets the value of the hausnummer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHausnummer() {
        return hausnummer;
    }

    /**
     * Sets the value of the hausnummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHausnummer(String value) {
        this.hausnummer = value;
    }

    /**
     * Gets the value of the hNrZusatz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHNrZusatz() {
        return hNrZusatz;
    }

    /**
     * Sets the value of the hNrZusatz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHNrZusatz(String value) {
        this.hNrZusatz = value;
    }

    /**
     * Gets the value of the anschriftenZusatz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnschriftenZusatz() {
        return anschriftenZusatz;
    }

    /**
     * Sets the value of the anschriftenZusatz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnschriftenZusatz(String value) {
        this.anschriftenZusatz = value;
    }

    /**
     * Gets the value of the ort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrt() {
        return ort;
    }

    /**
     * Sets the value of the ort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrt(String value) {
        this.ort = value;
    }

    /**
     * Gets the value of the plz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPLZ() {
        return plz;
    }

    /**
     * Sets the value of the plz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPLZ(String value) {
        this.plz = value;
    }

    /**
     * Gets the value of the auslandsPLZ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuslandsPLZ() {
        return auslandsPLZ;
    }

    /**
     * Sets the value of the auslandsPLZ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuslandsPLZ(String value) {
        this.auslandsPLZ = value;
    }

    /**
     * Gets the value of the land property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLand() {
        return land;
    }

    /**
     * Sets the value of the land property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLand(String value) {
        this.land = value;
    }

    /**
     * Gets the value of the postfachOrt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostfachOrt() {
        return postfachOrt;
    }

    /**
     * Sets the value of the postfachOrt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostfachOrt(String value) {
        this.postfachOrt = value;
    }

    /**
     * Gets the value of the postfach property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostfach() {
        return postfach;
    }

    /**
     * Sets the value of the postfach property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostfach(String value) {
        this.postfach = value;
    }

    /**
     * Gets the value of the postfachPLZ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostfachPLZ() {
        return postfachPLZ;
    }

    /**
     * Sets the value of the postfachPLZ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostfachPLZ(String value) {
        this.postfachPLZ = value;
    }

    /**
     * Gets the value of the gkplz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGKPLZ() {
        return gkplz;
    }

    /**
     * Sets the value of the gkplz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGKPLZ(String value) {
        this.gkplz = value;
    }

    /**
     * Gets the value of the telefon property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefon() {
        return telefon;
    }

    /**
     * Sets the value of the telefon property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefon(String value) {
        this.telefon = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

}
