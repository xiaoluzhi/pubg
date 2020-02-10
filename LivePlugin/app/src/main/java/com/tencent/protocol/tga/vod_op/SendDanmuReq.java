// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\vod_op.proto
package com.tencent.protocol.tga.vod_op;

import com.squareup.tga.Message;
import com.squareup.tga.ProtoField;
import okiotga.ByteString;

import static com.squareup.tga.Message.Datatype.BYTES;
import static com.squareup.tga.Message.Datatype.UINT32;

/**
 * SUBCMD_SEND_DANMU = 0x1;  // 发送弹幕
 */
public final class SendDanmuReq extends Message {

  public static final ByteString DEFAULT_USERID = ByteString.EMPTY;
  public static final ByteString DEFAULT_OPENID = ByteString.EMPTY;
  public static final ByteString DEFAULT_VID = ByteString.EMPTY;
  public static final ByteString DEFAULT_NICK = ByteString.EMPTY;
  public static final ByteString DEFAULT_AVATAR = ByteString.EMPTY;
  public static final Integer DEFAULT_OFFSET_MS = 0;
  public static final ByteString DEFAULT_MSG = ByteString.EMPTY;
  public static final ByteString DEFAULT_GAMEID = ByteString.EMPTY;
  public static final Integer DEFAULT_GAME_LEVEL = 0;

  @ProtoField(tag = 1, type = BYTES)
  public final ByteString userid;

  @ProtoField(tag = 2, type = BYTES)
  public final ByteString openid;

  @ProtoField(tag = 3, type = BYTES)
  public final ByteString vid;

  /**
   * 视频vid
   */
  @ProtoField(tag = 4, type = BYTES)
  public final ByteString nick;

  @ProtoField(tag = 5, type = BYTES)
  public final ByteString avatar;

  /**
   * 头像
   */
  @ProtoField(tag = 6, type = UINT32)
  public final Integer offset_ms;

  /**
   * 时间偏移, 毫秒
   */
  @ProtoField(tag = 7, type = BYTES)
  public final ByteString msg;

  /**
   * 消息内容
   */
  @ProtoField(tag = 8, type = BYTES)
  public final ByteString gameid;

  /**
   * 游戏id, "smoba"
   */
  @ProtoField(tag = 9, type = UINT32)
  public final Integer game_level;

  public SendDanmuReq(ByteString userid, ByteString openid, ByteString vid, ByteString nick, ByteString avatar, Integer offset_ms, ByteString msg, ByteString gameid, Integer game_level) {
    this.userid = userid;
    this.openid = openid;
    this.vid = vid;
    this.nick = nick;
    this.avatar = avatar;
    this.offset_ms = offset_ms;
    this.msg = msg;
    this.gameid = gameid;
    this.game_level = game_level;
  }

  private SendDanmuReq(Builder builder) {
    this(builder.userid, builder.openid, builder.vid, builder.nick, builder.avatar, builder.offset_ms, builder.msg, builder.gameid, builder.game_level);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof SendDanmuReq)) return false;
    SendDanmuReq o = (SendDanmuReq) other;
    return equals(userid, o.userid)
        && equals(openid, o.openid)
        && equals(vid, o.vid)
        && equals(nick, o.nick)
        && equals(avatar, o.avatar)
        && equals(offset_ms, o.offset_ms)
        && equals(msg, o.msg)
        && equals(gameid, o.gameid)
        && equals(game_level, o.game_level);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = userid != null ? userid.hashCode() : 0;
      result = result * 37 + (openid != null ? openid.hashCode() : 0);
      result = result * 37 + (vid != null ? vid.hashCode() : 0);
      result = result * 37 + (nick != null ? nick.hashCode() : 0);
      result = result * 37 + (avatar != null ? avatar.hashCode() : 0);
      result = result * 37 + (offset_ms != null ? offset_ms.hashCode() : 0);
      result = result * 37 + (msg != null ? msg.hashCode() : 0);
      result = result * 37 + (gameid != null ? gameid.hashCode() : 0);
      result = result * 37 + (game_level != null ? game_level.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<SendDanmuReq> {

    public ByteString userid;
    public ByteString openid;
    public ByteString vid;
    public ByteString nick;
    public ByteString avatar;
    public Integer offset_ms;
    public ByteString msg;
    public ByteString gameid;
    public Integer game_level;

    public Builder() {
    }

    public Builder(SendDanmuReq message) {
      super(message);
      if (message == null) return;
      this.userid = message.userid;
      this.openid = message.openid;
      this.vid = message.vid;
      this.nick = message.nick;
      this.avatar = message.avatar;
      this.offset_ms = message.offset_ms;
      this.msg = message.msg;
      this.gameid = message.gameid;
      this.game_level = message.game_level;
    }

    public Builder userid(ByteString userid) {
      this.userid = userid;
      return this;
    }

    public Builder openid(ByteString openid) {
      this.openid = openid;
      return this;
    }

    public Builder vid(ByteString vid) {
      this.vid = vid;
      return this;
    }

    /**
     * 视频vid
     */
    public Builder nick(ByteString nick) {
      this.nick = nick;
      return this;
    }

    public Builder avatar(ByteString avatar) {
      this.avatar = avatar;
      return this;
    }

    /**
     * 头像
     */
    public Builder offset_ms(Integer offset_ms) {
      this.offset_ms = offset_ms;
      return this;
    }

    /**
     * 时间偏移, 毫秒
     */
    public Builder msg(ByteString msg) {
      this.msg = msg;
      return this;
    }

    /**
     * 消息内容
     */
    public Builder gameid(ByteString gameid) {
      this.gameid = gameid;
      return this;
    }

    /**
     * 游戏id, "smoba"
     */
    public Builder game_level(Integer game_level) {
      this.game_level = game_level;
      return this;
    }

    @Override
    public SendDanmuReq build() {
      return new SendDanmuReq(this);
    }
  }
}
