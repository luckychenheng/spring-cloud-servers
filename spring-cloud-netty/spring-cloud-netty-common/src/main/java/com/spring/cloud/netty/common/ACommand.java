package com.spring.cloud.netty.common;

import io.netty.buffer.ByteBuf;

/**
 * @author wangmj
 * @since 2018/11/13
 */
public abstract class ACommand implements ICommand {
    private byte headFlag;//头标识
    private byte endFlag;//尾标识
    private int msgLen;//数据长度(包括头标识、数据头、数据体和尾标识)
    private int msgSNo;//报文序列号
    private short msgFlagId;//业务数据类型
    private byte encryptFlag;//报文加密标识位b: 0表示报文不加密，1表示报文加密
    private int encryptKey;//数据加密的密匙，长度为4个字节


    private int cmdHeadSize;
    private int cmdEndSize;

    final static byte ENCRYPT_FLAG_UNENCRYPTED = 0;//报文未加密
    final static byte ENCRYPT_FLAG_ENCRYPTED = 1;//报文加密

    public ACommand() {
        this.cmdHeadSize = 4 + 1 + 1 + 4 + 4 + 2 + 1 + 4;//魔数+头标识+...+加密秘钥
        this.cmdEndSize = 3;
    }

    /**
     * 获取每个具体命令体的长度
     *
     * @return
     */
    protected abstract int getCmdBodySize();

    @Override
    public boolean disposeData(ByteBuf channelBuffer) {
        //todo 魔数验证
        if (channelBuffer != null && channelBuffer.readableBytes() >= this.getCmdSize()) {
            this.headFlag = channelBuffer.readByte();
            this.msgLen = channelBuffer.readInt();
            this.msgSNo = channelBuffer.readInt();
            this.msgFlagId = channelBuffer.readShort();
            this.encryptFlag = channelBuffer.readByte();
            this.encryptKey = channelBuffer.readInt();

            //解析的数据可先不检测CRC

            int sIndex = channelBuffer.readerIndex();
            int eIndex = sIndex + getCmdBodySize();
            if (this.encryptFlag == ENCRYPT_FLAG_ENCRYPTED) {
                this.encryptData(channelBuffer, sIndex, eIndex, encryptKey);
            }

            disposeCmdBody(channelBuffer);
            return true;
        }
        return false;
    }

    /**
     * 解析每个具体命令消息体的内容
     *
     * @param channelBuffer
     */
    protected abstract void disposeCmdBody(ByteBuf channelBuffer);

    /**
     * 数据加解密算法
     *
     * @param buffer
     * @param sIndex（包含）
     * @param eIndex（不含）
     * @param key
     */
    private void encryptData(ByteBuf buffer, int sIndex, int eIndex, long key) {
        //todo 解密
    }


    public byte getHeadFlag() {
        return headFlag;
    }

    public int getMsgLen() {
        return msgLen;
    }

    public int getMsgSNo() {
        return msgSNo;
    }

    public short getMsgFlagId() {
        return msgFlagId;
    }

    public void setMsgFlagId(short msgFlagId) {
        this.msgFlagId = msgFlagId;
    }

    public byte getEncryptFlag() {
        return encryptFlag;
    }

    public int getEncryptKey() {
        return encryptKey;
    }

    public byte getEndFlag() {
        return endFlag;
    }
}
