package com.ncfxy.xmind.helper.convert;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
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

public class ConvertFromHtml extends AbstractConvert {


	public ConvertFromHtml(String filePath) {
		super(filePath);
	}
	
	public Document parseHtml(){
		org.jsoup.nodes.Document document = null;
		try {
			document = Jsoup.parse(new File(this.filePath), "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document;
	}

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
			}else{
				ITopic topic = workbook.createTopic();
				topic.setTitleText("<"+node.nodeName()+">");
				rootTopic.add(topic);
				dfs(workbook, topic, node);
			}
		}
	}

	public IWorkbook generateXindWorkBook(Document document) {
		IWorkbookBuilder builder = Core.getWorkbookBuilder();
		IWorkbook workbook = builder.createWorkbook();
		ISheet defSheet = workbook.getPrimarySheet();
		ITopic rootTopic = defSheet.getRootTopic();
		
		rootTopic.setTitleText(document.nodeName());

		dfs(workbook, rootTopic, document);
		return workbook;
	}

	public int convert() {
		Document document = parseHtml();
		IWorkbook workbook = generateXindWorkBook(document);

		try {
			workbook.save(this.desPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

}
