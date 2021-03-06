package com.cln.ws;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.cln.handler.BusiHandler;
import com.cln.utils.StaticUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;

//处理文本协议数据，处理TextWebSocketFrame类型的数据，websocket专门处理文本的frame就是TextWebSocketFrame
public class WsFrameHandler extends SimpleChannelInboundHandler<Object>
{
	static Logger log = Logger.getLogger(WsFrameHandler.class.getName());

	/**
	 * 读到客户端的内容并且向客户端去写内容
	 */
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		// 如果是HTTP请求，进行HTTP操作
		if (msg instanceof WebSocketFrame)
		{
			handleHttpRequest(ctx, (FullHttpRequest) msg);
		}
		// 如果是HTTP请求，进行HTTP操作
		if (msg instanceof FullHttpRequest)
		{
			handleHttpRequest(ctx, (FullHttpRequest) msg);
		}
		// 如果是Websocket请求，则进行websocket操作
		else if (msg instanceof WebSocketFrame)
		{
			handleWebSocketFrame(ctx, (TextWebSocketFrame) msg);
		}
	}

	/**
	 * websocket处理
	 * 
	 * @param ctx
	 * @param msg
	 */
	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req)
	{
		// 会话ID
		String newChannelId = ctx.channel().id().asLongText();
		// 原始数据
		String data = null;
		log.info("****************接收客户端消息****************channelId=" + newChannelId + ">>>>>>>>data=" + data);
		// 是否第一次建立连接
		boolean firstFlag = false;
		// 用户名,手机号码
		String userName = null;
		// 查询
		JSONObject reqData = new JSONObject();

		try
		{

			HttpMethod method = req.method();
			String uri = req.uri();
			ByteBuf buf = req.content();
			data = buf.toString();

			if (method == HttpMethod.GET && "/shbhttp".equals(uri))
			{

				// 1.解析获取会话ID和userName等
				newChannelId = ctx.channel().id().asLongText();
				userName = ChannelHandler.getChannelUserName(ctx);

				// 2.当userName为空时,则为第一次建立连接
				if (data != null)
				{
					reqData = JSONObject.parseObject(data);
					// userName = reqData.getString("phone");
				}
				if (userName == null)
				{
					// TODO 获取用户名
					JSONObject jsonObject = reqData.getJSONObject("data");
					userName = jsonObject.getString("userPhone");
					if (StringUtils.isNotEmpty(userName))
					{
						firstFlag = true;
					}
				}

				// 3.将通道绑定到该用户名下面;
				if (firstFlag)
				{
					ChannelHandler.addChannel(ctx, userName);
				}
				// 具体业务
				//BusiHandler.doHandler(reqData, ctx, userName);
			}
			else if (method == HttpMethod.POST && "/register".equals(uri))
			{
				// 1.解析获取会话ID和userName等
				newChannelId = ctx.channel().id().asLongText();
				userName = ChannelHandler.getChannelUserName(ctx);

				// 2.当userName为空时,则为第一次建立连接
				if (data != null)
				{
					reqData = JSONObject.parseObject(data);
					// userName = reqData.getString("phone");
				}
				if (userName == null)
				{
					// TODO 获取用户名
					JSONObject jsonObject = reqData.getJSONObject("data");
					userName = jsonObject.getString("userPhone");
					if (StringUtils.isNotEmpty(userName))
					{
						firstFlag = true;
					}
				}

				// 3.将通道绑定到该用户名下面;
				if (firstFlag)
				{
					ChannelHandler.addChannel(ctx, userName);
				}
				// 具体业务
				//BusiHandler.doHandler(reqData, ctx, userName);
			}

			// ctx.channel().writeAndFlush(new
			// TextWebSocketFrame("vvvvvvvvvvv-->userName="+userName));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("XXXXXXXXXXXXXXXXXXXXXXXXX请求处理失败(cmd=" + reqData.getString("cmd") + ",userName=" + userName
					+ ",channelId=" + newChannelId + ",data=" + data + "):" + ex.getMessage());
		}
	}

	/**
	 * websocket处理
	 * 
	 * @param ctx
	 * @param msg
	 */
	private void handleWebSocketFrame(ChannelHandlerContext ctx, TextWebSocketFrame msg)
	{
		// 会话ID
		String newChannelId = ctx.channel().id().asLongText();
		// 原始数据
		String data = msg.text();
		log.info("****************接收客户端消息****************channelId=" + newChannelId + ">>>>>>>>data=" + data);
		// 是否第一次建立连接
		boolean firstFlag = false;
		// 用户名,手机号码
		String userName = null;
		// 查询
		JSONObject reqData = new JSONObject();

		try
		{
			// 1.解析获取会话ID和userName等
			newChannelId = ctx.channel().id().asLongText();
			userName = ChannelHandler.getChannelUserName(ctx);

			// 2.当userName为空时,则为第一次建立连接
			if (data != null)
			{
				reqData = JSONObject.parseObject(data);
				// userName = reqData.getString("phone");
			}
			if (userName == null)
			{
				// TODO 获取用户名
				JSONObject jsonObject = reqData.getJSONObject("data");
				userName = jsonObject.getString("userPhone");
				if (StringUtils.isNotEmpty(userName))
				{
					firstFlag = true;
				}
			}

			// 3.将通道绑定到该用户名下面;
			if (firstFlag)
			{
				ChannelHandler.addChannel(ctx, userName);
			}
			// 具体业务
			//BusiHandler.doHandler(reqData, ctx, userName);
			// ctx.channel().writeAndFlush(new
			// TextWebSocketFrame("vvvvvvvvvvv-->userName="+userName));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("XXXXXXXXXXXXXXXXXXXXXXXXX请求处理失败(cmd=" + reqData.getString("cmd") + ",userName=" + userName
					+ ",channelId=" + newChannelId + ",data=" + data + "):" + ex.getMessage());
		}
	}

	// 每个channel都有一个唯一的id值
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception
	{
		String channelId = ctx.channel().id().asLongText();
		log.info("+++++++++++++++++++建立新连接+++++++++++++++@@@@@@@：channel=" + channelId + ",客户端地址="
				+ ctx.channel().remoteAddress());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception
	{
		ChannelHandler.removeChannel(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		// sessionId
		String channelId = null;
		// 用户名
		String userName = null;
		try
		{
			if (ctx != null)
			{
				// 获取sessionId
				channelId = ctx.channel().id().asLongText();
				// 获取用户名
				userName = StaticUtils.USER_MAP.get(channelId);
				// 打印日志
				log.info("&&&&&&&&&&&&&&连接发生异常&&&&&&&&&&&&&&(userName=" + userName + ",channelId=" + channelId + ")"
						+ ",cause=" + cause.getMessage());
				// 删除id对应用户
				ChannelHandler.removeChannel(ctx);
			}
		}
		catch (Exception ex)
		{
			log.error("XXXXXXXXXXXXXXXXXXXXXXXXX:" + ex.getMessage());
		}
	}

}