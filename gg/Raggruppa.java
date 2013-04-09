
import java.io.*;
import java.lang.*;
import java.util.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

public class Raggruppa
{
		public static void main(String[] args)
		{
				//System.out.println("Ciao");
				File dir = new File(args[0]);
				File[] list = dir.listFiles();
				System.out.println(list.length);
				Document complessiDoc = new Document(new Element("complessi"));
				Element complessiRoot = complessiDoc.getRootElement();
				SAXBuilder builder = new SAXBuilder();
				int cut = 1000;
				int max;
				if(cut < list.length)
						max = cut;
				else
						max = list.length;
				for(int ii=0; ii < max; ii++)
				{
						try
						{
								Document doc = builder.build(list[ii]);
								Element c = doc.getRootElement();
								String archivio = c
									.getChild("did")
									.getChild("repository")
									.getChild("corpname")
									.getText();
								if(archivio.equals("ARCHIVIO DI STATO DI NAPOLI"))
								{
										complessiRoot.addContent((Element) (c.clone()));
								}
						}
						catch(java.lang.Exception e)
						{
								System.err.print(e.toString() + ": ");
								System.err.println(list[ii].getName());
						}
				}
				try
				{
						XMLOutputter out = new XMLOutputter();
						out.output(complessiDoc, new FileWriter(new File("napoli.xml")));
				}
				catch(java.lang.Exception e)
				{
						System.err.println(e.toString());
				}
		}
}
