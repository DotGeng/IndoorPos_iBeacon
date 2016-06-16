package com.hqu.indoor_pos.bean;

/**
 * <p>封装的环境影响因素对象</p>
 * <p>在进行定位的程序中，从数据库中或直接赋值得到这三个因素的值，然后用这三个值为类变量赋值。
 * 定位的算法通过类变量拿到这三个值进行定位运算。</p>
 * @author megagao
 */
public class EnvFactor {
	
	/**
	 * 高度补偿值
	 */
	public static double height;
	
	/**
	 * 环境衰减因子
	 */
	public static double n;
	
	/**
	 * 一米处接收到的rssi值
	 */
	public static double p0;
	
	public static void setHeight(double height1) {
		height = height1;
	}

	public static void setN(double n1) {
		n = n1;
	}

	public static void setP0(double p) {
		p0 = p;
	}
	
	
}
