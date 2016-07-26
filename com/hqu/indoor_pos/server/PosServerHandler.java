package com.hqu.indoor_pos.util2;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hqu.indoor_pos.bean.Location;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class PosServerHandler extends ChannelHandlerAdapter {

	public  static Map<Integer,Location> locsToDB = new ConcurrentHashMap<Integer,Location>();
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        System.out.println("server channelRead..");
        String str = (String) msg;
        System.out.println(str);
    	Location loc = Server.dealer.getLocation(str);
        if(loc!=null){
            Server.locs.put(loc);
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
        cause.printStackTrace();
        ctx.close();
    }


}
