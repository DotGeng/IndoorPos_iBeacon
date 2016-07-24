package com.hqu.indoor_pos.server;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.hqu.indoor_pos.bean.Location;
import com.hqu.indoor_pos.util.DBUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PosServerHandler extends ChannelHandlerAdapter {

	public  static AtomicInteger i = new AtomicInteger();
	
	public  static Map<Integer,Location> locsToDB = new ConcurrentHashMap<Integer,Location>();
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        System.out.println("server channelRead..");
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String str = new String(req, "UTF-8");
        System.out.println(msg);
        Location loc = Server.dealer.getLocation(str);
        String resultStr = loc.getEmPid()+","+loc.getCoordinateSys()+","+loc.getxAxis()+","+loc.getyAxis();
        Server.locs.put(resultStr);
        locsToDB.put(i.incrementAndGet(), loc);
        if(i.get()>=20){
        	synchronized (this) {
        		if(i.get()>=20){
	             	Connection conn = DBUtil.getConnection();
	                PreparedStatement stat = conn.prepareStatement("insert into location(em_pid,x_axis,y_axis,timestamp,coordinate_id) values(?,?,?,?,?)");
	             	int j = i.get();
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
        		}
             }
		}
       
        
    

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server channelReadComplete..");
        ctx.flush();//刷新后才将数据发出到SocketChannel
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        System.out.println("server exceptionCaught..");
        ctx.close();
    }


}
