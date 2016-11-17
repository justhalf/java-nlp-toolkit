package justhalf.nlp.reader.acereader;

import java.util.ArrayList;
import java.util.List;

import justhalf.nlp.reader.acereader.ACERelationMention.ACETimestampType;

/**
 * Represents a relation between ACEEntity
 * @author Aldrian Obaja (aldrianobaja.m@gmail.com)
 *
 */
public class ACERelation extends ACEObject {
	
	public static enum ACERelationType implements ACEObjectType {
		PHYS("Physical", true, true),
		PER_SOC("Personal/Social", true, true),
		ART("Agent-Artifact", true, true),
		METONYMY("Metonymy", true, true),
		
		// Only in ACE2004
		EMP_ORG("Employee/Membership/Subsidiary", true, false),
		OTHER_AFF("PER/ORG Affiliation", true, false),
		GPE_AFF("GPE Affiliation", true, false),
		DISC("Discourse", true, false),
		
		// Only in ACE2005
		PART_WHOLE("Part-whole", false, true),
		ORG_AFF("ORG-Affiliation", false, true),
		GEN_AFF("Gen-Affiliation", false, true),
		;
		
		public final String text;
		public final boolean in2004;
		public final boolean in2005;
		private List<ACERelationSubType> subtypes;
		
		private ACERelationType(String text, boolean in2004, boolean in2005){
			this.text = text;
			this.in2004 = in2004;
			this.in2005 = in2005;
		}
		
		@Override
		public List<ACERelationSubType> subtypes(){
			if(subtypes == null){
				List<ACERelationSubType> subtypeList = new ArrayList<ACERelationSubType>();
				for(ACERelationSubType subtype: ACERelationSubType.values()){
					if(subtype.type == this){
						subtypeList.add(subtype);
					}
				}
				subtypes = subtypeList;
			}
			return subtypes;
		}
		
		public String toString(){
			return text;
		}
	}
	
	public static enum ACERelationSubType implements ACEObjectSubType {
		// Physical
		PHYS_LOCATED("Located", ACERelationType.PHYS, true, true),
		PHYS_NEAR("Near", ACERelationType.PHYS, true, true),
		// Only in 2004
		PHYS_PART_WHOLE("Part-Whole", ACERelationType.PHYS, true, false),
		
		// Personal-Social
		PER_SOC_BUSINESS("Business", ACERelationType.PER_SOC, true, true),
		PER_SOC_FAMILY("Family", ACERelationType.PER_SOC, true, true),
		// Only in 2004
		PER_SOC_OTHER("Other", ACERelationType.PER_SOC, true, false),
		// Only in 2005
		PER_SOC_LASTING_PERSONAL("Lasting-Personal", ACERelationType.PER_SOC, false, true),

		// Artifact
		// Only in 2004
		ART_USER_OR_OWNER("User-or-Owner", ACERelationType.ART, true, false),
		ART_INVENTOR_OR_MANUFACTURER("Inventor-or-Manufacturer", ACERelationType.ART, true, false),
		ART_OTHER("Other", ACERelationType.ART, true, false),
		// Only in 2005
		ART_USER_OWNER_INVENTOR_MANUFACTURER("User-Owner-Inventor-Manufacturer", ACERelationType.ART, false, true),
		
		// Metonymy
		METONYMY_METONYMY("", ACERelationType.METONYMY, true, true),
		
		// Employee-Organization (only in 2004)
		EMP_ORG_EMPLOY_EXECUTIVE("Employ-Executive", ACERelationType.EMP_ORG, true, false),
		EMP_ORG_EMPLOY_STAFF("Employ-Staff", ACERelationType.EMP_ORG, true, false),
		EMP_ORG_EMPLOY_UNDETERMINED("Employ-Undetermined", ACERelationType.EMP_ORG, true, false),
		EMP_ORG_MEMBER_OF_GROUP("Member-of-Group", ACERelationType.EMP_ORG, true, false),
		EMP_ORG_SUBSIDIARY("Subsidiary", ACERelationType.EMP_ORG, true, false),
		EMP_ORG_PARTNER("Partner", ACERelationType.EMP_ORG, true, false),
		EMP_ORG_OTHER("Other", ACERelationType.EMP_ORG, true, false),
		
		// PER/ORG Affiliation (only in 2004)
		OTHER_AFF_ETHNIC("Ethnic", ACERelationType.OTHER_AFF, true, false),
		OTHER_AFF_IDEOLOGY("Ideology", ACERelationType.OTHER_AFF, true, false),
		OTHER_AFF_OTHER("Other", ACERelationType.OTHER_AFF, true, false),
		
		// GPE Affiliation (only in 2004)
		GPE_AFF_CITIZEN_OR_RESIDENT("Citizen-or-Resident", ACERelationType.GPE_AFF, true, false),
		GPE_AFF_BASED_IN("Based-In", ACERelationType.GPE_AFF, true, false),
		GPE_AFF_OTHER("Other", ACERelationType.GPE_AFF, true, false),
		
		// Discourse (only in 2004)
		DISC_DISC("", ACERelationType.DISC, true, false),
		
		// Part-Whole (only in 2005)
		PART_WHOLE_GEOGRAPHICAL("Geographical", ACERelationType.PART_WHOLE, false, true),
		PART_WHOLE_SUBSIDIARY("Subsidiary", ACERelationType.PART_WHOLE, false, true),
		PART_WHOLE_ARTIFACT("Artifact", ACERelationType.PART_WHOLE, false, true),
		
