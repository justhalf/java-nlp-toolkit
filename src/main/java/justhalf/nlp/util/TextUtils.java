package justhalf.nlp.util;

import edu.stanford.nlp.util.StringUtils;

public class TextUtils {
	
	public static String join(String[] tokens, String glue){
		return StringUtils.join(tokens, glue);
	}
}
