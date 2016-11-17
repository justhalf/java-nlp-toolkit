package justhalf.nlp.reader.acereader;

import edu.stanford.nlp.util.Comparators;

public class ACEEventMention extends ACEObjectMention<ACEEvent> implements Comparable<ACEEventMention> {
	
	public Span scopeSpan;
	public Span anchorSpan;
	public String scopeText;
	public String anchorText;
	public ACEObjectMention<?>[] args;

	public ACEEventMention(String id, Span span, String text, ACEEvent parent,
						   Span scopeSpan, String scopeText, Span anchorSpan, String anchorText,
						   ACEObjectMention<?>[] args) {
		super(id, span, text, parent);
		this.scopeSpan = scopeSpan;
		this.scopeText = scopeText;
		this.anchorSpan = anchorSpan;
		this.anchorText = anchorText;
		this.args = args;
	}

	@Override
	public int compareTo(ACEEventMention o) {
		int result = Comparators.nullSafeCompare(span, o.span);
		if(result != 0) return result;
		return Comparators.nullSafeCompare(id, o.id);
	}

}
