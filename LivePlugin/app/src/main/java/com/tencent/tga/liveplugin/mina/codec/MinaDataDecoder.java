package com.tencent.tga.liveplugin.mina.codec;

import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.mina.utils.ByteConvert;
import com.tencent.tga.liveplugin.mina.utils.MinaConstants;
import com.tencent.tga.net.mina.core.buffer.IoBuffer;
import com.tencent.tga.net.mina.core.session.IoSession;
import com.tencent.tga.net.mina.filter.codec.CumulativeProtocolDecoder;
import com.tencent.tga.net.mina.filter.codec.ProtocolDecoderOutput;

public class MinaDataDecoder extends CumulativeProtocolDecoder {

    private static final String TAG = MinaConstants.TAG+"MinaDataDecoder";

    private int BEGIN_FLAG = 14;
    private int MAX_LENGTH = 2048;
    /**
     * 返回值含义:
     * 1、当内容刚好时，返回false，告知父类接收下一批内容
     * 2、内容不够时需要下一批发过来的内容，此时返回false，这样父类 CumulativeProtocolDecoder
     * 会将内容放进IoSession中，等下次来数据后就自动拼装再交给本类的doDecode
     * 3、当内容多时，返回true，因为需要再将本批数据进行读取，父类会将剩余的数据再次推送本类的doDecode方法
     */
    @Override
    public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
            throws Exception {
        /**
         * 假定消息格式为：消息头（int类型：表示消息体的长度、short类型：表示事件号）+消息体
         */

        if(Configs.Debug)
            TLog.e(TAG, "i客户端接收到的信息为:" + in.getHexDump());
        if (in.remaining() < 1 || in.remaining()>MAX_LENGTH)//是用来当拆包时候剩余长度小于1的时候的保护，不加容易出错
        {
            TLog.e(TAG, "in.remaining is 1 : " + in.remaining());
            clearAllData(in,out);
            return false;
        }
        if (in.remaining() > 0) {
            //以便后继的reset操作能恢复position位置
            in.mark();
            ////前6字节是包头，一个int和一个short，我们先取一个int
            int len = getLength(in);//先获取包体数据长度值
            TLog.e(TAG, "in.remaining is 2 : " + in.remaining());
            TLog.e(TAG, "len is : " + len);
            TLog.e(TAG,"position is : " + in.position());

            if(len < 0){
                in.reset();
                clearAllData(in,out);
                return false;
            }
            //比较消息长度和实际收到的长度是否相等，这里-2是因为我们的消息头有个short值还没取
            if (len > in.remaining()+4) {
                //出现断包，则重置恢复position位置到操作前,进入下一轮, 接收新数据，以拼凑成完整数据

                int beginFlag = getBeginFlag(in);
                if(beginFlag != BEGIN_FLAG){
                    in.reset();
                    TLog.e(TAG,"beginFlag is : "+beginFlag +" ; "+in.position());
                    clearAllData(in,out);
                    return false;
                }
                in.reset();
                return false;
            } else {
                //消息内容足够
                in.reset();//重置恢复position位置到操作前
                int sumLen = len;//总长 = 包头+包体
                byte[] packArr = new byte[sumLen];
                in.get(packArr, 0, sumLen);
                IoBuffer buffer = IoBuffer.allocate(sumLen);
                buffer.put(packArr);
                buffer.flip();
                out.write(buffer);
                buffer.free();//必须释放不然会报异常
                //走到这里会调用DefaultHandler的messageReceived方法

                if (in.remaining() > 0) {//出现粘包，就让父类再调用一次，进行下一次解析
                    return true;
                }
            }
        }

        return false;
    }

    public static int getLength(IoBuffer in){
        byte[] b = new byte[4];
        in.get(b,0,4);
        return ByteConvert.bytesToInt(b);

    }

    public static int getBeginFlag(IoBuffer in){

        TLog.e(TAG,"getBeginFlag position : "+in.position());
        byte[] b = new byte[1];
        in.get(b,0,1);

        return b[0]&0xff;
    }

    public static void clearAllData(IoBuffer in, ProtocolDecoderOutput out){
        int sumLen = in.remaining();//总长 = 包头+包体
        byte[] packArr = new byte[sumLen];
        in.get(packArr, 0, sumLen);
        IoBuffer buffer = IoBuffer.allocate(sumLen);
        buffer.put(packArr);
        buffer.flip();
        out.write(buffer);
        buffer.free();
    }
}