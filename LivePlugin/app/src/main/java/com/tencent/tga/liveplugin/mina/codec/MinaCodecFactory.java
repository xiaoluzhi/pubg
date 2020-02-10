package com.tencent.tga.liveplugin.mina.codec;

import com.tencent.tga.net.mina.core.session.IoSession;
import com.tencent.tga.net.mina.filter.codec.ProtocolCodecFactory;
import com.tencent.tga.net.mina.filter.codec.ProtocolDecoder;
import com.tencent.tga.net.mina.filter.codec.ProtocolEncoder;

public class MinaCodecFactory implements ProtocolCodecFactory {
    private MinaDataDecoder decoder;
    private MinaDataEncoder encoder;
    public MinaCodecFactory() {
        encoder = new MinaDataEncoder();
        decoder = new MinaDataDecoder();
    }
    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }
    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }
}
