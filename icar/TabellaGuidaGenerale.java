
import java.io.*;
import java.util.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.jdom.xpath.*;

public class TabellaGuidaGenerale
{
	/*
	 * Funzione di servizio che ripulisce una stringa in modo che sia accettabile per il db
	 */
	public static String clean(String in)
	{
		in = in.replaceAll("\n", "-");
		in = in.replaceAll("\r", "-");
		in = in.replaceAll("'", "\\'\\'");
		//in = in.replaceAll("\"", "\\\\\"");
		return "'" + in + "'";
	}

	//	elimina tutti i tag dall'albero, lasciando solo il testo

	public static void stripTags(Element in)
	{
		XMLOutputter xo = new XMLOutputter();
		String out = xo.outputString(in);
		out = out.replaceAll("<[^>]*>", " ");
		out = out.replaceAll("[ \n\r]*\\.", ".");
		in.setText(out);
	}

	public static Element select(Element root, String xpath)
	{
		Element temp = null;
		try
		{
			temp =(Element) XPath.selectSingleNode(root, xpath);
		}
		catch( Exception e)
		{
			e.printStackTrace();
		}
		return temp;
	}

	/*
	 * Procedura principale. Al momento i vari elementi sono elaborati 
	 * direttamente a questo livello, il che crea una certa confusione
	 */
	public static void main(String[] args) 
	{
		Properties prop = new Properties();
		try
		{
			prop.load(new FileReader(new File("config-0")));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		prop.getProperty("elenco-archivi");
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		Element root;
		BufferedReader archivi = null;
		try 
		{
			archivi = new BufferedReader(new FileReader(prop.getProperty("elenco-archivi")));
			String aDir = prop.getProperty("directory-archivi");
			System.err.println(prop.getProperty("elenco-archivi"));
			while(archivi.ready())
			{
				String archivio = archivi.readLine();
				if(! archivio.startsWith("#"))
				{
					File sDir = new File(aDir + archivio);		// directory contenente i file XML dei complessi
					System.err.println("\"" + aDir + "/" + archivio +"\"");
					File[] sList = sDir.listFiles();
					PrintWriter insertFile = null;		// file che conterrà le istruzioni SQL
					String insertFields = null;			// stringa per i nomi dei campi
					String insertValues = null;			// stringa per i valori
					String tempString = null;			// stringa ausiliaria
					Element tempElement = null;			// elemento ausiliario
					String field = null;
					int maxNoteArch = 0;
					int maxNoteIst = 0;

					/*
					 *  Mappa campi-xpath
					 *  Non è una corrispondenza perfetta, perché alcuni campi richiedono due elementi, e per
					 *  giunta sotto certe condizioni. Ad esempio le genealogie e le date ancora 
					 *  non sono gestite così, e la denoncompleta usa due elementi.
					 */
					HashMap<String, String> xp = new HashMap<String, String>();
					xp.put("[Nrecord]", "//did/unitid[@type='nrecord']");
					xp.put("[DenomCompleta]", "//did/unittitle[not(@type)]");
					xp.put("[DenomCompleta2]", "//note[@type='genealogia']/list/item/unittitle[@label='fondo']");
					xp.put("[Integrazione]", "//did/unittitle[@type='integrazione']");
					xp.put("[Consistenza]", "//did/physdesc");
					xp.put("[NoteArchivistiche]", "//scopecontent");
					xp.put("[NoteIstituzionali]", "//bioghist");
					try 
					{
						insertFile = new PrintWriter(prop.getProperty("directory-file-sql") + archivio + ".sql");
					}
					catch (Exception e1) 
					{
						e1.printStackTrace();
					}

					// Contatore dei complessi
					int cNum = 0;
					/*
					 * Cicla su tutti i file che trova nella sDir.
					 * Attenzione, la prima parte della INSERT è legata ad un archivio specifico,
					 * mentre andrebbe parametrizzata
					 */
					for(int ii=0; ii < sList.length; ii++)
					{
						String archivista = archivio.replaceAll("ARCHIVIO DI STATO DI", "");
						archivista = "Archivista_" + archivista;
						insertFields = "INSERT INTO ["+ archivista + "].[dbo].[GuidaGenerale] (";
						insertValues = " VALUES (";
						try
						{
							/*
							 * Carica un file XML e seleziona la sua root
							 */
							doc = builder.build(sList[ii]);
							root = doc.getRootElement();
							cNum++;

							/* 
							 * Comincia a gestire i singoli campi
							 */

							// nrecord
							field = "[Nrecord]";
							insertFields += field;
							insertValues += clean(select(root, xp.get(field)).getTextNormalize());

							// level
							field = "[Level]";
							insertFields += ", " + field;
							insertValues +=	", " + clean(root.getAttributeValue("level"));

							// denomcompleta (attenzione, può essere in due parti)

							field = "[DenomCompleta]";
							insertFields += ", " + field;
							Element titolo = select(root, xp.get(field));
							if(titolo != null)
							{
								tempString = titolo.getTextNormalize();
							}
							field = "[DenomCompleta2]";
							tempElement = select(root, xp.get(field));
							if(tempElement != null)
							{
								tempString += " > " + tempElement.getTextNormalize();
								tempString = tempString.replaceAll("....@", "");
							}
							insertValues += ", " + clean(tempString);

							//genealogia1 e genealogia2 (attenzione agli if)
							field = "[Genealogia1]";
							Element noteFondo = select(root, "//note[@type='genealogia']/list/item/unittitle[@label='fondo']");
							Element noteFondiAccorpati = select(root, 
							"//note[@type='genealogia']/list/item/unittitle[@label='fondiAccorpati']");
							if(noteFondiAccorpati != null)
							{
								insertFields += ", " + field;
								insertValues += ", " 
									+ clean(noteFondiAccorpati.getTextNormalize().replaceAll("^.*@", ""));
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

							// date
							Element data = 
								(Element) XPath.selectSingleNode(root, "//did/unitdate");
							if(data != null)
							{
								insertFields += ", [Date]";
								insertValues += ", " + clean(data.getTextNormalize());
								Attribute dateNormal = data.getAttribute("normal");
								if(dateNormal != null)
								{
									insertFields += ", [EstremiCronologici]";
									insertValues += ", " + clean(dateNormal.getValue());
								}
							}

							// integrazione
							field = "[Integrazione]";
							Element integ = select(root, xp.get(field));
							if(integ != null)
							{
								insertFields += ", " + field;
								insertValues += ", " + clean(integ.getTextNormalize());
							}

							// consistenza
							field = "[Consistenza]";
							Element consist = select(root, xp.get(field));
							if(consist != null)
							{
								insertFields += ", " + field;
								insertValues += ", " + clean(consist.getTextNormalize());
							}

							// note archivistiche
							field = "[NoteArchivistiche]";
							Element noteArch = select(root, xp.get(field));
							if(noteArch != null)
							{
								insertFields += ", " + field;
								stripTags(noteArch);
								tempString = clean(noteArch.getTextNormalize());
								if(tempString.length() > maxNoteArch)
								{
									maxNoteArch = tempString.length();
								}
								insertValues += ", " + tempString;					
							}

							// note istituzionali
							field = "[NoteIstituzionali]";
							Element noteIst = select(root, xp.get(field));
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
						}
						catch(java.lang.Exception e)
						{
							e.printStackTrace();
							System.err.println(sList[ii].getName());
						}
						insertFile.println(insertFields + insertValues);
					}
					insertFile.close();
					System.out.println("Lunghezze massime note archivistiche e istituzionali: " 
							+ maxNoteArch
							+ ", "
							+ maxNoteIst);
				}
			}
		}
		catch (Exception e2) 
		{
			e2.printStackTrace();
		}
	}
}
