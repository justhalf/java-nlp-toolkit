package justhalf.nlp.sentencesplitter;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import edu.stanford.nlp.process.TokenizerFactory;

public class StanfordSentenceSplitter implements SentenceSplitter {
	
	private TokenizerFactory<CoreLabel> tokenizerFactory;

	public StanfordSentenceSplitter() {
		tokenizerFactory = PTBTokenizerFactory.newCoreLabelTokenizerFactory("ptb3Escaping=false,invertible=true");
	}

	@Override
	public String[] splitToString(String input) {
		List<String> sentenceList = new ArrayList<String>();
		for(List<CoreLabel> sentenceTokenized: splitAndTokenize(input)){
			StringBuilder sentence = new StringBuilder();
			int lastIndex = -1;
			for(CoreLabel word: sentenceTokenized){
				int curIndex = word.beginPosition();
				if(curIndex < lastIndex) continue;
				if(sentence.length() > 0){
					sentence.append(input.substring(lastIndex, curIndex));
				}
				sentence.append(word.word());
				lastIndex = word.endPosition();
			}
			sentenceList.add(sentence.toString());
		}
		return sentenceList.toArray(new String[sentenceList.size()]);
	}
	
	@Override
	public List<CoreLabel> split(String input){
		List<CoreLabel> sentenceList = new ArrayList<CoreLabel>();
		for(List<CoreLabel> sentenceTokenized: splitAndTokenize(input)){
			StringBuilder sentenceText = new StringBuilder();
			int sentenceBegin = sentenceTokenized.get(0).beginPosition();
			String before = sentenceTokenized.get(0).before();
			String after = sentenceTokenized.get(sentenceTokenized.size()-1).after();
			int lastIndex = -1;
			for(CoreLabel word: sentenceTokenized){
				int curIndex = word.beginPosition();
				if(curIndex < lastIndex) continue;
				if(sentenceText.length() > 0){
					sentenceText.append(input.substring(lastIndex, curIndex));
				}
				sentenceText.append(word.word());
				lastIndex = word.endPosition();
			}
			CoreLabel sentence = new CoreLabel();
			sentence.setBefore(before);
			sentence.setAfter(after);
			sentence.setBeginPosition(sentenceBegin);
			sentence.setEndPosition(lastIndex);
			sentence.setOriginalText(sentenceText.toString());
			sentence.setWord(sentenceText.toString());
			sentence.setValue(sentenceText.toString());
			sentenceList.add(sentence);
		}
		return sentenceList;
	}
	
	public List<List<CoreLabel>> splitAndTokenize(String input){
		DocumentPreprocessor splitter = new DocumentPreprocessor(new StringReader(input));
		splitter.setTokenizerFactory(tokenizerFactory);
		List<List<CoreLabel>> sentenceList = new ArrayList<List<CoreLabel>>();
		for(List<HasWord> sentenceTokenized: splitter){
			List<CoreLabel> sentence = new ArrayList<CoreLabel>();
			for(HasWord word: sentenceTokenized){
				sentence.add((CoreLabel)word);
			}
			sentenceList.add(sentence);
		}
		return sentenceList;
	}
	
	@Override
	public boolean isThreadSafe(){
		return true;
	}
}
