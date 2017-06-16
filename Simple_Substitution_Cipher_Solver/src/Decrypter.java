import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Decrypter {
	
	private StringBuilder ciphertext;
	private int[] frequencies;
	//private char[] lettersByFrequency = { 'e', 't', 'a', 'o', 'i', 'n', 's', 'h', 'r', 'd', 'l', 'c', 'u', 'm', 'w',
	//		'f', 'g', 'y', 'p', 'b', 'v', 'k', 'j', 'x', 'q', 'z' };
	private char[] lettersByFrequency = {'e', 't', 'a', 'o', 'i'};
	private ArrayList<Character> mostCommonLetters;
	private ArrayList<Character> currentPermutation;
	private List<ArrayList<Character>> allPermutations;
	
	private String[] oneLetterWords = { "a", "i" };
	private String mask;
	
	private Dictionary dictionary;
	private String[] cipherwords;
	private StringBuilder[] plainwords;
	private HashMap<Character, Character> keyMap;


	public Decrypter(String ciphertext) {
		this.ciphertext = new StringBuilder(ciphertext.toLowerCase());
		this.dictionary = new Dictionary();
		this.cipherwords = ciphertext.split(" ");
		initializePlainwords();
	}

	public static void main(String[] args) {
		Decrypter decrypter = new Decrypter("Rbo rpktigo vcrb bwucja wj kloj hcjd km sktpqo cq rbwr loklgo vcgg cjqcqr kj skhcja wgkja wjd rpycja rk ltr rbcjaq cj cr Roppy Lpwrsborr");
		decrypter.initializeFrequencyTable();
		decrypter.getFrequencies();
		decrypter.displayFrequencies();
		decrypter.solve(false, 0);
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

	private boolean solve(boolean finished, int permutation){
		if(permutation >= 5)
			//when the recursion failed completely
			return false;
		//currentPermutation = allPermutations.get(permutation);
		addPermutationToKey();
		applyKey();
		if(!solveOneLetterWords(false))
			//if the recursion failed with the current permutation try the next
			solve(false, permutation+1);
		else
			return true;

		//when something went really, really wrong
		return false;
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
						/**
						 * try the one letter words one by one
						 * add this combination to key
						 * check whether that worked by calling the "the" function
						 * if not remove mapping from key list
						 */
					}
				}
				else{
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * Try to set at any three letter word with t_e the blank to an h
	 * Map this Mapping to the key
	 * check whether this worked by calling the function nLetterWords
	 * if it workes, return true
	 * else return false and go back to oneLetterWords() function
	 * 
	 * @param finished
	 * @return
	 */
	private boolean solveThe(boolean finished){
		return false;
	}
	
	/**
	 * get for every not finished words, which still contains " " the regular expression
	 * then get all the possible matches from the dictionary
	 * if it is the last match, this word must be the correct solution
	 * if not try to use one by one and add the new mappings to the key
	 * try the rest of the words
	 * if it works return true
	 * if not try the next word
	 * 
	 * @param finished
	 * @return boolean
	 */
	private boolean solveNLetterWords(boolean finished){
		
		return false;
	}

	/**
	 * Initializes the fields of the array that will count the frequencies for all letters
	 */
	private void initializeFrequencyTable() {
		frequencies = new int[26];
		for (int i = 0; i < frequencies.length; i++) {
			frequencies[i] = 0;
		}
	}

	/**
	 * Calculates the frequencies
	 */
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

	/** 
	 * Rewritten to only get maximum the five most common letters and prepare them for permutation.
	 */
	private void getFrequencyMapping() {
		keyMap = new HashMap<Character, Character>();
		mostCommonLetters= new ArrayList<Character>();
		for (int i = 0; i < 5; i++) {
			int currentMaxIndex = getNextMaxFromFrequencyTable();
			if (currentMaxIndex == -1)
				return;
			mostCommonLetters.add((char)(currentMaxIndex + 97));
		}
	}
	
	/**
	 * ToDo: add functionality to set the current permutation mapped to the plaintext letters as key
	 */
	private void addPermutationToKey() {
		
	}
	/** ToDo: add functionality to get all permutations of "mostCommonLetters".
	 * Add them list by list to "allPermutations".
	 */
	private void initializePermutation(){
		
	}

	/**
	 * Checks for the next maximum value and sets it to 0 to get the five most common letters in the ciphertext
	 * 
	 * @return maxIndex
	 */
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