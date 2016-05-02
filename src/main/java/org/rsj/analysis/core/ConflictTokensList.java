package org.rsj.analysis.core;

import java.util.Iterator;
import java.util.LinkedList;



public class ConflictTokensList implements Comparable<ConflictTokensList>{
	private LinkedList<Token> conflictList;
	
	//这个冲突链表的的起始和终止位置
	private int begin;
	private int end;
	private int size;
	
	public ConflictTokensList() {
		// TODO Auto-generated constructor stub
		this.conflictList = new LinkedList<Token>();
		this.begin = -1;
		this.end = -1;
		this.size = 0;
	}
	
	public ConflictTokensList(Token token) {
		// TODO Auto-generated constructor stub
		this.conflictList = new LinkedList<Token>();
		this.begin = token.getBegin();
		this.end = token.getBegin() + token.getLength();
		this.conflictList.add(token);
		this.size = 1;
	}
	
	/**
	 * 冲突链表由小到大排列，起始位置越靠前越小，长度越长越小
	 * @param token
	 * @return
	 */
	public boolean addConflictToken(Token token) {
		if(this.conflictList.isEmpty()){
			this.addToken(token);
			this.begin = token.getBegin();
			this.end = token.getBegin() + token.getLength();
			this.size = 1;
			return true;
			
		}else if(this.conflictCheck(token)){
			this.addToken(token);
			if(token.getBegin() + token.getLength() > this.end){
				this.end = token.getBegin() + token.getLength();
			}
			this.size++;
			return true;
			
		}else{
			return  false;
			
		}
	}
	
	/**
	 * 向ConflictTokenList追加不相交的Token
	 * @param token
	 * @return 
	 */
	boolean addNotConflictToken(Token token){
		if(this.conflictList.isEmpty()){
			this.addToken(token);
			this.begin = token.getBegin();
			this.end = token.getBegin() + token.getLength();
			this.size = 1;
			return true;
		}else if(this.conflictCheck(token)){
			return  false;
		}else{
			this.addToken(token);
//			this.payloadLength += lexeme.getLength();
//			Lexeme head = this.peekFirst();
			this.begin = this.conflictList.peekFirst().getBegin();
//			Lexeme tail = this.peekLast();
			this.end = this.conflictList.peekLast().getBegin()+this.conflictList.peekLast().getLength();
			this.size++;
			return true;
			
		}
	}
	
	public boolean conflictCheck(Token token) {
		return (token.getBegin() >= this.begin && token.getBegin() < this.end) || 
				(this.begin >= token.getBegin() && this.begin < token.getBegin() + token.getLength());
	}
	
	/**
	 * 将token加到List中
	 * @param token
	 */
	private void addToken(Token token) {
		if(this.conflictList.isEmpty()){
			conflictList.add(token);
			return;
		}
		if(this.conflictList.peekLast().compareTo(token) < 0){//链表中最后一个词比token小，词元接入链表尾部
			conflictList.addLast(token);
		}else if(this.conflictList.peekFirst().compareTo(token) > 0){//链表中最后一个词比token大，词元接入链表头部
			conflictList.addFirst(token);
		}else{	
			System.out.println("inser");
			int index = conflictList.size() - 1;
			while(index > 0 && conflictList.get(index).compareTo(token) > 0) {
				index--;
			}
			if (conflictList.get(index).compareTo(token) < 0) {//防止等于0，相等不加
				conflictList.add(index + 1, token);
			}
		}
	}

	public Token removeLastToken() {
		Token removeToken = this.conflictList.pollLast();
		if(this.conflictList.isEmpty()){
			this.begin = -1;
			this.end = -1;
			this.size = 0;			
		}else{		
			Token newTail = this.conflictList.peekLast();
			this.end = newTail.getBegin() + newTail.getLength();
			this.size--;
		}
		return removeToken;
		
	}
	public LinkedList<Token> getConflictList() {
		return conflictList;
	}

	public void setConflictList(LinkedList<Token> conflictList) {
		this.conflictList = conflictList;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}
	
	public int getSize() {
		return size;
	}
	
	/**
	 * 注意：这里是浅拷贝，不过因为我们不会修改token里的值，所以浅拷贝足以
	 * @return
	 */
	ConflictTokensList copy(){
		ConflictTokensList theCopy = new ConflictTokensList();
		theCopy.getConflictList().addAll(this.conflictList);
		theCopy.begin = this.begin;
		theCopy.end = this.end;
//		Cell c = this.getHead();
//		while( c != null && c.getLexeme() != null){
//			theCopy.addLexeme(c.getLexeme());
//			c = c.getNext();
//		}
		return theCopy;
	}

	/**
	 * X权重（词元长度积）
	 * @return
	 */
	int getXWeight(){
		int product = 1;
		for (Token token : conflictList) {
			product *= token.getLength();
		}
		return product;
	}
	
	/**
	 * 词元位置权重
	 * @return
	 */
	int getPWeight(){
		int pWeight = 0;
		int p = 0;
		for (Token token : conflictList) {
			p++;
			pWeight += p * token.getLength();
		}
		return pWeight;		
	}
	
	/*
	 * 优先级
	 * 覆盖的范围大的List小，即排在前面
	 * 范围一样，则比较token个数，token越小越好
	 */
	public int compareTo(ConflictTokensList o) {//歧义消除的关键算法
		//比较有效文本长度
		if((this.end - this.begin) > (o.getEnd() - o.getBegin())){
			return -1;
		}else if((this.end - this.begin) > (o.getEnd() - o.getBegin())){
			return 1;
		}else{
			//比较词元个数，越少越好
			if(this.size < o.getSize()){
				return -1;
			}else if (this.size > o.getSize()){
				return 1;
			}else{
				//根据统计学结论，逆向切分概率高于正向切分，因此位置越靠后的优先
				if(this.end > o.getEnd()){
					return -1;
				}else if(this.end < o.getEnd()){
					return 1;
				}else{
					//词长越平均越好
					if(this.getXWeight() > o.getXWeight()){
						return -1;
					}else if(this.getXWeight() < o.getXWeight()){
						return 1;
					}else {
						//词元位置权重比较
						if(this.getPWeight() > o.getPWeight()){
							return -1;
						}else if(this.getPWeight() < o.getPWeight()){
							return 1;
						}
						
					}
				}
			}
		}
		return 0;
	}
	
	void printAllTokens() {
		for (Token token : conflictList) {
			System.out.println(token);
		}
	}
}
