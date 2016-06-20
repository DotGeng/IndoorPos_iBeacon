package test1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import com.hqu.indoor_pos.Centroid;
import com.hqu.indoor_pos.DBUtil;
import com.hqu.indoor_pos.Dealer;
import com.hqu.indoor_pos.bean.BleBase;
import com.hqu.indoor_pos.bean.EnvFactor;
import com.hqu.indoor_pos.bean.Round;

public class test {
	@Test
	public void testjdbc(){
		DBUtil d = new DBUtil();
		Connection c = DBUtil.getConnection();
		try {
			PreparedStatement stat = c.prepareStatement("select base_id,x_axis,y_axis from base_station where base_id in (?,?,?,?)");
			stat.setString(1, "1111");
			stat.setString(2, "5555");
			stat.setString(3, "9999");
			stat.setString(4, "7777");
			ResultSet rs = stat.executeQuery();
			while(rs.next()){
				System.out.println(rs.getString(1));
				System.out.println(rs.getDouble(2));
				System.out.println(rs.getDouble(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void SBSQL(){
		StringBuilder sb = new StringBuilder("select base_id,x_axis,y_axis from base_station where base_id in (?,?,?");
		if(true){
			sb.append(",?");
			System.out.println(sb);
		}
		if(true){
			sb.append(")");
			System.out.println(sb);
		}
	}
	
	@Test
	public void centroid(){
		Round r1 = new Round(0, 0, 1.2);
		Round r2 = new Round(-1, 1, 1.8);
		Round r3 = new Round(0.3, 1.4, 1.7);
		System.out.println(Centroid.triCentroid(r1, r2, r3));
	}
	
	@Test
	public void main(){
		
		String id = "";
		int rssi = 0;
		
		/*方法一：运行时手动设置值*/
		EnvFactor.setHeight(1);//设置高度补偿值
		EnvFactor.setN(3);//设置环境衰减因子
		EnvFactor.setP0(-67);//设置一米处接收到的rssi值
		
		/*方法二：从数据库中取值*/
		Connection conn = DBUtil.getConnection();
		try {
			PreparedStatement stat; 
			stat = conn.prepareStatement("select height,atten_factor,p0 from env_factorr order by time_stamp");
			ResultSet rs = stat.executeQuery();
			rs.last();//根据时间戳找出最近一次修改的环境因素值，也可根据修改的版本号指定特定的环境因素值，相应的修改sql语句即可
			EnvFactor.setHeight(rs.getDouble(1));//设置高度补偿值
			EnvFactor.setN(rs.getDouble(2));//设置环境衰减因子
			EnvFactor.setP0(rs.getDouble(3));//设置一米处接收到的rssi值
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		List<BleBase> bases = null;
		//分别根据接收到的基站的id和对应的rssi值创建BleBase对象，并将这些对象分别添加到bases中，此部分可在客户端完成，也可在s端
		BleBase base1 = new BleBase(id, rssi);
		bases.add(base1);
		//创建一个继承自Dealer接口的具体算法对象，调用该对象的getLocation方法，传入接收到的一个基站列表，即可得到定位坐标
		Dealer dealer = new Centroid();//使用加权质心定位算法
		//Dealer dealer = new Trilateral();使用加权三边定位算法
		double[] location = dealer.getLocation(bases);//其中location[0]是定位得到的横坐标，location[2]是纵坐标
	}
	@Test
	public void env(){
		
		Connection conn = DBUtil.getConnection();
		try {
			PreparedStatement stat; 
			stat = conn.prepareStatement("select height,atten_factor,p0 from env_factor order by time_stamp");
			ResultSet rs = stat.executeQuery();
			rs.last();//根据时间戳找出最近一次修改的环境因素值，也可根据修改的版本号指定特定的环境因素值，相应的修改sql语句即可
			System.out.println(rs.getString(1));
			System.out.println(rs.getDouble(2));
			System.out.println(rs.getDouble(3));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
