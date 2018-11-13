package com.spring.cloud.netty.common.enums;

import com.spring.cloud.netty.common.constant.Const;

/**
 * 命令类型枚举类
 *
 * @author wangmj
 * @since 2018/11/12
 */
public enum CmdTypeEnum {
    LOGIN_COMMAND(Const.CONNECT_REQ);

    private short cmdNumber;

    CmdTypeEnum(short cmdNumber) {
        this.cmdNumber = cmdNumber;
    }

    public short getCmdNumber() {
        return cmdNumber;
    }

    public static CmdTypeEnum getTypeEnum(short cmdNumber) {
        for (CmdTypeEnum cmdTypeEnum : CmdTypeEnum.values()) {
            if (cmdTypeEnum.getCmdNumber() == cmdNumber) {
                return cmdTypeEnum;
            }
        }
        return null;
    }
}
