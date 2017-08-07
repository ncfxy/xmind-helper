package com.ncfxy.xmind.helper.convert;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.ISheet;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;
import org.xmind.core.IWorkbookBuilder;

import com.google.gson.JsonElement;

public class ConvertFromHtmlSupportForneus extends ConvertFromHtml {


	public ConvertFromHtmlSupportForneus(String filePath) {
		super(filePath);
	}
	
	private boolean isForneusComponent(Element ele){
		Set<String> classes = ele.classNames();
		if(!classes.contains("component")){
			return false;
		}
		for(String aClass : classes){
			if(aClass.startsWith("wap-core-ui")){
				return true;
			}
		}
		return false;
	}
	
	
	private String getForneusComponentName(Element ele){
		Set<String> classes = ele.classNames();
		// 先找要求严格的
		for(String aClass : classes){
			if(aClass.startsWith("wap-core-ui-")){
				return aClass;
			}
		}
		for(String aClass : classes){
			if(aClass.startsWith("wap-")){
				return aClass;
			}
		}
		return null;
	}

	/**
	 * 修改dfs的过程
	 * @param ele
	 * @return
	 */
	public void dfs(IWorkbook workbook, ITopic rootTopic, Node root) {
		List<Node> list = root.childNodes();
		for(Node node: list){
			if(node instanceof TextNode){
				if(!((TextNode) node).isBlank()){
					String text = ((TextNode) node).text();
					ITopic topic = workbook.createTopic();
					topic.setTitleText("\""+text+"\"");
					rootTopic.add(topic);
				}
			}else if(node instanceof Element){
				Element ele  = (Element)node;
				if(isForneusComponent(ele)){
//					System.out.println(getForneusComponentName(ele));
					ITopic topic = workbook.createTopic();
					topic.setTitleText(getForneusComponentName(ele).replaceAll("-", "."));
					rootTopic.add(topic);
					dfs(workbook, topic, node);
				}else{
					dfs(workbook, rootTopic, node);
				}
			}else if(node instanceof DataNode){
//				System.out.println(node.toString());
				// 获取Javascript或者css标签
				dfs(workbook, rootTopic, node);
			}else{
				ITopic topic = workbook.createTopic();
				topic.setTitleText("<"+node.nodeName()+">");
				rootTopic.add(topic);
				dfs(workbook, topic, node);
			}
		}
	}

}
