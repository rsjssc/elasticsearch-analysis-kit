package org.rsj.analysis.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.rsj.analysis.core.AnalysisContext;
import org.rsj.analysis.core.Configuration;
import org.rsj.analysis.core.Segmenter;
import org.rsj.analysis.core.Token;
import org.rsj.analysis.dic.Dictionaries;


public class testMyAnalysis {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Configuration configuration = new Configuration();
		System.out.println(configuration.getDictRoot());
		Dictionaries.initial(configuration);
//		Reader rd = new StringReader("学生会写文章");
//		Reader rd = new StringReader("处理机器事故三百五十二起");
//		Reader rd = new StringReader("中华人民共和国成立了");
		Reader rd = new StringReader("诺贝尔奖得主中华人的数量惨不忍睹");
//		Reader rd = new StringReader("东华山，位于京城东郊，是一处风水宝地。传言大黎国开国皇帝便是在此发现一处宝藏，从而领着一干兄弟推翻前朝，建立大黎王朝。现如今关于东华山的故事早就无人谈及，反倒是东华山西面");
//		Reader rd = new StringReader("QQ直播网更新的视频是最快、最清晰的，如果您在观看视频时发现播放太慢可以先暂停一会再观看，效果会更佳，我爱beijingtiananmenpapi酱very much i love you,this is 我的邮箱c35brsjssc@sina.com,还有450277781@qq.com,我的电话是15116984069");
//		Reader rd = new FileReader(new File("C:/Users/kit/Downloads/公主在上.txt"));
		Segmenter myAnalysis = new Segmenter(rd);
//		ik.reset(rd);
		try {
			Token token = myAnalysis.next();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try {
//			while ((token = myAnalysis.next()) != null) {
//				System.out.println(token.toString());
//				
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		AnalysisContext context = myAnalysis.getContext();
//		QuickSortSet orgLexemes = context.getOrgLexemes();
//		System.out.println(orgLexemes.size());
//		orgLexemes.printlnAllCell();
	}

}
