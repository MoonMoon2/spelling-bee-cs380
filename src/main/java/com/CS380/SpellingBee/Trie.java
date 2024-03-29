package com.CS380.SpellingBee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class Trie implements TrieMethods {

	/**
	 * holds the value equal to depth of lowest leafNode.
	 */
	int maxDepth;



	/**
	 * Root node of the trie, has a layer of 0 and a parent of null
	 */
	TrieNode root;

	/**
	 * Variable to keep track to the number of words contained within the Trie
	 */
	int numWordsInTrie;

	/**
	 * Constructor for a Trie
	 * 
	 * Creates a new Trie with a blank root note with layer = 0 using the corresponding TrieNode constructor
	 * 
	 * TODO: consider adding a layer of abstraction to the TrieNode() constructor using a method called 'constructRootNode' and making TrieNode() private
	 */
	public Trie () {

		root = new TrieNode();

	}


	/**
	 * Adds a word to the trie.
	 * <p>
	 * Creates nodes where nodes do not exist
	 * </p>
	 */
	@Override
	public boolean addWord(String input) {

		/*
		 * Ensures input is a valid word for the game and is not contained before adding
		 */
		if (!Magic.isValidWordForGame(input) || containsWord(input) != null) {
			return false;
		}

		/*
		 * takes in String and turns into character array
		 * iterates through creating nodes where ones do not already exists 
		 * sets final node.word to true
		 */
		TrieNode current = this.root;

		input = input.toLowerCase();

		char[] working= input.toCharArray();

		for (int i=0; i<working.length; i++) {

			if (current.hasChild(working[i])) {

				current = current.getChild(working[i]);

			} else {

				current = new TrieNode(working[i], false, current); 	// Child is automatically set in constructor as the child of current

			}
		}

		current.setWord(true);		// end of word!

		numWordsInTrie++;
		
		if (current.getLayer() > this.maxDepth) {
			setMaxDepth(current.getLayer());
		}

		return current.isWord();
	}

	/**
	 * Add words from a list or a set
	 */
	@Override
	public void addWords(String... wordsIn) {
		if (wordsIn == null) {
			return;
		}
		for (String word: wordsIn) {
			addWord(word);
		}

	}

	/**
	 * Checks rWord using containsWord
	 * if node exists sets node word boolean to false and returns true
	 * else returns false. 
	 *
	 */
	@Override
	public boolean removeWord(String rWord) {

		if (!Magic.isValidWordForGame(rWord)) {			// ensure alphanumeric and non-empty/null and within the length limits of the game
			return false;
		}

		TrieNode endOfWord = this.containsWord(rWord);	// use containedWord to get the lowest node in the word if it exists


		if (endOfWord == null) return false; 		// if the word is not in the set, return
		else endOfWord.setWord(false);

		if (endOfWord.getLayer() == this.getMaxDepth()) {
			fixupMaxDepth();
		}
		
		numWordsInTrie--;
		return true;


	}

	/**
	 * Assumes the input stream contains words separated by a newline character
	 * @param inStream ReaderStream
	 * @throws IOException 
	 */
	@Override
	public void addWordsFromStream(Reader inStream) throws IOException {

		BufferedReader daReader = new BufferedReader(inStream);

		String word = daReader.readLine();		// this makes the assumption that the input in the stream is seperated by newline characters

		while (word != null) {
			addWord(word);
			word = daReader.readLine();
		}
	}

	/**
	 * Return the current number of words stored in the trie
	 */
	@Override
	public int numWordsContained() {
		return numWordsInTrie;
	}

	/**
	 * Returns the node if the word is in the trie, returns null if it is not
	 */
	@Override
	public TrieNode containsWord(String cWord) {
		if (numWordsInTrie <= 0 || !Magic.isValidWordForGame(cWord)) {
			return null;
		}

		TrieNode current = root;
		int i = 0;
		char[] wordArray = cWord.toCharArray();

		while (i < wordArray.length && current.hasChild(wordArray[i])) {
			current = current.getChild(wordArray[i]);
			i++;
		}

		if (i < wordArray.length) {
			return null;
		} else if (current.isWord()) {
			return current;
		} else { return null; }

	}

	/**
	 * returns maxDepth
	 */
	public int getMaxDepth() {
		fixupMaxDepth();
		return maxDepth;
	}

	/**
	 * sets maxDepth as param
	 * @param maxDepth
	 */
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	/**
	 * Fixup the maxdepth using a TrieVisitor to gather all of the words of each length
	 */
	public void fixupMaxDepth() {
		
		TrieVisitor contents = new TrieVisitor(this);
		String[] content = contents.wordsOfLength(maxDepth);
		
		if (content != null) {		// return if there's still a word at the max depth
			return;
		}
		
		// TODO: Update App.MINIMUM_WORD_LENGTH to a better location
		
		for (int i = this.maxDepth-1; i >= App.MINIMUM_WORD_LENGTH; i++) {		// loop until we get too small
			
			content = contents.wordsOfLength(i);
			
			if (content != null) {			// if the new content has words in it, set the max depth to i
				this.setMaxDepth(i);
				return;						// we're good!
			}
		}
		
		this.maxDepth = 0;					// if we never find any words, there is no maxdepth
	}


}