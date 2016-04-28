package org.rsj.analysis.dic;

import java.io.IOException;

/**
 * 监控指定目录是否包含新的动态词语文件
 * @author kit
 *
 */
public class Monitor implements Runnable{
	private String DynamicWordsPath;
	private String last_modified;
	
	public Monitor(String Path) {
		this.DynamicWordsPath = Path;
		this.last_modified = null;
	}
	/**
	 * 监控流程：
	 *  ①向词库服务器发送Head请求
	 *  ②从响应中获取Last-Modify、ETags字段值，判断是否变化
	 *  ③如果未变化，休眠1min，返回第①步
	 * 	④如果有变化，重新加载词典
	 *  ⑤休眠1min，返回第①步
	 */

	public void run() {
		System.out.println("this is monitor");
//		//超时设置
//		RequestConfig rc = RequestConfig.custom().setConnectionRequestTimeout(10*1000)
//				.setConnectTimeout(10*1000).setSocketTimeout(15*1000).build();
//
//		HttpHead head = new HttpHead(location);
//		head.setConfig(rc);
//
//		//设置请求头
//		if (last_modified != null) {
//			head.setHeader("If-Modified-Since", last_modified);
//		}
//		if (eTags != null) {
//			head.setHeader("If-None-Match", eTags);
//		}
//
//		CloseableHttpResponse response = null;
//		try {
//
//			response = httpclient.execute(head);
//
//			//返回200 才做操作
//			if(response.getStatusLine().getStatusCode()==200){
//
//				if (!response.getLastHeader("Last-Modified").getValue().equalsIgnoreCase(last_modified)
//						||!response.getLastHeader("ETag").getValue().equalsIgnoreCase(eTags)) {
//
//					// 远程词库有更新,需要重新加载词典，并修改last_modified,eTags
//					Dictionaries.getSingleton().reLoadMainDict();
//					last_modified = response.getLastHeader("Last-Modified")==null?null:response.getLastHeader("Last-Modified").getValue();
//					eTags = response.getLastHeader("ETag")==null?null:response.getLastHeader("ETag").getValue();
//				}
//			}else if (response.getStatusLine().getStatusCode()==304) {
//				//没有修改，不做操作
//				//noop
//			}else{
//				Dictionary.logger.info("remote_ext_dict {} return bad code {}" , location , response.getStatusLine().getStatusCode() );
//			}
//
//		} catch (Exception e) {
//			Dictionary.logger.error("remote_ext_dict {} error!",e , location);
//		}finally{
//			try {
//				if (response != null) {
//					response.close();
//				}
//			} catch (IOException e) {
//				logger.error(e.getMessage(), e);
//			}
//		}
	}
}
