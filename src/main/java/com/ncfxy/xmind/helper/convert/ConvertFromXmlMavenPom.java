package com.ncfxy.xmind.helper.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConvertFromXmlMavenPom extends ConvertFromXml{
	
	private String mavenRepositoryPath = System.getProperties().get("user.home")+"/.m2/repository";
	
	private List<String> blindTags = new ArrayList<String>();
	
	private boolean useBlind = false;
	
	/**
	 * 防止出现死循环
	 */
	private List<String> topicCache = new ArrayList<String>();
	

	public ConvertFromXmlMavenPom(String filePath) {
		super(filePath);
		blindTags = Arrays.asList(
				"dependencies",
				"modelVersion",
				"groupId",
				"artifactId",
				"version",
				"packaging",
				"build",
				"repositories",
				"properties",
				"profiles",
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
	 * 判断一个topic的名字是否曾今出现过
	 * @param topicName
	 * @return
	 */
	private boolean displayed(String topicName){
		for(String str : topicCache){
			if(topicName.equals(str)){
				return true;
			}
		}
		return false;
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
		
		Document document = null;
		try {
			document = this.parserXml(filePath);
		} catch (DocumentException e) {
			System.out.println("No File: " + filePath);
			return;
		}
		dfsNewProject(workbook, rootTopic, groupId, artifactId, version, document);
	}

	/**
	 * 判断是否要继续在一个新项目上dfs
	 * @param workbook
	 * @param rootTopic
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @param document
	 */
	private void dfsNewProject(IWorkbook workbook, ITopic rootTopic, String groupId, String artifactId, String version,
			Document document) {
		Element root = document.getRootElement();
		ITopic topic = workbook.createTopic();
		String topicName = groupId + "\n" + artifactId + "\n"+version;
		topic.setTitleText(topicName);
		rootTopic.add(topic);
		if(displayed(topicName)){
			// 如果曾今出现过直接跳过
			return;
		}
		this.topicCache.add(topicName);
		dfs(workbook, topic, root);
	}
	
	private void analysisModules(IWorkbook workbook, ITopic rootTopic, 
			String groupId, String artifactId, String version){
		String filePath = mavenRepositoryPath;
		for(String s : groupId.split("\\.")){
			filePath += "/" + s;
		}
		filePath += "/" + artifactId;
		filePath += "/" + version;
		filePath += "/" + artifactId + "-" + version + ".pom";
		Document document = null;
		try {
			document = this.parserXml(filePath);
		} catch (DocumentException e) {
			System.out.println("No File: " + filePath);
			return;
		}
		dfsNewProject(workbook, rootTopic, groupId, artifactId, version, document);
	}
	
	public void dfs(IWorkbook workbook, ITopic rootTopic, Element root) {
		Element parent = root.element("parent");
		if(parent != null){
			ITopic topic = workbook.createTopic();
			topic.setTitleText("ParentPom");
			rootTopic.add(topic);
			analysisParent(workbook, topic, parent);
		}
		Element modules = null; //root.element("modules");
		if(modules != null){
			Element groupIdEle = root.element("groupId");
			Element versionEle = root.element("version");
			String groupId = groupIdEle == null ? root.element("parent").elementText("groupId"):groupIdEle.getText();
			String version = versionEle == null ? root.element("parent").elementText("version"):groupIdEle.getText();
			
			
			ITopic childTopic = workbook.createTopic();
			childTopic.setTitleText("childModulePom");
			rootTopic.add(childTopic);
			for(Iterator i = modules.elementIterator();i.hasNext();){
				String moduleName = ((Element) i.next()).getText();
				analysisModules(workbook, childTopic, groupId, moduleName, version);
			}
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
