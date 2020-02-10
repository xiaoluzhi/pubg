// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\chat_msg.proto
package com.tencent.protocol.tga.chatMsg;

import com.squareup.tga.ProtoEnum;

public enum BalconyTipsType
    implements ProtoEnum {
  E_TIPS_TYPE_NORMAL_TIPS(1000),
  /**
   * 普通tips，用户进退房间
   */
  E_TIPS_TYPE_INVITE_TIPS(1001);

  private final int value;

  private BalconyTipsType(int value) {
    this.value = value;
  }

  @Override
  public int getValue() {
    return value;
  }
}