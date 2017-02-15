package justhalf.nlp.util;

import java.util.List;

import com.google.common.collect.Ordering;

public class TextUtils {
	
	public <E extends Comparable<E>> List<E> topK(Iterable<E> list, int k){
		Ordering<E> ordering = Ordering.natural();
		return ordering.greatestOf(list, k);
	}
	
}
