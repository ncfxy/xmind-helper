package com.ncfxy.xmind.helper.convert;

public interface Convert {
	
	/**
	 * 对文件进行转换生成新xmind文件
	 * @return
	 */
	public int convert();
	
	/**
	 * 返回新生成文件的Path
	 * @return
	 */
	public String getDesPath();

}
