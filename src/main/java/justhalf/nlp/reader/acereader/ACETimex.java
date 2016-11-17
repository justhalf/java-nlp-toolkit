package justhalf.nlp.reader.acereader;

import java.util.ArrayList;
import java.util.List;

import justhalf.nlp.reader.acereader.ACEValue.ACEValueSubType;
import justhalf.nlp.reader.acereader.ACEValue.ACEValueType;

public class ACETimex extends ACEObject {
	
	public static enum ACETimexAnchorDir {
		WITHIN,
		STARTING,
		ENDING,
		AS_OF,
		BEFORE,
		AFTER,
		
		NOT_TAGGED,
		;
	}
	
	public static enum ACETimexMod {
		BEFORE,
		AFTER,
		ON_OR_BEFORE,
		ON_OR_AFTER,
		LESS_THAN,
		MORE_THAN,
		EQUAL_OR_LESS,
		EQUAL_OR_MORE,
		START,
		MID,
		END,
		APPROX,
		
		NOT_TAGGED,
		;
	}
	
	public String val;
	public final ACEValueType type = ACEValueType.TIME;
	public final ACEValueSubType subtype = ACEValueSubType.TIME_TIME;
	public ACETimexMod mod;
	public String anchorVal;
	public ACETimexAnchorDir anchorDir;
	public boolean set;
	public String comment;
	public List<ACETimexMention> mentions;

	public ACETimex(String id, String val, String mod, String anchorVal, String anchorDir, String set, String comment) {
		super(id);
		this.val = val;
		this.mod = (mod == null || mod.length() == 0) ? ACETimexMod.NOT_TAGGED : ACETimexMod.valueOf(mod);
		this.anchorVal = anchorVal;
		this.anchorDir = (anchorDir == null || anchorDir.length() == 0) ? ACETimexAnchorDir.NOT_TAGGED : ACETimexAnchorDir.valueOf(anchorDir);
		this.set = set.equals("YES");
		this.comment = comment;
		this.mentions = new ArrayList<ACETimexMention>();
	}
	
	public void addMention(ACETimexMention mention){
		this.mentions.add(mention);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ACETimexMention> mentions() {
		return mentions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ACEValueType type() {
		return type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ACEValueSubType subtype() {
		return subtype;
	}

}
