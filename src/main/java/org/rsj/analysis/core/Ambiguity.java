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
		ConflictTokensList conflictList = context.getOrgListsWhitConflict().pollFirst();
		while(conflictList != null) {
//			System.out.println("do some judge work");
			ConflictTokensList resultList = judge(conflictList);
//			System.out.println("best judge result");
//			for (Token token : resultList.getConflictList()) {
//				System.out.println(token.toString());
//			}
			context.addToResults(resultList);
//			if(segment.getConflictList().size() == 1){//conflictList只有一个词没有歧义
//				context.addToResults(conflictList);
//			}else{
//				//对当前的conflictList进行歧义处理
//				ConflictTokensList judgeResultList = this.judge(context, conflictList);
//				//输出歧义处理结果judgeResult
//				context.addToResults(judgeResultList);
//			}
			conflictList = context.getOrgListsWhitConflict().pollFirst();
		}
	}
//	public void process(AnalysisContext context){
//		SentenceSegment conflictList = context.get().pollFirst();
//		while(conflictList != null) {
//			if(conflictList.getConflictList().size() == 1){//conflictList只有一个词没有歧义
//				context.addToResults(conflictList);
//			}else{
//				//对当前的conflictList进行歧义处理
//				ConflictTokensList judgeResultList = this.judge(context, conflictList);
//				//输出歧义处理结果judgeResult
//				context.addToResults(judgeResultList);
//			}
//			conflictList = context.getOrgListsWhitConflict().pollFirst();
//		}
//	}
	
//	private ConflictTokensList judge(AnalysisContext context, ConflictTokensList conflictList) {
//		System.out.println("do some judge work");
//		//判断歧义
//		ConflictTokensList judgeResultList = new ConflictTokensList();
//		System.out.println("after the judge");
//		for (Token token : judgeResultList.getConflictList()) {
//			token.setText(String.valueOf(context.getSegmentBuff() , token.getBegin() , token.getLength()));
//			System.out.println(token);
//		}
//		return judgeResultList;
//	}
	
	/**
	 * 歧义识别
	 * @param lexemeCell 歧义路径链表头
	 * @param fullTextLength 歧义路径文本长度
	 * @return
	 */
	private ConflictTokensList judge(ConflictTokensList conflictList){ //贪心算法消除歧义
//		System.out.println("do some judging work");
		//候选路径集合
		TreeSet<ConflictTokensList> pathOptions = new TreeSet<ConflictTokensList>();
		//候选结果路径
		ConflictTokensList option = new ConflictTokensList();
		//将没有歧义的词放入option
		
		//对crossPath进行一次遍历,同时返回本次遍历中有冲突的token块，未冲突加入option
		Stack<Integer> tokenStack = this.forwardPath(conflictList , 0, option);
		
//		System.out.println("find a new option path:");
//		option.printAllTokens();
//		System.out.println("add option path to pathOption");
//		System.out.println("the Conflict token stack size is:"+tokenStack.size());
//		System.out.println(tokenStack.toString());
		
		//当前词元链并非最理想的，加入候选路径集合
		pathOptions.add(option.copy());
		
		//存在歧义词，处理
		int conflictIndex;
		while(!tokenStack.isEmpty()){//存在歧义词时
//			System.out.println("the Conflict token stack is not empty,pop stack back path");
			conflictIndex = tokenStack.pop();
//			System.out.println("the conflict token is: "+conflictList.getConflictList().get(conflictIndex)+",will remove it's conflict tokens in option");
			//回滚词元链
			this.backPath(conflictList.getConflictList().get(conflictIndex), option);
			//从歧义词位置开始，递归，生成可选方案
			this.forwardPath(conflictList , conflictIndex, option);
			pathOptions.add(option.copy());//add的时候调用LexemePath的compareTo方法
			
//			System.out.println("find a new option path:");
//			option.printAllTokens();
//			System.out.println("add option path to pathOption");
		}
		
//		System.out.println("ambiguity process finish, see all pathOptions");
//		Iterator<ConflictTokensList> it = pathOptions.iterator();
//		while (it.hasNext()) {
//			System.out.println("this is one option");
//			for (Token token : it.next().getConflictList()) {
//				System.out.println(token.toString());
//			}
//			System.out.println();
//		}
		
		
		//返回集合中的最优方案
		return pathOptions.first();

	}
	
	/**
	 * 向前遍历，添加词元，构造一个无歧义词元组合
//	 * @param LexemePath path
	 * @return
	 */
	private Stack<Integer> forwardPath(ConflictTokensList conflictList , int conflictIndex, ConflictTokensList option){
		//发生冲突的Lexeme栈
		Stack<Integer> conflictStack = new Stack<Integer>();
//		Token c = conflictList.getConflictList().get(conflictIndex);
		//迭代遍历Lexeme链表
		while(conflictIndex < conflictList.getConflictList().size()){
			if(!option.addNotConflictToken(conflictList.getConflictList().get(conflictIndex))){
				//词元交叉，添加失败则加入lexemeStack栈
				conflictStack.push(conflictIndex);
			}
//			c = c.getNext();
			conflictIndex++;
		}
		return conflictStack;
	}
	
	/**
	 * 回滚词元链，直到它能够接受指定的词元
//	 * @param lexeme
	 * @param l
	 */
	private void backPath(Token token  , ConflictTokensList option){
		while(option.conflictCheck(token)){
			Token remove = option.removeLastToken();
//			System.out.println("remove "+remove.toString()+" ,from option");
		}
		
	}
}
