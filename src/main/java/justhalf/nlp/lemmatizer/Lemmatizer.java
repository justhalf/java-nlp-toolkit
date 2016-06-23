package justhalf.nlp.lemmatizer;

import justhalf.nlp.NLPInterface;

/**
 * An interface for lemmatizers
 */
public interface Lemmatizer extends NLPInterface{
	/**
	 * Lemmatize a word
	 * @param word
	 * 		The word to be lemmatized
	 * @return
	 * 		The most likely lemma of the word
	 */
	public String lemmatize(String word);
	
	/**
	 * Lemmatize a word, given its POS tag<br>
	 * 
	 * A POS tag sometimes can be useful in choosing the correct lemma of a word.<br>
	 * 
	 * For example, the word "lay" may be a root word "lay", meaning "to put or place",
	 * or it can be the past form of "lie", meaning "to rest or recline".
	 * A POS tag of "VBD" (in Penn tagset) will indicate that the lemma should be "lie",
	 * while a POS tag of "VB" or "VBP" will indicate that the lemma should be "lay".
	 * @param word
	 * 		The word to be lemmatized
	 * @param pos
	 *		The POS tag of the word 
	 * @return
	 *		The most likely lemma of the word		
	 */
	public String lemmatize(String word, String pos);
}
