package com.hqu.indoor_pos.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class DispServer {

	/*显示客户端连接端口号*/
    public static final int DISP_SERVERPORT = 5005; 
    
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
                	String str = Server.locs.take();
                	os = new DataOutputStream(dispClient.getOutputStream()); 
                    os.writeUTF(str);
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
