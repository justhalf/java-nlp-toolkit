package justhalf.nlp.sentenceparser;

import java.util.List;

import edu.stanford.nlp.trees.Tree;
import justhalf.nlp.NLPInterface;

public interface SentenceParser extends NLPInterface{
	/**
	 * Parse a sentence, returning a Tree
	 * @param sentence
	 * @return
	 */
	public Tree parse(String sentence);
	
	/**
	 * Parse a tokenized sentence (list of words), returning a Tree
	 * @param sentence
	 * @return
	 */
	public Tree parse(List<String> sentence);
}
