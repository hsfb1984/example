package server;

import java.net.InetSocketAddress;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cln.handler.PusSchMsg;
import com.cln.utils.StaticUtils;
import com.cln.ws.WsChannelInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

//websocket
public class WsServer
{
	public static void main(String[] args) throws Exception
	{
		System.setProperty("spring.profiles.active", "dev");
		//启动消息推送的定时
		PusSchMsg.excuteScheduleJob();
		
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup wokerGroup = new NioEventLoopGroup();

		try
		{
			System.out.println("============listen port:"+StaticUtils.SERVER_IP);
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, wokerGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new WsChannelInitializer());

			ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(StaticUtils.SERVER_IP)).sync();
			channelFuture.channel().closeFuture().sync();
		}
		finally
		{
			bossGroup.shutdownGracefully();
			wokerGroup.shutdownGracefully();
		}

	}
}