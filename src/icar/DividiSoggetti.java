package icar;
/*
 * $Log: DividiSoggetti.java,v $
 * Revision 1.2  2012-10-10 12:54:52  agiuliano
 * Corretta la posizione del contatore cNum, incrementato ora solo
 * quando il file descrive un soggetto produttore.
 *
 * Revision 1.1  2012-10-10 12:52:03  agiuliano
 * Era DividiRepertori, rinominato in DividiSoggetti perché in effetti lo scopo è considerare solo i soggetti produttori e dividerli in base all'archivio di competenza.
 * Funziona su record di tipo "eac", considerando solo quelli con type="soggettoProduttore".
 *
 */


import java.io.*;
import org.jdom.*;
import org.jdom.input.*;

public class DividiSoggetti
{
	public static void main(String[] args)
	{
		File sDir = new File(args[0]);
		File[] sList = sDir.listFiles();
		File tDir = new File(sDir.getParent(), "/soggetti-per-archivio");
		tDir.mkdir();
		SAXBuilder builder = new SAXBuilder();
		int cut = 100000;
		int max;
		if(cut < sList.length)
			max = cut;
		else
			max = sList.length;
		int cNum = 0;
		try
		{
			Document doc;
			Element root;
			String rootName;
			FileReader in;
			FileWriter out;
			String archivio;
			for(int ii=0; ii < max; ii++)
			{
				System.out.println(sList[ii]);
				try
				{
					doc = builder.build(sList[ii]);
					root = doc.getRootElement();
					rootName = root.getName();
					if(rootName == "eac")
					{
						String baseName = sList[ii].getName();
						in = new FileReader(sList[ii]);
						Element eacheader = root.getChild("eacheader");
						if(eacheader.getAttributeValue("type").equals("soggettoProduttore"))
						{
							cNum++;
							Element eacid = eacheader.getChild("eacid");
							archivio = root
							.getChild("condesc")
							.getChild("resourcerels")
							.getChild("resourcerel")
							.getChild("archunit")
							.getChild("repository")
							.getChild("corpname")
							.getText();
							archivio = archivio.replaceAll(" *\n *", " - ");
							archivio = archivio.replaceAll(" *\r *", " - ");
							archivio = archivio.trim();
							baseName = eacid.getText() + ".xml";  
							File aDir = new File(tDir, archivio);
							aDir.mkdir();
							out = new FileWriter(new File(aDir, baseName));
							int cc;
							while ((cc = in.read()) != -1)
							    out.write(cc); in.close();
							out.close();
						}
					}
				}
				catch(java.lang.Exception e)
				{
					e.printStackTrace(System.out);
					//System.err.print(e.toString() + ": ");
					System.err.println(sList[ii].getName());
				}

			}
			System.out.println("Trovati " 
					+ cNum + " soggetti produttori su " 
					+ sList.length + " totali");
		}
		catch(java.lang.Exception e)
		{
			System.err.print(e.toString() + ": ");
		}
	}
}
