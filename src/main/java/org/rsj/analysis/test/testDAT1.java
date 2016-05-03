package org.rsj.analysis.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import org.rsj.analysis.core.CharacterUtil;
import org.rsj.analysis.dic.DoubleArrayTrie1;
import org.rsj.analysis.dic.Word;

public class testDAT1 {
	private static final String[] KEYS = {"abcdefg","ab","abcd","中","中华","中华人民","中华人民共和国","华","华人","人民","人民共和国","共和国","共和","共","和","国"};
    static List<Word> validKeys ;
    
	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		
		validKeys = generateValidKeys(KEYS);
		
		System.out.println("size: "+validKeys.size());
//        Set<byte[]> invalidKeys = generateInvalidKeys(10, validKeys);
		byte[] matchKey = new String("中华人民共和国").getBytes();
//		byte[] matchKey = new String("abcdefg").getBytes();
		System.out.println("test : "+Arrays.toString(CharacterUtil.charToBytes('1')));
		System.out.println("test : "+Arrays.toString(CharacterUtil.charToBytes('号')));
		System.out.println("test : "+Arrays.toString(CharacterUtil.charToBytes('店')));
		System.out.println("test : "+Arrays.toString(CharacterUtil.charToBytes('A')));
		System.out.println("test : "+Arrays.toString(CharacterUtil.charToBytes('型')));
        testDarts(validKeys, matchKey);
	}
	
	    
	    
	    private static void testDarts(List<Word> Words, byte[] matchKey) {
	        byte[][] byteKeys = new byte[Words.size()][];
	        for (int i = 0; i < byteKeys.length; i++) {
	        	byteKeys[i] = Words.get(i).getWord().getBytes();
	        	System.out.println("String: "+Words.get(i).getWord()+", byte size: "+byteKeys[i].length+",  byte[] : "+Arrays.toString(byteKeys[i]));
			}
	        long start = System.currentTimeMillis();
	        
//	        DoubleArrayTrie1 dict = new DoubleArrayTrie1();
//	        dict.build(byteKeys);
//	        System.out.println("bulid time: "+(System.currentTimeMillis() - start));
//	        testDict(dict, byteKeys, null, matchKey);
	    }
	    
	    private static void testDict(DoubleArrayTrie1 dict,
	    byte[][] keys, String[] values, byte[] matchKey) {
	    	int result = dict.exactMatchSearch(matchKey);
	    	System.out.println("exactMatch: "+result);
//	    	List<Integer> results = dict.commonPrefixSearch(
//	    			matchKey, 0);
//	    	org.rsj.analysis.dic.DoubleArrayTrie1.Pair<Integer, Integer>[] paris = (Pair<Integer, Integer>[]) results.toArray();
//	    	for (int i = 0; i < paris.length; i++) {
//				System.out.println(paris[i].first);
//			}
//	    	Iterator<Integer> it = results.iterator();
//	    	while (it.hasNext()) {
//	    		Word match = validKeys.get(it.next());
//				System.out.println(match.getWord()+" , "+match.getWordType());
//				
//			}
	    }

//	    private static void testCommonPrefixSearch(DoubleArrayTrie1 dict,
//	            byte[][] keys, int[] values) {
//	        for (int i = 0; i < keys.length; ++i) {
//	            List<Pair<Integer, Integer>> results = dict.commonPrefixSearch(
//	                    keys[i], 0, MAX_NUM_RESULTS);
//	            
//	            assertTrue(results.size() >= 1);
//	            assertTrue(results.size() < 10);
//	            
//	            assertEquals(keys[i].length, results.get(results.size() - 1).first.intValue());
//	            assertEquals(values[i], results.get(results.size() - 1).second.intValue());
//	        }
//	    }
	    
	    private static List<Word> generateValidKeys(String[] keys) throws UnsupportedEncodingException {
	    	long start = System.currentTimeMillis();
	    	List<Word> Words = new ArrayList<Word>();
	    	String Path = "D:/workspace/my-analysis/target/config/main1.dict";
	    	BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(Path));
				String line;
		        while ((line = reader.readLine()) != null)
		        {
		        	Word newWord = new Word(line);
		        	Words.add(newWord);
		        }
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if(reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			System.out.println("word's size:"+Words.size());
			System.out.println("read dict time: "+(System.currentTimeMillis() - start));
			start = System.currentTimeMillis();
			Collections.sort(Words);
			System.out.println("sort time: "+(System.currentTimeMillis() - start));
			
//			TreeSet<Word> validKeys = new TreeSet<Word>();
//	        for (int i = 0; i < Words.size(); i++) {
//	        	if (validKeys.contains(Words.get(i))) {
//					System.out.println("has contain: "+Words.get(i).toString());
//				}
//	        	validKeys.add(Words.get(i));
//			}
//	        for (int i = 0; i < keys.length; i++) {
//	        	validKeys.add(keys[i].getBytes("UTF-8"));
//			}
	        return Words;
	    }
}
