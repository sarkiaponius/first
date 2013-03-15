package math;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int a = Integer.valueOf(args[0]);
		int b = Integer.valueOf(args[1]);
		Rational r = new Rational(a, b);
		System.out.println(r.mcd(a, b));
	}

}
