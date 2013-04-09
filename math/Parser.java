import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.StringTokenizer;

public class Parser 
{
	LinkedList<String> os;
	LinkedList<String> oq;
	String in;
	Properties precedence;

	public Parser(String arg) {
		os = new LinkedList<String>();
		oq = new LinkedList<String>();
		in = arg;
		precedence = new Properties();
		precedence.setProperty("-", "2");
		precedence.setProperty("+", "2");
		precedence.setProperty("*", "3");
		precedence.setProperty("/", "3");
		precedence.setProperty("^", "4");
	}

	private int precedence(String op) {
		String tmp = precedence.getProperty(op);
		return Integer.parseInt(tmp);
	}

	private boolean isOperator(String t) {
		if (t.equals("+") || t.equals("-") || t.equals("*") || t.equals("/")
				|| t.equals("^"))
			return true;
		else
			return false;
	}
	
	private void printOS()
	{
		Iterator<String> i = os.iterator();
		System.err.print("Stack: ");
		while(i.hasNext())
		{
			System.err.print(" " + i.next());
		}
		System.err.println();
	}

	private void printOQ()
	{
		Iterator<String> i = oq.iterator();
		System.err.print("Coda: ");
		while(i.hasNext())
		{
			System.err.print(" " + i.next());
		}
		System.err.println();
	}

	public String convert()
	{
		String t1, t2;
		StringTokenizer st = new StringTokenizer(in);
		String tmp = new String();
		String op;
		while(st.hasMoreTokens())
		{
			t1 = st.nextToken();
			//System.err.println("Token: " + t1);
			if(isOperator(t1))
			{
				//System.err.println(t1 + " è un operatore");
				t2 = os.peek();
				if(t2 != null && isOperator(t2))
				{
					//System.err.println(t2 + " era sulla stack");
					if(precedence(t1) < precedence(t2))
						oq.add(os.pop());
				}
				os.push(t1);
			}
			else if(t1.equals("("))
			{
				os.push(t1);
			}
			else if(t1.equals(")"))
			{
				do
				{
					op = os.peek();
					if(op != null)
					{
						if(! op.equals("("))
						{
							oq.add(os.pop());
							//System.err.println(op + " era sulla stack");
						}
						else
						{
							os.pop();
						}
					}
					else
					{
						System.err.println("Parentesi non corrispondenti");
					}
				}
				while(! op.equals("("));
				//os.push(t1);
			}
			else
			{
				oq.add(t1);
			}
			printOS();
			printOQ();
		}
		
		while(os.peek() != null)
		{
			oq.add(os.pop());
			printOS();
			printOQ();
		}
		while(oq.peek() != null)
		{
			tmp += " " + oq.poll();
		}
		return tmp;
	}

	public int evaluate(String rpn)
	{
		StringTokenizer st = new StringTokenizer(rpn);
		String t;
		LinkedList<Integer> ns = new LinkedList<Integer>();
		int n1, n2;
		//System.err.println(rpn);
		while (st.hasMoreTokens()) 
		{
			t = st.nextToken();
			if (isOperator(t))
			{
				n2 = ns.pop();
				n1 = ns.pop();
				if (t.equals("+"))
					ns.push(n1 + n2);
				if (t.equals("-"))
					ns.push(n1 - n2);
				if (t.equals("*"))
					ns.push(n1 * n2);
				if (t.equals("/"))
					ns.push(n1 / n2);
				if (t.equals("^"))
					ns.push(new Double(Math.pow(n1, n2)).intValue());
				//System.err.println("Metto sulla stack  il numero " + ns.peek());
			} 
			else
			{
				//System.err.println("Trovato il numero " + t);
				ns.push(Integer.parseInt(t));
			}
		}
		return ns.pop();
	}

	public static void main(String args[]) 
	{
		args[0] = "( 1 + 4 ) ^ ( ( 5 - 4 ) * 2 )";
		System.err.println("Infix: " + args[0]);
		Parser p = new Parser(args[0]);
		System.out.println("Infix: " + args[0] + ", RPN: " + p.convert());
		System.out.println("Valore: " + p.evaluate(p.convert()));
	}
}
