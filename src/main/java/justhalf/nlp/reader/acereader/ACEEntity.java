package justhalf.nlp.reader.acereader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an Entity with possibly multiple mentions
 * @author Aldrian Obaja (aldrianobaja.m@gmail.com)
 *
 */
public class ACEEntity extends ACEObject {
	
	/**
	 * Based on:
	 * <ol>
	 * <li>ACE2004: https://www.ldc.upenn.edu/sites/www.ldc.upenn.edu/files/english-edt-v4.2.6.pdf</li>
	 * <li>ACE2005: https://www.ldc.upenn.edu/sites/www.ldc.upenn.edu/files/english-entities-guidelines-v5.6.6.pdf</li>
	 * </ol>
	 * Use {@link #toString()} method to get the actual textual form used in the annotation files.
	 * @author Aldrian Obaja (aldrianobaja.m@gmail.com)
	 *
	 */
	public static enum ACEEntityType implements ACEObjectType {
		PER("Person"),
		ORG("Organization"),
		LOC("Location"),
		GPE("Geo-Political Entity"),
		FAC("Facility"),
		VEH("Vehicle"),
		WEA("Weapon"),
		;
		
		/** A more descriptive text form of this entity type */
		public final String text;
		private List<ACEEntitySubType> subtypes;
		
		private ACEEntityType(String text) {
			this.text = text;
		}
		
		public List<ACEEntitySubType> subtypes(){
			if(subtypes == null){
				List<ACEEntitySubType> subtypeList = new ArrayList<ACEEntitySubType>();
				for(ACEEntitySubType subtype: ACEEntitySubType.values()){
					if(subtype.type == this){
						subtypeList.add(subtype);
					}
				}
				subtypes = subtypeList;
			}
			return subtypes;
		}
		
		public String toString(){
			return name();
		}
	}

	/**
	 * Based on:
	 * <ol>
	 * <li>ACE2004: https://www.ldc.upenn.edu/sites/www.ldc.upenn.edu/files/english-edt-v4.2.6.pdf</li>
	 * <li>ACE2005: https://www.ldc.upenn.edu/sites/www.ldc.upenn.edu/files/english-entities-guidelines-v5.6.6.pdf</li>
	 * </ol>
	 * Use {@link #toString()} method to get the actual textual form used in the annotation files.
	 * @author Aldrian Obaja (aldrianobaja.m@gmail.com)
	 *
	 */
	public static enum ACEEntitySubType implements ACEObjectSubType {
		// Person
		// Only in 2004
		PER_PER("", ACEEntityType.PER, true, false),
		// Only in 2005
		PER_INDIVIDUAL("Individual", ACEEntityType.PER, false, true),
		PER_GROUP("Group", ACEEntityType.PER, false, true),
		PER_INDETERMINATE("Indeterminate", ACEEntityType.PER, false, true),
		
		// Organization
		ORG_GOVERNMENT("Government", ACEEntityType.ORG, true, true),
		ORG_COMMERCIAL("Commercial", ACEEntityType.ORG, true, true),
		ORG_EDUCATIONAL("Educational", ACEEntityType.ORG, true, true),
		ORG_NON_PROFIT("Non-Profit", ACEEntityType.ORG, true, true),
		// Only in 2004
		ORG_OTHER("Other", ACEEntityType.ORG, true, false),
		// Only in 2005
		ORG_ENTERTAINMENT("Entertainment", ACEEntityType.ORG, false, true),
		ORG_NON_GOVERNMENTAL("Non-Governmental", ACEEntityType.ORG, false, true),
		ORG_MEDIA("Media", ACEEntityType.ORG, false, true),
		ORG_RELIGIOUS("Religious", ACEEntityType.ORG, false, true),
		ORG_MEDICAL_SCIENCE("Medical-Science", ACEEntityType.ORG, false, true),
		ORG_SPORTS("Sports", ACEEntityType.ORG, false, true),
		
