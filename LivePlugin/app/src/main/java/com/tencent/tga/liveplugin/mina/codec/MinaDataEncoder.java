package com.tencent.tga.liveplugin.mina.codec;

import com.tencent.tga.net.mina.core.buffer.IoBuffer;
import com.tencent.tga.net.mina.core.session.IoSession;
import com.tencent.tga.net.mina.filter.codec.ProtocolEncoderAdapter;
import com.tencent.tga.net.mina.filter.codec.ProtocolEncoderOutput;

/**
 *  编码器将数据直接发出去(不做处理)
 */
public class MinaDataEncoder extends ProtocolEncoderAdapter {

    @Override
    public void encode(IoSession session, Object message,
                       ProtocolEncoderOutput out) throws Exception {
        IoBuffer value = (IoBuffer) message;
        out.write(value);
        out.flush();

    }
}