package justhalf.nlp.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreLabel;

public class RegexTokenizer implements Tokenizer {
	
	public static final String DEFAULT_REGEX =
			"[ \\t\\r\\n]+|"
			+ "((?<=[\\w\\p{IsL}])(?=[^\\w\\p{IsL}]))|" // Previous char is letter, next is non-letter
			+ "((?<=[^\\w\\p{IsL}])(?=[\\w\\p{IsL}]))"; // Previous char is non-letter, next is letter
	
	public Pattern pattern;

	public RegexTokenizer() {
		pattern = Pattern.compile(DEFAULT_REGEX);
	}
	
	public RegexTokenizer(String regex){
		pattern = Pattern.compile(regex);
	}

	@Override
	public String[] tokenizeToString(String sentence) {
		List<CoreLabel> words = tokenize(sentence);
		String[] result = new String[words.size()];
		for(int i=0; i<words.size(); i++){
			result[i] = words.get(i).word();
		}
		return result;
	}

	@Override
	public List<CoreLabel> tokenize(String sentence) {
		List<CoreLabel> result = new ArrayList<CoreLabel>();
		Matcher matcher = pattern.matcher(sentence);
		int lastEndPos = 0;
		String lastBetweenText = "";
		while(matcher.find()){
			int start = matcher.start();
			int end = matcher.end();
			if(start == lastEndPos && end == lastEndPos){
				continue;
			}
			String wordText = sentence.substring(lastEndPos, start);
			String betweenText = sentence.substring(start, end);
			CoreLabel word = new CoreLabel();
			word.setBefore(lastBetweenText);
			word.setBeginPosition(lastEndPos);
			word.setEndPosition(start);
			word.setValue(wordText);
			word.setWord(wordText);
			word.setOriginalText(wordText);
			word.setAfter(betweenText);
			lastEndPos = end;
			lastBetweenText = betweenText;
			result.add(word);
		}
		if(lastEndPos != sentence.length()){
			int start = sentence.length();
			int end = sentence.length();
			String wordText = sentence.substring(lastEndPos, start);
			String betweenText = sentence.substring(start, end);
			CoreLabel word = new CoreLabel();
			word.setBefore(lastBetweenText);
			word.setBeginPosition(lastEndPos);
			word.setEndPosition(start);
			word.setValue(wordText);
			word.setWord(wordText);
			word.setOriginalText(wordText);
			word.setAfter(betweenText);
			lastEndPos = end;
			lastBetweenText = betweenText;
			result.add(word);
		}
		return result;
	}
	
	@Override
	public boolean isThreadSafe(){
		return true;
	}

}
