import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Decrypter {

	private StringBuilder ciphertext;
	private int[] frequencies;
	private char[] lettersByFrequency = { 'e', 't', 'a', 'o', 'i' };
	private HashSet<Character> alphabet = new HashSet<Character>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));
	private ArrayList<Character> mostCommonLetters;
	private ArrayList<Character> currentPermutation;
	private char[] permutations;
	private HashMap<Integer, ArrayList<Character>> allPermutations = new HashMap<Integer, ArrayList<Character>>();

	private int permutationCounter = 0;

	private char currentCipherH;
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
		Decrypter decrypter = new Decrypter("GIUIFG CEI IPRC TPNN DU P QPRCNI");
		decrypter.initializeFrequencyTable();
		decrypter.getFrequencies();
		decrypter.displayFrequencies();
		decrypter.getFrequencyMapping();
		decrypter.displayKey();

		if (decrypter.solveWithPermutationAmount()) {
			decrypter.displayPlaintext();
		} else
			System.out.println("Didn't work");
	}

	/**
	 * Initalizes the plainwords array
	 */
	private void initializePlainwords() {
		plainwords = new StringBuilder[cipherwords.length];

		for (int i = 0; i < plainwords.length; i++) {
			plainwords[i] = new StringBuilder();
			for (int j = 0; j < cipherwords[i].length(); j++) {
				plainwords[i].append(" ");
			}
		}
	}

	/**
	 * Solves the ciphertext by trying all permutations. The amount of elements
	 * in the permutation decreases after every unsuccessful run. For every
	 * permutation the method <code>solve</code> gets called.
	 * 
	 * If non of the permutations worked the method tries once without any
	 * predefined permutations by calling the method
	 * <code>solveWithoutPermutation</code>.
	 * 
	 * @return If the method ran successfully it returns <code>true</code> else
	 *         <code>false</code>.
	 */
	private boolean solveWithPermutationAmount() {
		for (int i = 5; i >= 0; i--) {
			if (i > 0) {
				permutationCounter = 0;
				System.out.println("Trying for permutation of length " + i);
				char[] currentPermutation = new char[i];
				for (int j = 0; j < i; j++) {
					currentPermutation[j] = permutations[j];
				}
				allPermutations.clear();
				initializePermutation(currentPermutation.length, currentPermutation);
				if (solve(false, 0)) {
					return true;
				} else {
					/*
					 * remove no longer used letters because of permutation
					 * decrease from key
					 */
					removeKey(permutations[i - 1]);
				}
			} else {
				if (solveWithoutPermutation(false))
					return true;
				else
					return false;
			}
		}
		return false;
	}

	/**
	 * This method solves the cipher by applying every permutation to the key.
	 * Afterwards it runs the method <code>solveOneLetterWords</code>.
	 * If this doesn't work, it tries the next permutation.
	 * 
	 * @param finished
	 * @param permutation
	 * @return returns <code>true</code> if the permutation worked else <code>false</code>.
	 */
	private boolean solve(boolean finished, int permutation) {
		System.out.println("" + (permutation + 1) + " for current length: " + allPermutations.size());
		if (permutation >= allPermutations.size())
			// when the recursion failed completely
			return false;
		currentPermutation = allPermutations.get(permutation);
		System.out.println("Trying: " + currentPermutation.toString());
		addPermutationToKey();
		applyKey();
		displayKey();
		if (!solveOneLetterWords(false))
			// if the recursion failed with the current permutation try the next
			solve(false, permutation + 1);
		else
			return true;
		return false;
	}

	/**
	 * This method solves the cipher without a permutation and directly applys the method <code>solveNLetterWords</code>.
	 * If this doesn't work the ciphertext could not be broken.
	 * 
	 * @param finished
	 * @return returns <code>true</code> if the ciphertext was solved else <code>false</code>.
	 */
	private boolean solveWithoutPermutation(boolean finished) {
		System.out.println("Trying without permutation ");
		ArrayList<String> remainingCipherWords = getRemainingCipherWords();
		ArrayList<StringBuilder> remainingPlainWords = getRemainingPlainWords();
		if (solveNLetterWords(false, remainingCipherWords, remainingPlainWords)) {
			return true;
		} else
			return false;

	}

	/**
	 * This method is used to search for one letter words and apply a matching key.
	 * If both words are already used, the method gets skipped.
	 * @param finished
	 * @return
	 */
	private boolean solveOneLetterWords(boolean finished) {
		if (finished)
			return true;
		else {
			for (int k = 0; k < cipherwords.length; k++) {
				char cipherI = '0';
				char cipherA = '0';
				if (keyMap.containsValue('a') == false) {
					for (int i = 0; i < cipherwords.length; i++) {
						if (cipherwords[i].length() == 1) {
							if (plainwords[i].toString().contains(" ")) {
								addKey(cipherwords[i].charAt(0), 'a');
								cipherA = cipherwords[i].charAt(0);
								applyKey();
							}
						}
					}
				}
				if (keyMap.containsValue('i') == false) {
					for (int i = 0; i < cipherwords.length; i++) {
						if (cipherwords[i].length() == 1) {
							if (plainwords[i].toString().contains(" ")) {
								addKey(cipherwords[i].charAt(0), 'i');
								cipherI = cipherwords[i].charAt(0);
								applyKey();
							}
						}
					}
				}
				if (cipherA != '0' || cipherI != '0') {
					if (solveThe(false)) {
						return true;
					} else {
						if (cipherA != '0')
							removeKey(cipherA);
						if (cipherI != '0')
							removeKey(cipherA);
					}
				}
			}
			if (solveThe(false))
				return true;
			else
				return false;
		}
	}

	/**
	 * Try to set at any three letter word with t_e the blank to an h Map this
	 * Mapping to the key check whether this worked by calling the function
	 * nLetterWords if it works, return true else return false and go back to
	 * oneLetterWords() function
	 * 
	 * @param finished
	 * @return
	 */
	private boolean solveThe(boolean finished) {
		for (int i = 0; i < plainwords.length; i++) {
			if (plainwords[i].toString().matches("[t][ ][e]")) {
				currentCipherH = cipherwords[i].charAt(1);
				addKey(currentCipherH, 'h');
				applyKey();
				ArrayList<String> remainingCipherWords = getRemainingCipherWords();
				ArrayList<StringBuilder> remainingPlainWords = getRemainingPlainWords();
				if (!solveNLetterWords(false, remainingCipherWords, remainingPlainWords)) {
					removeKey(currentCipherH);
				} else
					return true;
			}
		}
		ArrayList<String> remainingCipherWords = getRemainingCipherWords();
		ArrayList<StringBuilder> remainingPlainWords = getRemainingPlainWords();
		if (!solveNLetterWords(false, remainingCipherWords, remainingPlainWords))
			return false;
		else
			return true;
	}

	/**
	 * get for every not finished words, which still contains " " the regular
	 * expression then get all the possible matches from the dictionary if it is
	 * the last match, this word must be the correct solution if not try to use
	 * one by one and add the new mappings to the key try the rest of the words
	 * if it works return true if not try the next word
	 * 
	 * @param finished
	 * @return boolean
	 */
	private boolean solveNLetterWords(boolean finished, ArrayList<String> remainingCipherWords,
			ArrayList<StringBuilder> remainingPlainWords) {
		if (remainingPlainWords.size() == 0) {
			return true;
		} else {
			StringBuilder currentWord = remainingPlainWords.get(0);
			String regEx = setUpRegEx(currentWord.toString());
			ArrayList<String> result = dictionary.search(regEx);
			ArrayList<Character> currentAddedKeys = new ArrayList<Character>();
			for (String resultWord : result) {
				for (int i = 0; i < currentWord.length(); i++) {
					if (currentWord.charAt(i) == ' ') {
						char key = remainingCipherWords.get(0).charAt(i);
						if (!keyMap.containsKey(key)) {
							addKey(key, resultWord.charAt(i));
							currentAddedKeys.add(key);
							applyKey();
						}
					}
				}
				ArrayList<String> newRemainingCipherWords = getRemainingCipherWords();
				ArrayList<StringBuilder> newRemainingPlainWords = getRemainingPlainWords();
				if (solveNLetterWords(false, newRemainingCipherWords, newRemainingPlainWords))
					return true;
				else {
					for (int i = 0; i < currentAddedKeys.size(); i++) {
						removeKey(currentAddedKeys.get(i));
					}
				}
			}
		}
		return false;
	}

	/**
	 * This method creates a regular expression from a string.
	 * @param word the string to be processed.
	 * @return the regular expression.
	 */
	private String setUpRegEx(String word) {
		HashSet<Character> currentAlphabet = alphabet;
		currentAlphabet.removeAll(keyMap.values());

		String letters = "";
		for (char letter : currentAlphabet) {
			letters += letter;
		}

		String regEx = "";
		for (int i = 0; i < word.length(); i++) {
			switch (word.charAt(i)) {
			case ' ':
				regEx += "\\[" + letters + "\\]";
				break;
			default:
				regEx += "\\[" + word.charAt(i) + "\\]";
			}
		}
		return regEx;
	}
	
	/**
	 * Gets the current remaining strings of the array plainwords to decrease calculations.
	 * @return Returns an array of the remaining plainwords.
	 */
	private ArrayList<StringBuilder> getRemainingPlainWords() {
		ArrayList<StringBuilder> remainingWords = new ArrayList<StringBuilder>();
		for (int i = 0; i < plainwords.length; i++) {
			if (plainwords[i].toString().contains(" ")) {
				remainingWords.add(plainwords[i]);
			}
		}
		return remainingWords;
	}

	/**
	 * Gets the current remaining strings of the array cipherwords to decrease calculations.
	 * @return Returns an array of the remaining cipherwords.
	 */
	private ArrayList<String> getRemainingCipherWords() {
		ArrayList<String> remainingWords = new ArrayList<String>();
		for (int i = 0; i < plainwords.length; i++) {
			if (plainwords[i].toString().contains(" ")) {
				remainingWords.add(cipherwords[i]);
			}
		}
		return remainingWords;
	}

	/**
	 * Add this specified key to the keyMap
	 * 
	 * @param cipherChar
	 * @param plainChar
	 */
	private void addKey(char cipherChar, char plainChar) {
		keyMap.put(cipherChar, plainChar);
	}

	/**
	 * Remove the key for the backtracking process
	 * 
	 * @param cipherChar
	 */
	private void removeKey(char cipherChar) {
		keyMap.remove(cipherChar);
		for (int i = 0; i < cipherwords.length; i++) {
			for (int j = 0; j < cipherwords[i].length(); j++) {
				if (cipherwords[i].charAt(j) == cipherChar)
					plainwords[i].setCharAt(j, ' ');
			}
		}
	}
	
	/**
	 * This method puts the keys onto the plainwords mask.
	 */
	private void applyKey() {
		for (int i = 0; i < plainwords.length; i++) {
			for (int j = 0; j < plainwords[i].length(); j++) {
				if (plainwords[i].charAt(j) == ' ') {
					if (keyMap.containsKey(cipherwords[i].charAt(j))) {
						plainwords[i].setCharAt(j, keyMap.get(cipherwords[i].charAt(j)));
					}
				}
			}
		}
	}

	/**
	 * Initializes the fields of the array that will count the frequencies for
	 * all letters
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
		// Test
		displayFrequencies();
	}

	/**
	 * Rewritten to only get maximum the five most common letters and prepare
	 * them for permutation.
	 */
	private void getFrequencyMapping() {
		keyMap = new HashMap<Character, Character>();
		mostCommonLetters = new ArrayList<Character>();
		permutations = new char[5];
		for (int i = 0; i < 5; i++) {
			int currentMaxIndex = getNextMaxFromFrequencyTable();
			if (currentMaxIndex == -1)
				return;
			mostCommonLetters.add((char) (currentMaxIndex + 97));
			// maps cipherchar as key to plainchar as value
			keyMap.put((char) (currentMaxIndex + 97), lettersByFrequency[i]);
			permutations[i] = (char) (currentMaxIndex + 97);
		}
	}
	
	/**
	 * Checks for the next maximum value and sets it to 0 to get the five most
	 * common letters in the ciphertext
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

	/**
	 * ToDo: add functionality to set the current permutation mapped to the
	 * plaintext letters as key
	 */
	private void addPermutationToKey() {
		keyMap.clear();
		for (int i = 0; i < currentPermutation.size(); i++) {
			// maps cipherchar to plainchar
			keyMap.put(currentPermutation.get(i), lettersByFrequency[i]);
		}
	}

	/**
	 * Gets all permutations of "mostCommonLetters".
	 * Adds them list by list to "allPermutations".
	 * 
	 * Source:
	 * http://www2.cs.uni-paderborn.de/cs/ag-boettcher/lehrealt/swe1-w02/
	 * loesungen/A27.java
	 */
	private void initializePermutation(int N, char[] arr) {
		if (N == 0) {
			// System.out.println(arr);
			addPermutationArrayToList(arr);
			// System.out.println(currentPermutation);

		} else {
			for (int i = 0; i < N; i++) {
				swapElements(arr, i, N - 1);
				initializePermutation(N - 1, arr);
				swapElements(arr, i, N - 1);
			}
		}
	}

	/**
	 * Used by the method <code>initializePermutation</code> to get the permutations by swapping elements.
	 * @param arr
	 * @param i
	 * @param N
	 */
	private void swapElements(char[] arr, int i, int N) {
		char hilf = arr[i];
		arr[i] = arr[N];
		arr[N] = hilf;
	}

	/**
	 * Used by the method <code>initializePermutation</code> to add the calculated permutation to the list.
	 * @param arr
	 */
	private void addPermutationArrayToList(char[] arr) {
		ArrayList<Character> currentList = new ArrayList<Character>();
		for (int i = 0; i < arr.length; i++) {
			currentList.add(arr[i]);
		}
		allPermutations.put(permutationCounter, currentList);
		permutationCounter++;
	}

	/**
	 * Displays all calculated Permutations.
	 */
	private void displayAllPermutations() {
		for (ArrayList<Character> list : allPermutations.values()) {
			for (char value : list) {
				System.out.print(value + " ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Displays all Frequencies.
	 */
	private void displayFrequencies() {
		System.out.println("Frequencies");
		for (int i = 0; i < frequencies.length; i++) {
			System.out.println("" + (char) (i + 97) + " -> " + frequencies[i]);
		}
		System.out.println();
	}

	/**
	 * Displays the current Key.
	 */
	private void displayKey() {
		System.out.println("Current Key");
		for (char key : keyMap.keySet()) {
			System.out.println("" + key + " -> " + keyMap.get(key));
		}
		System.out.println();
	}
	
	/**
	 * Shows the plaintext
	 */
	private void displayPlaintext() {
		for (int i = 0; i < plainwords.length; i++) {
			System.out.print(plainwords[i] + " ");
		}
		System.out.println();
	}
}