package justhalf.nlp.sentencesplitter;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;

public interface SentenceSplitter {
	public String[] splitToString(String input);
	public List<CoreLabel> split(String input);
	public List<List<CoreLabel>> splitAndTokenize(String input);
}
