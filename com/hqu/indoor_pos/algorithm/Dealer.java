package com.hqu.indoor_pos;

import java.util.List;

import com.hqu.indoor_pos.bean.BleBase;

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
     * @param bases	接收到的一组基站对象列表
     * 
     * @return double[]	返回定位坐标。
     *    
     */
	public double[] getLocation(List<BleBase> bases);
	
}
