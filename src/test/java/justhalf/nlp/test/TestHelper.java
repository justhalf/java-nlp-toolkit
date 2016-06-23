package justhalf.nlp.test;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import junit.framework.ComparisonCompactor;

/**
 * Generic helper class for test classes
 */
public class TestHelper {
	
	protected static void addCount(Map<String, Integer> counter, String key){
		int count = counter.getOrDefault(key, 0);
		counter.put(key, count+1);
	}
	
	protected static String messageOnNotEqual(String expected, String actual){
		return new ComparisonCompactor(20, expected, actual).compact("");
	}
	
	protected static String getNextLine(Scanner sc){
		System.out.print(">>> ");
		try{
			return sc.nextLine();
		} catch (NoSuchElementException e){
			return null;
		}
	}

}
