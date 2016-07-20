package com.hqu.indoor_pos.util2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.hqu.indoor_pos.DBUtil;
import com.hqu.indoor_pos.Dealer;
import com.hqu.indoor_pos.Trilateral;
import com.hqu.indoor_pos.WeightTrilateral;
import com.hqu.indoor_pos.bean.EnvFactor;
import com.hqu.indoor_pos.bean.Location;

public class ServerMain implements Runnable{

	public static final String SERVERIP = "120.32.212.250"; 
	
	/*显示客户端连接端口号*/
    	public static final int DISP_SERVERPORT = 50005;  
    
	/*定位客户端连接端口号*/
	public static final int POS_SERVERPORT = 50006; 
    
    	/*坐标*/
    	private volatile double[] loc =new double[2];
    
    
	public static void main(String[] args) {
		
		Connection conn = DBUtil.getConnection();
		try {
			PreparedStatement stat; 
			stat = conn.prepareStatement("select height,atten_factor,p0 from env_factor order by time_stamp");
			ResultSet rs = stat.executeQuery();
			rs.last();//根据时间戳找出最近一次修改的环境因素值，也可根据修改的版本号指定特定的环境因素值，相应的修改sql语句即可
			EnvFactor.setHeight(rs.getDouble(1));//设置高度补偿值
			EnvFactor.setN(rs.getDouble(2));//设置环境衰减因子
			EnvFactor.setP0(rs.getDouble(3));//设置一米处接收到的rssi值
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ServerMain serverMain = new ServerMain();
		
		/*启动定位客户端监听线程*/
		Thread PosServerThread = new Thread(serverMain);  
        PosServerThread.start();
        
        /*启动显示客户端监听线程*/
        Thread dispThread =new Thread(serverMain.new DispThread());  
        dispThread.start();
	}
	
	/*定位客户端*/
	@Override
	public void run() {
		
		ServerSocket serverSocket = null;
		
		try {  
            System.out.println("posServer Connecting...");  
  
            serverSocket = new ServerSocket(POS_SERVERPORT);
            
            while (true) {  
            	
            	/*等待接收定位客户端请求*/
                Socket client = serverSocket.accept();  
                
                new Thread(new PosHandlerThread(client)).start();
                
            }  
        } catch (Exception e) {  
            System.out.println("S: Error 4");  
            e.printStackTrace();  
        } finally{
        	 try {  
                 if(serverSocket != null){  
                     serverSocket.close();  
                 }  
             } catch (IOException e) {  
                 e.printStackTrace();  
             }  
        }
		
	}
	
	class PosHandlerThread implements Runnable{
		
		private Socket client;  
		  
	    public PosHandlerThread(Socket client) {  
	        this.client = client;  
	    }  
	    
		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			
			System.out.println("S: Receiving...");  
			List<Location> locations = new ArrayList<Location>();
            DataInputStream is = null;
            int i = 0;
            try {  
                 while(true){
                	//Thread.sleep(1000);
                	if(client.getInputStream()!=null){
                		System.out.println("received!");
                		is = new DataInputStream(client.getInputStream());
	                	String str =   is.readUTF();
	                	Dealer dealer = new Trilateral();
	                	if(dealer.getLocation(str) != null){
	                		loc = dealer.getLocation(str);
	                	}

	                	Timestamp ts = new Timestamp(System.currentTimeMillis());
	                    Location location = new Location("111", loc[0], loc[1], ts);
	                    locations.add(location);
	                    i++;
	                    if(i>=10){
	                    	Connection conn = DBUtil.getConnection();
	                    	PreparedStatement stat = conn.prepareStatement("insert into location(em_pid,x_axis,y_axis,timestamp) values(?,?,?,?)");
	                    	for (Location l : locations) {
	                    		stat.setString(1, l.getEmPid());
	                    		stat.setDouble(2, l.getxAxix());
	                    		stat.setDouble(3, l.getyAxix());
	                    		stat.setTimestamp(4, l.getTimeStamp());
	                    		stat.executeUpdate();
							}
	                    	i = 0;
	                    	locations = new ArrayList<Location>();
	                    }
                	}
                }
            } 
            catch (Exception e) {  
            	//e.printStackTrace();
            	try {  
                    if(is != null){  
                    	is.close();  
                    }  
                    if(client != null){  
                        client = null;  
                    }  
                } catch (IOException e1) {  
                    e.printStackTrace();  
                }   
            } finally{  
            	Connection conn = DBUtil.getConnection();
    	     	try {
    				PreparedStatement stat = conn.prepareStatement("insert into location(em_pid,x_axis,y_axis,timestamp) values(?,?,?,?)");
    				for (Location l : locations) {
    					stat.setString(1, l.getEmPid());
    					stat.setDouble(2, l.getxAxix());
    					stat.setDouble(3, l.getyAxix());
    					stat.setTimestamp(4, l.getTimeStamp());
    					stat.executeUpdate();
    					}
    			} catch (SQLException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
                
            }
		}
	}
	
	
	/*显示客户端*/
	private class DispThread implements Runnable{
		
		@Override
		public void run() {
			
			ServerSocket dispServerSocket = null;
			
			try {  
	            System.out.println("DispServer Connecting...");  
	  
	            dispServerSocket = new ServerSocket(DISP_SERVERPORT);
	            
	            while (true) {  
	            	
	            	/*等待接收显示客户端请求*/
	                Socket client = dispServerSocket.accept();  
	                
	                new Thread(new DispHandlerThread(client)).start();
	                
	            }  
	        } catch (Exception e) {  
	            System.out.println("S: Error 4");  
	            e.printStackTrace();  
	        } finally{
	        	 try {  
	                 if(dispServerSocket != null){  
	                	 dispServerSocket.close();  
	                 }  
	             } catch (IOException e) {  
	                 e.printStackTrace();  
	             }  
	        }
			
		}
	}
	
	class DispHandlerThread implements Runnable{
		
		private Socket dispClient;  
		  
	    public DispHandlerThread(Socket dispClient) {  
	        this.dispClient = dispClient;  
	    }  
		@Override
		public void run() {
			
            DataOutputStream os = null;  
            try {  
                 while(true){
 	            	Thread.sleep(2000);
 	            	os = new DataOutputStream(dispClient.getOutputStream()); 
                    os.writeUTF(loc[0]+","+loc[1]);
                    os.flush(); 
                 }
            }  
            catch (Exception e) {  
            	try {
					dispClient.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            } finally{
                try {  
                    if(os != null){  
                    	os.close();  
                    }  
                    if(dispClient != null){  
                    	dispClient = null;  
                    }  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }   
            } 
			
		}
		
	}

}
