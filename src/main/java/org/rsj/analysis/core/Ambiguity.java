package org.rsj.analysis.core;

import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;


public class Ambiguity {
	/**
	 * 分词歧义处理
	 * 根据词性和规则
	 */
	public void process(AnalysisContext context){
//		SentenceSegment segment = context.getOrgListsWhitConflict().pollFirst();
		//原来，是以conflictList来做
//		ConflictTokensList conflictList = context.getOrgListsWhitConflict().pollFirst();
		//现在把一个句子片段转换成conflictList
		ConflictTokensList segment = context.getOrgSentenceSegments().pollFirst();
		
//		while(conflictList != null) {
		while(segment != null && segment.getSize() > 0) {
			if(segment.getConflictList().size() == 1){//conflictList只有一个词没有歧义
//				System.out.println("no ambiguity!");
				context.addToResults(segment);
			}else {
				ConflictTokensList resultList = judgeByWordType(segment,context);
//				ConflictTokensList resultList = judgeByProfile(conflictList,context);
//				System.out.println("best judge result");
//				for (Token token : resultList.getConflictList()) {
//					System.out.println(token.toString());
//				}
				context.addToResults(resultList);
			}
			//old
//			conflictList = context.getOrgListsWhitConflict().pollFirst();
			//new
			segment = context.getOrgSentenceSegments().pollFirst();
		}
	}
	
//	/**
//	 * 歧义识别
//	 * 根据路径中token的统计特征
//	 * @return
//	 */
//	private ConflictTokensList judgeByProfile(ConflictTokensList conflictList, AnalysisContext context){ //根据token的数学特征，长度，位置，覆盖范围等，利用贪心算法消除歧义
//		System.out.println("do some judging work,judgeByProfile");
//		//候选路径集合
//		TreeSet<ConflictTokensList> pathOptions = new TreeSet<ConflictTokensList>();
//		//候选结果路径
//		ConflictTokensList option = new ConflictTokensList(conflictList.getSegmentCount());
//		//将没有歧义的词放入option
//		
//		//对crossPath进行一次遍历,同时返回本次遍历中有冲突的token块，未冲突加入option
//		Stack<Integer> tokenStack = this.forwardPath(conflictList , 0, option);
//		
//		System.out.println("find a new option path:");
//		context.printTokenList(option.getConflictList());
//		System.out.println("add option path to pathOption");
//		System.out.println("the Conflict token stack size is:"+tokenStack.size());
//		System.out.println(tokenStack.toString());
//		
//		//当前词元链并非最理想的，加入候选路径集合
//		pathOptions.add(option.copy());
//		
//		//存在歧义词，处理
//		int conflictIndex;
//		while(!tokenStack.isEmpty()){//存在歧义词时
//			System.out.println("the Conflict token stack is not empty,pop stack back path");
//			conflictIndex = tokenStack.pop();
//			System.out.println("the conflict token is: "+conflictList.getConflictList().get(conflictIndex)+" ,will remove it's conflict tokens in option");
//			//回滚词元链
//			this.backPath(conflictList.getConflictList().get(conflictIndex), option);
//			//从歧义词位置开始，递归，生成可选方案
//			this.forwardPath(conflictList , conflictIndex, option);
//			pathOptions.add(option.copy());//add的时候调用LexemePath的compareTo方法
//			
//			System.out.println("find a new option path:");
//			context.printTokenList(option.getConflictList());
//			System.out.println("add option path to pathOption");
//		}
//		
//		System.out.println("ambiguity process finish, see all pathOptions");
//		Iterator<ConflictTokensList> it = pathOptions.iterator();
//		while (it.hasNext()) {
//			ConflictTokensList optionPath = it.next();
//			System.out.println("this is one option");
//			System.out.println(optionPath.getSize());
//			for (Token token : optionPath.getConflictList()) {
//				System.out.println(token.toString());
//			}
//			System.out.println();
//		}
//		
//		
//		//返回集合中的最优方案
//		return pathOptions.first();
//
//	}
//	
//	/**
//	 * 向前遍历，添加词元，构造一个无歧义词元组合
////	 * @param LexemePath path
//	 * @return
//	 */
//	private Stack<Integer> forwardPath(ConflictTokensList conflictList , int conflictIndex, ConflictTokensList option){
//		//发生冲突的Lexeme栈
//		Stack<Integer> conflictStack = new Stack<Integer>();
////		Token c = conflictList.getConflictList().get(conflictIndex);
//		//迭代遍历conflictList链表
//		while(conflictIndex < conflictList.getConflictList().size()){
//			if(!option.addNotConflictToken(conflictList.getConflictList().get(conflictIndex))){
//				//词元交叉，添加失败则加入lexemeStack栈
//				conflictStack.push(conflictIndex);
//			}
////			c = c.getNext();
//			conflictIndex++;
//		}
//		return conflictStack;
//	}
//	
//	/**
//	 * 回滚词元链，直到它能够接受指定的词元
////	 * @param lexeme
//	 * @param l
//	 */
//	private void backPath(Token token  , ConflictTokensList option){
//		while(option.conflictCheck(token)){
//			Token remove = option.removeLastToken();
////			System.out.println("remove "+remove.toString()+" ,from option");
//		}
//		
//	}
	
