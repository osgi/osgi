import java.text.*;


public class TestBidi  {

	public static void main(String args[]) {
		String s= "Hello World العربي ة and again";
		Bidi bidi = new Bidi(s,Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
		System.out.println(bidi.isMixed());
		for ( int i =0; i<bidi.getRunCount(); i++ ) {
			System.out.printf( "%3d %d \n%s\n", i, (bidi.getRunLevel(i)), s.substring(bidi.getRunStart(i), bidi.getRunLimit(i)));
		}
	}
}
