package com.tencent.tga.liveplugin.mina.handler;

import com.tencent.tga.net.encrypt.MinaMessageHandler;

/**
 * Created by hyqiao on 2017/8/31.
 */

public class MinaWrapHandler {
    public MinaMessageHandler handler;
    public long timestamp;
    public int command;
    public int subcmd;
    public int seq;

    public MinaWrapHandler(MinaMessageHandler handler, long timestamp, int command, int subcmd, int seq) {
        this.handler = handler;
        this.timestamp = timestamp;
        this.command = command;
        this.subcmd = subcmd;
        this.seq = seq;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public MinaMessageHandler getHandler() {
        return handler;
    }

    public void setHandler(MinaMessageHandler handler) {
        this.handler = handler;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getSubcmd() {
        return subcmd;
    }

    public void setSubcmd(int subcmd) {
        this.subcmd = subcmd;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
