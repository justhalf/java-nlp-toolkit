package justhalf.nlp.test;

import java.util.Scanner;

import justhalf.nlp.postagger.POSTagger;
import justhalf.nlp.postagger.StanfordPOSTagger;

public class POSTaggerTest extends TestHelper{
	public static void main(String[] args){
		POSTagger tagger = new StanfordPOSTagger();
		Scanner sc = new Scanner(System.in);
		String line;
		while((line = getNextLine(sc)) != null){
			System.out.println(tagger.tag(line));
		}
		sc.close();
	}
}
