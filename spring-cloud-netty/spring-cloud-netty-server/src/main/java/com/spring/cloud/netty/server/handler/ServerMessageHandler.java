package com.spring.cloud.netty.server.handler;

import com.spring.cloud.netty.server.interfaces.ICommand;
import com.spring.cloud.netty.common.constant.Const;
import com.spring.cloud.netty.common.enums.CmdTypeEnum;
import com.spring.cloud.netty.server.util.SpringContextUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 接收客户端数据的逻辑请求
 *
 * @author wangmj
 * @since 2018/11/6
 */
@Slf4j
@Component
public class ServerMessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ByteBuf byteBuf = (ByteBuf) msg;
        int magic = byteBuf.readInt();
        if (magic != Const.MAGIC_DATA) {
            log.info("不符合数据传输协议,,magic:{}", magic);
            ctx.channel().close();
            return;
        }
        byte version = byteBuf.readByte();
        short cmd = byteBuf.readShort();

        CmdTypeEnum typeEnum = CmdTypeEnum.getTypeEnum(cmd);
        if (typeEnum == null) {
            log.info("不支持该指令的传输;指令：{}", cmd);
            return;
        }

        ICommand commandService = (ICommand) SpringContextUtil.getBean(typeEnum.getCmdName());
        if (commandService == null) {
            log.info("不支持该指令的传输;指令：{}", cmd);
            return;
        }
        commandService.disposeData(byteBuf);
        log.info(new Date() + ": 服务端读到数据 -> ");

        ByteBuf sendBuffer = commandService.getSendBuffer();
        // 回复数据到客户端
        log.info(new Date() + ": 服务端写出数据");
        ctx.channel().writeAndFlush(sendBuffer);
    }
}
