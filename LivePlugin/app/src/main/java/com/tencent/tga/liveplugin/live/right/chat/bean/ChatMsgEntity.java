
package com.tencent.tga.liveplugin.live.right.chat.bean;

/**
 *
 ******************************************
 * @文件名称	:  ChatMsgEntity.java
 * @文件描述	: 消息实体
 ******************************************
 */
public class ChatMsgEntity {

    public int msgType=0;

    public int subType=-1;//运营消息

    public int showType = 1;

    public String roomId = "";

    public String name = "";

    public String text = "";

    public String nickUrl = "";

    public boolean isSel;

    public String bageId;

    public String color;//弹幕背景色，以及聊天室的字体颜色

    public ChatMsgEntity(){}
}
