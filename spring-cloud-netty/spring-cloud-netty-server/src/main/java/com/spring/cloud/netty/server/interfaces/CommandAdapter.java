package com.spring.cloud.netty.server.interfaces;

import com.spring.cloud.netty.common.constant.Const;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.springframework.stereotype.Component;

/**
 * 抽象命令模板
 *
 * @author wangmj
 * @since 2018/11/14
 */
@Component
public abstract class CommandAdapter implements ICommand {

    private int magicData;
    private byte version;

    public CommandAdapter() {
        magicData = Const.MAGIC_DATA;
        version = Const.VERSION;
    }

    private int getCmdHeadSize() {
        return 4 + 1 + 2;
    }

    private void setFiled() {
        this.magicData = Const.MAGIC_DATA;
        this.version = Const.VERSION;
    }



    @Override
    public ByteBuf getSendBuffer() {
        setFiled();
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(getCmdBodySize() + getCmdHeadSize());
        fillChannelBuffer(byteBuf);
        return byteBuf;
    }

    private boolean fillChannelBuffer(ByteBuf byteBuf) {
        byteBuf.writeInt(this.magicData);
        byteBuf.writeByte(this.version);
        byteBuf.writeShort(getCmd());
        fillCmdBody(byteBuf);
        return true;
    }

    public abstract void fillCmdBody(ByteBuf byteBuf);

    public abstract short getCmd();

    public abstract int getCmdBodySize();
}