	/**
	 * 将歧义部分组成完成的句子成分
	 * 意图是利用path中token的词性组合，但现在也只是按照token的统计特征
	 * @param conflictList
	 * @param context
	 * @return
	 */
	private ConflictTokensList judgeByWordType(ConflictTokensList conflictList, AnalysisContext context){ 
//		System.out.println("do some judging work,judgeByWordType,this org list is");
//		context.printTokenList(conflictList.getConflictList());
		
		//候选路径集合
		TreeSet<ConflictTokensList> pathOptions = new TreeSet<ConflictTokensList>();
		//候选结果路径
		ConflictTokensList option = new ConflictTokensList(conflictList.getSegmentCount());
		//将没有歧义的词放入option
		findAllOptionPath(context, pathOptions, conflictList, 0, 0, option);
		
//		System.out.println("ambiguity process finish, see all pathOptions");
//		Iterator<ConflictTokensList> it = pathOptions.iterator();
//		while (it.hasNext()) {
//			System.out.println("this is one option");
//			StringBuffer sb = new StringBuffer();
//			for (Token token : it.next().getConflictList()) {
//				System.out.println(token.toString());
//				sb.append(token.getTextType());
//			}
//			System.out.println("this is option path is make: "+sb.toString());
//			System.out.println();
//		}
//		
		
		//返回集合中的最优方案
		return pathOptions.first();
	}
	/**
	 * 有冗余，option中第一个token的起始位置如果不是conflictList的begin其实就不用遍历了
	 * @param pathOptions
	 * @param conflictList
	 * @param currentIndex
	 * @param currentLength
	 * @param option
	 */
	private void findAllOptionPath(AnalysisContext context, TreeSet<ConflictTokensList> pathOptions, ConflictTokensList conflictList, int currentIndex, int currentLength, ConflictTokensList option) {
		if (option.getConflictList().peek() != null && option.getConflictList().peek().getBegin() != conflictList.getBegin()) {
			return;
		}
		if(currentLength == conflictList.getLength()){
//			System.out.println("find a new option path:");
//			context.printTokenList(option.getConflictList());
			pathOptions.add(option.copy());
			return;
		}
		while (currentIndex < conflictList.getSize()) {
			Token token = conflictList.getConflictList().get(currentIndex);
			if(option.getBegin() == -1 || option.getEnd() == token.getBegin()) {
//				option.addNotConflictToken(token);
				option.addTokenWhitoutCheck(token);//肯定不冲突，因为首尾相接
//				System.out.println("add a token: "+token.toString());
//				System.out.println("new currentLength: "+(currentLength + token.getLength())+", conflictList's length: "+conflictList.getLength());
				findAllOptionPath(context, pathOptions, conflictList, currentIndex + 1, currentLength + token.getLength(), option);
				option.removeLastToken();
			}
			
			currentIndex++;
		}
		
	}
}
