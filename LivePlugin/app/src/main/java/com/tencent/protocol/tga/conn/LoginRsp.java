// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\conn.proto
package com.tencent.protocol.tga.conn;

import com.squareup.tga.Message;
import com.squareup.tga.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.tga.Message.Datatype.STRING;
import static com.squareup.tga.Message.Datatype.UINT32;
import static com.squareup.tga.Message.Label.REPEATED;
import static com.squareup.tga.Message.Label.REQUIRED;

public final class LoginRsp extends Message {

  public static final Integer DEFAULT_RESULT = 0;
  public static final String DEFAULT_ERRMSG = "";
  public static final List<Integer> DEFAULT_CONNSVR_IP = Collections.emptyList();

  @ProtoField(tag = 1, type = UINT32, label = REQUIRED)
  public final Integer result;

  /**
   * 请求结果， 0 鉴权成功，1 鉴权失败, 2 票据过期, 3 服务器已满载重新登录其他服务器
   */
  @ProtoField(tag = 2, type = STRING)
  public final String errMsg;

  /**
   * 错误信息
   */
  @ProtoField(tag = 3, type = UINT32, label = REPEATED)
  public final List<Integer> connsvr_ip;

  public LoginRsp(Integer result, String errMsg, List<Integer> connsvr_ip) {
    this.result = result;
    this.errMsg = errMsg;
    this.connsvr_ip = immutableCopyOf(connsvr_ip);
  }

  private LoginRsp(Builder builder) {
    this(builder.result, builder.errMsg, builder.connsvr_ip);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof LoginRsp)) return false;
    LoginRsp o = (LoginRsp) other;
    return equals(result, o.result)
        && equals(errMsg, o.errMsg)
        && equals(connsvr_ip, o.connsvr_ip);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = this.result != null ? this.result.hashCode() : 0;
      result = result * 37 + (errMsg != null ? errMsg.hashCode() : 0);
      result = result * 37 + (connsvr_ip != null ? connsvr_ip.hashCode() : 1);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<LoginRsp> {

    public Integer result;
    public String errMsg;
    public List<Integer> connsvr_ip;

    public Builder() {
    }

    public Builder(LoginRsp message) {
      super(message);
      if (message == null) return;
      this.result = message.result;
      this.errMsg = message.errMsg;
      this.connsvr_ip = copyOf(message.connsvr_ip);
    }

    public Builder result(Integer result) {
      this.result = result;
      return this;
    }

    /**
     * 请求结果， 0 鉴权成功，1 鉴权失败, 2 票据过期, 3 服务器已满载重新登录其他服务器
     */
    public Builder errMsg(String errMsg) {
      this.errMsg = errMsg;
      return this;
    }

    /**
     * 错误信息
     */
    public Builder connsvr_ip(List<Integer> connsvr_ip) {
      this.connsvr_ip = checkForNulls(connsvr_ip);
      return this;
    }

    @Override
    public LoginRsp build() {
      checkRequiredFields();
      return new LoginRsp(this);
    }
  }
}