// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\conn.proto
package com.tencent.protocol.tga.conn;

import com.squareup.tga.Message;
import com.squareup.tga.ProtoField;
import java.util.Collections;
import java.util.List;
import okiotga.ByteString;

import static com.squareup.tga.Message.Datatype.BYTES;
import static com.squareup.tga.Message.Datatype.UINT32;
import static com.squareup.tga.Message.Label.REPEATED;
import static com.squareup.tga.Message.Label.REQUIRED;

/**
 * SUBCMD_CONN_ROOM_HELLO	= 0x13	房间心跳。
 */
public final class Room_HelloReq extends Message {

  public static final ByteString DEFAULT_ROOMID = ByteString.EMPTY;
  public static final Integer DEFAULT_CUR_TIME = 0;
  public static final List<ByteString> DEFAULT_BALCONY_LIST = Collections.emptyList();
  public static final Integer DEFAULT_ROOM_PUSH_FLAG = 0;
  public static final ByteString DEFAULT_JF_GAMEID = ByteString.EMPTY;

  @ProtoField(tag = 1, type = BYTES, label = REQUIRED)
  public final ByteString roomid;

  @ProtoField(tag = 2, type = UINT32, label = REQUIRED)
  public final Integer cur_time;

  @ProtoField(tag = 3, type = BYTES, label = REPEATED)
  public final List<ByteString> balcony_list;

  /**
   * 包厢id列表
   */
  @ProtoField(tag = 4, type = UINT32)
  public final Integer room_push_flag;

  /**
   * 房间推送标志,0:推送roomid的消息,1:屏蔽roomid的消息
   */
  @ProtoField(tag = 5, type = BYTES)
  public final ByteString jf_gameid;

  public Room_HelloReq(ByteString roomid, Integer cur_time, List<ByteString> balcony_list, Integer room_push_flag, ByteString jf_gameid) {
    this.roomid = roomid;
    this.cur_time = cur_time;
    this.balcony_list = immutableCopyOf(balcony_list);
    this.room_push_flag = room_push_flag;
    this.jf_gameid = jf_gameid;
  }

  private Room_HelloReq(Builder builder) {
    this(builder.roomid, builder.cur_time, builder.balcony_list, builder.room_push_flag, builder.jf_gameid);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Room_HelloReq)) return false;
    Room_HelloReq o = (Room_HelloReq) other;
    return equals(roomid, o.roomid)
        && equals(cur_time, o.cur_time)
        && equals(balcony_list, o.balcony_list)
        && equals(room_push_flag, o.room_push_flag)
        && equals(jf_gameid, o.jf_gameid);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = roomid != null ? roomid.hashCode() : 0;
      result = result * 37 + (cur_time != null ? cur_time.hashCode() : 0);
      result = result * 37 + (balcony_list != null ? balcony_list.hashCode() : 1);
      result = result * 37 + (room_push_flag != null ? room_push_flag.hashCode() : 0);
      result = result * 37 + (jf_gameid != null ? jf_gameid.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<Room_HelloReq> {

    public ByteString roomid;
    public Integer cur_time;
    public List<ByteString> balcony_list;
    public Integer room_push_flag;
    public ByteString jf_gameid;

    public Builder() {
    }

    public Builder(Room_HelloReq message) {
      super(message);
      if (message == null) return;
      this.roomid = message.roomid;
      this.cur_time = message.cur_time;
      this.balcony_list = copyOf(message.balcony_list);
      this.room_push_flag = message.room_push_flag;
      this.jf_gameid = message.jf_gameid;
    }

    public Builder roomid(ByteString roomid) {
      this.roomid = roomid;
      return this;
    }

    public Builder cur_time(Integer cur_time) {
      this.cur_time = cur_time;
      return this;
    }

    public Builder balcony_list(List<ByteString> balcony_list) {
      this.balcony_list = checkForNulls(balcony_list);
      return this;
    }

    /**
     * 包厢id列表
     */
    public Builder room_push_flag(Integer room_push_flag) {
      this.room_push_flag = room_push_flag;
      return this;
    }

    /**
     * 房间推送标志,0:推送roomid的消息,1:屏蔽roomid的消息
     */
    public Builder jf_gameid(ByteString jf_gameid) {
      this.jf_gameid = jf_gameid;
      return this;
    }

    @Override
    public Room_HelloReq build() {
      checkRequiredFields();
      return new Room_HelloReq(this);
    }
  }
}
