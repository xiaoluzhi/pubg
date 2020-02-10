// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\chat_msg.proto
package com.tencent.protocol.tga.chatMsg;

import com.squareup.tga.Message;
import com.squareup.tga.ProtoField;
import okiotga.ByteString;

import static com.squareup.tga.Message.Datatype.BYTES;
import static com.squareup.tga.Message.Datatype.UINT32;
import static com.squareup.tga.Message.Label.REQUIRED;

public final class SDKReceiveBoxCriticalMsg extends Message {

  public static final ByteString DEFAULT_USER_ID = ByteString.EMPTY;
  public static final ByteString DEFAULT_USER_NAME = ByteString.EMPTY;
  public static final ByteString DEFAULT_CONTENT = ByteString.EMPTY;
  public static final Integer DEFAULT_SEQ = 0;

  @ProtoField(tag = 1, type = BYTES, label = REQUIRED)
  public final ByteString user_id;

  /**
   * 暴击的用户id
   */
  @ProtoField(tag = 2, type = BYTES)
  public final ByteString user_name;

  /**
   * 暴击的用户名
   */
  @ProtoField(tag = 3, type = BYTES)
  public final ByteString content;

  /**
   * 暴击消息的提示文本
   */
  @ProtoField(tag = 4, type = UINT32)
  public final Integer seq;

  public SDKReceiveBoxCriticalMsg(ByteString user_id, ByteString user_name, ByteString content, Integer seq) {
    this.user_id = user_id;
    this.user_name = user_name;
    this.content = content;
    this.seq = seq;
  }

  private SDKReceiveBoxCriticalMsg(Builder builder) {
    this(builder.user_id, builder.user_name, builder.content, builder.seq);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof SDKReceiveBoxCriticalMsg)) return false;
    SDKReceiveBoxCriticalMsg o = (SDKReceiveBoxCriticalMsg) other;
    return equals(user_id, o.user_id)
        && equals(user_name, o.user_name)
        && equals(content, o.content)
        && equals(seq, o.seq);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = user_id != null ? user_id.hashCode() : 0;
      result = result * 37 + (user_name != null ? user_name.hashCode() : 0);
      result = result * 37 + (content != null ? content.hashCode() : 0);
      result = result * 37 + (seq != null ? seq.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<SDKReceiveBoxCriticalMsg> {

    public ByteString user_id;
    public ByteString user_name;
    public ByteString content;
    public Integer seq;

    public Builder() {
    }

    public Builder(SDKReceiveBoxCriticalMsg message) {
      super(message);
      if (message == null) return;
      this.user_id = message.user_id;
      this.user_name = message.user_name;
      this.content = message.content;
      this.seq = message.seq;
    }

    public Builder user_id(ByteString user_id) {
      this.user_id = user_id;
      return this;
    }

    /**
     * 暴击的用户id
     */
    public Builder user_name(ByteString user_name) {
      this.user_name = user_name;
      return this;
    }

    /**
     * 暴击的用户名
     */
    public Builder content(ByteString content) {
      this.content = content;
      return this;
    }

    /**
     * 暴击消息的提示文本
     */
    public Builder seq(Integer seq) {
      this.seq = seq;
      return this;
    }

    @Override
    public SDKReceiveBoxCriticalMsg build() {
      checkRequiredFields();
      return new SDKReceiveBoxCriticalMsg(this);
    }
  }
}
