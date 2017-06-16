import java.util.HashMap;

public class Decrypter {
	
	private String originalCiphertext;
	private StringBuilder ciphertext;
	private int[] frequencies;
	private char[] lettersByFrequency = { 'e', 't', 'a', 'o', 'i', 'n', 's', 'h', 'r', 'd', 'l', 'c', 'u', 'm', 'w',
			'f', 'g', 'y', 'p', 'b', 'v', 'k', 'j', 'x', 'q', 'z' };
	private String[] oneLetterWords = { "a", "i" };
	private HashMap<Character, Character> keyMap;
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
			plainwords[i] = new StringBuilder();
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
					if(keyMap.containsKey(cipherwords[i].charAt(j))){
						plainwords[i].setCharAt(j, keyMap.get(cipherwords[i].charAt(j)));
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
		keyMap = new HashMap<Character, Character>();
		for (int i = 0; i < frequencies.length; i++) {
			int currentMaxIndex = getNextMaxFromFrequencyTable();
			if (currentMaxIndex == -1)
				return;
			//cipherletter --> plainletter
			keyMap.put((char) (currentMaxIndex + 97), lettersByFrequency[i]);
			if(keyMap.size() >= 5)
				return;
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
			if (keyMap.get(ciphertext.charAt(i)) != null)
				mask += keyMap.get(ciphertext.charAt(i));
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
		for (char key : keyMap.keySet()) {
			System.out.println("" + key + " -> " + keyMap.get(key));
		}
		System.out.println();
	}

	private void displayMask() {
		System.out.println("Current Mask");
		System.out.println(mask);
		System.out.println();
	}
}