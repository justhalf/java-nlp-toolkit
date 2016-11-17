package justhalf.nlp.reader.acereader;

public class ACEEventArgumentSpec {
	
	public final String roleName;
	public final ACEEventArgumentType[] argTypes;

	public ACEEventArgumentSpec(String roleName, ACEEventArgumentType... argTypes) {
		this.roleName = roleName;
		this.argTypes = argTypes;
	}

}
