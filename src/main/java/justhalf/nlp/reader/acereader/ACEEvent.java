package justhalf.nlp.reader.acereader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import justhalf.nlp.reader.acereader.ACEEntity.ACEEntityType;
import justhalf.nlp.reader.acereader.ACERelation.ACETense;
import justhalf.nlp.reader.acereader.ACERelationMention.ACETimestampType;
import justhalf.nlp.reader.acereader.ACEValue.ACEValueSubType;
import justhalf.nlp.reader.acereader.ACEValue.ACEValueType;

public class ACEEvent extends ACEObject {
	
	public static enum ACEEventType implements ACEObjectType {
		LIFE("Life"),
		MOVEMENT("Movement"),
		TRANSACTION("Transaction"),
		BUSINESS("Business"),
		CONFLICT("Conflict"),
		CONTACT("Contact"),
		PERSONNEL("Personnel"),
		JUSTICE("Justice"),
		;
		
		public final String text;
		private List<ACEEventSubType> subtypes;
		
		private ACEEventType(String text){
			this.text = text;
		}
		
		public String toString(){
			return text;
		}
		
		public List<ACEEventSubType> subtypes(){
			if(subtypes == null){
				List<ACEEventSubType> subtypeList = new ArrayList<ACEEventSubType>();
				for(ACEEventSubType subtype: ACEEventSubType.values()){
					if(subtype.type == this){
						subtypeList.add(subtype);
					}
				}
				subtypes = subtypeList;
			}
			return subtypes;
		}
	}
	
