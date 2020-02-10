// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\auth.proto
package com.tencent.protocol.tga.auth;

import com.squareup.tga.ProtoEnum;

public enum AuthTokenErrCode
    implements ProtoEnum {
  AuthTokenErrCode_Success(0),
  /**
   * 成功
   */
  AuthTokenErrCode_UnknowAccount(1),
  /**
   * 不支持的帐号类型，重新进行认证
   */
  AuthTokenErrCode_QQAuthFail(2),
  /**
   * QQ认证失败，重新进行认证
   */
  AuthTokenErrCode_WeChatAuthFail(3);

  private final int value;

  private AuthTokenErrCode(int value) {
    this.value = value;
  }

  @Override
  public int getValue() {
    return value;
  }
}