package org.rsj.analysis.dic;
/**
 * 有词性的词典中的词
 * @author kit
 *
 */
public class Word implements Comparable<Word>{
	private String word;
	private String wordType;
	/**
	 * 词典中的一行构成一个词
	 * @param line
	 */
	public Word(String line) {
		String[] t = line.split(" ");
    	if(t.length != 2){System.out.println("dict wrong: "+ line);return;}
    	this.word = t[0];
    	this.wordType = t[1];
	}
	public String getWord() {
		return word;
	}
	public String getWordType() {
		return wordType;
	}
	@Override
	public int compareTo(Word o) {
		byte[] left = this.word.getBytes();
		byte[] right = o.getWord().getBytes();
		// TODO Auto-generated method stub
		for (int i = 0, j = 0; i < left.length && j < right.length; i++, j++) {
            int a = (left[i] & 0xff);
            int b = (right[j] & 0xff);
            if (a != b) {
                return a - b;
            }
        }
        return left.length - right.length;
	}
	@Override
	public String toString() {
		return "Word [word=" + word + ", wordType=" + wordType + "]";
	}
	
}