	public static enum ACEEventSubType implements ACEObjectSubType {
		LIFE_BE_BORN("Be-Born", ACEEventType.LIFE,
				new ACEEventArgumentSpec("Person", ACEEntityType.PER),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		LIFE_MARRY("Marry", ACEEventType.LIFE,
				new ACEEventArgumentSpec("Person", ACEEntityType.PER),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		LIFE_DIVORCE("Divorce", ACEEventType.LIFE,
				new ACEEventArgumentSpec("Person", ACEEntityType.PER),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		LIFE_INJURE("Injure", ACEEventType.LIFE,
				new ACEEventArgumentSpec("Agent", ACEEntityType.PER,
												  ACEEntityType.ORG,
												  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Victim", ACEEntityType.PER),
				new ACEEventArgumentSpec("Instrument", ACEEntityType.WEA,
													   ACEEntityType.VEH),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		LIFE_DIE("Die", ACEEventType.LIFE,
				new ACEEventArgumentSpec("Agent", ACEEntityType.PER,
												  ACEEntityType.ORG,
												  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Victim", ACEEntityType.PER),
				new ACEEventArgumentSpec("Instrument", ACEEntityType.WEA,
											   		   ACEEntityType.VEH),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		
		MOVEMENT_TRANSPORT("Transport", ACEEventType.MOVEMENT,
				new ACEEventArgumentSpec("Agent", ACEEntityType.PER,
												  ACEEntityType.ORG,
												  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Artifact", ACEEntityType.PER,
													 ACEEntityType.WEA,
													 ACEEntityType.VEH),
				new ACEEventArgumentSpec("Vehicle", ACEEntityType.VEH),
				new ACEEventArgumentSpec("Price", ACEValueType.NUMERIC),
				new ACEEventArgumentSpec("Origin", ACEEntityType.GPE,
												   ACEEntityType.LOC,
												   ACEEntityType.FAC),
				new ACEEventArgumentSpec("Destination", ACEEntityType.GPE,
														ACEEntityType.LOC,
														ACEEntityType.FAC),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME)),
		
		TRANSACTION_TRANSFER_OWNERSHIP("Transfer-Ownership", ACEEventType.TRANSACTION,
				new ACEEventArgumentSpec("Buyer", ACEEntityType.PER,
												  ACEEntityType.ORG,
												  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Seller", ACEEntityType.PER,
												   ACEEntityType.ORG,
												   ACEEntityType.GPE),
				new ACEEventArgumentSpec("Beneficiary", ACEEntityType.PER,
														ACEEntityType.ORG,
														ACEEntityType.GPE),
				new ACEEventArgumentSpec("Artifact", ACEEntityType.VEH,
													 ACEEntityType.WEA,
													 ACEEntityType.FAC,
													 ACEEntityType.ORG),
				new ACEEventArgumentSpec("Price", ACEValueSubType.NUMERIC_MONEY),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		TRANSACTION_TRANSFER_MONEY("Transfer-Money", ACEEventType.TRANSACTION,
				new ACEEventArgumentSpec("Giver", ACEEntityType.PER,
												  ACEEntityType.ORG,
												  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Recepient", ACEEntityType.PER,
													  ACEEntityType.ORG,
													  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Beneficiary", ACEEntityType.PER,
														ACEEntityType.ORG,
														ACEEntityType.GPE),
				new ACEEventArgumentSpec("Money", ACEValueSubType.NUMERIC_MONEY),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		
		BUSINESS_START_ORG("Start-Org", ACEEventType.BUSINESS,
				new ACEEventArgumentSpec("Agent", ACEEntityType.PER,
												  ACEEntityType.ORG,
												  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Org", ACEEntityType.ORG),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		BUSINESS_MERGE_ORG("Merge-Org", ACEEventType.BUSINESS,
				new ACEEventArgumentSpec("Org", ACEEntityType.ORG),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		BUSINESS_END_ORG("End-Org", ACEEventType.BUSINESS,
				new ACEEventArgumentSpec("Org", ACEEntityType.ORG),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		BUSINESS_DECLARE_BANKRUPTCY("Declare-Bankruptcy", ACEEventType.BUSINESS,
				new ACEEventArgumentSpec("Org", ACEEntityType.ORG),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		
		CONFLICT_ATTACK("Attack", ACEEventType.CONFLICT,
				new ACEEventArgumentSpec("Attacker", ACEEntityType.PER,
						   							 ACEEntityType.ORG,
						   							 ACEEntityType.GPE),
				new ACEEventArgumentSpec("Target", ACEEntityType.PER,
												   ACEEntityType.ORG,
												   ACEEntityType.VEH,
												   ACEEntityType.FAC,
												   ACEEntityType.WEA),
				new ACEEventArgumentSpec("Instrument", ACEEntityType.VEH,
													   ACEEntityType.WEA),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		CONFLICT_DEMONSTRATE("Demonstrate", ACEEventType.CONFLICT,
				new ACEEventArgumentSpec("Entity", ACEEntityType.PER,
												   ACEEntityType.ORG),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		
		CONTACT_MEET("Meet", ACEEventType.CONTACT,
				new ACEEventArgumentSpec("Entity", ACEEntityType.PER,
												   ACEEntityType.ORG,
												   ACEEntityType.GPE),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		CONTACT_PHONE_WRITE("Phone-Write", ACEEventType.CONTACT,
				new ACEEventArgumentSpec("Entity", ACEEntityType.PER,
												   ACEEntityType.ORG,
												   ACEEntityType.GPE),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME)),
		
		PERSONNEL_START_POSITION("Start-Position", ACEEventType.PERSONNEL,
				new ACEEventArgumentSpec("Person", ACEEntityType.PER),
				new ACEEventArgumentSpec("Entity", ACEEntityType.ORG,
												   ACEEntityType.GPE),
				new ACEEventArgumentSpec("Position", ACEValueType.JOB_TITLE),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		PERSONNEL_END_POSITION("End-Position", ACEEventType.PERSONNEL,
				new ACEEventArgumentSpec("Person", ACEEntityType.PER),
				new ACEEventArgumentSpec("Entity", ACEEntityType.ORG,
												   ACEEntityType.GPE),
				new ACEEventArgumentSpec("Position", ACEValueType.JOB_TITLE),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		PERSONNEL_NOMINATE("Nominate", ACEEventType.PERSONNEL,
				new ACEEventArgumentSpec("Person", ACEEntityType.PER),
				new ACEEventArgumentSpec("Agent", ACEEntityType.PER,
												  ACEEntityType.ORG,
												  ACEEntityType.GPE,
												  ACEEntityType.FAC),
				new ACEEventArgumentSpec("Position", ACEValueType.JOB_TITLE),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		PERSONNEL_ELECT("Elect", ACEEventType.PERSONNEL,
				new ACEEventArgumentSpec("Person", ACEEntityType.PER),
				new ACEEventArgumentSpec("Entity", ACEEntityType.PER,
												   ACEEntityType.ORG,
												   ACEEntityType.GPE),
				new ACEEventArgumentSpec("Position", ACEValueType.JOB_TITLE),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		
		JUSTICE_ARREST_JAIL("Arrest-Jail", ACEEventType.JUSTICE,
				new ACEEventArgumentSpec("Person", ACEEntityType.PER),
				new ACEEventArgumentSpec("Agent", ACEEntityType.PER,
												  ACEEntityType.ORG,
												  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Crime", ACEValueType.CRIME),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		JUSTICE_RELEASE_PAROLE("Release-Parole", ACEEventType.JUSTICE,
				new ACEEventArgumentSpec("Person", ACEEntityType.PER),
				new ACEEventArgumentSpec("Entity", ACEEntityType.PER,
												   ACEEntityType.ORG,
												   ACEEntityType.GPE),
				new ACEEventArgumentSpec("Crime", ACEValueType.CRIME),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		JUSTICE_TRIAL_HEARING("Trial-Hearing", ACEEventType.JUSTICE,
				new ACEEventArgumentSpec("Defendant", ACEEntityType.PER,
													  ACEEntityType.ORG,
													  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Prosecutor", ACEEntityType.PER,
													   ACEEntityType.ORG,
													   ACEEntityType.GPE),
				new ACEEventArgumentSpec("Adjudicator", ACEEntityType.PER,
														ACEEntityType.ORG,
														ACEEntityType.GPE),
				new ACEEventArgumentSpec("Crime", ACEValueType.CRIME),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		JUSTICE_CHARGE_INDICT("Charge-Indict", ACEEventType.JUSTICE,
				new ACEEventArgumentSpec("Defendant", ACEEntityType.PER,
													  ACEEntityType.ORG,
													  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Prosecutor", ACEEntityType.PER,
													   ACEEntityType.ORG,
													   ACEEntityType.GPE),
				new ACEEventArgumentSpec("Adjudicator", ACEEntityType.PER,
														ACEEntityType.ORG,
														ACEEntityType.GPE),
				new ACEEventArgumentSpec("Crime", ACEValueType.CRIME),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		JUSTICE_SUE("Sue", ACEEventType.JUSTICE,
				new ACEEventArgumentSpec("Plaintiff", ACEEntityType.PER,
													  ACEEntityType.ORG,
													  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Defendant", ACEEntityType.PER,
													  ACEEntityType.ORG,
													  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Adjudicator", ACEEntityType.PER,
														ACEEntityType.ORG,
														ACEEntityType.GPE),
				new ACEEventArgumentSpec("Crime", ACEValueType.CRIME),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		JUSTICE_CONVICT("Convict", ACEEventType.JUSTICE,
				new ACEEventArgumentSpec("Defendant", ACEEntityType.PER,
													  ACEEntityType.ORG,
													  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Adjudicator", ACEEntityType.PER,
														ACEEntityType.ORG,
														ACEEntityType.GPE),
				new ACEEventArgumentSpec("Crime", ACEValueType.CRIME),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		JUSTICE_SENTENCE("Sentence", ACEEventType.JUSTICE,
				new ACEEventArgumentSpec("Defendant", ACEEntityType.PER,
													  ACEEntityType.ORG,
													  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Adjudicator", ACEEntityType.PER,
														ACEEntityType.ORG,
														ACEEntityType.GPE),
				new ACEEventArgumentSpec("Crime", ACEValueType.CRIME),
				new ACEEventArgumentSpec("Sentence", ACEValueType.SENTENCE),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		JUSTICE_FINE("Fine", ACEEventType.JUSTICE,
				new ACEEventArgumentSpec("Entity", ACEEntityType.PER,
												   ACEEntityType.ORG,
												   ACEEntityType.GPE),
				new ACEEventArgumentSpec("Adjudicator", ACEEntityType.PER,
														ACEEntityType.ORG,
														ACEEntityType.GPE),
				new ACEEventArgumentSpec("Money", ACEValueType.NUMERIC),
				new ACEEventArgumentSpec("Crime", ACEValueType.CRIME),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		JUSTICE_EXECUTE("Execute", ACEEventType.JUSTICE,
				new ACEEventArgumentSpec("Person", ACEEntityType.PER),
				new ACEEventArgumentSpec("Agent", ACEEntityType.PER,
												  ACEEntityType.ORG,
												  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Crime", ACEValueType.CRIME),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		JUSTICE_EXTRADITE("Extradite", ACEEventType.JUSTICE,
				new ACEEventArgumentSpec("Agent", ACEEntityType.PER,
												  ACEEntityType.ORG,
												  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Person", ACEEntityType.PER),
				new ACEEventArgumentSpec("Destination", ACEEntityType.GPE,
														ACEEntityType.LOC,
														ACEEntityType.FAC),
				new ACEEventArgumentSpec("Origin", ACEEntityType.GPE,
												   ACEEntityType.LOC,
												   ACEEntityType.FAC),
				new ACEEventArgumentSpec("Crime", ACEValueType.CRIME),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME)),
		JUSTICE_ACQUIT("Acquit", ACEEventType.JUSTICE,
				new ACEEventArgumentSpec("Defendant", ACEEntityType.PER,
													  ACEEntityType.ORG,
													  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Adjudicator", ACEEntityType.PER,
														ACEEntityType.ORG,
														ACEEntityType.GPE),
				new ACEEventArgumentSpec("Crime", ACEValueType.CRIME),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		JUSTICE_PARDON("Pardon", ACEEventType.JUSTICE,
				new ACEEventArgumentSpec("Defendant", ACEEntityType.PER,
													  ACEEntityType.ORG,
													  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Adjudicator", ACEEntityType.PER,
														ACEEntityType.ORG,
														ACEEntityType.GPE),
				new ACEEventArgumentSpec("Crime", ACEValueType.CRIME),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		JUSTICE_APPEAL("Appeal", ACEEventType.JUSTICE,
				new ACEEventArgumentSpec("Defendant", ACEEntityType.PER,
													  ACEEntityType.ORG,
													  ACEEntityType.GPE),
				new ACEEventArgumentSpec("Prosecutor", ACEEntityType.PER,
													   ACEEntityType.ORG,
													   ACEEntityType.GPE),
				new ACEEventArgumentSpec("Adjudicator", ACEEntityType.PER,
														ACEEntityType.ORG,
														ACEEntityType.GPE),
				new ACEEventArgumentSpec("Crime", ACEValueType.CRIME),
				new ACEEventArgumentSpec("Time", ACEValueType.TIME),
				new ACEEventArgumentSpec("Place", ACEEntityType.GPE,
												  ACEEntityType.LOC,
												  ACEEntityType.FAC)),
		;
		
		public final String text;
		public final ACEEventType type;
		public final LinkedHashMap<String, ACEEventArgumentSpec> argSpecs;
		
		private ACEEventSubType(String text, ACEEventType type, ACEEventArgumentSpec... argSpecs){
			this.text = text;
			this.type = type;
			this.argSpecs = new LinkedHashMap<String, ACEEventArgumentSpec>();
			for(ACEEventArgumentSpec spec: argSpecs){
				this.argSpecs.put(spec.roleName, spec);
			}
		}
		
		public String toString(){
			return text;
		}
	}
	
	public static enum ACEEventGenericity {
		SPECIFIC("Specific"),
		GENERIC("Generic"),
		;
		
		public final String text;
		
		private ACEEventGenericity(String text){
			this.text = text;
		}
		
		public String toString(){
			return text;
		}
	}
	
	public static enum ACEEventPolarity {
		POSITIVE("Positive"),
		NEGATIVE("Negative"),
		;
		
		public final String text;
		
		private ACEEventPolarity(String text){
			this.text = text;
		}
		
		public String toString(){
			return text;
		}
	}
	
	public static enum ACEEventModality {
		ASSERTED("Asserted"),
		OTHER("Other"),
		;
		
		public final String text;
		
		private ACEEventModality(String text){
			this.text = text;
		}
		
		public String toString(){
			return text;
		}
	}
	
	public ACEEventType type;
	public ACEEventSubType subtype;
	public ACETense tense;
	public ACEEventGenericity genericity;
	public ACEEventPolarity polarity;
	public ACEEventModality modality;
	public LinkedHashMap<String, ACEObject> args;
	public List<ACEEventMention> mentions;
	public ACETimestampType timestampType;

	public ACEEvent(String id, String type, String subtype, String tense, String genericity, String polarity, String modality) {
		super(id);
		this.type = ACEEventType.valueOf(type.toUpperCase());
		if(subtype.length() == 0){
			subtype = type;
		}
		this.subtype = ACEEventSubType.valueOf((type+"_"+subtype).toUpperCase().replace("-", "_"));
		this.tense = ACETense.valueOf(tense.toUpperCase());
		this.genericity = ACEEventGenericity.valueOf(genericity.toUpperCase());
		this.polarity = ACEEventPolarity.valueOf(polarity.toUpperCase());
		this.modality = ACEEventModality.valueOf(modality.toUpperCase());
		this.args = new LinkedHashMap<String, ACEObject>();
		this.mentions = new ArrayList<ACEEventMention>();
	}
	
	public void setTimestampType(String timestampType){
		this.timestampType = ACETimestampType.valueOf(timestampType.toUpperCase().replace("-", "_"));
	}
	
	public void addArgument(String roleName, ACEObject arg){
		this.args.put(roleName, arg);
	}
	
	public void addMention(ACEEventMention mention){
		this.mentions.add(mention);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ACEEventMention> mentions() {
		return mentions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ACEEventType type() {
		return type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ACEEventSubType subtype() {
		return subtype;
	}

}
