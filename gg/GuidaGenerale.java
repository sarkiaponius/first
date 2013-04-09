
/*
 * $Log: GuidaGenerale.java,v $
 * Revision 1.8  2012-11-13 17:48:42  agiuliano
 * - Rimossi i vecchi metodi tabellaGuidaGenerale e tabellaSoggettiGG,
 * sostituiti con le nuove versioni in precedenza chiamate "*Bis".
 * - Anche il nuovo tabellaSoggettiGG legge i file da un'unica directory, selezionando
 * solo i file che risultano essere di soggetti produttori.
 * - Rimossi tutti i metodi relativi all'accesso diretto al database, non più usati da tempo
 * e comunque per niente sicuri.
 * - Rimosse diverse variabili inutili.
 *
 * Da fare:
 * - usare di nuovo delle properties per le directory, invece del file_prompt
 *
 * 
 * Revision 1.7 2012-11-13 15:23:18 agiuliano Qualche ritocco al metodo
 * tabellaGuidaGeneraleBis, soprattutto riguardo alle note di rinvio che ora si
 * aggiungono alle note archivistiche. In realtà sembra che siano sempre
 * mutuamente esclusive (c'è del codice che fa questo controllo, e non ho
 * riscontrato casi positivi).
 * 
 * Copiato il metodo tabellaSoggettiGG nel metodo*Bis, in vista di trasformare
 * anche questo in maniera da elaborare i file che trova in una certa cartella,
 * piuttosto che lavorare partendo da un elenco di archivi. Revision 1.6
 * 2012-11-09 15:51:55 agiuliano Metodo tabellaGuidaGeneraleBis - estratte dai
 * cicli più interni tutte le definizioni di variabili, per ridurre i tempi di
 * instanziazione - introdotto un contatore dei millisecondi nel ciclo più
 * interno
 * 
 * I tempi non sono calati, ma adesso posso almeno confrontarli seriamente.
 * Revision 1.5 2012-11-09 11:54:25 agiuliano Metodo tabellaGuidaGeneraleBis: -
 * eliminati alcuni blocchi fittizi, ovviamente non il codice, solo le parentesi
 * - eliminato molto codice sostanzialmente inutile perché legato alla gestione
 * precedente, basata su un elenco predefinito di archivi, non sui file EAD
 * trovati - testato, funziona
 * 
 * Da fare: - su grandi file EAD, come quello dell'archivio centrale, si nota
 * maggiore lentezza, nel senso che sembra volerci più tempo per elaborare il
 * singolo complesso; andrebbe capito il motivo di questa lentezza, o almeno
 * vedere se è reale o apparente
 */
import java.io.*;
import java.util.*;
import java.sql.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.jdom.xpath.*;

