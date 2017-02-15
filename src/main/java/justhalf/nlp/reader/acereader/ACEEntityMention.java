package justhalf.nlp.reader.acereader;

import java.io.Serializable;

import edu.stanford.nlp.util.Comparators;

/**
 * Represents an entity mention.
 * A mention maybe discontiguous, in which case there will be more than one {@link Span} objects.
 * @author Aldrian Obaja (aldrianobaja.m@gmail.com)
 *
 */
public class ACEEntityMention extends ACEObjectMention<ACEEntity> implements Comparable<ACEEntityMention>, Serializable{
	
	public static enum ACEMentionType {
		/**
		 * A proper name reference to an entity<br>
		 * Examples:
		 * <ol>
		 * <li>Defence secretary [William Cohen]</li>
		 * <li>The [house of representative]</li>
		 * </ol>
		 */
		NAM("Name", true, true),
		
		/**
		 * A common noun reference to an entity<br>
		 * Examples:
		 * <ol>
		 * <li>The [lawyer]</li>
		 * <li>this year's [Miss America]</li>
		 * </ol>
		 */
		NOM("Nominal", true, true),
		
		/**
		 * A pronominal reference to an entity<br>
		 * Examples:
		 * <ol>
		 * <li>[He] went there</li>
		 * <li>[That]'s not mine</li>
		 * </ol>
		 */
		PRO("Pronominal", true, true),
		
		/**
		 * Premodifier mentions are those mentions which occur in a modifying position before another word(s)<br>
		 * Examples:
		 * <ol>
		 * <li>[He] went there</li>
		 * <li>[That]'s not mine</li>
		 * </ol>
		 */
		PRE("Premodifier", true, false),
		;
		
		public final String text;
		public final boolean in2004;
		public final boolean in2005;
		
		private ACEMentionType(String text, boolean in2004, boolean in2005){
			this.text = text;
			this.in2004 = in2004;
			this.in2005 = in2005;
		}
	}
	
	public static enum LDCMentionType {
		/**
		 * A proper name reference to an entity<br>
		 * Examples:
		 * <ol>
		 * <li>Defence secretary [William Cohen]</li>
		 * <li>The [house of representative]</li>
		 * </ol>
		 */
		NAM("Name"),

		/**
		 * Premodifier mentions are those mentions which occur in a modifying position before another word(s)<br>
		 * Examples:
		 * <ol>
		 * <li>[Israeli] troops</li>
		 * <li>[Republican] voters</li>
		 * </ol>
		 */
		NAMPRE("Name Premodifier"),
		
		/**
		 * Premodifier mentions are those mentions which occur in a modifying position before another word(s)<br>
		 * Examples:
		 * <ol>
		 * <li>[He] went there</li>
		 * <li>[That]'s not mine</li>
		 * </ol>
		 */
		PRE("Premodifier", true, false),

		/**
		 * Premodifier mentions are those mentions which occur in a modifying position before another word(s)<br>
		 * Examples:
		 * <ol>
		 * <li>[union] leaders</li>
		 * <li>[Mr.] Clinton</li>
		 * </ol>
		 */
		NOMPRE("Nominal Premodifier"),
		
		/**
		 * A common noun reference to an entity<br>
		 * Examples:
		 * <ol>
		 * <li>The [lawyer]</li>
		 * <li>this year's [Miss America]</li>
		 * </ol>
		 */
		NOM("Quantified Nominal Mention"),
		
		/**
		 * A pronominal reference to an entity<br>
		 * Examples:
		 * <ol>
		 * <li>[He] went there</li>
		 * <li>[That]'s not mine</li>
		 * </ol>
		 */
		PRO("Pronouns"),
		
		/**
		 * A construction in which two or more heads that share the same premodifiers or postmodifiers<br> 
		 * Examples:
		 * <ol>
		 * <li>[20 angry _men and women_]</li>
		 * <li>[The _movers and shakers_ in Washington]</li>
		 * </ol>
		 */
		MWH("Nominal Mentions with Multiple-word Heads", true, false),
		
		/**
		 * An unquantified nominal construction. Both singular and plural constructions may be BAR.<br> 
		 * Examples:
		 * <ol>
		 * <li>[lawyers]</li>
		 * <li>[Americans] eagerly await the result of the election</li>
		 * </ol>
		 */
		BAR("Unquantified Nominal Mention"),

		/**
		 * WH-question words and the specifier 'that'<br> 
		 * Examples:
		 * <ol>
		 * <li>the executive, [who] spoke on the condition of anonymity</li>
		 * <li>[Who] is the president of Brazil?</li>
		 * <li>[Whose] underwear is this?</li>
		 * </ol>
		 */
		WHQ("WH-Question Words and Specifiers"),
		
