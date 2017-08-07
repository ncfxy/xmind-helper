package com.ncfxy.xmind.helper.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConvertFromXmlMavenPom extends ConvertFromXml{
	
	private String mavenRepositoryPath = System.getProperties().get("user.home")+"/.m2/repository";
	
	private List<String> blindTags = new ArrayList<String>();
	
	private boolean useBlind = true;
	

	public ConvertFromXmlMavenPom(String filePath) {
		super(filePath);
		blindTags = Arrays.asList(
				"dependencies",
				"modelVersion",
				"groupId",
				"artifactId",
				"version",
				"packaging",
//				"build",
				"repositories",
//				"properties",
//				"profiles",
				"parent",
				"modules",
				"distributionManagement",
				"dependencyManagement",
				"name",
				"pluginRepositories",
				"prerequisites",
				"url",
				"issueManagement",
				"ciManagement",
				"pluginRepositories",
				"organization",
				"scm",
				"description"
			);
	}
	
	/**
	 * 是否屏蔽某一个tag不输出
	 * @return
	 */
	public boolean isBlind(Element child, Element parent){
		if(!useBlind)return false;
		if(!"project".equals(parent.getName())){
			return false;
		}
		for(String blindName : blindTags){
			if(blindName.equals(child.getName())){
				return true;
			}
		}
		return false;
	}
	
	private void analysisParent(IWorkbook workbook, ITopic rootTopic, Element parent){
		String filePath = mavenRepositoryPath;
		String groupId = parent.element("groupId").getText();
		String artifactId = parent.element("artifactId").getText();
		String version = parent.element("version").getText();
		for(String s : groupId.split("\\.")){
			filePath += "/" + s;
		}
		filePath += "/" + artifactId;
		filePath += "/" + version;
		filePath += "/" + artifactId + "-" + version + ".pom";
		
		Document document = this.parserXml(filePath);
		Element root = document.getRootElement();
		rootTopic.setTitleText(groupId + "\n" + artifactId + "\n"+version);
		dfs(workbook, rootTopic, root);
	}
	
	public void dfs(IWorkbook workbook, ITopic rootTopic, Element root) {
		Element parent = root.element("parent");
		if(parent != null){
			ITopic topic = workbook.createTopic();
			topic.setTitleText("ParentPom");
			rootTopic.add(topic);
			analysisParent(workbook, topic, parent);
		}
		if(!root.elementIterator().hasNext()){
			ITopic topic = workbook.createTopic();
			topic.setTitleText(root.getText());
			rootTopic.add(topic);
			return;
		}
		for (Iterator i = root.elementIterator(); i.hasNext();) {
			Element child = (Element) i.next();
			if(isBlind(child, root)){
				continue;
			}
			ITopic topic = workbook.createTopic();
			topic.setTitleText(child.getName());
			rootTopic.add(topic);
			dfs(workbook, topic, child);
		}
	}
	
	public static void main(String[] args) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println(gson.toJson(System.getenv()));
		System.out.println(gson.toJson(System.getProperties()));
		System.getProperties().get("user.home");
		System.out.println(System.getProperties().get("user.home"));
	}
	
	

}
