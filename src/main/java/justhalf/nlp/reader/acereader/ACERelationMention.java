package justhalf.nlp.reader.acereader;

import edu.stanford.nlp.util.Comparators;

public class ACERelationMention extends ACEObjectMention<ACERelation> implements Comparable<ACERelationMention> {
	
	public static enum ACERelationSyntacticClass {
		POSSESSIVE("Possessive", false),
		PREPOSITION("Preposition", false),
		PREMOD("PreMod", false),
		COORDINATION("Coordination", true),
		FORMULAIC("Formulaic", false),
		PARTICIPIAL("Participial", true),
		VERBAL("Verbal", false),
		OTHER("Other", true),
		;
		
		public final String text;
		public final boolean in2004;
		public final boolean in2005;
		
		private ACERelationSyntacticClass(String text, boolean onlyIn2005){
			this.text = text;
			this.in2004 = !onlyIn2005;
			this.in2005 = true;
		}
	}
	
	public static enum ACETimestampType {
		TIME_WITHIN,
		TIME_HOLDS,
		TIME_STARTING,
		TIME_ENDING,
		TIME_BEFORE,
		TIME_AFTER,
		TIME_AT_BEGINNING,
		TIME_AT_END,
	}
	
	public ACERelation relation;
	public ACEEntityMention[] args;
	public ACERelationSyntacticClass syntacticClass;
	public ACETimexMention timestamp;
	public ACETimestampType timestampType;

	public ACERelationMention(ACEEntityMention[] args, String id, String syntacticClass, Span span, String text,
							  ACETimexMention timestamp, String timestampType, ACERelation relation) {
		super(id, span, text, relation);
		this.args = args;
		this.syntacticClass = ACERelationSyntacticClass.valueOf(syntacticClass.toUpperCase());
		this.relation = relation;
		if(timestamp != null){
			this.timestamp = timestamp;
			this.timestampType = ACETimestampType.valueOf(timestampType.toUpperCase().replace("-", "_"));
		}
	}

	public ACERelationMention(ACERelationMention mention) {
		super(mention.id, new Span(mention.span.start, mention.span.end), mention.text, mention.relation);
		this.args = new ACEEntityMention[mention.args.length];
		for(int i=0; i<this.args.length; i++){
			this.args[i] = new ACEEntityMention(mention.args[i]);
		}
		this.syntacticClass = mention.syntacticClass;
		this.relation = mention.relation;
		this.timestamp = mention.timestamp;
		this.timestampType = mention.timestampType;
	}
	
	public String getParentID(){
		return relation.id;
	}

	public String toString(){
		StringBuilder result = new StringBuilder();
		result.append("["+span.toString()+"]");
		result.append("[ID="+getFullID()+"]");
		result.append("[SyntacticClass="+syntacticClass+"]");
		return result.toString();
	}

	@Override
	public int compareTo(ACERelationMention o) {
		int result = Comparators.nullSafeCompare(span, o.span);
		if(result != 0) return result;
		return Comparators.nullSafeCompare(getFullID(), o.getFullID());
	}

}
