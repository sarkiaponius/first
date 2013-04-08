<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- Foglio di stile per la conversione di file 1.5 a file 1.6 -->

  <xsl:output method="xml" indent="yes" encoding="ISO-8859-1"/>
  <xsl:template match="/biblioteche">
    <xsl:element name="biblioteche">
      <xsl:if test="dataExport">
        <xsl:element name="data-export">
          <xsl:value-of select="dataExport"/>
        </xsl:element>
      </xsl:if>
      <xsl:if test="not(dataExport)">
        <xsl:element name="data-export">2000-12-31T00:00:00.000+01:00</xsl:element>
      </xsl:if>
      <xsl:apply-templates select="//biblioteca"/>
    </xsl:element>
  </xsl:template>

  <!-- Richiama i vari template, oppure copia gli elementi -->

  <xsl:template match="//biblioteca">
    <xsl:element name="biblioteca">
      <xsl:apply-templates select="anagrafica"/>
      <xsl:apply-templates select="cataloghi"/>
      <xsl:apply-templates select="patrimonio"/>
      <xsl:if test="specializzazione">
        <xsl:element name="specializzazioni">
          <xsl:for-each select="specializzazione">
            <xsl:copy-of select="."/>
          </xsl:for-each>
        </xsl:element>
      </xsl:if>
      <xsl:apply-templates select="servizi"/>
      <xsl:apply-templates select="amministrativa"/>
    </xsl:element>
  </xsl:template>

  <!-- Anagrafica, sono diversi template -->

  <xsl:template match="//anagrafica">
    <xsl:element name="anagrafica">
      <xsl:if test="dataCensimento">
        <xsl:element name="data-censimento">
          <xsl:value-of select="dataCensimento"/>
        </xsl:element>
      </xsl:if>
      <xsl:if test="dataAggiornamento">
        <xsl:element name="data-aggiornamento">
          <xsl:value-of select="dataAggiornamento"/>
        </xsl:element>
      </xsl:if>
      <xsl:if test="not(dataAggiornamento)">
        <xsl:element name="data-aggiornamento">2000-12-31T00:00:00.000+01:00</xsl:element>
      </xsl:if>
      <xsl:apply-templates select="nome"/>
      <xsl:apply-templates select="codici"/>
      <xsl:copy-of select="indirizzo"/>
      <xsl:apply-templates select="contatti"/>
      <!--<xsl:copy-of select="contatti"/>-->
      <xsl:apply-templates select="edificio"/>
      <xsl:apply-templates select="Istituzione"/>
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
      <xsl:if test="alternativo">
        <xsl:element name="alternativi">
          <xsl:for-each select="alternativo">
            <xsl:copy-of select="."/>
          </xsl:for-each>
        </xsl:element>
      </xsl:if>
    </xsl:element>
  </xsl:template>

  <!-- Contenitori per i contatti -->

  <xsl:template match="//contatti">
    <xsl:element name="contatti">
      <xsl:if test="telefonico">
        <xsl:element name="telefonici">
          <xsl:for-each select="telefonico">
            <xsl:copy-of select="."/>
          </xsl:for-each>
        </xsl:element>
      </xsl:if>
      <xsl:if test="altro">
        <xsl:element name="altri">
          <xsl:for-each select="altro">
            <xsl:copy-of select="."/>
          </xsl:for-each>
        </xsl:element>
      </xsl:if>
    </xsl:element>
  </xsl:template>

  <!-- Converte nomi in edificio -->

  <xsl:template match="//edificio">
    <xsl:element name="edificio">
      <xsl:copy-of select="denominazione"/>
      <xsl:copy-of select="monumentale"/>
      <xsl:if test="appositamenteCostruito">
        <xsl:element name="appositamente-costruito">
          <xsl:value-of select="appositamenteCostruito"/>
        </xsl:element>
      </xsl:if>

<!--
Viene gestita la data di costruzione, copiando anche l'attributo "tipo" per
distinguere anni e secoli. Dev'esserci un modo più semplice di farlo
-->
      <xsl:if test="dataCostruzione">
        <xsl:element name="data-costruzione">
					<xsl:if test="dataCostruzione/@tipo">
						<xsl:attribute name="tipo">
							<xsl:value-of select="dataCostruzione/@tipo"/>
						</xsl:attribute>
      		</xsl:if>
          <xsl:value-of select="dataCostruzione"/>
        </xsl:element>
      </xsl:if>
    </xsl:element>
  </xsl:template>

