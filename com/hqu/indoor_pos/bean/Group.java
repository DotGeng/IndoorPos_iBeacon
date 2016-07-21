package com.hqu.indoor_pos.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装的分组对象
 * @author :megagao
 */
public class Group {
	
	/*rssi值列表*/
	private List<Integer> rssis = new ArrayList<Integer>();

	public List<Integer> getRssis() {
		return rssis;
	}

	public void setRssis(List<Integer> rssis) {
		this.rssis = rssis;
	}
}
