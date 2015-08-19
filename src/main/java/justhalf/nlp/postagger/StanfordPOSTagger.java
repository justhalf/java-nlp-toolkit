package justhalf.nlp.postagger;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;

public class StanfordPOSTagger implements POSTagger {
	
	public static final String MODEL_DEFAULT = MaxentTagger.DEFAULT_JAR_PATH;
	
	private MaxentTagger tagger;
	
	public StanfordPOSTagger(){
		tagger = new MaxentTagger(MODEL_DEFAULT);
	}

	@Override
	public List<String> tag(String words) {
		String[] wordAndPOS = tagger.tagString(words).split(" ");
		List<String> result = new ArrayList<String>();
		for(String wordPos: wordAndPOS){
			result.add(wordPos.split("_")[1]);
		}
		return result;
	}

	@Override
	public List<String> tag(List<String> words) {
		String[] wordAndPOS = tagger.tagTokenizedString(StringUtils.join(words, " ")).split(" ");
		List<String> result = new ArrayList<String>();
		for(String wordPos: wordAndPOS){
			result.add(wordPos.split("_")[1]);
		}
		return result;
	}

}