		// GPE
		GPE_CONTINENT("Continent", ACEEntityType.GPE, true, true),
		GPE_NATION("Nation", ACEEntityType.GPE, true, true),
		GPE_STATE_OR_PROVINCE("State-or-Province", ACEEntityType.GPE, true, true),
		GPE_COUNTY_OR_DISTRICT("County-or-District", ACEEntityType.GPE, true, true),
		// Only in 2004
		GPE_CITY_OR_TOWN("City-or-Town", ACEEntityType.GPE, true, false),
		GPE_OTHER("Other", ACEEntityType.GPE, true, false),
		// Only in 2005
		GPE_POPULATION_CENTER("Population-Center", ACEEntityType.GPE, false, true),
		GPE_GPE_CLUSTER("GPE-Cluster", ACEEntityType.GPE, false, true),
		GPE_SPECIAL("Special", ACEEntityType.GPE, false, true),
		
		// Location
		LOC_ADDRESS("Address", ACEEntityType.LOC, true, true),
		LOC_BOUNDARY("Boundary", ACEEntityType.LOC, true, true),
		LOC_CELESTIAL("Celestial", ACEEntityType.LOC, true, true),
		LOC_WATER_BODY("Water-Body", ACEEntityType.LOC, true, true),
		LOC_LAND_REGION_NATURAL("Land-Region-Natural", ACEEntityType.LOC, true, true),
		LOC_REGION_INTERNATIONAL("Region-International", ACEEntityType.LOC, true, true),
		// Only in 2004
		LOC_REGION_LOCAL("Region-Local", ACEEntityType.LOC, true, false),
		LOC_REGION_SUBNATIONAL("Region-Subnational", ACEEntityType.LOC, true, false),
		LOC_REGION_NATIONAL("Region-National", ACEEntityType.LOC, true, false),
		LOC_OTHER("Other", ACEEntityType.LOC, true, false),
		// Only in 2005
		LOC_REGION_GENERAL("Region-General", ACEEntityType.LOC, false, true),
		
		// Facility
		FAC_PLANT("Plant", ACEEntityType.FAC, true, true),
		FAC_PATH("Path", ACEEntityType.FAC, true, true),
		// Only in 2004
		FAC_BUILDING("Building", ACEEntityType.FAC, true, false),
		FAC_SUBAREA_BUILDING("Subarea-Building", ACEEntityType.FAC, true, false),
		FAC_BOUNDED_AREA("Bounded-Area", ACEEntityType.FAC, true, false),
		FAC_CONDUIT("Conduit", ACEEntityType.FAC, true, false),
		FAC_BARRIER("Barrier", ACEEntityType.FAC, true, false),
		FAC_OTHER("Other", ACEEntityType.FAC, true, false),
		// Only in 2005
		FAC_AIRPORT("Airport", ACEEntityType.FAC, false, true),
		FAC_BUILDING_GROUNDS("Building-Grounds", ACEEntityType.FAC, false, true),
		FAC_SUBAREA_FACILITY("Subarea-Facility", ACEEntityType.FAC, false, true),
		
		// Vehicle
		VEH_AIR("Air", ACEEntityType.VEH, true, true),
		VEH_LAND("Land", ACEEntityType.VEH, true, true),
		VEH_WATER("Water", ACEEntityType.VEH, true, true),
		VEH_SUBAREA_VEHICLE("Subarea-Vehicle", ACEEntityType.VEH, true, true),
		// Only in 2004
		VEH_OTHER("Other", ACEEntityType.VEH, true, false),
		// Only in 2005
		VEH_UNDERSPECIFIED("Underspecified", ACEEntityType.VEH, false, true),
		
		// Weapon
		WEA_BLUNT("Blunt", ACEEntityType.WEA, true, true),
		WEA_EXPLODING("Exploding", ACEEntityType.WEA, true, true),
		WEA_SHARP("Sharp", ACEEntityType.WEA, true, true),
		WEA_CHEMICAL("Chemical", ACEEntityType.WEA, true, true),
		WEA_BIOLOGICAL("Biological", ACEEntityType.WEA, true, true),
		WEA_SHOOTING("Shooting", ACEEntityType.WEA, true, true),
		WEA_PROJECTILE("Projectile", ACEEntityType.WEA, true, true),
		WEA_NUCLEAR("Nuclear", ACEEntityType.WEA, true, true),
		// Only in 2004
		WEA_OTHER("Other", ACEEntityType.WEA, true, false),
		// Only in 2005
		WEA_UNDERSPECIFIED("Underspecified", ACEEntityType.WEA, false, true),
		;
		
