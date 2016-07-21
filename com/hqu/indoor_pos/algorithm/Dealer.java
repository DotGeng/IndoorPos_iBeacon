package com.hqu.indoor_pos;


import com.hqu.indoor_pos.bean.Location;


/**
 * <p>定位算法的父接口</p>
 * 
 * @author :megagao
 */
public interface Dealer {
	
	/**
     * <p>
     * 求定位终端坐标
     * </p>
     * 
     * @param str  接收到的一组基站组成的字符串格式为“id,rssi;id,rssi........id,rssi;terminalID”
     * 
     * @return Location	返回定位结果对象。。
     *    
     */
	public Location getLocation(String str);
	
}
