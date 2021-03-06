// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\OperationMsg.proto
package com.tencent.protocol.tga.OperationMsg;

import com.squareup.tga.Message;
import com.squareup.tga.ProtoField;
import okiotga.ByteString;

import static com.squareup.tga.Message.Datatype.BYTES;
import static com.squareup.tga.Message.Datatype.UINT32;
import static com.squareup.tga.Message.Label.REQUIRED;

/**
 * 运营消息
 */
public final class OperationMsg extends Message {

  public static final ByteString DEFAULT_NICK = ByteString.EMPTY;
  public static final ByteString DEFAULT_TEXT_MSG = ByteString.EMPTY;
  public static final Integer DEFAULT_SEQ = 0;
  public static final Integer DEFAULT_MSG_TYPE = 0;
  public static final ByteString DEFAULT_FACE_URL = ByteString.EMPTY;
  public static final Integer DEFAULT_SHOW_TYPE = 0;
  public static final ByteString DEFAULT_COLOR = ByteString.EMPTY;

  @ProtoField(tag = 1, type = BYTES, label = REQUIRED)
  public final ByteString nick;

  /**
   * 昵称
   */
  @ProtoField(tag = 2, type = BYTES, label = REQUIRED)
  public final ByteString text_msg;

  /**
   * 文本消息
   */
  @ProtoField(tag = 3, type = UINT32)
  public final Integer seq;

  /**
   * 消息序列号，唯一标识一条消息，可以用来去重
   */
  @ProtoField(tag = 4, type = UINT32)
  public final Integer msg_type;

  /**
   * 消息类型,4:系统消息,1:房管消息,2:男解说消息,3:男嘉宾消息,5:女解说消息,6:女嘉宾消息
   */
  @ProtoField(tag = 5, type = BYTES)
  public final ByteString face_url;

  /**
   * 玩家头像url
   */
  @ProtoField(tag = 6, type = UINT32)
  public final Integer show_type;

  /**
   * 显示类型 1:只在大厅展示,2:在大厅和包厢展示
   */
  @ProtoField(tag = 7, type = BYTES)
  public final ByteString color;

  public OperationMsg(ByteString nick, ByteString text_msg, Integer seq, Integer msg_type, ByteString face_url, Integer show_type, ByteString color) {
    this.nick = nick;
    this.text_msg = text_msg;
    this.seq = seq;
    this.msg_type = msg_type;
    this.face_url = face_url;
    this.show_type = show_type;
    this.color = color;
  }

  private OperationMsg(Builder builder) {
    this(builder.nick, builder.text_msg, builder.seq, builder.msg_type, builder.face_url, builder.show_type, builder.color);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof OperationMsg)) return false;
    OperationMsg o = (OperationMsg) other;
    return equals(nick, o.nick)
        && equals(text_msg, o.text_msg)
        && equals(seq, o.seq)
        && equals(msg_type, o.msg_type)
        && equals(face_url, o.face_url)
        && equals(show_type, o.show_type)
        && equals(color, o.color);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = nick != null ? nick.hashCode() : 0;
      result = result * 37 + (text_msg != null ? text_msg.hashCode() : 0);
      result = result * 37 + (seq != null ? seq.hashCode() : 0);
      result = result * 37 + (msg_type != null ? msg_type.hashCode() : 0);
      result = result * 37 + (face_url != null ? face_url.hashCode() : 0);
      result = result * 37 + (show_type != null ? show_type.hashCode() : 0);
      result = result * 37 + (color != null ? color.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<OperationMsg> {

    public ByteString nick;
    public ByteString text_msg;
    public Integer seq;
    public Integer msg_type;
    public ByteString face_url;
    public Integer show_type;
    public ByteString color;

    public Builder() {
    }

    public Builder(OperationMsg message) {
      super(message);
      if (message == null) return;
      this.nick = message.nick;
      this.text_msg = message.text_msg;
      this.seq = message.seq;
      this.msg_type = message.msg_type;
      this.face_url = message.face_url;
      this.show_type = message.show_type;
      this.color = message.color;
    }

    public Builder nick(ByteString nick) {
      this.nick = nick;
      return this;
    }

    /**
     * 昵称
     */
    public Builder text_msg(ByteString text_msg) {
      this.text_msg = text_msg;
      return this;
    }

    /**
     * 文本消息
     */
    public Builder seq(Integer seq) {
      this.seq = seq;
      return this;
    }

    /**
     * 消息序列号，唯一标识一条消息，可以用来去重
     */
    public Builder msg_type(Integer msg_type) {
      this.msg_type = msg_type;
      return this;
    }

    /**
     * 消息类型,4:系统消息,1:房管消息,2:男解说消息,3:男嘉宾消息,5:女解说消息,6:女嘉宾消息
     */
    public Builder face_url(ByteString face_url) {
      this.face_url = face_url;
      return this;
    }

    /**
     * 玩家头像url
     */
    public Builder show_type(Integer show_type) {
      this.show_type = show_type;
      return this;
    }

    /**
     * 显示类型 1:只在大厅展示,2:在大厅和包厢展示
     */
    public Builder color(ByteString color) {
      this.color = color;
      return this;
    }

    @Override
    public OperationMsg build() {
      checkRequiredFields();
      return new OperationMsg(this);
    }
  }
}