public class GuidaGenerale
{
	private Properties config;
	private SAXBuilder builder;
	private PrintWriter log = null;
	public GuidaGenerale()
	{
		config = new Properties();
		builder = new SAXBuilder();
		try
		{
			config.load(new FileReader(new File("config-0")));
			log = new PrintWriter(config.getProperty("file-log"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void log(String msg)
	{
		System.out.println(msg);
		log.println(msg);
	}

	/*
	 * Funzione di servizio che ripulisce una stringa in modo che sia accettabile
	 * per il db
	 */
	public String clean(String in)
	{
		in = in.replaceAll("\n", "-");
		in = in.replaceAll("\r", "-");
		in = in.replaceAll("'", "\\'\\'");
		// in = in.replaceAll("\"", "\\\\\"");
		return "'" + in + "'";
	}

	// elimina tutti i tag dall'albero, lasciando solo il testo

	public void stripTags(Element in)
	{
		XMLOutputter xo = new XMLOutputter();
		String out = xo.outputString(in);
		out = out.replaceAll("<[^>]*>", " ");
		out = out.replaceAll("[ \n\r]*\\.", ".");
		in.setText(out);
	}

	public void stripTags2(Element in)
	{
		XMLOutputter xo = new XMLOutputter();
		String out = xo.outputString(in);
		out = out.replaceAll("<note>", " (");
		out = out.replaceAll("</note>", ") ");
		out = out.replaceAll("<[^>]*>", " ");
		out = out.replaceAll("[ \n\r]*\\.", ".");
		in.setText(out);
	}

	/*
	 * seleziona il primo elemento dai risultati di un XPath
	 */
	public Element select(Element root, String xpath)
	{
		Element temp = null;
		try
		{
			temp = (Element) XPath.selectSingleNode(root, xpath);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return temp;
	}

	/*
	 * seleziona tutta la lista dei risultati di una XPath
	 */
	public List<Element> selectAll(Element root, String xpath)
	{
		List<Element> temp = null;
		try
		{
			temp = (List<Element>) XPath.selectNodes(root, xpath);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return temp;
	}

	/*
	 * Questo metodo è sostanzialmente identico a quello sopra, ma ricava i dati
	 * dai file inviati dal centro MAAS, che hanno una struttura gerarchica invece
	 * che piatta
	 */
	public void tabellaGuidaGenerale(File sDir)
	{
		builder = new SAXBuilder();
		Document doc;
		Element root;
		String idGG = null;
		String archivio = null;
		int totalRecords = 0;
		int totalArchivi = 0;
		try
		{
			/*
			 * Lista dei file EAD dei complessi trovati nella sDir. In questo nuovo
			 * metodo il file è in realtà uno solo contenente tutti i complessi.
			 * Notare la classe anonima usata per filtrare i soli file XML
			 */
			File[] sList = sDir.listFiles(new FilenameFilter()
			{
				public boolean accept(File aDir, String name)
				{
					if(name.endsWith(".xml"))
						return true;
					else
						return false;
				}
			});

			// Conteggia i file trovati, per controllo
			totalArchivi++;

			// File che conterrà le istruzioni SQL
			PrintWriter insertFile = null;

			// Variabili per stimare la lunghezza delle note e per conteggiare i
			// complessi effettivamente elaborati
			int maxNoteArch = 0;
			int maxNoteIst = 0;
			int cNum = 0;

			/*
			 * Mappa campi-xpath. Non è una corrispondenza perfetta, perché alcuni
			 * campi richiedono due elementi, e per giunta sotto certe condizioni. Ad
			 * esempio le genealogie e le date ancora non sono gestite così, e la
			 * denoncompleta usa due elementi.
			 */
			HashMap<String, String> xp = new HashMap<String, String>();
			xp.put("[ID_GuidaGenerale]", "did/unitid[@type='nrecord']");
			xp.put("[DenomCompleta]", "did/unittitle[not(@type)]");
			xp
					.put(
							"[DenomCompleta2]",
							"note[@type='genealogia']/list/item/unittitle[@label='fondo']"
									+ " | note[@type='genealogia']/list/item/unittitle[@label='fondiAccorpati']");
			xp.put("[Integrazione]", "did/unittitle[@type='integrazione']");
			xp.put("[Consistenza]", "did/physdesc");
			xp.put("[NoteArchivistiche]", "scopecontent");
			xp.put("[NoteIstituzionali]", "bioghist");

			/*
			 * Questo falso campo serve solo per arricchire le NoteArchivistiche
			 */
			xp.put("[NoteArchivisticheRinvio]", "did/note[@type='rinvio']");

			/*
			 * Cicla su tutti i file che trova nella sDir, ognuno dei quali contiene
			 * tutti i complessi di un certo archivio in una struttura gerarchica.
			 */
			log("Trovati " + sList.length + " file EAD nella directory "
					+ sDir.getName());
			List<Element> cList;
			String tempString = null; // stringa ausiliaria
			Element tempElement = null; // elemento ausiliario
			String field = null;
			Element c = null;
			Element titolo, noteFondiAccorpati, noteFondo, data, integ, consist, noteArch, noteIst = null;
			String insertFields; // stringa per i nomi dei campi
			String insertValues; // stringa per i valori
			long start, stop;

			for(int ii = 0; ii < sList.length; ii++)
			{
				insertFields = null;
				insertValues = null;
				try
				{
					/*
					 * Carica un file XML e seleziona la sua root
					 */
					doc = builder.build(sList[ii]);
					root = doc.getRootElement();

					// Per distinguere i file SQL fra di loro si ricava dal file EAD il
					// nome dell'istituzione. Quello sotto non è l'unico xpath in cui
					// compare, ma essendo nella archdesc mi è sembrato affidabile
					archivio = select(root, "/ead/archdesc/did/repository/corpname")
							.getTextNormalize();
					// Cerca di aprire il file SQL, su cui scriverà le insert in modo
					// bufferizzato per non rallentare troppo l'elaborazione
					try
					{
						insertFile = new PrintWriter(new BufferedWriter(new FileWriter(
								config.getProperty("directory-file-sql") + archivio + ".sql"),
								1024 * 2));
					}
					catch(Exception e1)
					{
						e1.printStackTrace();
					}
					/*
					 * Si crea l'elenco dei c. Questi si possono trovare a qualsiasi
					 * profondità, ma comunque finiscono nell'elenco. Non tutti gli
					 * elementi c vanno selezionati, ma forse va aggiunto un altro tipo,
					 * "fondiAccorpati". Proviamo a inserire anche la DELETE all'inizio
					 * del file
					 */
					insertFile.println("DELETE FROM [GuidaGenerale];");
					cList = selectAll(root, "//c[encodindAnalog='fondiStrutturati']"
							+ " | //c[@encodinganalog='fondo'] "
							+ " | //c[@encodinganalog='serie']");
					log("File " + sList[ii].getName() + " (" + archivio + "): trovati "
							+ cList.size() + " complessi c");
					// Contatore dei complessi
					cNum = 0;
					/*
					 * Itera sull'elenco degli elementi c
					 */
					Iterator<Element> ci = cList.iterator();
					while(ci.hasNext())
					{
						start = System.currentTimeMillis();
						c = ci.next();
						cNum++;
						insertFields = "INSERT INTO [GuidaGenerale] (";
						insertValues = " VALUES (";
						/*
						 * Comincia a gestire i singoli campi
						 */

						/*
						 * idGuidaGenerale. Si mette via come riferimento in caso di errori.
						 */
						field = "[ID_GuidaGenerale]";
						insertFields += field;
						idGG = select(c, xp.get(field)).getTextNormalize();
						insertValues += clean(idGG);

						// level
						field = "[Level]";
						insertFields += ", " + field;
						insertValues += ", " + clean(c.getAttributeValue("level"));

						// denomcompleta (attenzione, può essere in due parti)

						field = "[DenomCompleta]";
						insertFields += ", " + field;
						titolo = select(c, xp.get(field));
						if(titolo != null)
						{
							tempString = titolo.getTextNormalize();
						}
						field = "[DenomCompleta2]";
						tempElement = select(c, xp.get(field));
						if(tempElement != null)
						{
							tempString = tempElement.getTextNormalize() + " > " + tempString;
							tempString = tempString.replaceAll("....@", "");
						}
						insertValues += ", " + clean(tempString);

						/*
						 * DenomCompleta, così com'è, va bene anche come Denominazione
						 * secca. Tanto vale copiare il codice già usato sopra, con i
						 * necessari aggiustamenti
						 */
						if(titolo != null)
						{
							field = "[Denominazione]";
							insertFields += ", " + field;
							tempString = titolo.getTextNormalize();
							insertValues += ", " + clean(tempString);
						}

						// genealogia1 e genealogia2 (attenzione agli if)
						field = "[Genealogia1]";
						noteFondo = select(c,
								"//note[@type='genealogia']/list/item/unittitle[@label='fondo']");
						noteFondiAccorpati = select(c,
								"//note[@type='genealogia']/list/item/unittitle[@label='fondiAccorpati']");
						if(noteFondiAccorpati != null)
						{
							insertFields += ", " + field;
							insertValues += ", "
									+ clean(noteFondiAccorpati.getTextNormalize().replaceAll(
											"^.*@", ""));
						}
						else
						{
							if(noteFondo != null)
							{
								insertFields += ", " + field;
								insertValues += ", "
										+ clean(noteFondo.getTextNormalize().replaceAll("^.*@", ""));
							}
						}
						if(noteFondo != null)
						{
							field = "[Genealogia2]";
							insertFields += ", " + field;
							insertValues += ", "
									+ clean(noteFondo.getTextNormalize().replaceAll("^.*@", ""));
						}

						/*
						 * Date. Dopo varie riflessioni, al 2011-07-25 si invertono le
						 * destinazioni dei due tipi di date, per cui, nell'applicazione
						 * web, si vedranno come "estremi" quelle che, in xml, sono date
						 * testuali più o meno libere Al 2011-09-19 si torna a mettere in
						 * Date le date estese, e in EstremiCronologici le date XXX-YYY
						 */
						data = (Element) XPath.selectSingleNode(c, "//did/unitdate");
						if(data != null)
						{
							insertFields += ", [Date]";
							// controlla se è necessario troncare (per
							// limiti SIAS)
							if(data.getTextNormalize().length() > 64)
							{
								insertValues += ", "
										+ clean(data.getTextNormalize().substring(0, 64));
							}
							else
							{
								insertValues += ", " + clean(data.getTextNormalize());
							}
							Attribute dateNormal = data.getAttribute("normal");
							if(dateNormal != null)
							{
								insertFields += ", [EstremiCronologici]";
								insertValues += ", " + clean(dateNormal.getValue());
							}
						}

						// integrazione
						field = "[Integrazione]";
						integ = select(c, xp.get(field));
						if(integ != null)
						{
							insertFields += ", " + field;
							insertValues += ", " + clean(integ.getTextNormalize());
						}

						// consistenza
						field = "[Consistenza]";
						consist = select(c, xp.get(field));
						if(consist != null)
						{
							insertFields += ", " + field;
							insertValues += ", " + clean(consist.getTextNormalize());
						}

						/*
						 * Note archivistiche. Il campo è in realtà riempito con più
						 * elementi, non solo quello legato al nome del campo. Più avanti
						 * vengono riutilizzati sia "field" che "noteArch", ma nel metodo
						 * "select" sarà usato un secondo nome di campo in realtà fittizio
						 * solo per poter riutilizzare il codice già scritto.
						 */
						field = "[NoteArchivistiche]";
						noteArch = select(c, xp.get(field));
						int noteMiste = 0;
						if(noteArch != null)
						{
							noteMiste++;
							insertFields += ", " + field;
							stripTags(noteArch);
							tempString = clean(noteArch.getTextNormalize());
							if(tempString.length() > maxNoteArch)
							{
								maxNoteArch = tempString.length();
							}
							insertValues += ", " + tempString;
						}
						/*
						 * Come accennato sopra, si riutilizza il codice già scritto usando
						 * un finto nome di campo che permette però di individuare altre
						 * fonti per le note. Ho solo qualche dubbio circa le virgole di
						 * separazione
						 */
						noteArch = select(c, xp.get("[NoteArchivisticheRinvio]"));
						if(noteArch != null)
						{
							noteMiste++;
							insertFields += ", " + field;
							stripTags(noteArch);
							tempString = clean(noteArch.getTextNormalize());
							if(tempString.length() > maxNoteArch)
							{
								maxNoteArch = tempString.length();
							}
							insertValues += ", " + tempString;
						}
						if(noteMiste == 2)
						{
							log("scopecontent e note di rinvio presenti");
						}
						// note istituzionali
						field = "[NoteIstituzionali]";
						noteIst = select(c, xp.get(field));
						if(noteIst != null)
						{
							insertFields += ", " + field;
							stripTags(noteIst);
							tempString = clean(noteIst.getTextNormalize());
							if(tempString.length() > maxNoteIst)
							{
								maxNoteIst = tempString.length();
							}
							insertValues += ", " + tempString;
						}

						insertFields += ")";
						insertValues += ");";
						insertFile.println(insertFields + insertValues);
						stop = System.currentTimeMillis();
						log(cNum + ": " + clean(idGG) + " (" + (stop - start) + " millis)");
					}
				}
				catch(java.lang.Exception e)
				{
					e.printStackTrace();
					System.out.println(sList[ii].getName());
				}
				/*
				 * try { update(insertFields + insertValues); } catch(SQLException e) {
				 * log("Record " + cNum + "(complesso " + idGG + "): " +
				 * e.getMessage()); }
				 */
			}
			insertFile.close();
			System.out.println("Elaborati " + cNum + " record");
			System.out
					.println("Lunghezze massime note archivistiche e istituzionali: "
							+ maxNoteArch + ", " + maxNoteIst);
			totalRecords += cNum;

			System.out.println("Elaborati " + totalRecords + " record da "
					+ totalArchivi + " archivi");
		}
		catch(Exception e2)
		{
			e2.printStackTrace();
		}
	}

	/*
	 * elabora i file dei repertori, fra i quali si trovano quelli dei soggetti
	 * produttori
	 */
	public void tabellaSoggettiGG(File sDir)
	{
		config.getProperty("elenco-archivi");
		Document doc;
		Element root;
		File xml;
		PrintWriter insertFile = null; // file che conterrà le istruzioni SQL
		PrintWriter insertFile2 = null; // file che conterrà le istruzioni SQL
		// per la tabella di raccordo
		String insertFields = null; // stringa per i nomi dei campi
		String insertValues = null; // stringa per i valori
		String tempString = null; // stringa ausiliaria
		Element tempElement = null; // elemento ausiliario
		String field = null;
		String value = null;
		List<Element> tempList = null;
		Iterator<Element> iterator = null;
		String idSoggettoGG = null;
		String idGuidaGenerale = null;
		// int maxNoteArch = 0;
		// int maxNoteIst = 0;

		/*
		 * Mappa campi-xpath Non è una corrispondenza perfetta, perché alcuni campi
		 * richiedono due elementi, e per giunta sotto certe condizioni. Ad esempio
		 * le genealogie e le date ancora non sono gestite così, e la denoncompleta
		 * usa due elementi.
		 */
		HashMap<String, String> xp = new HashMap<String, String>();
		xp.put("ID_SoggettoGG", "/eac/eacheader/eacid");
		xp.put("TipoSoggetto", "/eac");
		xp.put("Denominazione",
				"/eac/condesc/identity/corphead/part[@type='denominazioneUfficiale']");
		xp.put("Integrazione",
				"/eac/condesc/identity/corphead/part[@type='integrazione']");
		xp.put("Date",
				"/eac/condesc/resourcerels/resourcerel/archunit/unitdate@normal");
		xp.put("DescrizioneEstremi",
				"/eac/condesc/resourcerels/resourcerel/archunit/unitdate");
		xp.put("Archivio",
				"/eac/condesc/resourcerels/resourcerel/archunit/repository/corpname");
		xp.put("Localita", "//identity/corphead/place[@type='sede']");
		xp.put("Storia", "/eac/condesc/desc/bioghist");
		xp.put("Note",
				"//identity/corphead/part[@type='ContestoArchivistico-istituzionale']");
		xp.put("IDGuidaGenerale",
				"/eac/condesc/resourcerels/resourcerel[@reltype='origination']");
		try
		{
			insertFile = new PrintWriter(config.getProperty("directory-file-sql")
					+ "/" + config.getProperty("file-insert-soggettiGG"));
			insertFile2 = new PrintWriter(config.getProperty("directory-file-sql")
					+ "/" + config.getProperty("file-insert-guidageneralesoggettiGG"));
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
		try
		{
			File[] sList = sDir.listFiles();
			log("Trovati " + sList.length + " file EAC nella directory "
					+ sDir.getPath());

			/*
			 * Cicla su tutti i file che trova nella sDir. Ogni file può essere sia un
			 * repertorio che un soggetto produttore, per cui più avanti si fa un test
			 * in proposito Attenzione, la prima parte della INSERT è legata ad un
			 * archivio specifico, mentre andrebbe parametrizzata
			 */
			long start, stop;
			int sNum = 0;
			for(int jj = 0; jj < sList.length; jj++)
			{
				start = System.currentTimeMillis();
				xml = sList[jj];

				// Contatore dei soggetti produttori
				try
				{
					/*
					 * Carica un file XML e seleziona la sua root
					 */
					doc = builder.build(xml);
					root = doc.getRootElement();

					// solo se il file riguarda un soggetto produttore
					String type = root.getChild("eacheader").getAttribute("type")
							.getValue();
					if(type.equals("soggettoProduttore"))
					{
						sNum++;
						insertFields = "INSERT INTO "
								+ config.getProperty("tabella-soggettiGG") + " (";
						insertValues = " VALUES (";

						/*
						 * Comincia a gestire i singoli campi
						 */

						// id_soggettogg
						field = "ID_SoggettoGG";
						value = clean(select(root, xp.get(field)).getTextNormalize());
						idSoggettoGG = value;
						if(value != null)
						{
							insertFields += field;
							insertValues += value;
						}

						// TipoSoggetto, forzato sempre a "ente"
						field = "TipoSoggetto";
						value = select(root, xp.get(field)).getAttributeValue("type");
						if(value != null)
						{
							if(value.equals("corporatebody"))
							{
								value = "'E'";
							}
							if(value.equals("person"))
							{
								value = "'P'";
							}
							if(value.equals("family"))
							{
								value = "'F'";
							}
							insertFields += ", " + field;
							insertValues += ", " + value;
						}

						// denominazione + integrazione: comanda il primo, e
						// in caso
						// si aggiunge il secondo dopo un "|"
						field = "Denominazione";
						tempElement = select(root, xp.get(field));
						if(tempElement != null)
						{
							insertFields += ", " + field;
							tempString = tempElement.getTextNormalize();
							field = "Integrazione";
							tempElement = select(root, xp.get(field));
							if(tempElement != null)
							{
								tempString += " | " + tempElement.getTextNormalize();
							}
							insertValues += ", " + clean(tempString);
						}

						// descrizione estremi
						field = "DescrizioneEstremi";
						tempList = selectAll(root, xp.get(field));
						if(!tempList.isEmpty())
						{
							insertFields += ", " + field;
							iterator = tempList.iterator();
							tempString = "";
							while(iterator.hasNext())
							{
								tempString += iterator.next().getTextNormalize();
								if(iterator.hasNext())
								{
									tempString += " | ";
								}
							}
							insertValues += ", " + clean(tempString);
						}

						// dataInizio dataFine. tempList è ancora utile...
						if(!tempList.isEmpty())
						{
							iterator = tempList.iterator();
							int min = Integer.MAX_VALUE;
							int max = Integer.MIN_VALUE;
							tempString = "";
							while(iterator.hasNext())
							{
								tempElement = iterator.next();
								if(tempElement.getAttributeValue("normal") != null)
								{
									String[] date = tempElement.getAttributeValue("normal")
											.split("/");
									if(Integer.parseInt(date[0]) < min)
									{
										min = Integer.parseInt(date[0]);
									}
									if(Integer.parseInt(date[1]) > max)
									{
										max = Integer.parseInt(date[1]);
									}
								}
							}
							insertFields += ", DataInizio";
							insertValues += ", " + min;
							insertFields += ", DataFine";
							insertValues += ", " + max;
						}

						// località
						field = "Localita";
						tempElement = select(root, xp.get(field));
						if(tempElement != null)
						{
							insertFields += ", " + field;
							insertValues += ", " + clean(tempElement.getTextNormalize());
						}
						else
						{
							/*
							 * Località è obbligatorio in fusione, quindi se manca nei
							 * sorgenti ci mettiamo il nome dell'archivio, se presente.
							 */
							tempElement = select(root, xp.get("Archivio"));
							if(tempElement != null)
							{
								insertFields += ", " + field;
								insertValues += ", " + clean(tempElement.getTextNormalize());
							}

						}

						// storia
						field = "Storia";
						tempElement = select(root, xp.get(field));
						if(tempElement != null)
						{
							stripTags2(tempElement);
							insertFields += ", " + field;
							insertValues += ", " + clean(tempElement.getTextNormalize());
						}

						// note
						field = "Note";
						tempElement = select(root, xp.get(field));
						if(tempElement != null)
						{
							stripTags(tempElement);
							insertFields += ", " + field;
							insertValues += ", " + clean(tempElement.getTextNormalize());
						}

						insertFields += ")";
						insertValues += ");";
						insertFile.println(insertFields + insertValues);

						/*
						 * Adesso avremmo finito con i dati per la tabella SoggettiGG, ma
						 * abbiamo l'opportunità di popolare nello stesso tempo la tabella
						 * di raccordo GuidaGeneraleSoggettiGG: basta prendere le
						 * resourcerel e tirar fuori gli attributi syskey quando
						 * reltype=origination
						 */

						tempList = selectAll(root, xp.get("IDGuidaGenerale"));
						if(!tempList.isEmpty())
						{
							iterator = tempList.iterator();
							while(iterator.hasNext())
							{
								idGuidaGenerale = clean(iterator.next().getAttributeValue(
										"syskey"));
								insertFile2.println("INSERT INTO" + " "
										+ config.getProperty("tabella-soggetti-complessiGG")
										+ " (ID_SoggettoGG, ID_GuidaGenerale)" + " VALUES("
										+ idSoggettoGG + ", " + idGuidaGenerale + ");");
							}
						}
						stop = System.currentTimeMillis();
						log(sNum + ": " + idSoggettoGG + " (" + (stop - start) + " millis)");
					}
				}
				catch(java.lang.Exception e)
				{
					e.printStackTrace();
					System.err.println(sList[jj].getName());
				}
			}
			insertFile.close();
			insertFile2.close();
			System.out.println("Elaborati " + sNum + " soggetti produttori da "
					+ sList.length + " file EAC");
		}
		catch(Exception e2)
		{
			e2.printStackTrace();
		}
	}

	/*
	 * Procedura principale. Al momento i vari elementi sono elaborati
	 * direttamente a questo livello, il che crea una certa confusione
	 */
	public static void main(String[] args)
	{
		GuidaGenerale gg = new GuidaGenerale();
		gg.tabellaGuidaGenerale(new File(args[0]));
		//gg.tabellaSoggettiGG(new File(args[1]));
	}
}
