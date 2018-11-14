package com.spring.cloud.netty.common.constant;

/**
 * @author wangmj
 * @since 2018/11/12
 */
public final class Const {

    public static final int MAGIC_DATA = 0x7F;//魔数

    public static final short CONNECT_REQ = 0x1001;//登录请求
    public static final short CONNECT_RESP = 0x1002;//登录请求

    public static final byte VERSION = 1;//版本号

    public final static byte CMD_HEAD = 0x5B;//消息命令头
    public final static byte CMD_END = 0x5D;//消息命令尾
}
