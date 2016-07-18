package com.hqu.indoor_pos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hqu.indoor_pos.bean.BleBase;
import com.hqu.indoor_pos.bean.EnvFactor;

import Jama.Matrix;

/**
 *  <p>三边加权定位算法</p>
 * 
 * @author megagao
 */
public class WeightTrilateral implements Dealer{
	
	/*所有组合的总权值*/
	private double totalWeight;
	
	/**
     * <p>
     * 求定位终端坐标
     * </p>
     * 
     * @param str  接收到的一组基站组成的字符串格式为“id,rssi;id,rssi........id,rssi;terminalID”
     * 
     * @return double[]	返回定位坐标。
     *    
     */
	@Override
	public double[] getLocation(String str){
		
		/*分组*/
		DoGroup doGrouper = new DoGroup();
		ArrayList<BleBase> uniqueBases = doGrouper.doGroup(bases);
		
		/*求组合数*/
		Integer[] a = doGrouper.getA();
		CombineAlgorithm ca = null;
		
		try {
			ca = new CombineAlgorithm(a,3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Object[][] c = ca.getResult();
		
		double[] tempLocation = new double[2];
		
		for(int i = 0; i<c.length; i++){
			
			/*创建一个列表，用来对每个组合进行计算*/
			List<BleBase> triBases = new ArrayList<BleBase>();
			
			for(int j = 0; j<3; j++){
				BleBase bb = uniqueBases.get((int) c[i][j]);
				triBases.add(bb);
			}
			
			/*三个基站为一组通过距离加权后求出的坐标*/
			double[] weightLocation = calculate(triBases);
			
			tempLocation[0]+=weightLocation[0];
			tempLocation[1]+=weightLocation[1];
			
		}
		
		double[] location = new double[]{tempLocation[0]/totalWeight,tempLocation[1]/totalWeight};
		
		return location;
	}
	
	 /**
     * <p>
     * 求出一组基站通过距离加权后的坐标
     * </p>
     * 
     * @param bases	接收到的一组基站对象列表，此处列表中的基站应当是id各异的。
     * 
     * @return double[]	返回一组基站通过距离加权后的坐标。
     *    
     */
	public double[] calculate(List<BleBase> bases){
		
		/*基站的id与坐标*/
		Map<String, double[]> basesLocation =new HashMap<String, double[]>();
		
		/*距离数组*/
		double[] distanceArray = new double[3];
		
		String[] ids = new String[3];
		
		double[] rawLocation;
		
		double[] location;
		
		int j = 0;
		
		/*得到环境影响因素的值*/
		double height = EnvFactor.height;
		double n = EnvFactor.n;
		double p0 = EnvFactor.p0;
		
		/*获得基站id*/
		for (BleBase base : bases) {
			ids[j] = base.getId();
			distanceArray[j] = base.getDistance(height, n, p0);
			j++;
		}
		
		/*基站的坐标信息应当根据id去数据库中查找*/
		/*如果每次参加运算的基站数大于3，可以用StringBuilder拼接sql语句*/
		Connection conn = DBUtil.getConnection();
		try {
			PreparedStatement stat = conn.prepareStatement("select base_id,x_axis,y_axis from base_station where base_id in (?,?,?)");
			for(int k=0;k<j;k++){
				stat.setString(k+1, ids[k]);
			}
			ResultSet rs = stat.executeQuery();
			while(rs.next()){
				double[] loc = new double[2];
				loc[0]=rs.getDouble(2);
				loc[1]=rs.getDouble(3);
				basesLocation.put(rs.getString(1), loc);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int disArrayLength = distanceArray.length;
		
		double[][] a = new double[2][2];
		
		double[][] b = new double[2][1];
		
		/*数组a初始化*/
		for(int i = 0; i < 2; i ++ ) {
 			a[i][0] = 2*(basesLocation.get(ids[i])[0]-basesLocation.get(ids[2])[0]);
			a[i][1] = 2*(basesLocation.get(ids[i])[1]-basesLocation.get(ids[2])[1]);
		}
		
		/*数组b初始化*/
		for(int i = 0; i < 2; i ++ ) {
			b[i][0] = Math.pow(basesLocation.get(ids[i])[0], 2) 
					- Math.pow(basesLocation.get(ids[2])[0], 2)
					+ Math.pow(basesLocation.get(ids[i])[1], 2)
					- Math.pow(basesLocation.get(ids[2])[1], 2)
					+ Math.pow(distanceArray[disArrayLength-1], 2)
					- Math.pow(distanceArray[i],2);
		}
		
		/*将数组封装成矩阵*/
		Matrix b1 = new Matrix(b);
		Matrix a1 = new Matrix(a);
		
		/*求矩阵的转置*/
		Matrix a2  = a1.transpose();
		
		/*求矩阵a1与矩阵a1转置矩阵a2的乘积*/
		Matrix tmpMatrix1 = a2.times(a1);
		Matrix reTmpMatrix1 = tmpMatrix1.inverse();
		Matrix tmpMatrix2 = reTmpMatrix1.times(a2);
		
		/*中间结果乘以最后的b1矩阵*/
		Matrix resultMatrix = tmpMatrix2.times(b1);
		double[][] resultArray = resultMatrix.getArray();
		
		rawLocation = new double[2];
		
		/*给未加权的结果数组赋值*/
		for(int i = 0; i < 2; i++) {
			rawLocation[i] = resultArray[i][0];
		}
		
		/*对应的权值*/
		double weight = 0;
		
		for(int i = 0; i<3; i++){
			weight+=(1.0/distanceArray[i]);
		}
		//weight+=(1.0/(distanceArray[0]+distanceArray[1]+distanceArray[2]));
		
		totalWeight+=weight;
		
		/*实例化结果数组*/
		location = new double[2];
		
		/*计算加权过后的坐标*/
		for(int i = 0; i < 2; i++) {
			location[i] = rawLocation[i]*weight;
		}
		
		return location;
	}
	
}