		/** The original text specifying this subtype. Might be empty if there is no actual subtype. */
		public final String text;
		public final ACEEntityType type;
		public final boolean in2004;
		public final boolean in2005;
		
		private final static List<ACEEntitySubType> subtypesIn2004 = new ArrayList<ACEEntitySubType>();
		private final static List<ACEEntitySubType> subtypesIn2005 = new ArrayList<ACEEntitySubType>();
		
		private ACEEntitySubType(String text, ACEEntityType type, boolean in2004, boolean in2005){
			this.text = text;
			this.type = type;
			this.in2004 = in2004;
			this.in2005 = in2005;
		}
		
		public List<ACEEntitySubType> getACE2004SubTypes(){
			if(subtypesIn2004.size() == 0){
				for(ACEEntitySubType subtype: values()){
					if(subtype.in2004){
						subtypesIn2004.add(subtype);
					}
				}
			}
			return subtypesIn2004;
		}
		
		public List<ACEEntitySubType> getACE2005SubTypes(){
			if(subtypesIn2005.size() == 0){
				for(ACEEntitySubType subtype: values()){
					if(subtype.in2005){
						subtypesIn2005.add(subtype);
					}
				}
			}
			return subtypesIn2005;
		}
		
		public String toString(){
			return this.text;
		}
	}

	/**
	 * The entity class (only in ACE2005)
	 * SPC: A particular, specific, and unique real world entity
	 * GEN: A kind or type of entity rather than a specific entity
	 * NEG: A negatively quantified (usually generic) entity
	 * USP: An underspecified entity (e.g., modal/uncertain/...)
	 */
	public static enum ACEEntitySpecificity {
		/** A particular, specific, and unique real world entity */
		SPC,
		/** A kind or type of entity rather than a specific entity */
		GEN,
		/** A negatively quantified (usually generic) entity */
		NEG,
		/** An underspecified entity (e.g., modal/uncertain/...) */
		USP,
		
		/** In ACE2004 there is no specificity defined */
		NOT_TAGGED,
	}
	
	/** The type of the entity, i.e., PER, ORG, LOC, GPE, FAC, VEH, WEA **/
	public ACEEntityType type;
	/** The subtype of the entity, e.g., Government, Address, Building, Subarea-Vehicle, Projectile **/
	public ACEEntitySubType subtype;
	public ACEEntitySpecificity specificity;
	public String textualRepresentation;
	public List<ACEEntityMention> mentions;
	
	public ACEEntity(String id, String type, String subtype, String entityClass){
		this(id, type, subtype, entityClass, (String)null);
	}

	public ACEEntity(String id, String type, String subtype, String specificity, String textualRepresentation){
		this(id, type, subtype, specificity, textualRepresentation, new ArrayList<ACEEntityMention>());
	}
	
	public ACEEntity(String id, String type, String subtype, String specificity, String textualRepresentation, List<ACEEntityMention> mentions){
		super(id);
		this.type = ACEEntityType.valueOf(type);
		if(subtype == null || subtype.length() == 0){
			subtype = type;
		}
		this.subtype = ACEEntitySubType.valueOf((type+"_"+subtype).toUpperCase().replace("-", "_"));
		if(specificity == null || specificity.length() == 0){
			specificity = ACEEntitySpecificity.NOT_TAGGED.name();
		}
		this.specificity = ACEEntitySpecificity.valueOf(specificity);
		this.textualRepresentation = textualRepresentation;
		this.mentions = mentions;
	}
	
	public ACEEntity(ACEEntity entity){
		super(entity.id);
		this.type = entity.type;
		this.subtype = entity.subtype;
		this.specificity = entity.specificity;
		this.textualRepresentation = entity.textualRepresentation;
		this.mentions = new ArrayList<ACEEntityMention>();
		Collections.copy(this.mentions, entity.mentions);
	}
	
	public void addMention(ACEEntityMention mention){
		this.mentions.add(mention);
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder();
		result.append("[ID="+id+"]");
		result.append("[Type="+type+"]");
		result.append("[Subtype="+subtype+"]");
		result.append("[Specificity="+specificity+"]");
		return result.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ACEEntityMention> mentions() {
		return mentions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ACEEntityType type() {
		return type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ACEEntitySubType subtype() {
		return subtype;
	}

}
