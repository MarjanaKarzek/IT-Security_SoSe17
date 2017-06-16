import java.util.HashMap;

public class Decrypter {
	
	private String originalCiphertext;
	private StringBuilder ciphertext;
	private int[] frequencies;
	private char[] lettersByFrequency = { 'e', 't', 'a', 'o', 'i', 'n', 's', 'h', 'r', 'd', 'l', 'c', 'u', 'm', 'w',
			'f', 'g', 'y', 'p', 'b', 'v', 'k', 'j', 'x', 'q', 'z' };
	private String[] oneLetterWords = { "a", "i" };
	private String[] twoLetterWords = { "of", "to", "in", "it", "is", "be", "as", "at", "so", "we", "he", "by",
										"or", "on", "do", "if", "me", "my", "up", "an", "go", "no", "us", "am"};
	private String[] threeLetterWords = { "the", "and", "for", "are", "but", "not", "you", "all", "any", "can",
										  "had", "her", "was", "one", "our", "out", "day", "get", "has", "him",
										  "his", "how", "man", "new", "now", "old", "see", "two", "way", "who",
										  "boy", "did", "its", "let", "put", "say", "she", "too", "use" };
	private HashMap<Character, Character> frequencyMapping;
	private String mask;
	
	private Dictionary dictionary;
	private String[] cipherwords;
	private StringBuilder[] plainwords;
	

	public Decrypter(String ciphertext) {
		//Refactor?
		this.originalCiphertext = ciphertext;
		this.ciphertext = new StringBuilder(ciphertext.toLowerCase());
		
		
		this.dictionary = new Dictionary();
		this.cipherwords = ciphertext.split(" ");
		initializePlainwords();
		//Frequency Analysis
		initializeFrequencyTable();
		getFrequencies();
		getFrequencyMapping();
		//Test
		displayFrequencyMapping();
		





		
		
		//generateMaskFromFrequencyMapping();
		//displayMask();
		//applyMask();
		//System.out.println(this.ciphertext);
	}

	public static void main(String[] args) {
		Decrypter decrypter = new Decrypter("Rbo rpktigo vcrb bwucja wj kloj hcjd km sktpqo cq rbwr loklgo vcgg cjqcqr kj skhcja wgkja wjd rpycja rk ltr rbcjaq cj cr Roppy Lpwrsborr");
		decrypter.solve();
	}
	
	private void initializePlainwords() {
		plainwords = new StringBuilder[cipherwords.length];
		for(int i = 0; i < plainwords.length; i++){
			for(int j = 0; j < cipherwords[i].length(); j++){
				plainwords[i].append(" ");				
			}
		}
	}

	private boolean solve(){
		//try Frequency Analysis
		initializeFrequencyTable();
		getFrequencies();
		displayFrequencies();
		applyKey();
		if(!solveOneLetterWords(false))
			return false;
		else
			return true;
	}
	
	private void applyKey() {
		for(int i = 0; i < plainwords.length; i++){
			for(int j = 0; j < plainwords[i].length(); j++){
				if(plainwords[i].charAt(j) == ' '){
					if(frequencyMapping.containsKey(cipherwords[i].charAt(j))){
						plainwords[i].setCharAt(j, frequencyMapping.get(cipherwords[i].charAt(j)));
					}
				}
			}
		}
		
	}

	private boolean solveOneLetterWords(boolean finished){
		if(finished)
			return true;
		else{
			for(int i = 0; i <cipherwords.length; i++){
				if(cipherwords[i].length() == 1 ){
					if(plainwords[i].toString().contains(" ")){
						
					}
				}
				else{
					return true;
				}
			}
			return false;
		}
	}
	
	private void initializeFrequencyTable() {
		frequencies = new int[26];
		for (int i = 0; i < frequencies.length; i++) {
			frequencies[i] = 0;
		}
	}

	private void getFrequencies() {
		for (int i = 0; i < ciphertext.length(); i++) {
			if (ciphertext.charAt(i) != ' ') {
				int charvalue = (int) ciphertext.charAt(i) - 97;
				frequencies[charvalue]++;
			}
		}
		//Test
		displayFrequencies();
	}

	private void getFrequencyMapping() {
		frequencyMapping = new HashMap<Character, Character>();
		for (int i = 0; i < frequencies.length; i++) {
			int currentMaxIndex = getNextMaxFromFrequencyTable();
			if (currentMaxIndex == -1)
				return;
			//cipherletter --> plainletter
			frequencyMapping.put((char) (currentMaxIndex + 97), lettersByFrequency[i]);
		}
	}

	private int getNextMaxFromFrequencyTable() {
		int maxIndex = -1;
		int maxValue = 0;
		for (int i = 0; i < frequencies.length; i++) {
			if (frequencies[i] > maxValue && frequencies[i] != 0) {
				maxValue = frequencies[i];
				maxIndex = i;
			}
		}
		if (maxIndex != -1)
			frequencies[maxIndex] = 0;
		return maxIndex;
	}

	private void generateMaskFromFrequencyMapping() {
		mask = "";
		for (int i = 0; i < ciphertext.length(); i++) {
			if (frequencyMapping.get(ciphertext.charAt(i)) != null)
				mask += frequencyMapping.get(ciphertext.charAt(i));
			else
				mask += " ";
		}
	}
	
	private void applyMask(){
		for(int i = 0; i < ciphertext.length(); i++){
			if(mask.charAt(i) != ' '){
				ciphertext.setCharAt(i, mask.charAt(i));
			}
		}
	}
	
	

	private void displayFrequencies() {
		System.out.println("Frequencies");
		for (int i = 0; i < frequencies.length; i++) {
			System.out.println("" + (char) (i + 97) + " -> " + frequencies[i]);
		}
		System.out.println();
	}

	private void displayFrequencyMapping() {
		System.out.println("Frequency Mapping");
		for (char key : frequencyMapping.keySet()) {
			System.out.println("" + key + " -> " + frequencyMapping.get(key));
		}
		System.out.println();
	}

	private void displayMask() {
		System.out.println("Current Mask");
		System.out.println(mask);
		System.out.println();
	}
}