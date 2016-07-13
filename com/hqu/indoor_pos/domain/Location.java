package com.hqu.indoor_pos.bean;

import java.sql.Timestamp;

/**
 * 定位结果位置对象
 * @author megagao
 */
public class Location {

	private String emPid;
	
	private Double xAxix;
	
	private Double yAxix;
	
	private Timestamp timeStamp;

	public Location(String emPid, Double xAxix, Double yAxix,
			Timestamp timeStamp) {
		super();
		this.emPid = emPid;
		this.xAxix = xAxix;
		this.yAxix = yAxix;
		this.timeStamp = timeStamp;
	}

	public String getEmPid() {
		return emPid;
	}
	
	public void setEmPid(String emPid) {
		this.emPid = emPid;
	}

	public Double getxAxix() {
		return xAxix;
	}

	public void setxAxix(Double xAxix) {
		this.xAxix = xAxix;
	}

	public Double getyAxix() {
		return yAxix;
	}

	public void setyAxix(Double yAxix) {
		this.yAxix = yAxix;
	}

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String toString() {
		return  emPid +"在"+ timeStamp + "时的位置是" + xAxix + ","
				+ yAxix;
	}
	
	
}
