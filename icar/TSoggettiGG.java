
import java.io.*;
import java.text.*;
import java.util.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.jdom.xpath.*;
//import org.saxpath.*;

public class TSoggettiGG
{
	public static String apici(String in)
	{
		in.replaceAll("\n", "-");
		in.replaceAll("\r", "-");
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
	
	public static void main(String[] args) 
	{
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		Element root;
		File sDir = new File(args[0]);
		File[] sList = sDir.listFiles();
		PrintWriter insertFile = null;
		try 
		{
			insertFile = new PrintWriter(new File(args[0] + ".sql"));
		}
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
		}
		int cNum = 0;
		for(int ii=0; ii < sList.length; ii++)
		{
			String insertFields = "INSERT INTO [Archivista_SIENA].[dbo].[GuidaGenerale] (";
			String insertValues = " VALUES (";
			String tempString = null;
			Element tempElement;
			try
			{
				doc = builder.build(sList[ii]);
				root = doc.getRootElement();
				cNum++;
				// nrecord
				insertFields += "[Nrecord]";
				insertValues += 
					apici(((Element) XPath.selectSingleNode(root, "//did/unitid[@type='nrecord']"))
					.getTextNormalize());
				// level
				insertFields += ", [Level]";
				insertValues += 
					", " 
					+ apici(root.getAttributeValue("level"))
					;
				// denomcompleta (attenzione, può essere in due parti)
				insertFields = insertFields + ", [DenomCompleta]";
				Element titolo = ((Element) XPath.selectSingleNode(root, "//did/unittitle[not(@type)]"));
				if(titolo != null)
				{
					tempString = titolo.getTextNormalize();
				}
				tempElement = (Element) XPath.selectSingleNode(root, "//note[@type='genealogia']/list/item/unittitle[@label='fondo']");
				if(tempElement != null)
				{
					tempString += " > " + tempElement.getTextNormalize();
					tempString = tempString.replaceAll("....@", "");
				}
				insertValues += ", " + apici(tempString);
				
				//genealogia1 e genealogia2 (attenzione agli if)
				Element noteFondo = (Element) XPath.selectSingleNode(root, 
				"//note[@type='genealogia']/list/item/unittitle[@label='fondo']");
				Element noteFondiAccorpati = (Element) XPath.selectSingleNode(root, 
				"//note[@type='genealogia']/list/item/unittitle[@label='fondiAccorpati']");
				if(noteFondiAccorpati != null)
				{
					insertFields += ", [Genealogia1]";
					insertValues += ", " 
						+ apici(noteFondiAccorpati.getTextNormalize().replaceAll("^.*@", ""));
				}
				else
				{
					if(noteFondo != null) 
					{
						insertFields += ", [Genealogia1]";
						insertValues += ", " 
							+ apici(noteFondo.getTextNormalize().replaceAll("^.*@", ""));
					}
				}
				if(noteFondo != null)
				{
					insertFields += ", [Genealogia2]";
					insertValues += ", " 
						+ apici(noteFondo.getTextNormalize().replaceAll("^.*@", ""));
				}
				
				// date
				Element data = 
					(Element) XPath.selectSingleNode(root, "//did/unitdate");
				if(data != null)
				{
					insertFields += ", [Date]";
					insertValues += ", " + apici(data.getTextNormalize());
					Attribute dateNormal = data.getAttribute("normal");
					if(dateNormal != null)
					{
						insertFields += ", [EstremiCronologici]";
						insertValues += ", " + apici(dateNormal.getValue());
					}
				}
				
				// integrazione
				Element integ = 
					(Element) XPath.selectSingleNode(root, "//did/unittitle[@type='integrazione']");
				if(integ != null)
				{
					insertFields += ", [Integrazione]";
					insertValues += ", " + apici(integ.getTextNormalize());
				}
				
				// consistenza
				Element consist = 
					(Element) XPath.selectSingleNode(root, "//did/physdesc");
				if(consist != null)
				{
					insertFields += ", [Consistenza]";
					insertValues += ", " + apici(consist.getTextNormalize());
				}
				
				// note archivistiche
				Element noteArch = 
					(Element) XPath.selectSingleNode(root, "//scopecontent");
				if(noteArch != null)
				{
					insertFields += ", [NoteArchivistiche]";
					stripTags(noteArch);
					insertValues += ", " + apici(noteArch.getTextNormalize());
				}
				
				// note istituzionali
				Element noteIst = 
					(Element) XPath.selectSingleNode(root, "//bioghist");
				if(noteIst != null)
				{
					insertFields += ", [NoteIstituzionali]";
					stripTags(noteIst);
					insertValues += ", " + apici(noteIst.getTextNormalize());
				}
				insertFields += ")";
				insertValues += ")";
			}
			catch(java.lang.Exception e)
			{
				e.printStackTrace();
				System.err.println(sList[ii].getName());
			}
			//System.out.println(insertFields + insertValues);
			insertFile.println(insertFields + insertValues);
		}
		insertFile.close();
	}

}
