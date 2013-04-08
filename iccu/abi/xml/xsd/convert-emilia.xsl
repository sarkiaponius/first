<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!--
	Foglio di stile per la correzione dei file inviati dalla 
	Regione Emilia-Romagna. Molti elementi sono al posto sbagliato, 
	oppure non sono rispettate le cardinalità.
-->

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/biblioteche"> 
		<xsl:element name="biblioteche">
  	  <xsl:copy-of select="dataExport"/>
		  <xsl:apply-templates select="//biblioteca"/>
		</xsl:element>
  </xsl:template>

<!-- 
	Cataloghi e servizi sono le uniche, ma importanti, fonti di guai.
-->
  <xsl:template match="//biblioteca"> 
		<xsl:element name="biblioteca">
  		<xsl:apply-templates select="anagrafica"/>
  		<xsl:apply-templates select="cataloghi"/>
  		<xsl:copy-of select="patrimonio"/>
  		<xsl:copy-of select="specializzazione"/>
  		<xsl:apply-templates select="servizi"/>
  		<xsl:copy-of select="amministrativa"/>
		</xsl:element>
  </xsl:template>

  <xsl:template match="//anagrafica"> 
		<xsl:element name="anagrafica">
		  <xsl:copy-of select="dataCensimento"/>
		  <xsl:copy-of select="dataAggiornamento"/>
		  <xsl:copy-of select="nome"/>
		  <xsl:apply-templates select="codici"/>
		  <xsl:copy-of select="indirizzo"/>
		  <xsl:copy-of select="contatti"/>
		  <xsl:copy-of select="edificio"/>
		  <xsl:apply-templates select="istituzione"/>
		  <xsl:apply-templates select="Istituzione"/>
		</xsl:element>
  </xsl:template>

<!--
	I primi due dei prossimi template correggono il famoso errore 
  "Istituzione". Il successivo corregge i codici ISIL, ma solo
  se mancanti dell'IT- e non lunghi 6. Non è un gran test.
-->
  <xsl:template match="//istituzione"> 
		<xsl:element name="Istituzione">
		<xsl:value-of select="."/>
		</xsl:element>
  </xsl:template>

  <xsl:template match="//Istituzione"> 
		<xsl:copy-of select="."/>
  </xsl:template>

  <xsl:template match="//codici"> 
		<xsl:element name="codici">
      <xsl:if test="not(string-length(iccu) = 6)">
        <xsl:comment> *** CODICE ISIL ERRATO *** </xsl:comment>
      </xsl:if>
		  <xsl:element name="iccu">
        <xsl:if test="not(starts-with(iccu, 'IT-'))">
          <xsl:value-of select="concat('IT-', iccu)"/>
        </xsl:if>
        <xsl:if test="starts-with(iccu, 'IT-')">
          <xsl:value-of select="iccu"/>
        </xsl:if>
		  </xsl:element>
		  <xsl:copy-of select="acnp"/>
		  <xsl:copy-of select="rism"/>
		  <xsl:copy-of select="sbn"/>
		  <xsl:copy-of select="cei"/>
		  <xsl:copy-of select="cmbs"/>
		</xsl:element>
  </xsl:template>

  <xsl:template match="//cataloghi"> 
		<xsl:element name="cataloghi">
		  <xsl:copy-of select="catalogo-generale"/>
		  <xsl:apply-templates select="catalogo-speciale"/>
		  <xsl:apply-templates select="catalogo-collettivo"/>
		</xsl:element>
  </xsl:template>

<!--
	Nei cataloghi speciali e collettivi i materiali sono spesso
	ripetuti. Questi template conservano solo il primo elemento.
-->
  <xsl:template match="//catalogo-speciale"> 
		<xsl:element name="catalogo-speciale">
		<xsl:copy-of select="forme"/>
		<xsl:copy-of select="copertura"/>
		<xsl:copy-of select="nome"/>
		<xsl:for-each select="materiale">
		  <xsl:if test="position()=1">
			  <xsl:copy-of select="."/>
		  </xsl:if>
		</xsl:for-each>
		</xsl:element>
  </xsl:template>

  <xsl:template match="//catalogo-collettivo"> 
		<xsl:element name="catalogo-collettivo">
		<xsl:copy-of select="forme"/>
		<xsl:copy-of select="copertura"/>
		<xsl:copy-of select="nome"/>
		<xsl:for-each select="materiale">
		  <xsl:if test="position()=1">
			  <xsl:copy-of select="."/>
		  </xsl:if>
		</xsl:for-each>
		<xsl:copy-of select="zona"/>
		</xsl:element>
  </xsl:template>

