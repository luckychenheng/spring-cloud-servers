package com.spring.cloud.netty.client.handler;

import com.spring.cloud.netty.common.constant.Const;
import com.spring.cloud.netty.common.entity.LoginInfo;
import com.spring.cloud.netty.common.protocol.SerializationUtil;
import com.spring.cloud.netty.common.util.BuildByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.UUID;

/**
 * @author wangmj
 * @since 2018/11/7
 */
@Slf4j
public class ClientMessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("客户端登录开始");
        login(ctx);
    }

    private void login(ChannelHandlerContext ctx) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUserId(UUID.randomUUID().toString());
        loginInfo.setUserName("root");
        loginInfo.setPassword("123456");
        loginInfo.setPlatform("ios");
        loginInfo.setVersion("1.0");
        byte[] loginData = SerializationUtil.serialize(loginInfo);
        ByteBuf data = BuildByteBuf.build(loginData, Const.CONNECT_REQ);
        ctx.writeAndFlush(data);
        log.info("发送登录请求，userId:{}", loginInfo.getUserId());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(new Date() + ": 客户端读到数据 -> " + byteBuf.toString(Charset.forName("utf-8")));
    }
}
