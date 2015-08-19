package justhalf.nlp.postagger;

import java.util.List;

public interface POSTagger {
	/**
	 * Tokenize and tag the sentence with POS tags
	 * @param sentence
	 * @return
	 */
	public List<String> tag(String sentence);
	
	/**
	 * Tag the sequence of words with POS tags
	 * @param sentence
	 * @return
	 */
	public List<String> tag(List<String> sentence);
}
