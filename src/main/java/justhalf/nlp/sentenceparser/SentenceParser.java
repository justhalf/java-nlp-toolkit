package justhalf.nlp.sentenceparser;

import java.util.List;

import edu.stanford.nlp.trees.Tree;
import justhalf.nlp.NLPInterface;

/**
 * An interface for sentence parser
 */
public interface SentenceParser extends NLPInterface{
	/**
	 * Parse a sentence, returning a Tree
	 * @param sentence
	 * 		The sentence to be parsed.
	 * 		The sentence is not assumed to be tokenized.
	 * @return
	 * 		The parsed sentence
	 */
	public Tree parse(String sentence);
	
	/**
	 * Parse a tokenized sentence (list of words), returning a Tree
	 * @param sentence
	 * 		The sentence to be parsed as a list of tokens
	 * @return
	 * 		The parsed sentence
	 */
	public Tree parse(List<String> sentence);
}
