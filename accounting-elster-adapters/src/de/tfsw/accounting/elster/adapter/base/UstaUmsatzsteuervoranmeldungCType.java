package de.tfsw.accounting.elster.adapter.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for usta_UmsatzsteuervoranmeldungCType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="usta_UmsatzsteuervoranmeldungCType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Jahr" type="{http://www.elster.de/2002/XMLSchema}usta_JahrSType"/>
 *         &lt;element name="Zeitraum" type="{http://www.elster.de/2002/XMLSchema}usta_ZeitraumSType"/>
 *         &lt;element name="Steuernummer" type="{http://www.elster.de/2002/XMLSchema}allg_StNrSType"/>
 *         &lt;element name="Kz09" type="{http://www.elster.de/2002/XMLSchema}usta_Kz09SType"/>
 *         &lt;element name="Kz10" type="{http://www.elster.de/2002/XMLSchema}usta_Kz10SType" minOccurs="0"/>
 *         &lt;element name="Kz21" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz22" type="{http://www.elster.de/2002/XMLSchema}usta_Kz22SType" minOccurs="0"/>
 *         &lt;element name="Kz26" type="{http://www.elster.de/2002/XMLSchema}usta_Kz26SType" minOccurs="0"/>
 *         &lt;element name="Kz29" type="{http://www.elster.de/2002/XMLSchema}usta_Kz29SType" minOccurs="0"/>
 *         &lt;element name="Kz35" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz36" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz39" type="{http://www.elster.de/2002/XMLSchema}usta_PositiveKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz41" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz42" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz43" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz44" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz45" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz46" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz47" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz48" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz49" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz52" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz53" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz59" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz60" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz61" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz62" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz63" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz64" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz65" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz66" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz67" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz68" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz69" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz73" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz74" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz76" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz77" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz78" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz79" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz80" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz81" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz83" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent"/>
 *         &lt;element name="Kz84" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz85" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz86" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz89" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz91" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz93" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz94" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz95" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSType" minOccurs="0"/>
 *         &lt;element name="Kz96" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *         &lt;element name="Kz98" type="{http://www.elster.de/2002/XMLSchema}usta_DefaultKZSTypewithCent" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "usta_UmsatzsteuervoranmeldungCType", propOrder = {

})
public class UstaUmsatzsteuervoranmeldungCType {

    @XmlElement(name = "Jahr", required = true)
    protected String jahr;
    @XmlElement(name = "Zeitraum", required = true)
    protected String zeitraum;
    @XmlElement(name = "Steuernummer", required = true)
    protected String steuernummer;
    @XmlElement(name = "Kz09", required = true)
    protected String kz09;
    @XmlElement(name = "Kz10")
    protected String kz10;
    @XmlElement(name = "Kz21")
    protected String kz21;
    @XmlElement(name = "Kz22")
    protected String kz22;
    @XmlElement(name = "Kz26")
    protected String kz26;
    @XmlElement(name = "Kz29")
    protected String kz29;
    @XmlElement(name = "Kz35")
    protected String kz35;
    @XmlElement(name = "Kz36")
    protected String kz36;
    @XmlElement(name = "Kz39")
    protected String kz39;
    @XmlElement(name = "Kz41")
    protected String kz41;
    @XmlElement(name = "Kz42")
    protected String kz42;
    @XmlElement(name = "Kz43")
    protected String kz43;
    @XmlElement(name = "Kz44")
    protected String kz44;
    @XmlElement(name = "Kz45")
    protected String kz45;
    @XmlElement(name = "Kz46")
    protected String kz46;
    @XmlElement(name = "Kz47")
    protected String kz47;
    @XmlElement(name = "Kz48")
    protected String kz48;
    @XmlElement(name = "Kz49")
    protected String kz49;
    @XmlElement(name = "Kz52")
    protected String kz52;
    @XmlElement(name = "Kz53")
    protected String kz53;
    @XmlElement(name = "Kz59")
    protected String kz59;
    @XmlElement(name = "Kz60")
    protected String kz60;
    @XmlElement(name = "Kz61")
    protected String kz61;
    @XmlElement(name = "Kz62")
    protected String kz62;
    @XmlElement(name = "Kz63")
    protected String kz63;
    @XmlElement(name = "Kz64")
    protected String kz64;
    @XmlElement(name = "Kz65")
    protected String kz65;
    @XmlElement(name = "Kz66")
    protected String kz66;
    @XmlElement(name = "Kz67")
    protected String kz67;
    @XmlElement(name = "Kz68")
    protected String kz68;
    @XmlElement(name = "Kz69")
    protected String kz69;
    @XmlElement(name = "Kz73")
    protected String kz73;
    @XmlElement(name = "Kz74")
    protected String kz74;
    @XmlElement(name = "Kz76")
    protected String kz76;
    @XmlElement(name = "Kz77")
    protected String kz77;
    @XmlElement(name = "Kz78")
    protected String kz78;
    @XmlElement(name = "Kz79")
    protected String kz79;
    @XmlElement(name = "Kz80")
    protected String kz80;
    @XmlElement(name = "Kz81")
    protected String kz81;
    @XmlElement(name = "Kz83", required = true)
    protected String kz83;
    @XmlElement(name = "Kz84")
    protected String kz84;
    @XmlElement(name = "Kz85")
    protected String kz85;
    @XmlElement(name = "Kz86")
    protected String kz86;
    @XmlElement(name = "Kz89")
    protected String kz89;
    @XmlElement(name = "Kz91")
    protected String kz91;
    @XmlElement(name = "Kz93")
    protected String kz93;
    @XmlElement(name = "Kz94")
    protected String kz94;
    @XmlElement(name = "Kz95")
    protected String kz95;
    @XmlElement(name = "Kz96")
    protected String kz96;
    @XmlElement(name = "Kz98")
    protected String kz98;

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
     * Gets the value of the zeitraum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZeitraum() {
        return zeitraum;
    }

