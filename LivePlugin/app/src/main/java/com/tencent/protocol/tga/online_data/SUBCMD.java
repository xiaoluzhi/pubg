// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\online_data.proto
package com.tencent.protocol.tga.online_data;

import com.squareup.tga.ProtoEnum;

public enum SUBCMD
    implements ProtoEnum {
  SUBCMD_ONLINE_HELLO(1);

  private final int value;

  private SUBCMD(int value) {
    this.value = value;
  }

  @Override
  public int getValue() {
    return value;
  }
}
