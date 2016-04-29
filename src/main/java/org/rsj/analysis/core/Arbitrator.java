package org.rsj.analysis.core;

public class Arbitrator {
	/**
	 * 分词歧义处理
//	 * @param orgLexemes
	 * @param useSmart
	 */
	public void process(AnalysisContext context){
		ConflictTokensList conflictList = context.getOrgListsWhitConflict().pollFirst();
		while(conflictList != null) {
			if(conflictList.getConflictList().size() == 1){//conflictList只有一个词没有歧义
				context.addToResults(conflictList);
			}else{
				//对当前的conflictList进行歧义处理
				ConflictTokensList judgeResultList = this.judge(context, conflictList);
				//输出歧义处理结果judgeResult
				context.addToResults(judgeResultList);
			}
			conflictList = context.getOrgListsWhitConflict().pollFirst();
		}
	}
	
	private ConflictTokensList judge(AnalysisContext context, ConflictTokensList conflictList) {
		System.out.println("do some judge work");
		//判断歧义
		ConflictTokensList judgeResultList = new ConflictTokensList();
		System.out.println("after the judge");
		for (Token token : judgeResultList.getConflictList()) {
			token.setText(String.valueOf(context.getSegmentBuff() , token.getBegin() , token.getLength()));
			System.out.println(token);
		}
		return judgeResultList;
	}
		
}
