// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\hpjy_treasure.proto
package com.tencent.protocol.tga.hpjy_treasure;

import com.squareup.tga.Message;
import com.squareup.tga.ProtoField;
import okiotga.ByteString;

import static com.squareup.tga.Message.Datatype.BYTES;

public final class ReceiveItem extends Message {

  public static final ByteString DEFAULT_PIC = ByteString.EMPTY;
  public static final ByteString DEFAULT_TIP = ByteString.EMPTY;

  @ProtoField(tag = 1, type = BYTES)
  public final ByteString pic;

  @ProtoField(tag = 2, type = BYTES)
  public final ByteString tip;

  public ReceiveItem(ByteString pic, ByteString tip) {
    this.pic = pic;
    this.tip = tip;
  }

  private ReceiveItem(Builder builder) {
    this(builder.pic, builder.tip);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof ReceiveItem)) return false;
    ReceiveItem o = (ReceiveItem) other;
    return equals(pic, o.pic)
        && equals(tip, o.tip);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = pic != null ? pic.hashCode() : 0;
      result = result * 37 + (tip != null ? tip.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<ReceiveItem> {

    public ByteString pic;
    public ByteString tip;

    public Builder() {
    }

    public Builder(ReceiveItem message) {
      super(message);
      if (message == null) return;
      this.pic = message.pic;
      this.tip = message.tip;
    }

    public Builder pic(ByteString pic) {
      this.pic = pic;
      return this;
    }

    public Builder tip(ByteString tip) {
      this.tip = tip;
      return this;
    }

    @Override
    public ReceiveItem build() {
      return new ReceiveItem(this);
    }
  }
}
