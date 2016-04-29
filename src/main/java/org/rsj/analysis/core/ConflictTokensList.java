package org.rsj.analysis.core;

import java.util.Iterator;
import java.util.LinkedList;

public class ConflictTokensList {
	private LinkedList<Token> conflictList;
	
	//这个冲突链表的的起始和终止位置
	private int begin;
	private int end;
	
	public ConflictTokensList() {
		// TODO Auto-generated constructor stub
		this.conflictList = new LinkedList<Token>();
		this.begin = -1;
		this.end = -1;
	}
	
	public ConflictTokensList(Token token) {
		// TODO Auto-generated constructor stub
		this.conflictList = new LinkedList<Token>();
		this.begin = token.getBegin();
		this.end = token.getBegin() + token.getLength();
		this.conflictList.add(token);
	}
	
	
	public boolean addConflictToken(Token token) {
		if(this.conflictList.isEmpty()){
			this.addToken(token);
			this.begin = token.getBegin();
			this.end = token.getBegin() + token.getLength();
			return true;
			
		}else if(this.conflictChech(token)){
			this.addToken(token);
			if(token.getBegin() + token.getLength() > this.end){
				this.end = token.getBegin() + token.getLength();
			}
			return true;
			
		}else{
			return  false;
			
		}
	}
	
	private boolean conflictChech(Token token) {
		return (token.getBegin() >= this.begin && token.getBegin() < this.end) || 
				(this.begin >= token.getBegin() && this.begin < token.getBegin() + token.getLength());
	}
	
	/**
	 * 这里可能有问题
	 * @param token
	 */
	private void addToken(Token token) {
		if(this.conflictList.isEmpty()){
			conflictList.add(token);
			return;
		}
		if(this.conflictList.peekLast().compareTo(token) < 0){//词元接入链表尾部
			conflictList.addLast(token);
		}else if(this.conflictList.peekFirst().compareTo(token) > 0){//词元接入链表头部
			conflictList.addFirst(token);
		}else{	
			int index = conflictList.size() - 1;
			while(index > 0 && conflictList.get(index).compareTo(token) < 0) {
				conflictList.add(index, token);
				break;
			}
		}
	}

	public LinkedList<Token> getConflictList() {
		return conflictList;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}
}