<!-- 
Converte nomi in Istituzone. Gestisce anche gli attributi "tipo" delle date,
per distinguere anni e secoli
 -->

  <xsl:template match="//Istituzione">
    <xsl:element name="istituzione">
      <xsl:if test="dataIstituzione">
        <xsl:element name="data-istituzione">
					<xsl:if test="dataIstituzione/@tipo">
						<xsl:attribute name="tipo">
							<xsl:value-of select="dataIstituzione/@tipo"/>
						</xsl:attribute>
      		</xsl:if>
          <xsl:value-of select="dataIstituzione"/>
        </xsl:element>
      </xsl:if>
      <xsl:if test="dataFondazione">
        <xsl:element name="data-fondazione">
					<xsl:if test="dataFondazione/@tipo">
						<xsl:attribute name="tipo">
							<xsl:value-of select="dataFondazione/@tipo"/>
						</xsl:attribute>
      		</xsl:if>
          <xsl:value-of select="dataFondazione"/>
        </xsl:element>
      </xsl:if>
    </xsl:element>
  </xsl:template>

	<!-- 
	Cataloghi, basta un solo template, ma è complicato il caso dei materiali
	nei cataloghi speciali e collettivi 
	-->

  <xsl:template match="//cataloghi">
    <xsl:element name="cataloghi">
      <xsl:if test="catalogo-generale">
        <xsl:element name="cataloghi-generali">
          <xsl:for-each select="catalogo-generale">
            <xsl:copy-of select="."/>
          </xsl:for-each>
        </xsl:element>
      </xsl:if>
      <xsl:if test="catalogo-speciale">
        <xsl:element name="cataloghi-speciali">
          <xsl:for-each select="catalogo-speciale">
						<xsl:element name="catalogo-speciale">
							<xsl:copy-of select="nome"/>
							<xsl:if test="materiale">
								<xsl:element name="materiali">
									<xsl:for-each select="materiale">
									<xsl:element name="materiale">
										<xsl:attribute name="nome">
											<xsl:if test="not(contains(., '*'))">
												<xsl:value-of select="."/>
											</xsl:if>
											<xsl:if test="contains(., '*')">
												<xsl:copy-of select="substring-before(.,'*')"/>
											</xsl:if>
										</xsl:attribute>
										<xsl:copy-of select="../forme"/>
									</xsl:element>
									</xsl:for-each>
								</xsl:element>
							</xsl:if>
						</xsl:element>
          </xsl:for-each>
        </xsl:element>
      </xsl:if>
      <xsl:if test="catalogo-collettivo">
        <xsl:element name="cataloghi-collettivi">
          <xsl:for-each select="catalogo-collettivo">
						<xsl:element name="catalogo-collettivo">
							<xsl:copy-of select="nome"/>
							<xsl:if test="materiale">
								<xsl:element name="materiali">
									<xsl:for-each select="materiale">
									<xsl:element name="materiale">
										<xsl:attribute name="nome">
											<xsl:if test="not(contains(., '*'))">
												<xsl:value-of select="."/>
											</xsl:if>
											<xsl:if test="contains(., '*')">
												<xsl:copy-of select="substring-before(.,'*')"/>
											</xsl:if>
										</xsl:attribute>
										<xsl:copy-of select="../forme"/>
									</xsl:element>
									</xsl:for-each>
								</xsl:element>
							</xsl:if>
						</xsl:element>
          </xsl:for-each>
        </xsl:element>
      </xsl:if>
    </xsl:element>
  </xsl:template>

  <!-- Patrimonio, basta un solo template, ma c'è da gestire gli asterischi -->

	<xsl:template match="//patrimonio">
    <xsl:element name="patrimonio">
      <xsl:if test="materiale">
        <xsl:element name="materiali">
          <xsl:for-each select="materiale">
						<xsl:element name="materiale">
						  <xsl:attribute name="nome">
							  <xsl:if test="not(contains(@nome, '*'))">
									<xsl:value-of select="@nome"/>
								</xsl:if>
							  <xsl:if test="contains(@nome, '*')">
									<xsl:copy-of select="substring-before(@nome,'*')"/>
								</xsl:if>
							</xsl:attribute>
							<xsl:copy-of select="@posseduto"/>
							<xsl:copy-of select="@acquisti-ultimo-anno"/>
						</xsl:element>
          </xsl:for-each>
        </xsl:element>
      </xsl:if>
      <xsl:if test="fondo-speciale">
        <xsl:element name="fondi-speciali">
          <xsl:for-each select="fondo-speciale">
						<xsl:element name="fondo-speciale">
							<xsl:copy-of select="nome"/>
							<xsl:copy-of select="descrizione"/>
							<xsl:copy-of select="cdd"/>
							<xsl:copy-of select="depositato"/>
							<xsl:if test="catalogoInventario">
								<xsl:element name="catalogo-inventario">
									<xsl:value-of select="catalogoInventario"/>
								</xsl:element>
							</xsl:if>
							<xsl:if test="catalogoInventarioUrl">
								<xsl:element name="catalogo-inventario-url">
									<xsl:value-of select="catalogoInventarioUrl"/>
								</xsl:element>
							</xsl:if>
						</xsl:element>
          </xsl:for-each>
        </xsl:element>
      </xsl:if>
      <xsl:copy-of select="fondi-antichi"/>
      <xsl:copy-of select="inventario"/>
      <xsl:copy-of select="catalogo-topografico"/>
      <xsl:copy-of select="acquisti-ultimi-quindici-anni"/>
      <xsl:copy-of select="totale-posseduto"/>
      <xsl:copy-of select="totale-posseduto-ragazzi"/>
    </xsl:element>
  </xsl:template>

  <!--
	I primi due dei prossimi template correggono il famoso errore 
  "Istituzione". Il successivo corregge i codici ISIL, ma solo
  se mancanti dell'IT- e non lunghi 6. Non è un gran test.
