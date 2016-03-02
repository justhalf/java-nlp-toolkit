package justhalf.nlp.sentencesplitter;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.tokenization.EnglishTokenizer;
import edu.stanford.nlp.ling.CoreLabel;

public class NLP4JSentenceSplitter implements SentenceSplitter {
	
	public edu.emory.mathcs.nlp.tokenization.Tokenizer nlp4jTokenizer;

	public NLP4JSentenceSplitter() {
		nlp4jTokenizer = new EnglishTokenizer();
	}

	@Override
	public boolean isThreadSafe() {
		return true;
	}

	@Override
	public String[] splitToString(String input) {
		List<CoreLabel> sentences = split(input);
		String[] result = new String[sentences.size()];
		for(int i=0; i<result.length; i++){
			result[i] = sentences.get(i).word();
		}
		return result;
	}

	@Override
	public List<CoreLabel> split(String input) {
		List<NLPNode[]> sentences = nlp4jTokenizer.segmentize(input);
		List<CoreLabel> result = new ArrayList<CoreLabel>();
		int lastEnd = 0;
		String between = "";
		for(NLPNode[] tokens: sentences){
			CoreLabel sentence = new CoreLabel();
			int start = tokens[0].getStartOffset();
			int end = tokens[tokens.length-1].getEndOffset();
			between = input.substring(lastEnd, start);
			if(result.size() > 0){
				result.get(result.size()-1).setAfter(between);
			}
			sentence.setBefore(between);
			sentence.setBeginPosition(start);
			sentence.setEndPosition(end);
			String sentenceText = input.substring(start, end);
			sentence.setOriginalText(sentenceText);
			sentence.setWord(sentenceText);
			sentence.setValue(sentenceText);
			result.add(sentence);
		}
		between = input.substring(lastEnd);
		if(result.size() > 0){
			result.get(result.size()-1).setAfter(between);
		}
		return result;
	}

}
