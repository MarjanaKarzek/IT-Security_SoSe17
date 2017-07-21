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
	private HashMap<Integer, ArrayList<String>> possibleMatches = new HashMap<Integer, ArrayList<String>>();

	private int permutationCounter = 0;

	// private String[] oneLetterWords = { "a", "i" };
	/**
	 * no one letter word mapping needed, because a and i are in the permutation
	 */

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
		//Decrypter decrypter = new Decrypter("Rbo rpktigo vcrb bwucja wj kloj hcjd km sktpqo cq rbwr loklgo vcgg cjqcqr kj skhcja wgkja wjd rpycja rk ltr rbcjaq cj cr Roppy Lpwrsborr");
		Decrypter decrypter = new Decrypter("SIAA ZQ LKBA VA ZOA RFPBLUAOAR");
		decrypter.initializeFrequencyTable();
		decrypter.getFrequencies();
		decrypter.displayFrequencies();
		decrypter.getFrequencyMapping();
		decrypter.displayFrequencyMapping();
		decrypter.initializePermutation(decrypter.getPermutations().length, decrypter.getPermutations());
		decrypter.displayAllPermutations();

		if (decrypter.solve(false, 0)) {
			decrypter.displayPlaintext();
		} else
			System.out.println("Didn't work");
	}

	private void displayPlaintext() {
		for (int i = 0; i < plainwords.length; i++) {
			System.out.print(plainwords[i] + " ");
		}
		System.out.println();
	}

	private char[] getPermutations() {
		return permutations;
	}

	private void initializePlainwords() {
		plainwords = new StringBuilder[cipherwords.length];

		for (int i = 0; i < plainwords.length; i++) {
			plainwords[i] = new StringBuilder();
			for (int j = 0; j < cipherwords[i].length(); j++) {
				plainwords[i].append(" ");
			}
		}
	}

	private boolean solve(boolean finished, int permutation) {
		System.out.println(permutation);
		if (permutation >= permutations.length)
			// when the recursion failed completely
			return false;
		currentPermutation = allPermutations.get(permutation);
		addPermutationToKey();
		applyKey();
		if (!solveThe(false))
			// if the recursion failed with the current permutation try the next
			solve(false, permutation + 1);
		else
			return true;
		// when something went really, really wrong
		return false;
	}

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

	/*
	 * private boolean solveOneLetterWords(boolean finished) { if (finished)
	 * return true; else { for (int i = 0; i < cipherwords.length; i++) { if
	 * (cipherwords[i].length() == 1) { if (plainwords[i].toString().contains(
	 * " ")) {
	 * 
	 * try the one letter words one by one add this combination to key check
	 * whether that worked by calling the "the" function if not remove mapping
	 * from key list
	 * 
	 * } } else { return true; } } return false; } }
	 */

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
				// if (plainwords[i].length() == 3) {
				// if (plainwords[i].charAt(0) == 't' && plainwords[i].charAt(2)
				// == 'e') {
				// if (plainwords[i].charAt(2) == ' ') {
				currentCipherH = cipherwords[i].charAt(1);
				// if an h was found
				addKey(currentCipherH, 'h');
				applyKey();
				ArrayList<String> remainingCipherWords = getRemainingCipherWords();
				ArrayList<StringBuilder> remainingPlainWords = getRemainingPlainWords();
				if (!solveNLetterWords(false, remainingCipherWords, remainingPlainWords)) {
					removeKey(currentCipherH);
				} else
					return true;
			}
			// }
			// }
		}
		ArrayList<String> remainingCipherWords = getRemainingCipherWords();
		ArrayList<StringBuilder> remainingPlainWords = getRemainingPlainWords();
		if (!solveNLetterWords(false, remainingCipherWords, remainingPlainWords))
			return false;
		else
			return true;
	}

	private ArrayList<StringBuilder> getRemainingPlainWords() {
		ArrayList<StringBuilder> remainingWords = new ArrayList<StringBuilder>();
		for (int i = 0; i < plainwords.length; i++) {
			if (plainwords[i].toString().contains(" ")) {
				remainingWords.add(plainwords[i]);
			}
		}
		return remainingWords;
	}

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
		// if everything is very, very sad and didn't work
		return false;
	}

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
				regEx += "[" + letters + "]";
				break;
			default:
				regEx += "[" + word.charAt(i) + "]";
			}
		}
		return regEx;
	}

	private void addKey(char cipherChar, char plainChar) {
		keyMap.put(cipherChar, plainChar);
	}

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
			// maps cipherchar to plainchar
			keyMap.put((char) (currentMaxIndex + 97), lettersByFrequency[i]);
			permutations[i] = (char) (currentMaxIndex + 97);
		}
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
	 * ToDo: add functionality to get all permutations of "mostCommonLetters".
	 * Add them list by list to "allPermutations".
	 * 
	 * Source:
	 * http://www2.cs.uni-paderborn.de/cs/ag-boettcher/lehrealt/swe1-w02/
	 * loesungen/A27.java
	 */
	private void initializePermutation(int N, char[] arr) {
		if (N == 0)
			addPermutationArrayToList(arr);
		else {
			for (int i = 0; i < N; i++) {
				swapElements(arr, i, N - 1);
				initializePermutation(N - 1, arr);
				swapElements(arr, i, N - 1);
			}
		}
	}

	private void swapElements(char[] arr, int i, int N) {
		char hilf = arr[i];
		arr[i] = arr[N];
		arr[N] = hilf;
	}

	private void addPermutationArrayToList(char[] arr) {
		ArrayList<Character> currentList = new ArrayList<Character>();
		for (int i = 0; i < arr.length; i++) {
			currentList.add(arr[i]);
		}
		allPermutations.put(permutationCounter, currentList);
		permutationCounter++;
	}

	private void displayAllPermutations() {
		// Set<ArrayList<Character>> values = (Set<ArrayList<Character>>)
		// allPermutations.values();
		for (ArrayList<Character> list : allPermutations.values()) {
			for (char value : list) {
				System.out.print(value + " ");
			}
			System.out.println();
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
}