package traitement;
import org.tartarus.snowball.ext.EnglishStemmer;

public class Stemming {
	private String data;
	public Stemming(String data) {
		this.data=data;
	}
	
	public String Text_lemm(){
		EnglishStemmer stemmer = new EnglishStemmer();
		stemmer.setCurrent(this.data.toLowerCase());
        stemmer.stem();	
        String result=stemmer.getCurrent();
        return result;
	}
}
