package com.ncfxy.xmind.helper.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.ISheet;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;
import org.xmind.core.IWorkbookBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONFunction;
import net.sf.json.JSONObject;

public class ConvertFromJson {
	
	private String filePath = null;
	
	private String desPath = null;
	
	
	/**
	 * 要分析的文件的path
	 * @param filePath
	 */
	public ConvertFromJson(String filePath) {
		super();
		this.filePath = filePath;
		File file = new File(this.filePath);
		File directory = file.getParentFile();
		File desFile = new File(directory.getAbsolutePath()+"/"+file.getName()+".xmind");
		if(!desFile.exists()){
			try {
				desFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(desFile.exists()){
			desPath = desFile.getAbsolutePath();
		}
	}


	/**
	 * 通过文件路径，把文件读入到字符串中
	 * @param filePath
	 * @return 从文件中读取到的字符串
	 */
	public String readFromFile(){
		File file = new File(this.filePath);
		BufferedReader reader = null;
		StringBuffer buffer = new StringBuffer();
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	buffer.append(tempString+"\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
		
		return buffer.toString();
	}
	
	
	/**
	 * 使用Gson工具从json字符串中获取到JsonElement元素
	 * @param jsonStr
	 * @return
	 */
	public JsonElement getFromJsonFile(String jsonStr){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement element = gson.fromJson(jsonStr, JsonElement.class);
		return element;
	}
	
	/**
	 * 通过dfs遍历json的结构，插入到xind文件的结构中去
	 * @param workbook
	 * @param rootTopic
	 * @param root
	 */
	public void dfs(IWorkbook workbook, ITopic rootTopic, JsonElement root){
		if(root.isJsonPrimitive()){
			ITopic topic = workbook.createTopic();
    		topic.setTitleText(root.getAsJsonPrimitive().getAsString());
    		rootTopic.add(topic);
        }else if(root.isJsonArray()){
        	JsonArray array = root.getAsJsonArray();
        	for(Integer i = 1;i <= array.size();i++){
        		ITopic topic = workbook.createTopic();
        		topic.setTitleText(i.toString());
        		rootTopic.add(topic);
        		dfs(workbook, topic, array.get(i-1));
        	}
        }else if(root.isJsonObject()){
        	JsonObject ob = root.getAsJsonObject();
        	Set<Entry<String, JsonElement>> set = ob.entrySet();
        	for(Entry<String, JsonElement> entry: set){
        		ITopic topic = workbook.createTopic();
        		topic.setTitleText(entry.getKey());
        		rootTopic.add(topic);
        		dfs(workbook, topic, entry.getValue());
        	}
        }else if(root.isJsonNull()){
        	ITopic topic = workbook.createTopic();
    		topic.setTitleText("null");
    		rootTopic.add(topic);
        }
	}
	
	public IWorkbook generateXindWorkBook(JsonElement root){
		IWorkbookBuilder builder = Core.getWorkbookBuilder();
        IWorkbook workbook = builder.createWorkbook();
        ISheet defSheet = workbook.getPrimarySheet();
        ITopic rootTopic = defSheet.getRootTopic();
        rootTopic.setTitleText("Root");
        // 先把根节点命名为Root
        
        dfs(workbook, rootTopic, root);
        
        return workbook;
	}
	
	public int covert(){
		String jsonStr = readFromFile();
		JsonElement jsonEle = getFromJsonFile(jsonStr);
		IWorkbook workbook = generateXindWorkBook(jsonEle);
		
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
	
	
	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public String getDesPath() {
		return desPath;
	}


	public void setDesPath(String desPath) {
		this.desPath = desPath;
	}


	public static void main(String[] args) {
		ConvertFromJson m = new ConvertFromJson("src/main/resources/sample.json");
		String jsonStr = m.readFromFile();
		JsonElement jsonEle = m.getFromJsonFile(jsonStr);
		IWorkbook workbook = m.generateXindWorkBook(jsonEle);
		
		
		try {
			workbook.save(m.desPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
