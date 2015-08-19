package justhalf.nlp.test;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Generic helper class for test classes
 * @author Aldrian Obaja <aldrianobaja.m@gmail.com>
 *
 */
public class TestHelper {
	
	protected static String getNextLine(Scanner sc){
		System.out.print(">>> ");
		try{
			return sc.nextLine();
		} catch (NoSuchElementException e){
			return null;
		}
	}

}
