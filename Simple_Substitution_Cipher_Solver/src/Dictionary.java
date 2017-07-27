import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class Dictionary {
	private static HashSet<String> dictionary;
	
	public Dictionary(){
		dictionary = new HashSet<String>();
		fillDictionary();
	}
	
	private void fillDictionary(){
		BufferedReader br;
		try {
			//br = new BufferedReader(new FileReader("C://Users//marja//Documents//HTW//5. Semester//IT Security//Cipher//src//words.txt"));
			br = new BufferedReader(new FileReader(".//src//words.txt"));
			String line = br.readLine();
			while (line != null) {
				line = br.readLine();
				if (line != null) {
					dictionary.add(line);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> search(String regEx){
		ArrayList<String> result = new ArrayList<String>();
		for(String word: dictionary){
			if(word.matches(regEx))
				result.add(word);
				
		}
		return result;
	}
}