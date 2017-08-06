package com.ncfxy.xmind.helper.convert;

import java.io.File;
import java.io.IOException;

public abstract class AbstractConvert implements Convert{
	
	
	
	public AbstractConvert(String filePath) {
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

	protected String filePath = null;
	
	protected String desPath = null; 
	
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

	public abstract int convert();


}
