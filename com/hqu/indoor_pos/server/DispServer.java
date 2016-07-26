package com.hqu.indoor_pos.util2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hqu.indoor_pos.bean.Location;
import com.hqu.indoor_pos.util.DBUtil;


public class DispServer {

	/*显示客户端连接端口号*/
    public static final int DISP_SERVERPORT = 5005; 
    int i = 0;
    public Map<Integer, Location> locsToDB = new HashMap<Integer, Location>();
    
	public void startDispServer() {
		/*启动显示客户端监听线程*/
        Thread dispThread =new Thread(new DispThread());  
        dispThread.start();
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
			
			System.out.println("DispServer Connected"); 
			
            DataOutputStream os = null;  
            try {  
                 while(true){
                	Location loc = Server.locs.take();
                	os = new DataOutputStream(dispClient.getOutputStream()); 
                    os.writeUTF(loc.getEmPid()+","+loc.getCoordinateSys()+","+loc.getxAxis()+","+loc.getyAxis());
                    locsToDB.put(++i, loc);
                    os.flush();
                    /*if(j==20){
                      	Connection conn = DBUtil.getConnection();
                         PreparedStatement stat = conn.prepareStatement("insert into location(em_pid,x_axis,y_axis,timestamp,coordinate_id) values(?,?,?,?,?)");
                         for (int k=1; k<=j; k++){
                      		Location location = locsToDB.get(k);
                             	stat.setString(1, location.getEmPid());
                             	stat.setDouble(2, location.getxAxis());
                             	stat.setDouble(3, location.getyAxis());
                             	stat.setTimestamp(4, location.getTimeStamp());
                             	stat.setInt(5, location.getCoordinateSys());
                             	stat.executeUpdate();
                         }
                      	i.set(0);
                      	locsToDB = new ConcurrentHashMap<Integer,Location>();
             		}*/
                 }
            }  
            catch (Exception e) {  
            	try {
					dispClient.close();
				} catch (IOException e1) {
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
