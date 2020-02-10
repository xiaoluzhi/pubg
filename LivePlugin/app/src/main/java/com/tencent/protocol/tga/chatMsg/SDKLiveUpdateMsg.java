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
 * sdk直播定时更新消息
 */
public final class SDKLiveUpdateMsg extends Message {

  public static final ByteString DEFAULT_CURRENT_LIVE_INFO = ByteString.EMPTY;
  public static final Integer DEFAULT_SEQ = 0;
  public static final Integer DEFAULT_DATE_TIME = 0;

  @ProtoField(tag = 1, type = BYTES, label = REQUIRED)
  public final ByteString current_live_info;

  /**
   * 当前直播信息,是match.proto中GetCurrentMatchRsp序列化的字符串
   */
  @ProtoField(tag = 2, type = UINT32)
  public final Integer seq;

  /**
   * 消息序列号，唯一标识一条消息，可以用来去重
   */
  @ProtoField(tag = 3, type = UINT32)
  public final Integer date_time;

  public SDKLiveUpdateMsg(ByteString current_live_info, Integer seq, Integer date_time) {
    this.current_live_info = current_live_info;
    this.seq = seq;
    this.date_time = date_time;
  }

  private SDKLiveUpdateMsg(Builder builder) {
    this(builder.current_live_info, builder.seq, builder.date_time);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof SDKLiveUpdateMsg)) return false;
    SDKLiveUpdateMsg o = (SDKLiveUpdateMsg) other;
    return equals(current_live_info, o.current_live_info)
        && equals(seq, o.seq)
        && equals(date_time, o.date_time);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = current_live_info != null ? current_live_info.hashCode() : 0;
      result = result * 37 + (seq != null ? seq.hashCode() : 0);
      result = result * 37 + (date_time != null ? date_time.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<SDKLiveUpdateMsg> {

    public ByteString current_live_info;
    public Integer seq;
    public Integer date_time;

    public Builder() {
    }

    public Builder(SDKLiveUpdateMsg message) {
      super(message);
      if (message == null) return;
      this.current_live_info = message.current_live_info;
      this.seq = message.seq;
      this.date_time = message.date_time;
    }

    public Builder current_live_info(ByteString current_live_info) {
      this.current_live_info = current_live_info;
      return this;
    }

    /**
     * 当前直播信息,是match.proto中GetCurrentMatchRsp序列化的字符串
     */
    public Builder seq(Integer seq) {
      this.seq = seq;
      return this;
    }

    /**
     * 消息序列号，唯一标识一条消息，可以用来去重
     */
    public Builder date_time(Integer date_time) {
      this.date_time = date_time;
      return this;
    }

    @Override
    public SDKLiveUpdateMsg build() {
      checkRequiredFields();
      return new SDKLiveUpdateMsg(this);
    }
  }
}