		/**
		 * Headless mentions are constructions in which the nominal head is not overtly expressed.<br>
		 * Although these mentions are technically headless, we will assign as 
		 * head the right most premodifier that falls directly before the spot
		 * where the head would be<br>
		 * Examples:
		 * <ol>
		 * <li>[the _toughest_]</li>
		 * <li>[60_%_] said</li>
		 * <li>They will [each] pay $5.</li>
		 * </ol>
		 */
		HLS("Headless Mentions"),
		
		/**
		 * Partitive constructions have two elements: the part and the whole.<br>
		 * The first element of a partitive construction lacks a head and
		 * quantifies over the second element.<br>
		 * Just as in Headless mentions, we will tag the right most premodifier of
		 * the first element as the head of the partitive construction<br>
		 * Examples:
		 * <ol>
		 * <li>[_some_ of the lawyers]</li>
		 * <li>[sixty _percent_ of the participants] </li>
		 * </ol>
		 * There are some constructions with prepositional phrase that greatly resemble partitives,
		 * but should not be tagged as partitives.<br>
		 * The first element of these constructions is a nominal that can function as a head.<br>
		 * Examples:
		 * <ol>
		 * <li>two members of the team</li>
		 * <li>a stockpile of weapons</li>
		 * </ol>
		 */
		PTV("Partitive Constructions"),
		;
		
		public final String text;
		public final boolean in2004;
		public final boolean in2005;
		
		private LDCMentionType(String text){
			this(text, true, true);
		}
		
		private LDCMentionType(String text, boolean in2004, boolean in2005){
			this.text = text;
			this.in2004 = in2004;
			this.in2005 = in2005;
		}
	}
	
	public static enum ACEGPEMentionRole {
		ORG,
		PER,
		LOC,
		GPE,
	}
	
	private static final long serialVersionUID = -3442499301550024781L;
	
	public SpanLabel label;
	public Span headSpan;
	
	public ACEEntity entity;
	public ACEMentionType mentionType;
	public LDCMentionType ldcMentionType;
	public String ldcAttribute;
	public ACEGPEMentionRole gpeMentionRole;
	
	public String headText;
	
	/**
	 * Perform a deep copy of another entity mention
	 * @param mention
	 */
	public ACEEntityMention(ACEEntityMention mention){
		super(mention.id, new Span(mention.span.start, mention.span.end), mention.text, mention.parent);
		this.label = mention.label;
		this.headSpan = new Span(mention.headSpan.start, mention.headSpan.end);
		this.entity = mention.entity;
		this.mentionType = mention.mentionType;
		this.ldcMentionType = mention.ldcMentionType;
		this.ldcAttribute = mention.ldcAttribute;
		this.gpeMentionRole = mention.gpeMentionRole;
		this.headText = mention.headText;
	}
	
	public ACEEntityMention(String id, String mentionType, String ldcMentionType, String ldcAttribute, ACEEntity entity,
							Span span, Span headSpan, String text, String headText, SpanLabel label){
		super(id, span, text, entity);
		this.mentionType = ACEMentionType.valueOf(mentionType);
		this.ldcMentionType = LDCMentionType.valueOf(ldcMentionType);
		this.ldcAttribute = ldcAttribute;
		this.entity = entity;
		this.headSpan = headSpan;
		this.headText = headText;
		this.label = label;
	}
	
	public boolean overlapsWith(ACEEntityMention mention){
		return (span.start < mention.span.end && span.start >= mention.span.start) ||
			   (mention.span.start < span.end && mention.span.start >= span.start);
	}
	
	/**
	 * Two {@link ACEEntityMention}s are the same iff the span, headSpan, and the label are the same
	 */
	public boolean equals(Object o){
		if(o instanceof ACEEntityMention){
			ACEEntityMention s = (ACEEntityMention)o;
			if(!span.equals(s.span)) return false;
			if(!headSpan.equals(s.headSpan)) return false;
			return label.equals(s.label);
		}
		return false;
	}

	@Override
	public int compareTo(ACEEntityMention o) {
		int result = Comparators.nullSafeCompare(span, o.span);
		if(result != 0) return result;
		result = Comparators.nullSafeCompare(headSpan, o.headSpan);
		if(result != 0) return result;
		result = Comparators.nullSafeCompare(label, o.label);
		if(result != 0) return result;
		return Comparators.nullSafeCompare(getFullID(), o.getFullID());
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder();
		result.append("["+span.toString()+"]");
		result.append("[Label="+label.form+"]");
		result.append("[ID="+id+"]");
		result.append("[Type="+mentionType+"]");
		result.append("[LDCType="+ldcMentionType+"]");
		result.append("[LDCAttr="+ldcAttribute+"]");
		return result.toString();
	}

}