    /**
     * Sets the value of the zeitraum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZeitraum(String value) {
        this.zeitraum = value;
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
     * Gets the value of the kz21 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz21() {
        return kz21;
    }

    /**
     * Sets the value of the kz21 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz21(String value) {
        this.kz21 = value;
    }

    /**
     * Gets the value of the kz22 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz22() {
        return kz22;
    }

    /**
     * Sets the value of the kz22 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz22(String value) {
        this.kz22 = value;
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
     * Gets the value of the kz35 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz35() {
        return kz35;
    }

    /**
     * Sets the value of the kz35 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz35(String value) {
        this.kz35 = value;
    }

    /**
     * Gets the value of the kz36 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz36() {
        return kz36;
    }

    /**
     * Sets the value of the kz36 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz36(String value) {
        this.kz36 = value;
    }

    /**
     * Gets the value of the kz39 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz39() {
        return kz39;
    }

    /**
     * Sets the value of the kz39 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz39(String value) {
        this.kz39 = value;
    }

    /**
     * Gets the value of the kz41 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz41() {
        return kz41;
    }

    /**
     * Sets the value of the kz41 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz41(String value) {
        this.kz41 = value;
    }

    /**
     * Gets the value of the kz42 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz42() {
        return kz42;
    }

    /**
     * Sets the value of the kz42 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz42(String value) {
        this.kz42 = value;
    }

    /**
     * Gets the value of the kz43 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz43() {
        return kz43;
    }

    /**
     * Sets the value of the kz43 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz43(String value) {
        this.kz43 = value;
    }

    /**
     * Gets the value of the kz44 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz44() {
        return kz44;
    }

    /**
     * Sets the value of the kz44 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz44(String value) {
        this.kz44 = value;
    }

    /**
     * Gets the value of the kz45 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz45() {
        return kz45;
    }

    /**
     * Sets the value of the kz45 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz45(String value) {
        this.kz45 = value;
    }

    /**
     * Gets the value of the kz46 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz46() {
        return kz46;
    }

    /**
     * Sets the value of the kz46 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz46(String value) {
        this.kz46 = value;
    }

    /**
     * Gets the value of the kz47 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz47() {
        return kz47;
    }

    /**
     * Sets the value of the kz47 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz47(String value) {
        this.kz47 = value;
    }

    /**
     * Gets the value of the kz48 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz48() {
        return kz48;
    }

    /**
     * Sets the value of the kz48 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz48(String value) {
        this.kz48 = value;
    }

    /**
     * Gets the value of the kz49 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz49() {
        return kz49;
    }

    /**
     * Sets the value of the kz49 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz49(String value) {
        this.kz49 = value;
    }

    /**
     * Gets the value of the kz52 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz52() {
        return kz52;
    }

    /**
     * Sets the value of the kz52 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz52(String value) {
        this.kz52 = value;
    }

    /**
     * Gets the value of the kz53 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz53() {
        return kz53;
    }

    /**
     * Sets the value of the kz53 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz53(String value) {
        this.kz53 = value;
    }

    /**
     * Gets the value of the kz59 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz59() {
        return kz59;
    }

    /**
     * Sets the value of the kz59 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz59(String value) {
        this.kz59 = value;
    }

    /**
     * Gets the value of the kz60 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz60() {
        return kz60;
    }

    /**
     * Sets the value of the kz60 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz60(String value) {
        this.kz60 = value;
    }

    /**
     * Gets the value of the kz61 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz61() {
        return kz61;
    }

    /**
     * Sets the value of the kz61 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz61(String value) {
        this.kz61 = value;
    }

    /**
     * Gets the value of the kz62 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz62() {
        return kz62;
    }

    /**
     * Sets the value of the kz62 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz62(String value) {
        this.kz62 = value;
    }

    /**
     * Gets the value of the kz63 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz63() {
        return kz63;
    }

    /**
     * Sets the value of the kz63 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz63(String value) {
        this.kz63 = value;
    }

    /**
     * Gets the value of the kz64 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz64() {
        return kz64;
    }

    /**
     * Sets the value of the kz64 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz64(String value) {
        this.kz64 = value;
    }

    /**
     * Gets the value of the kz65 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz65() {
        return kz65;
    }

    /**
     * Sets the value of the kz65 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz65(String value) {
        this.kz65 = value;
    }

    /**
     * Gets the value of the kz66 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz66() {
        return kz66;
    }

    /**
     * Sets the value of the kz66 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz66(String value) {
        this.kz66 = value;
    }

    /**
     * Gets the value of the kz67 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz67() {
        return kz67;
    }

    /**
     * Sets the value of the kz67 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz67(String value) {
        this.kz67 = value;
    }

    /**
     * Gets the value of the kz68 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz68() {
        return kz68;
    }

    /**
     * Sets the value of the kz68 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz68(String value) {
        this.kz68 = value;
    }

    /**
     * Gets the value of the kz69 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz69() {
        return kz69;
    }

    /**
     * Sets the value of the kz69 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz69(String value) {
        this.kz69 = value;
    }

    /**
     * Gets the value of the kz73 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz73() {
        return kz73;
    }

    /**
     * Sets the value of the kz73 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz73(String value) {
        this.kz73 = value;
    }

    /**
     * Gets the value of the kz74 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz74() {
        return kz74;
    }

    /**
     * Sets the value of the kz74 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz74(String value) {
        this.kz74 = value;
    }

    /**
     * Gets the value of the kz76 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz76() {
        return kz76;
    }

    /**
     * Sets the value of the kz76 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz76(String value) {
        this.kz76 = value;
    }

    /**
     * Gets the value of the kz77 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz77() {
        return kz77;
    }

    /**
     * Sets the value of the kz77 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz77(String value) {
        this.kz77 = value;
    }

    /**
     * Gets the value of the kz78 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz78() {
        return kz78;
    }

    /**
     * Sets the value of the kz78 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz78(String value) {
        this.kz78 = value;
    }

    /**
     * Gets the value of the kz79 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz79() {
        return kz79;
    }

    /**
     * Sets the value of the kz79 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz79(String value) {
        this.kz79 = value;
    }

    /**
     * Gets the value of the kz80 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz80() {
        return kz80;
    }

    /**
     * Sets the value of the kz80 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz80(String value) {
        this.kz80 = value;
    }

    /**
     * Gets the value of the kz81 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz81() {
        return kz81;
    }

    /**
     * Sets the value of the kz81 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz81(String value) {
        this.kz81 = value;
    }

    /**
     * Gets the value of the kz83 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz83() {
        return kz83;
    }

    /**
     * Sets the value of the kz83 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz83(String value) {
        this.kz83 = value;
    }

    /**
     * Gets the value of the kz84 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz84() {
        return kz84;
    }

    /**
     * Sets the value of the kz84 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz84(String value) {
        this.kz84 = value;
    }

    /**
     * Gets the value of the kz85 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz85() {
        return kz85;
    }

    /**
     * Sets the value of the kz85 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz85(String value) {
        this.kz85 = value;
    }

    /**
     * Gets the value of the kz86 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz86() {
        return kz86;
    }

    /**
     * Sets the value of the kz86 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz86(String value) {
        this.kz86 = value;
    }

    /**
     * Gets the value of the kz89 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz89() {
        return kz89;
    }

    /**
     * Sets the value of the kz89 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz89(String value) {
        this.kz89 = value;
    }

    /**
     * Gets the value of the kz91 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz91() {
        return kz91;
    }

    /**
     * Sets the value of the kz91 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz91(String value) {
        this.kz91 = value;
    }

    /**
     * Gets the value of the kz93 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz93() {
        return kz93;
    }

    /**
     * Sets the value of the kz93 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz93(String value) {
        this.kz93 = value;
    }

    /**
     * Gets the value of the kz94 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz94() {
        return kz94;
    }

    /**
     * Sets the value of the kz94 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz94(String value) {
        this.kz94 = value;
    }

    /**
     * Gets the value of the kz95 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz95() {
        return kz95;
    }

    /**
     * Sets the value of the kz95 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz95(String value) {
        this.kz95 = value;
    }

    /**
     * Gets the value of the kz96 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz96() {
        return kz96;
    }

    /**
     * Sets the value of the kz96 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz96(String value) {
        this.kz96 = value;
    }

    /**
     * Gets the value of the kz98 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKz98() {
        return kz98;
    }

    /**
     * Sets the value of the kz98 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKz98(String value) {
        this.kz98 = value;
    }

}
