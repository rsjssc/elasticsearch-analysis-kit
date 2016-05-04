package org.rsj.analysis.core;

import java.util.LinkedList;

public class SentenceSegment {
	LinkedList<ConflictTokensList> orgListsWhitConflict;
	
	//这个冲突链表的的起始和终止位置
	private int begin;
	private int end;
	
	public SentenceSegment() {
		// TODO Auto-generated constructor stub
		this.orgListsWhitConflict = new LinkedList<ConflictTokensList>();
		this.begin = -1;
		this.end = -1;
	}
	
	public SentenceSegment(ConflictTokensList tokenList) {
		// TODO Auto-generated constructor stub
		this.orgListsWhitConflict = new LinkedList<ConflictTokensList>();
		this.begin = tokenList.getBegin();
		this.end = tokenList.getEnd();
		this.orgListsWhitConflict.add(tokenList);
	}
	
	/**
	 * 链表由小到大排列，起始位置越靠前越小，长度越长越小
	 * @param token
	 * @return
	 */
	public void addToSentenceSeg(ConflictTokensList tokenList) {
//		if(this.orgListsWhitConflict.isEmpty()){
//			this.orgListsWhitConflict.add(tokenList);
//			this.begin = tokenList.getBegin();
//			this.end = tokenList.getEnd();
//		}else{
//			this.orgListsWhitConflict.add(tokenList);
////			this.addConflictList(tokenList);
//			if(tokenList.getEnd() > this.end){
//				this.end = tokenList.getEnd();
//			}
//		}
		this.orgListsWhitConflict.add(tokenList);
	}
	
//	private void addConflictList(ConflictTokensList tokenList) {
//		if(this.orgListsWhitConflict.isEmpty()){
//			tokensList.add(token);
//			return;
//		}
//		if(this.tokensList.peekLast().compareTo(token) < 0){//链表中最后一个词比token小，词元接入链表尾部
//			tokensList.addLast(token);
//		}else if(this.tokensList.peekFirst().compareTo(token) > 0){//链表中最后一个词比token大，词元接入链表头部
//			tokensList.addFirst(token);
//		}else{	
//			System.out.println("inser");
//			int index = tokensList.size() - 1;
//			while(index > 0 && tokensList.get(index).compareTo(token) > 0) {
//				index--;
//			}
//			if (tokensList.get(index).compareTo(token) < 0) {//防止等于0，相等不加
//				tokensList.add(index + 1, token);
//			}
//		}
//	}

	public LinkedList<ConflictTokensList> getConflictLists() {
		return orgListsWhitConflict;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}
}
