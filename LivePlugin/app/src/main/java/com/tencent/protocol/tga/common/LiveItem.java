// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\common.proto
package com.tencent.protocol.tga.common;

import com.squareup.tga.Message;
import com.squareup.tga.ProtoField;
import okiotga.ByteString;

import static com.squareup.tga.Message.Datatype.BYTES;
import static com.squareup.tga.Message.Datatype.UINT32;

public final class LiveItem extends Message {

  public static final ByteString DEFAULT_ROOMID = ByteString.EMPTY;
  public static final ByteString DEFAULT_TITLE = ByteString.EMPTY;
  public static final ByteString DEFAULT_COVER_IMAGE = ByteString.EMPTY;
  public static final ByteString DEFAULT_USERID = ByteString.EMPTY;
  public static final ByteString DEFAULT_USER_NAME = ByteString.EMPTY;
  public static final ByteString DEFAULT_GAME_ID = ByteString.EMPTY;
  public static final ByteString DEFAULT_GAME_NAME = ByteString.EMPTY;
  public static final Integer DEFAULT_ONLINE_NUM = 0;
  public static final ByteString DEFAULT_USER_TAG_NAME = ByteString.EMPTY;
  public static final Integer DEFAULT_STREAM_TYPE = 0;

  @ProtoField(tag = 1, type = BYTES)
  public final ByteString roomid;

  /**
   * 直播房间号
   */
  @ProtoField(tag = 2, type = BYTES)
  public final ByteString title;

  /**
   * 直播标题
   */
  @ProtoField(tag = 3, type = BYTES)
  public final ByteString cover_image;

  /**
   * 直播封面
   */
  @ProtoField(tag = 4, type = BYTES)
  public final ByteString userid;

  /**
   * 主播ID
   */
  @ProtoField(tag = 5, type = BYTES)
  public final ByteString user_name;

  /**
   * 主播名称
   */
  @ProtoField(tag = 6, type = BYTES)
  public final ByteString game_id;

  /**
   * 游戏ID
   */
  @ProtoField(tag = 7, type = BYTES)
  public final ByteString game_name;

  /**
   * 游戏名称
   */
  @ProtoField(tag = 8, type = UINT32)
  public final Integer online_num;

  /**
   * 在线人数
   */
  @ProtoField(tag = 9, type = BYTES)
  public final ByteString user_tag_name;

  /**
   * 主播标签
   */
  @ProtoField(tag = 10, type = UINT32)
  public final Integer stream_type;

  public LiveItem(ByteString roomid, ByteString title, ByteString cover_image, ByteString userid, ByteString user_name, ByteString game_id, ByteString game_name, Integer online_num, ByteString user_tag_name, Integer stream_type) {
    this.roomid = roomid;
    this.title = title;
    this.cover_image = cover_image;
    this.userid = userid;
    this.user_name = user_name;
    this.game_id = game_id;
    this.game_name = game_name;
    this.online_num = online_num;
    this.user_tag_name = user_tag_name;
    this.stream_type = stream_type;
  }

  private LiveItem(Builder builder) {
    this(builder.roomid, builder.title, builder.cover_image, builder.userid, builder.user_name, builder.game_id, builder.game_name, builder.online_num, builder.user_tag_name, builder.stream_type);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof LiveItem)) return false;
    LiveItem o = (LiveItem) other;
    return equals(roomid, o.roomid)
        && equals(title, o.title)
        && equals(cover_image, o.cover_image)
        && equals(userid, o.userid)
        && equals(user_name, o.user_name)
        && equals(game_id, o.game_id)
        && equals(game_name, o.game_name)
        && equals(online_num, o.online_num)
        && equals(user_tag_name, o.user_tag_name)
        && equals(stream_type, o.stream_type);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = roomid != null ? roomid.hashCode() : 0;
      result = result * 37 + (title != null ? title.hashCode() : 0);
      result = result * 37 + (cover_image != null ? cover_image.hashCode() : 0);
      result = result * 37 + (userid != null ? userid.hashCode() : 0);
      result = result * 37 + (user_name != null ? user_name.hashCode() : 0);
      result = result * 37 + (game_id != null ? game_id.hashCode() : 0);
      result = result * 37 + (game_name != null ? game_name.hashCode() : 0);
      result = result * 37 + (online_num != null ? online_num.hashCode() : 0);
      result = result * 37 + (user_tag_name != null ? user_tag_name.hashCode() : 0);
      result = result * 37 + (stream_type != null ? stream_type.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<LiveItem> {

    public ByteString roomid;
    public ByteString title;
    public ByteString cover_image;
    public ByteString userid;
    public ByteString user_name;
    public ByteString game_id;
    public ByteString game_name;
    public Integer online_num;
    public ByteString user_tag_name;
    public Integer stream_type;

    public Builder() {
    }

    public Builder(LiveItem message) {
      super(message);
      if (message == null) return;
      this.roomid = message.roomid;
      this.title = message.title;
      this.cover_image = message.cover_image;
      this.userid = message.userid;
      this.user_name = message.user_name;
      this.game_id = message.game_id;
      this.game_name = message.game_name;
      this.online_num = message.online_num;
      this.user_tag_name = message.user_tag_name;
      this.stream_type = message.stream_type;
    }

    public Builder roomid(ByteString roomid) {
      this.roomid = roomid;
      return this;
    }

    /**
     * 直播房间号
     */
    public Builder title(ByteString title) {
      this.title = title;
      return this;
    }

    /**
     * 直播标题
     */
    public Builder cover_image(ByteString cover_image) {
      this.cover_image = cover_image;
      return this;
    }

    /**
     * 直播封面
     */
    public Builder userid(ByteString userid) {
      this.userid = userid;
      return this;
    }

    /**
     * 主播ID
     */
    public Builder user_name(ByteString user_name) {
      this.user_name = user_name;
      return this;
    }

    /**
     * 主播名称
     */
    public Builder game_id(ByteString game_id) {
      this.game_id = game_id;
      return this;
    }

    /**
     * 游戏ID
     */
    public Builder game_name(ByteString game_name) {
      this.game_name = game_name;
      return this;
    }

    /**
     * 游戏名称
     */
    public Builder online_num(Integer online_num) {
      this.online_num = online_num;
      return this;
    }

    /**
     * 在线人数
     */
    public Builder user_tag_name(ByteString user_tag_name) {
      this.user_tag_name = user_tag_name;
      return this;
    }

    /**
     * 主播标签
     */
    public Builder stream_type(Integer stream_type) {
      this.stream_type = stream_type;
      return this;
    }

    @Override
    public LiveItem build() {
      return new LiveItem(this);
    }
  }
}
