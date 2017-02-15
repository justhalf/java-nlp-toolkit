package justhalf.nlp.reader.acereader;

import edu.stanford.nlp.util.Comparators;

public class ACEEventMention extends ACEObjectMention<ACEEvent> implements Comparable<ACEEventMention> {
	
	/** The span of the containing sentence that describes this event. */
	public Span scopeSpan;
	/** The span of the anchor word (the trigger). */
	public Span anchorSpan;
	/** The sentence that describes this event. */
	public String scopeText;
	/** The anchor word that trigger this event. */
	public String anchorText;
	/** The arguments of this event. */
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
