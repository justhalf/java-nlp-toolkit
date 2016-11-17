package justhalf.nlp.reader.acereader;

import edu.stanford.nlp.util.Comparators;

public class ACEValueMention extends ACEObjectMention<ACEValue> implements Comparable<ACEValueMention> {
	
	public ACEValueMention(String id, Span span, String text, ACEValue value) {
		super(id, span, text, value);
	}

	@Override
	public int compareTo(ACEValueMention o) {
		int result = Comparators.nullSafeCompare(span, o.span);
		if(result != 0) return result;
		return Comparators.nullSafeCompare(id, o.id);
	}

}
