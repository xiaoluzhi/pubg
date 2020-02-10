// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\chat.proto
package com.tencent.protocol.tga.chat;

import com.squareup.tga.Message;
import com.squareup.tga.ProtoField;
import okiotga.ByteString;

import static com.squareup.tga.Message.Datatype.BYTES;
import static com.squareup.tga.Message.Datatype.UINT32;

public final class HitDirtyWordInfo extends Message {

  public static final ByteString DEFAULT_USERID = ByteString.EMPTY;
  public static final Integer DEFAULT_FIRST_HIT_TIME = 0;
  public static final Integer DEFAULT_HIT_CNT = 0;

  @ProtoField(tag = 1, type = BYTES)
  public final ByteString userid;

  @ProtoField(tag = 2, type = UINT32)
  public final Integer first_hit_time;

  @ProtoField(tag = 3, type = UINT32)
  public final Integer hit_cnt;

  public HitDirtyWordInfo(ByteString userid, Integer first_hit_time, Integer hit_cnt) {
    this.userid = userid;
    this.first_hit_time = first_hit_time;
    this.hit_cnt = hit_cnt;
  }

  private HitDirtyWordInfo(Builder builder) {
    this(builder.userid, builder.first_hit_time, builder.hit_cnt);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof HitDirtyWordInfo)) return false;
    HitDirtyWordInfo o = (HitDirtyWordInfo) other;
    return equals(userid, o.userid)
        && equals(first_hit_time, o.first_hit_time)
        && equals(hit_cnt, o.hit_cnt);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = userid != null ? userid.hashCode() : 0;
      result = result * 37 + (first_hit_time != null ? first_hit_time.hashCode() : 0);
      result = result * 37 + (hit_cnt != null ? hit_cnt.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<HitDirtyWordInfo> {

    public ByteString userid;
    public Integer first_hit_time;
    public Integer hit_cnt;

    public Builder() {
    }

    public Builder(HitDirtyWordInfo message) {
      super(message);
      if (message == null) return;
      this.userid = message.userid;
      this.first_hit_time = message.first_hit_time;
      this.hit_cnt = message.hit_cnt;
    }

    public Builder userid(ByteString userid) {
      this.userid = userid;
      return this;
    }

    public Builder first_hit_time(Integer first_hit_time) {
      this.first_hit_time = first_hit_time;
      return this;
    }

    public Builder hit_cnt(Integer hit_cnt) {
      this.hit_cnt = hit_cnt;
      return this;
    }

    @Override
    public HitDirtyWordInfo build() {
      return new HitDirtyWordInfo(this);
    }
  }
}