package justhalf.nlp.sentenceparser;

import java.util.List;

import edu.stanford.nlp.trees.Tree;

public interface SentenceParser {
	public Tree parse(String sentence);
	public Tree parse(List<String> sentence);
}
