package com.globalegrow.util;

import java.util.ArrayList;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.Parser;

public class HtmlParse {
	
	static Parser parser = new Parser();
	
	/**
	 * 解析出所有的<a>链接
	 * @author yuyang
	 * 创建时间：2016-09-03
	 * 更新时间：2016-09-03
	 */
	static NodeFilter lnkFilter = new NodeFilter() {
		private static final long serialVersionUID = 1L;

		public boolean accept(Node node) {
			if (node instanceof LinkTag)
				return true;
			return false;
		}
	};
	
	/**
	 * 返回html页面的全部<a>连接
	 * @param str_html_content html内容
	 * @return 匹配到的全部links
	 * @author yuyang
	 * 创建时间：2016-09-03
	 * 更新时间：2016-09-03
	 */
	public static ArrayList<String> getAllALink(String str_html_content) throws ParserException {
		ArrayList<String> links = new ArrayList<String>();
		parser.setInputHTML(str_html_content);
		NodeList nlist = parser.extractAllNodesThatMatch(lnkFilter);
		
		for (int i = 0; i < nlist.size(); i++) {
			CompositeTag node = (CompositeTag) nlist.elementAt(i);
			if (node instanceof LinkTag) {
				LinkTag link = (LinkTag) node;
				links.add(link.getLink());
			}
		}
		return links;
	}

}
