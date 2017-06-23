import java.util.Arrays;
import java.util.HashSet;

public class Test {

	public static void main(String[] args) {

		HashSet<Character> alphabet = new HashSet<Character>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));
		StringBuilder current = new StringBuilder(alphabet.toString());
		String letters = "[";
		for(char letter: alphabet){
			letters += letter;
		}
		letters += "]";
		System.out.println(letters);
		

	}

}
