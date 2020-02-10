// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\expressmsg.proto
package com.tencent.protocol.tga.expressmsg;

import com.squareup.tga.Message;
import com.squareup.tga.ProtoField;
import java.util.Collections;
import java.util.List;
import okiotga.ByteString;

import static com.squareup.tga.Message.Datatype.BYTES;
import static com.squareup.tga.Message.Label.REPEATED;

/**
 * ///////////////////////////////////////////////////////
 */
public final class PushNotify extends Message {

  public static final List<ByteString> DEFAULT_BODY = Collections.emptyList();

  @ProtoField(tag = 1, type = BYTES, label = REPEATED)
  public final List<ByteString> body;

  public PushNotify(List<ByteString> body) {
    this.body = immutableCopyOf(body);
  }

  private PushNotify(Builder builder) {
    this(builder.body);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof PushNotify)) return false;
    return equals(body, ((PushNotify) other).body);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    return result != 0 ? result : (hashCode = body != null ? body.hashCode() : 1);
  }

  public static final class Builder extends Message.Builder<PushNotify> {

    public List<ByteString> body;

    public Builder() {
    }

    public Builder(PushNotify message) {
      super(message);
      if (message == null) return;
      this.body = copyOf(message.body);
    }

    public Builder body(List<ByteString> body) {
      this.body = checkForNulls(body);
      return this;
    }

    @Override
    public PushNotify build() {
      return new PushNotify(this);
    }
  }
}