-->
  <xsl:template match="//codici">
    <xsl:element name="codici">
      <xsl:if test="not(string-length(iccu) = 9)">
        <xsl:comment> *** CODICE ISIL ERRATO *** </xsl:comment>
      </xsl:if>
      <xsl:element name="isil">
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
<!--
Servizi, qui servono diversi template e va gestito l'attributo "attivo"
-->
  <xsl:template match="//servizi">
    <xsl:element name="servizi">
      <xsl:apply-templates select="orario"/>
      <xsl:apply-templates select="prestito"/>
      <xsl:if test="prestito/riproduzioni">
        <xsl:element name="riproduzioni">
          <xsl:attribute name="attivo">s</xsl:attribute>
          <xsl:for-each select="prestito/riproduzioni">
            <xsl:element name="riproduzione">
              <xsl:copy-of select="./*"/>
            </xsl:element>
          </xsl:for-each>
        </xsl:element>
      </xsl:if>
			<xsl:copy-of select="materiali-esclusi-locale"/>
      <xsl:if test="informazioni-bibliografiche">
        <xsl:element name="informazioni-bibliografiche">
          <xsl:attribute name="attivo">s</xsl:attribute>
          <xsl:copy-of select="informazioni-bibliografiche/*"/>
        </xsl:element>
      </xsl:if>

<!-- 
Gestione dell'accesso a internet. Si è passati ad una gestione più coerente,
per cui vanno valorizzati tre flag in base a valori opportuni dell'elemento
sorgente, che era una semplice stringa. Inoltre, se presente l'elemento, anche
vuoto, va valorizzato l'attributo "attivo"
-->
      <xsl:if test="internet">
        <xsl:element name="internet">
          <xsl:attribute name="attivo">s</xsl:attribute>
          <xsl:if test="internet/modo = 'limitato'">
						<xsl:element name="con-proxy">s</xsl:element>
          </xsl:if>
          <xsl:if test="internet/modo = 'Selezionato'">
						<xsl:element name="con-proxy">s</xsl:element>
          </xsl:if>
          <xsl:if test="internet/modo = 'libero'">
						<xsl:element name="con-proxy">n</xsl:element>
          </xsl:if>
          <xsl:if test="internet/modo = 'Libero'">
						<xsl:element name="con-proxy">n</xsl:element>
          </xsl:if>
          <xsl:if test="internet/modo = 'A pagamento'">
						<xsl:element name="a-pagamento">s</xsl:element>
          </xsl:if>
          <xsl:if test="internet/modo = 'A tempo'">
						<xsl:element name="a-tempo">s</xsl:element>
          </xsl:if>
        </xsl:element>
      </xsl:if>
      <xsl:apply-templates select="accesso"/>
      <xsl:copy-of select="sistemi"/>
      <xsl:copy-of select="sezioni-speciali"/>
    </xsl:element>
  </xsl:template>

