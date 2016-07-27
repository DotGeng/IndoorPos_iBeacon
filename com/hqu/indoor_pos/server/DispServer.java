package com.hqu.indoor_pos.util2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hqu.indoor_pos.bean.Location;
import com.hqu.indoor_pos.util.DBUtil;


public class DispServer {

	/*显示客户端连接端口号*/
    public static final int DISP_SERVERPORT = 5005;
    
    private ArrayList<DispClient> dispClients = null;
    
    private int i = 0;
    
    public Map<Integer, Location> locsToDB = new HashMap<Integer, Location>();
    
	public void startDispServer() {
		
		/*启动显示客户端监听线程*/
        Thread dispThread =new Thread(new DispThread());  
        dispThread.start();
        
        Location loc = null;
        
        while(true){
        	
        	try {
				loc = Server.locs.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	String str = loc.getEmPid()+","+loc.getCoordinateSys()+","+loc.getxAxis()+","+loc.getyAxis();
        	for (DispClient DispClient : dispClients) {
				DispClient.sendLoc(str);
			}
        	
        	locsToDB.put(++i, loc);
        	/*每15条存一次数据库*/
        	if(i == 15){
        		Connection conn = null;
				try {
					conn = DBUtil.getConnection();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                try {
					PreparedStatement stat = conn.prepareStatement("insert into location(em_pid,x_axis,y_axis,timestamp,coordinate_id) values(?,?,?,?,?)");
					for (int k=1; k<=i; k++){
						Location location = locsToDB.get(k);
					    	stat.setString(1, location.getEmPid());
					    	stat.setDouble(2, location.getxAxis());
					    	stat.setDouble(3, location.getyAxis());
					    	stat.setTimestamp(4, location.getTimeStamp());
					    	stat.setInt(5, location.getCoordinateSys());
					    	stat.executeUpdate();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
             	locsToDB = new ConcurrentHashMap<Integer,Location>();
        	}
        }
	}
	
	/*显示客户端*/
	private class DispThread implements Runnable{
		
		@Override
		public void run() {
			
			ServerSocket dispServerSocket = null;
			
			dispClients = new ArrayList<DispClient>();
			
			try {
				
	            System.out.println("DispServer Connecting...");  
	  
	            dispServerSocket = new ServerSocket(DISP_SERVERPORT);
	            
	            while (true) {  
	            	
	            	/*等待接收显示客户端请求*/
	                Socket dispClient = dispServerSocket.accept();  
	                System.out.println("DispServer Connected"); 
	                DispClient dc = new DispClient(dispClient);
	                
	                new Thread(dc).start();
	                
	                dispClients.add(dc);
	                
	            }  
	        } catch (Exception e) {  
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
	
	class DispClient implements Runnable {
		private Socket socket = null;
		private OutputStream os = null;
		private DataOutputStream dos = null;

		DispClient(Socket socket) {
			this.socket = socket;
			try {
				os = socket.getOutputStream();
				dos = new DataOutputStream(os);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
		}

		public void sendLoc(String string) {
			try {
				dos.writeUTF(string);
			} catch (SocketException exception) {
				System.out.println("Thread:" + dispClients.remove(this));//
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
