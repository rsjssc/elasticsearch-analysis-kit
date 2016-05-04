package org.rsj.analysis.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.rsj.analysis.core.AnalysisContext;
import org.rsj.analysis.core.Configuration;
import org.rsj.analysis.core.Segmenter;
import org.rsj.analysis.core.Token;
import org.rsj.analysis.dic.Dictionaries;


public class testMyAnalysis {

	public static void main(String[] args) throws IOException {
		Writer out = new FileWriter(new File("C:/Users/kit/Desktop/my-analysis_token.txt"), false);
		BufferedWriter bw = new BufferedWriter(out);
		int segmentCount = 1;
		// TODO Auto-generated method stub
		Configuration configuration = new Configuration();
		System.out.println(configuration.getDictRoot());
		Dictionaries.initial(configuration);
//		Reader rd = new StringReader("第6章");//错误
//		Reader rd = new StringReader("处理机器事故三百五十二起");
//		Reader rd = new StringReader("计算机会下象棋");//错误
//		Reader rd = new StringReader("中华人民共和国成立了,计算机会下象棋,学生会写文章,处理机器事故三百五十二起");
//		Reader rd = new StringReader("诺贝尔奖得主中华人的数量连一个都没有，和和美美团团圆圆，更快捷不行动");
//		Reader rd = new StringReader("东华山，位于京城东郊，是一处风水宝地。传言大黎国开国皇帝便是在此发现一处宝藏，从而领着一干兄弟推翻前朝，建立大黎王朝。现如今关于东华山的故事早就无人谈及，反倒是东华山西面");
//		Reader rd = new StringReader("我爱beijingtiananmenpapi酱very much i love you,this is 我的邮箱c35brsjssc@sina.com,还有450277781@qq.com,我的电话是15116984069");
		Reader rd = new FileReader(new File("C:/Users/kit/Downloads/公主在上.txt"));
//		Reader rd = new FileReader(new File("C:/Users/kit/Downloads/傲世九重天.txt"));
		Segmenter myAnalysis = new Segmenter(rd);
//		ik.reset(rd);
//		try {
//			Token token = myAnalysis.next();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		long start = System.currentTimeMillis();
		Token token ;
		int count = 0;
		try {
			while ((token = myAnalysis.next()) != null) {
//				System.out.println(token.toString());
				if (segmentCount != token.getSegmentCount()) {
					bw.newLine();
					segmentCount = token.getSegmentCount();
				}
				bw.write(token.getText()+" ");
				count++;
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("total words count:"+count);
		System.out.println("total time: "+ (System.currentTimeMillis() - start));
		
//		List<String> result;
//		try {
//			result = myAnalysis.parseAll();
//			for (String string : result) {
//				System.out.println(string);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
//		AnalysisContext context = myAnalysis.getContext();
//		QuickSortSet orgLexemes = context.getOrgLexemes();
//		System.out.println(orgLexemes.size());
//		orgLexemes.printlnAllCell();
		bw.close();
	}

}