<!--
Gestisce anche i limiti di età, ora ridotti ad una stringa libera, 
controllata solo dai vocabolari.
-->
	<xsl:template match="accesso">
    <xsl:element name="accesso">
      <xsl:copy-of select="aperta"/>
      <xsl:copy-of select="handicap"/>
			<xsl:if test="condizioni-accesso">
				<xsl:element name="modalita-accesso">
					<xsl:for-each select="condizioni-accesso/documenti/tipo">
						<xsl:element name="modo">
						  <xsl:value-of select="."/>
						</xsl:element>
					</xsl:for-each>
					<xsl:for-each select="condizioni-accesso/eta[@min]">
						<xsl:element name="modo">
								<xsl:text>Limite di eta': &gt;= </xsl:text>
						  <xsl:value-of select="@min"/>
						</xsl:element>
					</xsl:for-each>
					<xsl:for-each select="condizioni-accesso/eta[@max]">
						<xsl:element name="modo">
								<xsl:text>Limite di eta': &lt;= </xsl:text>
						  <xsl:value-of select="@max"/>
						</xsl:element>
					</xsl:for-each>
					<xsl:if test="condizioni-accesso/appuntamento">
						<xsl:element name="modo">
						  <xsl:text>appuntamento</xsl:text>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:if>
    </xsl:element>
  </xsl:template>

  <!-- Prestito, qualche elemento contenitore -->

  <xsl:template match="//prestito">
    <xsl:element name="prestito">
      <xsl:if test="locale">
        <xsl:element name="locale">
          <xsl:attribute name="attivo">s</xsl:attribute>
          <xsl:copy-of select="locale/automatizzato"/>
          <xsl:if test="materiali-esclusi-locale">
            <xsl:element name="materiali-esclusi">
              <xsl:for-each select="materiali-esclusi-locale/materiale">
                <xsl:element name="materiale-escluso">
                  <xsl:value-of select="."/>
                </xsl:element>
              </xsl:for-each>
            </xsl:element>
          </xsl:if>
          <xsl:copy-of select="locale/durata"/>
          <xsl:copy-of select="locale/utenti-ammessi"/>
          <xsl:copy-of select="locale/totale-prestiti"/>
        </xsl:element>
      </xsl:if>
      <xsl:if test="interbibliotecario">
					<xsl:element name="interbibliotecario">
						<!--<xsl:attribute name="attivo">s</xsl:attribute>-->
					<xsl:copy-of select="interbibliotecario/*"/>
        </xsl:element>
      </xsl:if>
    </xsl:element>
  </xsl:template>

<!-- 
Orario, qualche elemento contenitore. Sarebbe opportuno non copiare elementi
"periodo" vuoti, ma comunque non disturbano l'import
-->

  <xsl:template match="//orario">
    <xsl:element name="orario">
      <xsl:copy-of select="ufficiale"/>
      <xsl:copy-of select="variazione"/>
      <xsl:if test="chiusura">
        <!--<xsl:element name="chiusure">-->
          <xsl:for-each select="chiusura">
            <xsl:copy-of select="."/>
          </xsl:for-each>
        <!--</xsl:element>-->
      </xsl:if>
      <xsl:copy-of select="ore-settimanali"/>
      <xsl:copy-of select="ore-settimanali-pomeridiane"/>
      <xsl:copy-of select="settimane-apertura"/>
    </xsl:element>
  </xsl:template>
  <!-- sezione amministrativa, va copiato tutto eccetto la funzione-obiettivo;
sicuramente il template si può fare molto meglio di così -->
  <xsl:template match="//amministrativa">
    <xsl:element name="amministrativa">
      <xsl:copy-of select="codice-fiscale"/>
      <xsl:copy-of select="partita-iva"/>
      <xsl:copy-of select="autonoma"/>
      <xsl:apply-templates select="ente"/>
      <xsl:copy-of select="regolamento"/>
      <xsl:copy-of select="carta-servizi"/>
      <xsl:copy-of select="depositi-legali"/>
      <xsl:copy-of select="strutture"/>
      <xsl:copy-of select="utenti"/>
      <xsl:copy-of select="personale"/>
      <xsl:copy-of select="bilancio"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="//ente">
    <xsl:element name="ente">
      <xsl:copy-of select="nome"/>
      <xsl:copy-of select="tipologia-amministrativa"/>
      <xsl:copy-of select="tipologia-funzionale"/>
      <xsl:copy-of select="stato"/>
      <xsl:copy-of select="codice-fiscale"/>
      <xsl:copy-of select="partita-iva"/>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
