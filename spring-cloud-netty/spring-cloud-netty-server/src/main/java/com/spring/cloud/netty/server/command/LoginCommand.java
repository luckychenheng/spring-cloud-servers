package com.spring.cloud.netty.server.command;

import com.spring.cloud.netty.server.cache.ServerConnectionMap;
import com.spring.cloud.netty.server.interfaces.CommandAdapter;
import com.spring.cloud.netty.common.constant.Const;
import com.spring.cloud.netty.common.entity.LoginInfo;
import com.spring.cloud.netty.common.protocol.SerializationUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author wangmj
 * @since 2018/11/13
 */
@Component
@Slf4j
public class LoginCommand extends CommandAdapter {

    private LoginInfo loginInfo;

    private int bodySize;

    private byte result;

    @Override
    public boolean disposeData(ByteBuf buffer) {
        int dataLength = buffer.readInt();
        byte[] data = new byte[dataLength];
        buffer.readBytes(data);
        loginInfo = SerializationUtil.deserialize(data, LoginInfo.class);
        log.info(loginInfo.toString());
        ServerConnectionMap.setLoginState("isLogin");
        result = 0x00;
        return true;
    }

    @Override
    public void fillCmdBody(ByteBuf byteBuf) {
        byteBuf.writeByte(result);
    }

    @Override
    public short getCmd() {
        return Const.CONNECT_RESP;
    }

    @Override
    public int getCmdBodySize() {
        return 1;
    }
}
