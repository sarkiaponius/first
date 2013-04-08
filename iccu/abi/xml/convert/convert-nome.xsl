<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- Foglio di stile per la conversione di file 1.5 a file 1.6 -->

  <xsl:output method="xml" indent="yes" encoding="ISO-8859-1"/>
  <xsl:template match="/biblioteche">
    <xsl:element name="biblioteche">
      <xsl:copy-of select="data-export"/>
      <xsl:apply-templates select="//biblioteca"/>
    </xsl:element>
  </xsl:template>

  <!-- Richiama i vari template, oppure copia gli elementi -->

  <xsl:template match="//biblioteca">
    <xsl:element name="biblioteca">
      <xsl:apply-templates select="anagrafica"/>
      <xsl:copy-of select="cataloghi"/>
      <xsl:copy-of select="patrimonio"/>
      <xsl:copy-of select="specializzazione"/>
      <xsl:copy-of select="servizi"/>
      <xsl:copy-of select="amministrativa"/>
    </xsl:element>
  </xsl:template>

  <!-- Anagrafica, sono diversi template -->

  <xsl:template match="//anagrafica">
    <xsl:element name="anagrafica">
      <xsl:copy-of select="data-censimento"/>
      <xsl:copy-of select="data-aggiornamento"/>
      <xsl:apply-templates select="nome"/>
      <xsl:copy-of select="codici"/>
      <xsl:copy-of select="indirizzo"/>
      <xsl:copy-of select="contatti"/>
      <xsl:copy-of select="edificio"/>
      <xsl:copy-of select="istituzione"/>
    </xsl:element>
  </xsl:template>

  <!-- Contenitori per i nomi -->

  <xsl:template match="//nome">
    <xsl:element name="nomi">
      <xsl:copy-of select="attuale"/>
      <xsl:if test="precedente">
        <xsl:element name="precedenti">
          <xsl:for-each select="precedente">
            <xsl:copy-of select="."/>
          </xsl:for-each>
        </xsl:element>
      </xsl:if>
      <xsl:if test="alternative">
        <xsl:element name="alternativi">
          <xsl:for-each select="alternative/alternativo">
            <xsl:copy-of select="."/>
          </xsl:for-each>
        </xsl:element>
      </xsl:if>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>