		// ORG Affiliation (only in 2005)
		ORG_AFF_EMPLOYMENT("Employment", ACERelationType.ORG_AFF, false, true),
		ORG_AFF_OWNERSHIP("Ownership", ACERelationType.ORG_AFF, false, true),
		ORG_AFF_FOUNDER("Founder", ACERelationType.ORG_AFF, false, true),
		ORG_AFF_STUDENT_ALUM("Student-Alum", ACERelationType.ORG_AFF, false, true),
		ORG_AFF_SPORTS_AFFILIATION("Sports-Affiliation", ACERelationType.ORG_AFF, false, true),
		ORG_AFF_INVESTOR_SHAREHOLDER("Investor-Shareholder", ACERelationType.ORG_AFF, false, true),
		ORG_AFF_MEMBERSHIP("Membership", ACERelationType.ORG_AFF, false, true),
		
		// GEN Affiliation (only in 2005)
		GEN_AFF_CITIZEN_RESIDENT_RELIGION_ETHNICITY("Citizen-Resident-Religion-Ethnicity", ACERelationType.GEN_AFF, false, true),
		GEN_AFF_ORG_LOCATION("Org-Location", ACERelationType.GEN_AFF, false, true),
		;
		
		public final String text;
		public final ACERelationType type;
		public final boolean in2004;
		public final boolean in2005;
		
		private ACERelationSubType(String text, ACERelationType type, boolean in2004, boolean in2005){
			this.text = text;
			this.type = type;
			this.in2004 = in2004;
			this.in2005 = in2005;
		}
		
		public String toString(){
			return text;
		}
	}
	
	/**
	 * Represents the time at which the ACE relation holds.<br>
	 * There are four possible tenses:
	 * <ol>
	 * <li>Past: the Relation is taken to hold only for some span prior to the time of speech</li>
	 * <li>Future: the Relation is taken to hold only for some span after the time of speech</li>
	 * <li>Present: the Relation is taken to hold for a limited time overlapping with the time of speech</li>
	 * <li>Unspecified: the Relation is ‘static’ or the span of time for which it holds cannot
	 * 	   be determined with certainty</li>
	 * </ol>
	 * @author Aldrian Obaja (aldrianobaja.m@gmail.com)
	 *
	 */
	public static enum ACETense {
		FUTURE("Future"),
		PRESENT("Present"),
		PAST("Past"),
		UNSPECIFIED("Unspecified"),
		
		/** In ACE2004 there is no tense defined */
		NOT_TAGGED("Not tagged"),
		;
		
		public final String text;
		
		private ACETense(String text){
			this.text = text;
		}
	}
	
	/**
	 * Represents the modality aspect of the ACE relations.<br>
	 * This only presents in ACE2005 dataset.<br>
	 * There are two possible modalities:
	 * <ol>
	 * <li>Asserted: when the Reasonable Reader Rule is interpreted relative to the 'Real' world</li>
	 * <li>Other: when the Reasonable Reader Rule is taken to hold in a particular counterfactual world.</li>
	 * </ol> 
	 * @author Aldrian Obaja (aldrianobaja.m@gmail.com)
	 *
	 */
	public static enum ACEModality {
		ASSERTED("Asserted"),
		OTHER("Other"),
		NOT_TAGGED("Not tagged"),
		;
		
		public final String text;
		
		private ACEModality(String text){
			this.text = text;
		}
	}
	
	public ACERelationType type;
	public ACERelationSubType subtype;
	public ACEEntity[] args;
	public ACETense tense;
	public ACEModality modality;
	public List<ACERelationMention> mentions;
	public ACETimex timestamp;
	public ACETimestampType timestampType;

	public ACERelation(ACEEntity[] args, String id, String type, String subtype) {
		this(args, id, type, subtype, ACETense.NOT_TAGGED.name(), ACEModality.NOT_TAGGED.name(), null, "");
	}
	
	public ACERelation(ACEEntity[] args, String id, String type, String subtype, String tense, String modality,
					   ACETimex timestamp, String timestampType){
		super(id);
		this.args = args;
		this.type = ACERelationType.valueOf(type.replace("-", "_"));
		if(subtype.length() == 0){
			subtype = type;
		}
		this.subtype = ACERelationSubType.valueOf((type+"_"+subtype).toUpperCase().replace("-", "_"));
		if(tense == ""){
			this.tense = ACETense.NOT_TAGGED;
		} else {
			this.tense = ACETense.valueOf(tense.toUpperCase());
		}
		if(modality == ""){
			this.modality = ACEModality.NOT_TAGGED;
		} else {
			this.modality = ACEModality.valueOf(modality.toUpperCase());
		}
		this.mentions = new ArrayList<ACERelationMention>();
		if(timestamp != null){
			this.timestamp = timestamp;
			this.timestampType = ACETimestampType.valueOf(timestampType.toUpperCase().replace("-", "_"));
		}
	}

	public void addMention(ACERelationMention mention){
		this.mentions.add(mention);
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder();
		result.append("[ID="+id+"]");
		result.append("[Type="+type+"]");
		result.append("[Subtype="+subtype+"]");
		for(int i=0; i<args.length; i++){
			result.append("[Arg"+(i+1)+"="+args[i]+"]");
		}
		return result.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ACERelationMention> mentions() {
		return mentions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ACERelationType type() {
		return type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ACERelationSubType subtype() {
		return subtype;
	}

}
