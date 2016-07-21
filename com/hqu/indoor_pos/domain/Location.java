package com.hqu.indoor_pos.bean;

import java.sql.Timestamp;

/**
 * 定位结果位置对象
 * @author megagao
 */
public class Location {
	
	/*员工id*/
	private String emPid;
	
	/*所处坐标系id*/
	private Integer coordinateId;
	
	/*x轴坐标*/
	private Double xAxis;
	
	/*y轴坐标*/
	private Double yAxis;
	
	/*时间戳*/
	private Timestamp timeStamp;

	public Location(String emPid, Double xAxis, Double yAxis,
			Timestamp timeStamp) {
		super();
		this.emPid = emPid;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.timeStamp = timeStamp;
	}
	
	public Location(Integer coordinateId, String emPid, Double xAxis,
			Double yAxis, Timestamp timeStamp) {
		super();
		this.coordinateId = coordinateId;
		this.emPid = emPid;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.timeStamp = timeStamp;
	}

	public Integer getCoordinateSys() {
		return coordinateId;
	}

	public void setCoordinateSys(Integer coordinateId) {
		this.coordinateId = coordinateId;
	}

	public String getEmPid() {
		return emPid;
	}
	
	public void setEmPid(String emPid) {
		this.emPid = emPid;
	}

	public Double getxAxis() {
		return xAxis;
	}

	public void setxAxis(Double xAxis) {
		this.xAxis = xAxis;
	}

	public Double getyAxis() {
		return yAxis;
	}

	public void setyAxis(Double yAxis) {
		this.yAxis = yAxis;
	}

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String toString() {
		return  emPid +"在"+ timeStamp + "时的位置是" + xAxis + ","
				+ yAxis;
	}
	
	
}
