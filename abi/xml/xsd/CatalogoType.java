//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.20 at 03:22:19 PM CET 
//


package it.sbn.iccu.abi.xml.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Tutti contengono le quattro forme "schede", "volume",
 * 				"microfilm" e "digitale", ciascuna con la sua
 * 				percentuale di copertura (vedi "formaType"), e tutti
 * 				hanno una copertura temporale. La forma "digitale" ha un
 * 				tipo particolare, "digitaleType", che aggiunge a
 * 				"formaType" il solo sott-elemento "supporto".
 * 			
 * 
 * <p>Java class for catalogoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="catalogoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="forme" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="schede" type="{}formaType" minOccurs="0"/>
 *                   &lt;element name="volume" type="{}volumeType" minOccurs="0"/>
 *                   &lt;element name="microforme" type="{}formaType" minOccurs="0"/>
 *                   &lt;element name="digitale" type="{}digitaleType" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="copertura" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="da-anno">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;pattern value="[0-9]{4}"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="ad-anno">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;pattern value="[0-9]{4}"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/all>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="tipo" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "catalogoType", propOrder = {
    "forme",
    "copertura"
})
public class CatalogoType {

    protected CatalogoType.Forme forme;
    protected CatalogoType.Copertura copertura;
    @XmlAttribute(required = true)
    protected String tipo;

    /**
     * Gets the value of the forme property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogoType.Forme }
     *     
     */
    public CatalogoType.Forme getForme() {
        return forme;
    }

    /**
     * Sets the value of the forme property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogoType.Forme }
     *     
     */
    public void setForme(CatalogoType.Forme value) {
        this.forme = value;
    }

    /**
     * Gets the value of the copertura property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogoType.Copertura }
     *     
     */
    public CatalogoType.Copertura getCopertura() {
        return copertura;
    }

    /**
     * Sets the value of the copertura property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogoType.Copertura }
     *     
     */
    public void setCopertura(CatalogoType.Copertura value) {
        this.copertura = value;
    }

    /**
     * Gets the value of the tipo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Sets the value of the tipo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipo(String value) {
        this.tipo = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;all>
     *         &lt;element name="da-anno">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;pattern value="[0-9]{4}"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="ad-anno">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;pattern value="[0-9]{4}"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *       &lt;/all>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class Copertura {

        @XmlElement(name = "da-anno", required = true)
        protected String daAnno;
        @XmlElement(name = "ad-anno", required = true)
        protected String adAnno;

        /**
         * Gets the value of the daAnno property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDaAnno() {
            return daAnno;
        }

        /**
         * Sets the value of the daAnno property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDaAnno(String value) {
            this.daAnno = value;
        }

        /**
         * Gets the value of the adAnno property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAdAnno() {
            return adAnno;
        }

        /**
         * Sets the value of the adAnno property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAdAnno(String value) {
            this.adAnno = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="schede" type="{}formaType" minOccurs="0"/>
     *         &lt;element name="volume" type="{}volumeType" minOccurs="0"/>
     *         &lt;element name="microforme" type="{}formaType" minOccurs="0"/>
     *         &lt;element name="digitale" type="{}digitaleType" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "schede",
        "volume",
        "microforme",
        "digitale"
    })
    public static class Forme {

        protected FormaType schede;
        protected VolumeType volume;
        protected FormaType microforme;
        protected DigitaleType digitale;

        /**
         * Gets the value of the schede property.
         * 
         * @return
         *     possible object is
         *     {@link FormaType }
         *     
         */
        public FormaType getSchede() {
            return schede;
        }

        /**
         * Sets the value of the schede property.
         * 
         * @param value
         *     allowed object is
         *     {@link FormaType }
         *     
         */
        public void setSchede(FormaType value) {
            this.schede = value;
        }

        /**
         * Gets the value of the volume property.
         * 
         * @return
         *     possible object is
         *     {@link VolumeType }
         *     
         */
        public VolumeType getVolume() {
            return volume;
        }

        /**
         * Sets the value of the volume property.
         * 
         * @param value
         *     allowed object is
         *     {@link VolumeType }
         *     
         */
        public void setVolume(VolumeType value) {
            this.volume = value;
        }

        /**
         * Gets the value of the microforme property.
         * 
         * @return
         *     possible object is
         *     {@link FormaType }
         *     
         */
        public FormaType getMicroforme() {
            return microforme;
        }

        /**
         * Sets the value of the microforme property.
         * 
         * @param value
         *     allowed object is
         *     {@link FormaType }
         *     
         */
        public void setMicroforme(FormaType value) {
            this.microforme = value;
        }

        /**
         * Gets the value of the digitale property.
         * 
         * @return
         *     possible object is
         *     {@link DigitaleType }
         *     
         */
        public DigitaleType getDigitale() {
            return digitale;
        }

        /**
         * Sets the value of the digitale property.
         * 
         * @param value
         *     allowed object is
         *     {@link DigitaleType }
         *     
         */
        public void setDigitale(DigitaleType value) {
            this.digitale = value;
        }

    }

}
