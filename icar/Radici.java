
import java.io.*;
import java.text.*;
import org.jdom.*;
import org.jdom.input.*;

public class Radici
{
		public static void main(String[] args)
		{
				DecimalFormat df = new DecimalFormat("00000");
				File dir = new File(args[0]);
				File[] list = dir.listFiles();
				SAXBuilder builder = new SAXBuilder();
				int cut = 100000;
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
								File cdir = new File(c.getName());
								cdir.mkdir();
								String baseName = list[ii].getName();
								FileReader in = new FileReader(list[ii]);
								baseName = c.getName() + "-" + df.format(ii);  
								FileWriter out = new FileWriter(new File(cdir, baseName));
								int cc;
								while ((cc = in.read()) != -1)
								out.write(cc); in.close();
								out.close();
						}
						catch(java.lang.Exception e)
						{
								System.err.print(e.toString() + ": ");
								System.err.println(list[ii].getName());
						}
				}
				try
				{
						//XMLOutputter out = new XMLOutputter();
						//out.output(complessiDoc, System.out);
				}
				catch(java.lang.Exception e)
				{
						System.err.println(e.toString());
				}
		}
}
