package com.hqu.indoor_pos.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.hqu.indoor_pos.Dealer;
import com.hqu.indoor_pos.Trilateral;
import com.hqu.indoor_pos.bean.EnvFactor;
import com.hqu.indoor_pos.util.DBUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class Server {
	
	public static BlockingQueue<String> locs;
	
	public static Dealer dealer;
	
    public static void main(String[] args) throws Exception {
    	
        int port = 5006;
        
        locs = new LinkedBlockingQueue<>();
        
        dealer = new Trilateral();
        
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
		
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {

            }
        }
        
        /*启动显示客户端监听线程*/
        new Thread(new Runnable() {
			@Override
			public void run() {
				 DispServer dispServer = new DispServer();
			        dispServer.startDispServer();
			}
		}).start();
       
        new Server().bindPos(port);
       
    }
    
	public void bindPos(int port) throws Exception {
    	
    	/*配置服务器的NIO线程租*/
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());

            /*绑定端口，同步等待成功*/
            ChannelFuture f = b.bind(port).sync();
            /*等待服务端监听端口关闭*/
            f.channel().closeFuture().sync();
        } finally {
            /*优雅退出，释放线程池资源*/
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel sc) throws Exception {
        	
            System.out.println("server initChannel..");
            // 解码转String
            //sc.pipeline().addLast(new StringDecoder());
            //sc.pipeline().addLast(new ByteArrayEncoder());
     
    		// 编码器 String
            //sc.pipeline().addLast(new StringEncoder()); 
            
            //业务处理
            sc.pipeline().addLast(new PosServerHandler());
            
            //sc.pipeline().addLast(new ByteArrayDecoder());
        }
    }
    
}
