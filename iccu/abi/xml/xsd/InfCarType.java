//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.20 at 03:22:19 PM CET 
//


package it.sbn.iccu.abi.xml.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Un elemento di questo tipo serve solo a specificare una
 * 				forma cartacea e una forma informatizzata di qualcosa,
 * 				tipicamente un catalogo, ma non solo. Entrambi gli
 * 				elementi che definisce sono semplici siNoType.
 * 			
 * 
 * <p>Java class for infCarType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="infCarType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="informatizzato" type="{}siNoType" minOccurs="0"/>
 *         &lt;element name="cartaceo" type="{}siNoType" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "infCarType", propOrder = {

})
public class InfCarType {

    protected SiNoType informatizzato;
    protected SiNoType cartaceo;

    /**
     * Gets the value of the informatizzato property.
     * 
     * @return
     *     possible object is
     *     {@link SiNoType }
     *     
     */
    public SiNoType getInformatizzato() {
        return informatizzato;
    }

    /**
     * Sets the value of the informatizzato property.
     * 
     * @param value
     *     allowed object is
     *     {@link SiNoType }
     *     
     */
    public void setInformatizzato(SiNoType value) {
        this.informatizzato = value;
    }

    /**
     * Gets the value of the cartaceo property.
     * 
     * @return
     *     possible object is
     *     {@link SiNoType }
     *     
     */
    public SiNoType getCartaceo() {
        return cartaceo;
    }

    /**
     * Sets the value of the cartaceo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SiNoType }
     *     
     */
    public void setCartaceo(SiNoType value) {
        this.cartaceo = value;
    }

}
