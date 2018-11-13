package com.spring.cloud.netty.common;

import io.netty.buffer.ByteBuf;

/**
 * @author wangmj
 * @since 2018/11/13
 */
public interface ICommand {

    //命令长度（从头标识到尾标识完整的长度）
    int getCmdSize();

    //解析收到的命令
    boolean disposeData(ByteBuf buffer);

    /**
     * 获取给命令完整可发送的channel buffer<p>
     * 切记：获取前先将每个具体命令的字段设置好
     * @return 可直接发送的channel buffer
     */
    ByteBuf getSendBuffer();

}