<!--
	Prestito e accesso sono due campi di battaglia. Già l'ordine 
	degli elementi va rimesso a posto.
-->
  <xsl:template match="//servizi"> 
		<xsl:element name="servizi">
		<xsl:copy-of select="orario"/>
		<xsl:apply-templates select="prestito"/>
		<xsl:copy-of select="informazioni-bibliografiche"/>
		<xsl:copy-of select="internet"/>
		<xsl:apply-templates select="accesso"/>
		<xsl:copy-of select="sistemi"/>
		</xsl:element>
  </xsl:template>

<!--
	Le condizioni di accesso sono un problema per l'ordine degli
	elementi e per l'elemento "eta", che arriva con due elementi
	"min" e "max" invece che con due omonimi attributi.
-->
  <xsl:template match="//accesso"> 
		<xsl:element name="accesso">
		<xsl:copy-of select="aperta"/>
		<xsl:copy-of select="handicap"/>
		<xsl:copy-of select="categoria-ammessa"/>
		<xsl:apply-templates select="condizioni-accesso"/>
		<xsl:copy-of select="destinazioni-sociali"/>
		</xsl:element>
  </xsl:template>

  <xsl:template match="//condizioni-accesso"> 
		<xsl:element name="condizioni-accesso">
		<xsl:apply-templates select="eta"/>
		<xsl:copy-of select="documenti"/>
		<xsl:copy-of select="appuntamento"/>
		</xsl:element>
  </xsl:template>

  <xsl:template match="//eta"> 
		<xsl:element name="eta">
		  <xsl:apply-templates select="min"/>
		  <xsl:apply-templates select="max"/>
		</xsl:element>
  </xsl:template>

  <xsl:template match="//min"> 
		<xsl:attribute name="min">
		  <xsl:value-of select="."/>
		</xsl:attribute>
  </xsl:template>

  <xsl:template match="//max"> 
		<xsl:attribute name="max">
		  <xsl:value-of select="."/>
		</xsl:attribute>
  </xsl:template>

<!--
  Come in altri casi, anche qui è un problema già l'ordine, anche
	ai livelli inferiori.
-->
  <xsl:template match="//prestito"> 
		<xsl:element name="prestito">
		<xsl:apply-templates select="locale"/>
		<xsl:apply-templates select="interbibliotecario"/>
		<xsl:copy-of select="materiali-esclusi-locale"/>
		<xsl:copy-of select="riproduzioni"/>
		</xsl:element>
  </xsl:template>

<!--
  Gli utenti ammessi, opzionali, non sono ripetibili: si conserva
	solo il primo elemento trovato.
-->
  <xsl:template match="//locale"> 
		<xsl:element name="locale">
		<xsl:copy-of select="automatizzato"/>
		<xsl:copy-of select="materiale-escluso"/>
		<xsl:copy-of select="durata"/>
		<xsl:for-each select="utenti-ammessi">
		  <xsl:if test="position()=1">
			  <xsl:copy-of select="."/>
		  </xsl:if>
		</xsl:for-each>
		<xsl:copy-of select="totale-prestiti"/>
		</xsl:element>
  </xsl:template>

  <xsl:template match="//interbibliotecario"> 
		<xsl:element name="interbibliotecario">
		<xsl:apply-templates select="tipo-prestito"/>
		<xsl:copy-of select="automatizzato"/>
		<xsl:copy-of select="totale-prestiti"/>
		<xsl:copy-of select="sistema-ill"/>
		</xsl:element>
  </xsl:template>

<!--
  Il ruolo di una biblioteca è obbligatorio e non ripetibile. Questo
	rende necessario non solo scartare occorrenze oltre la prima, ma
	anche assicurarsi che ce ne sia almeno una, che invece a volte manca.
	In questi casi ne viene creata una di tipo DSC, tanto per dire.
-->
  <xsl:template match="//tipo-prestito"> 
		<xsl:element name="tipo-prestito">
		<xsl:copy-of select="internazionale"/>
		<xsl:copy-of select="nazionale"/>
    <xsl:if test="ruolo">
		  <xsl:for-each select="ruolo">
		    <xsl:if test="position()=1">
			    <xsl:copy-of select="."/>
		    </xsl:if>
		  </xsl:for-each>
		</xsl:if>
    <xsl:if test="not(ruolo)">
		  <xsl:element name="ruolo">DSC</xsl:element>
		</xsl:if>
		</xsl:element>
  </xsl:template>
</xsl:stylesheet>
