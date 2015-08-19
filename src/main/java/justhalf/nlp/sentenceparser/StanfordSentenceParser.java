package justhalf.nlp.sentenceparser;

import java.util.List;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;

public class StanfordSentenceParser implements SentenceParser {
	
	public static final String MODEL_LEXICAL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	
	private LexicalizedParser parser;
	
	public StanfordSentenceParser(){
		parser = LexicalizedParser.loadModel(MODEL_LEXICAL);
	}

	public Tree parse(String sentence) {
		return parser.parse(sentence);
	}

	public Tree parse(List<String> sentence) {
		return parser.parseStrings(sentence);
	}

}
