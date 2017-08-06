package com.ncfxy.xmind.helper.convert;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.ISheet;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;
import org.xmind.core.IWorkbookBuilder;

import com.google.gson.JsonElement;

public class ConvertFromXml extends AbstractConvert {


	public ConvertFromXml(String filePath) {
		super(filePath);
	}

	public Document parserXml() {
		File inputXml = new File(this.filePath);
		SAXReader saxReader = new SAXReader();

		Document document = null;
		try {
			document = saxReader.read(inputXml);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document;

	}

	public void dfs(IWorkbook workbook, ITopic rootTopic, Element root) {
		if(!root.elementIterator().hasNext()){
			ITopic topic = workbook.createTopic();
			topic.setTitleText(root.getText());
			rootTopic.add(topic);
			return;
		}
		for (Iterator i = root.elementIterator(); i.hasNext();) {
			Element child = (Element) i.next();
			ITopic topic = workbook.createTopic();
			topic.setTitleText(child.getName());
			rootTopic.add(topic);
			dfs(workbook, topic, child);
		}
	}

	public IWorkbook generateXindWorkBook(Document document) {
		IWorkbookBuilder builder = Core.getWorkbookBuilder();
		IWorkbook workbook = builder.createWorkbook();
		ISheet defSheet = workbook.getPrimarySheet();
		ITopic rootTopic = defSheet.getRootTopic();

		Element root = document.getRootElement();

		rootTopic.setTitleText(root.getName());

		dfs(workbook, rootTopic, root);

		return workbook;
	}

	public int convert() {
		Document document = parserXml();
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
