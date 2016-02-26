package justhalf.nlp.tokenizer;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;

public class WhitespaceTokenizer implements Tokenizer {

	@Override
	public String[] tokenizeToString(String sentence) {
		return sentence.split("[ \t\r\n]+");
	}

	@Override
	public List<CoreLabel> tokenize(String sentence) {
		List<CoreLabel> output = new ArrayList<CoreLabel>();
		String[] tokens = tokenizeToString(sentence);
		for(String token: tokens){
			output.add(new CoreLabel(new Word(token)));
		}
		return output;
	}

}
