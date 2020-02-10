// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\chat_msg.proto
package com.tencent.protocol.tga.chatMsg;

import com.squareup.tga.Message;
import com.squareup.tga.ProtoField;
import okiotga.ByteString;

import static com.squareup.tga.Message.Datatype.BYTES;
import static com.squareup.tga.Message.Datatype.UINT32;
import static com.squareup.tga.Message.Label.REQUIRED;

/**
 * 包厢用户1v1互动消息通知
 */
public final class Balcony1V1Msg extends Message {

  public static final ByteString DEFAULT_MSG = ByteString.EMPTY;
  public static final Integer DEFAULT_MSG_TYPE = 0;
  public static final Integer DEFAULT_SEQ = 0;

  @ProtoField(tag = 1, type = BYTES, label = REQUIRED)
  public final ByteString msg;

  /**
   * 消息内容
   */
  @ProtoField(tag = 2, type = UINT32, label = REQUIRED)
  public final Integer msg_type;

  /**
   * 消息类型,0:拒绝邀请,1对方已经在包厢,其它:暂未使用
   */
  @ProtoField(tag = 3, type = UINT32, label = REQUIRED)
  public final Integer seq;

  public Balcony1V1Msg(ByteString msg, Integer msg_type, Integer seq) {
    this.msg = msg;
    this.msg_type = msg_type;
    this.seq = seq;
  }

  private Balcony1V1Msg(Builder builder) {
    this(builder.msg, builder.msg_type, builder.seq);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Balcony1V1Msg)) return false;
    Balcony1V1Msg o = (Balcony1V1Msg) other;
    return equals(msg, o.msg)
        && equals(msg_type, o.msg_type)
        && equals(seq, o.seq);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = msg != null ? msg.hashCode() : 0;
      result = result * 37 + (msg_type != null ? msg_type.hashCode() : 0);
      result = result * 37 + (seq != null ? seq.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<Balcony1V1Msg> {

    public ByteString msg;
    public Integer msg_type;
    public Integer seq;

    public Builder() {
    }

    public Builder(Balcony1V1Msg message) {
      super(message);
      if (message == null) return;
      this.msg = message.msg;
      this.msg_type = message.msg_type;
      this.seq = message.seq;
    }

    public Builder msg(ByteString msg) {
      this.msg = msg;
      return this;
    }

    /**
     * 消息内容
     */
    public Builder msg_type(Integer msg_type) {
      this.msg_type = msg_type;
      return this;
    }

    /**
     * 消息类型,0:拒绝邀请,1对方已经在包厢,其它:暂未使用
     */
    public Builder seq(Integer seq) {
      this.seq = seq;
      return this;
    }

    @Override
    public Balcony1V1Msg build() {
      checkRequiredFields();
      return new Balcony1V1Msg(this);
    }
  }
}
