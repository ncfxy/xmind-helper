package com.ncfxy.xmind.helper.ui;

import java.awt.GridLayout;
import java.io.File;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.ncfxy.xmind.helper.convert.Convert;
import com.ncfxy.xmind.helper.convert.ConvertFromHtml;
import com.ncfxy.xmind.helper.convert.ConvertFromJson;
import com.ncfxy.xmind.helper.convert.ConvertFromXml;

public class Main {
	
	
	public static void main(String[] args) {
		JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Please select your JSON or XML file.");
        int result = fileChooser.showOpenDialog(fileChooser);  // 打开"打开文件"对话框
        // int result = dlg.showSaveDialog(this);  // 打"开保存文件"对话框
        if (result == JFileChooser.APPROVE_OPTION) {
        	File file = fileChooser.getSelectedFile();
        	Convert convert = null;
        	if(file.getAbsolutePath().endsWith(".json")){
        		convert = new ConvertFromJson(file.getAbsolutePath());
        	}else if(file.getAbsolutePath().endsWith(".xml")){
        		convert = new ConvertFromXml(file.getAbsolutePath());
        	}else if(file.getAbsolutePath().endsWith(".html") ){
        		convert = new ConvertFromHtml(file.getAbsolutePath());
        	}else{
        		JOptionPane.showMessageDialog(null, "只支持.json, .xml和.html格式的文件");
        	}
        	int r = convert.convert();
        	if(r == 1){
        		JOptionPane.showMessageDialog(null, "转换成功!\n新文件为："+convert.getDesPath());
        	}else{
        		JOptionPane.showMessageDialog(null, "发生错误");
        	}
        	
        }
	}
	
}
