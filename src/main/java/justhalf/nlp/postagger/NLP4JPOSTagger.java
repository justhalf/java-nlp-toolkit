package justhalf.nlp.postagger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.template.util.NLPUtils;
import edu.emory.mathcs.nlp.decode.NLPDecoder;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * An implementation of {@link POSTagger} using POS tagger from NLP4J
 */
public class NLP4JPOSTagger implements POSTagger {
	
	public NLPDecoder nlp4jPOSTagger;

	public NLP4JPOSTagger() {
		try {
			nlp4jPOSTagger = new NLPDecoder(IOUtils.getInputStreamFromURLOrClasspathOrFileSystem("config-decode-en-pos.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isThreadSafe() {
		return true;
	}

	@Override
	public List<String> tag(String sentence) {
		NLPNode[] nodes = nlp4jPOSTagger.decode(sentence);
		return nodeToString(nodes);
	}

	@Override
	public List<String> tag(List<String> sentence) {
		List<NLPNode> initNodes = new ArrayList<NLPNode>();
		Iterator<String> sentIter = sentence.iterator();
		int lastEnd = -1;
		while(sentIter.hasNext()){
			String token = sentIter.next();
			int start = lastEnd + 1;
			int end = start + token.length();
			NLPNode node = new NLPNode(start, end, token);
			initNodes.add(node);
			lastEnd = end;
		}
		NLPNode[] nodes = NLPUtils.toNodeArray(initNodes);
		nodes = nlp4jPOSTagger.decode(nodes);
		
		List<String> result = nodeToString(nodes);
		return result;
	}

	private List<String> nodeToString(NLPNode[] nodes) {
		List<String> result = new ArrayList<String>();
		for(int i=1; i<nodes.length; i++){
			NLPNode node = nodes[i];
			result.add(node.getPartOfSpeechTag());
		}
		return result;
	}

	@Override
	public List<CoreLabel> tagCoreLabels(List<CoreLabel> sentence) {
		List<NLPNode> initNodes = new ArrayList<NLPNode>();
		Iterator<CoreLabel> sentIter = sentence.iterator();
		int lastEnd = -1;
		while(sentIter.hasNext()){
			String token = sentIter.next().word();
			int start = lastEnd + 1;
			int end = start + token.length();
			NLPNode node = new NLPNode(start, end, token);
			initNodes.add(node);
			lastEnd = end;
		}
		NLPNode[] nodes = NLPUtils.toNodeArray(initNodes);
		nodes = nlp4jPOSTagger.decode(nodes);
		
		sentIter = sentence.iterator();
		int idx = 1;
		while(sentIter.hasNext()){
			CoreLabel token = sentIter.next();
			NLPNode node = nodes[idx];
			token.setTag(node.getPartOfSpeechTag());
			idx += 1;
		}
		return sentence;
	}

}
