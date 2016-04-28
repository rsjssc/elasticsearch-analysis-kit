package org.rsj.analysis.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.rsj.analysis.core.Configuration;
import org.rsj.analysis.dic.Dictionaries;
import org.rsj.analysis.dic.DoubleArrayTrie;



public class testDAT {

//	public static void main(String[] args) throws IOException {
//		Configuration configuration = new Configuration();
//		System.out.println(configuration.getDictRoot());
//		Dictionaries.initial(configuration);
//	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader reader = new BufferedReader(new FileReader("C:/Users/kit/Desktop/littleDic.txt"));
        String line;
        List<String> words = new ArrayList<String>();
        Set<Character> charset = new HashSet<Character>();
        while ((line = reader.readLine()) != null)
        {
            words.add(line);
            // 制作一份码表debug
            for (char c : line.toCharArray())
            {
                charset.add(c);
            }
        }
        reader.close();
        {
            String infoCharsetValue = "";
            String infoCharsetCode = "";
            for (Character c : charset)
            {
                infoCharsetValue += c.charValue() + "    ";
                infoCharsetCode += (int)c.charValue() + " ";
            }
            infoCharsetValue += '\n';
            infoCharsetCode += '\n';
            System.out.print(infoCharsetValue);
            System.out.print(infoCharsetCode);
            System.out.println("");
            DoubleArrayTrie dat = new DoubleArrayTrie();
            System.out.println("是否错误: " + dat.build(words));
            System.out.println(dat);
            
            System.out.println("");System.out.println("");System.out.println("");
//            String text = "一举成名天下知papi酱是一个集美貌与才华于一身的女子万";
            String text = "fasdfsdcdspapi酱cdscsaca";
            for(int i = 0; i < text.length(); i++){
            	List<Integer> integerList = dat.commonPrefixSearch(text.substring(i).toCharArray());
                for (int index : integerList)
                {
                    System.out.println(words.get(index));
                }
            }
        }
	}
	
	public void addWordsMakeDic() throws IOException {
		List<String> words = new ArrayList<String>();
	    // 这个字典如果要加入新词必须按字典序，参考下面的代码
	    words.add("一举");
	      words.add("一举一动");
	      words.add("一举成名");
	      words.add("一举成名天下知");
	      words.add("天下");
	      words.add("举");
	      words.add("一");
	      words.add("知");
	      words.add("万能");
	      words.add("万能胶");
	      Collections.sort(words);
	      BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/kit/Desktop/littleDic.txt", false));
	      for (String w : words)
	      {
	          writer.write(w);
	          writer.newLine();
	      }
	      System.out.println("字典词条：" + words.size());
	      writer.close();
	}
}
