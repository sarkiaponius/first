public class Rational
{
	int num, den;

	public Rational(int n, int d)
	{
		int m = mcd(n, d);
		num = n / m;
		den = d / m;
	}

	public int mcd(int a, int b)
	{
		int r;
		do
		{
			r = a % b;
			a = b;
			b = r;
		}
		while(r > 0);
		return a;
	}
}
