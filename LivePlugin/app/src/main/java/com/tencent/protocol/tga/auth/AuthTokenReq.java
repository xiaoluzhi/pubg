// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\auth.proto
package com.tencent.protocol.tga.auth;

import com.squareup.tga.Message;
import com.squareup.tga.ProtoField;
import okiotga.ByteString;

import static com.squareup.tga.Message.Datatype.BYTES;
import static com.squareup.tga.Message.Datatype.UINT32;
import static com.squareup.tga.Message.Label.REQUIRED;

/**
 * 0x1001-0x01 认证并获取票据 请求
 * 组包说明：用户通过QQ登陆，QQ号为123456
 * 则请求参数为：account_name="123456"，account_type=AccountType_QQ，client_type填分配的值，st_type和st_buf填SSO返回的签名，access_token不填
 * 组包说明：用户通过微信登陆，微信openid为oDF3iYx0ro3_7jD4HFRDfrjdCM58，微信访问token为sFopJ9lMmLl4u-ad61ojKpS0TolhN2s3SnHoI2Mh5GgdiYb35i-7DG2T2CDyQKMe
 * 则请求参数为：account_name="oDF3iYx0ro3_7jD4HFRDfrjdCM58"，account_type=AccountType_WeChat，access_token="sFopJ9lMmLl4u-ad61ojKpS0TolhN2s3SnHoI2Mh5GgdiYb35i-7DG2T2CDyQKMe"，client_type填分配的值，st_type和st_buf不填
 * 游客：account_type填AccountType_Tourist，account_name填机器码，openappid、client_type、areaid填相应的值
 */
public final class AuthTokenReq extends Message {

  public static final ByteString DEFAULT_ACCOUNT_NAME = ByteString.EMPTY;
  public static final Integer DEFAULT_ACCOUNT_TYPE = 0;
  public static final Integer DEFAULT_CLIENT_TYPE = 0;
  public static final ByteString DEFAULT_ACCESS_TOKEN = ByteString.EMPTY;
  public static final Integer DEFAULT_ST_TYPE = 0;
  public static final ByteString DEFAULT_ST_BUF = ByteString.EMPTY;
  public static final ByteString DEFAULT_CLIENTIP = ByteString.EMPTY;
  public static final ByteString DEFAULT_MCODE = ByteString.EMPTY;
  public static final Integer DEFAULT_CLIENT_TIME = 0;
  public static final ByteString DEFAULT_LIVE_TOKEN = ByteString.EMPTY;
  public static final ByteString DEFAULT_GAMEID = ByteString.EMPTY;
  public static final Integer DEFAULT_AREAID = 0;
  public static final ByteString DEFAULT_GAME_OPENID = ByteString.EMPTY;
  public static final ByteString DEFAULT_DEVICE_INFO = ByteString.EMPTY;
  public static final ByteString DEFAULT_SDK_APPID = ByteString.EMPTY;
  public static final ByteString DEFAULT_GAME_UID = ByteString.EMPTY;

  @ProtoField(tag = 1, type = BYTES, label = REQUIRED)
  public final ByteString account_name;

  /**
   * 帐号名
   */
  @ProtoField(tag = 2, type = UINT32)
  public final Integer account_type;

  /**
   * 帐号类型，见AccountType定义
   */
  @ProtoField(tag = 3, type = UINT32)
  public final Integer client_type;

  /**
   * 终端类型
   */
  @ProtoField(tag = 4, type = BYTES)
  public final ByteString access_token;

  /**
   * 微信登录时填写，访问token
   */
  @ProtoField(tag = 5, type = UINT32)
  public final Integer st_type;

  /**
   * QQ登录时填写，st_type: 就是AppID
   */
  @ProtoField(tag = 6, type = BYTES)
  public final ByteString st_buf;

  /**
   * QQ登录时填写，st_buf: SSO返回的签名信息
   */
  @ProtoField(tag = 7, type = BYTES)
  public final ByteString clientip;

  /**
   * 客户端ip
   */
  @ProtoField(tag = 8, type = BYTES)
  public final ByteString mcode;

  /**
   * 客户端机器码
   */
  @ProtoField(tag = 9, type = UINT32)
  public final Integer client_time;

  /**
   * 客户端时间戳
   */
  @ProtoField(tag = 10, type = BYTES)
  public final ByteString live_token;

  /**
   * 主播工具端登录使用
   */
  @ProtoField(tag = 11, type = BYTES)
  public final ByteString gameid;

  /**
   * 插件的游戏id
   */
  @ProtoField(tag = 12, type = UINT32)
  public final Integer areaid;

  /**
   * 游戏大区id
   */
  @ProtoField(tag = 13, type = BYTES)
  public final ByteString game_openid;

  /**
   * 用户游戏openid
   */
  @ProtoField(tag = 14, type = BYTES)
  public final ByteString device_info;

  /**
   * 设备机型
   */
  @ProtoField(tag = 15, type = BYTES)
  public final ByteString sdk_appid;

  /**
   * sdk的appid
   */
  @ProtoField(tag = 16, type = BYTES)
  public final ByteString game_uid;

  public AuthTokenReq(ByteString account_name, Integer account_type, Integer client_type, ByteString access_token, Integer st_type, ByteString st_buf, ByteString clientip, ByteString mcode, Integer client_time, ByteString live_token, ByteString gameid, Integer areaid, ByteString game_openid, ByteString device_info, ByteString sdk_appid, ByteString game_uid) {
    this.account_name = account_name;
    this.account_type = account_type;
    this.client_type = client_type;
    this.access_token = access_token;
    this.st_type = st_type;
    this.st_buf = st_buf;
    this.clientip = clientip;
    this.mcode = mcode;
    this.client_time = client_time;
    this.live_token = live_token;
    this.gameid = gameid;
    this.areaid = areaid;
    this.game_openid = game_openid;
    this.device_info = device_info;
    this.sdk_appid = sdk_appid;
    this.game_uid = game_uid;
  }

  private AuthTokenReq(Builder builder) {
    this(builder.account_name, builder.account_type, builder.client_type, builder.access_token, builder.st_type, builder.st_buf, builder.clientip, builder.mcode, builder.client_time, builder.live_token, builder.gameid, builder.areaid, builder.game_openid, builder.device_info, builder.sdk_appid, builder.game_uid);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof AuthTokenReq)) return false;
    AuthTokenReq o = (AuthTokenReq) other;
    return equals(account_name, o.account_name)
        && equals(account_type, o.account_type)
        && equals(client_type, o.client_type)
        && equals(access_token, o.access_token)
        && equals(st_type, o.st_type)
        && equals(st_buf, o.st_buf)
        && equals(clientip, o.clientip)
        && equals(mcode, o.mcode)
        && equals(client_time, o.client_time)
        && equals(live_token, o.live_token)
        && equals(gameid, o.gameid)
        && equals(areaid, o.areaid)
        && equals(game_openid, o.game_openid)
        && equals(device_info, o.device_info)
        && equals(sdk_appid, o.sdk_appid)
        && equals(game_uid, o.game_uid);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = account_name != null ? account_name.hashCode() : 0;
      result = result * 37 + (account_type != null ? account_type.hashCode() : 0);
      result = result * 37 + (client_type != null ? client_type.hashCode() : 0);
      result = result * 37 + (access_token != null ? access_token.hashCode() : 0);
      result = result * 37 + (st_type != null ? st_type.hashCode() : 0);
      result = result * 37 + (st_buf != null ? st_buf.hashCode() : 0);
      result = result * 37 + (clientip != null ? clientip.hashCode() : 0);
      result = result * 37 + (mcode != null ? mcode.hashCode() : 0);
      result = result * 37 + (client_time != null ? client_time.hashCode() : 0);
      result = result * 37 + (live_token != null ? live_token.hashCode() : 0);
      result = result * 37 + (gameid != null ? gameid.hashCode() : 0);
      result = result * 37 + (areaid != null ? areaid.hashCode() : 0);
      result = result * 37 + (game_openid != null ? game_openid.hashCode() : 0);
      result = result * 37 + (device_info != null ? device_info.hashCode() : 0);
      result = result * 37 + (sdk_appid != null ? sdk_appid.hashCode() : 0);
      result = result * 37 + (game_uid != null ? game_uid.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<AuthTokenReq> {

    public ByteString account_name;
    public Integer account_type;
    public Integer client_type;
    public ByteString access_token;
    public Integer st_type;
    public ByteString st_buf;
    public ByteString clientip;
    public ByteString mcode;
    public Integer client_time;
    public ByteString live_token;
    public ByteString gameid;
    public Integer areaid;
    public ByteString game_openid;
    public ByteString device_info;
    public ByteString sdk_appid;
    public ByteString game_uid;

    public Builder() {
    }

    public Builder(AuthTokenReq message) {
      super(message);
      if (message == null) return;
      this.account_name = message.account_name;
      this.account_type = message.account_type;
      this.client_type = message.client_type;
      this.access_token = message.access_token;
      this.st_type = message.st_type;
      this.st_buf = message.st_buf;
      this.clientip = message.clientip;
      this.mcode = message.mcode;
      this.client_time = message.client_time;
      this.live_token = message.live_token;
      this.gameid = message.gameid;
      this.areaid = message.areaid;
      this.game_openid = message.game_openid;
      this.device_info = message.device_info;
      this.sdk_appid = message.sdk_appid;
      this.game_uid = message.game_uid;
    }

    public Builder account_name(ByteString account_name) {
      this.account_name = account_name;
      return this;
    }

    /**
     * 帐号名
     */
    public Builder account_type(Integer account_type) {
      this.account_type = account_type;
      return this;
    }

    /**
     * 帐号类型，见AccountType定义
     */
    public Builder client_type(Integer client_type) {
      this.client_type = client_type;
      return this;
    }

    /**
     * 终端类型
     */
    public Builder access_token(ByteString access_token) {
      this.access_token = access_token;
      return this;
    }

    /**
     * 微信登录时填写，访问token
     */
    public Builder st_type(Integer st_type) {
      this.st_type = st_type;
      return this;
    }

    /**
     * QQ登录时填写，st_type: 就是AppID
     */
    public Builder st_buf(ByteString st_buf) {
      this.st_buf = st_buf;
      return this;
    }

    /**
     * QQ登录时填写，st_buf: SSO返回的签名信息
     */
    public Builder clientip(ByteString clientip) {
      this.clientip = clientip;
      return this;
    }

    /**
     * 客户端ip
     */
    public Builder mcode(ByteString mcode) {
      this.mcode = mcode;
      return this;
    }

    /**
     * 客户端机器码
     */
    public Builder client_time(Integer client_time) {
      this.client_time = client_time;
      return this;
    }

    /**
     * 客户端时间戳
     */
    public Builder live_token(ByteString live_token) {
      this.live_token = live_token;
      return this;
    }

    /**
     * 主播工具端登录使用
     */
    public Builder gameid(ByteString gameid) {
      this.gameid = gameid;
      return this;
    }

    /**
     * 插件的游戏id
     */
    public Builder areaid(Integer areaid) {
      this.areaid = areaid;
      return this;
    }

    /**
     * 游戏大区id
     */
    public Builder game_openid(ByteString game_openid) {
      this.game_openid = game_openid;
      return this;
    }

    /**
     * 用户游戏openid
     */
    public Builder device_info(ByteString device_info) {
      this.device_info = device_info;
      return this;
    }

    /**
     * 设备机型
     */
    public Builder sdk_appid(ByteString sdk_appid) {
      this.sdk_appid = sdk_appid;
      return this;
    }

    /**
     * sdk的appid
     */
    public Builder game_uid(ByteString game_uid) {
      this.game_uid = game_uid;
      return this;
    }

    @Override
    public AuthTokenReq build() {
      checkRequiredFields();
      return new AuthTokenReq(this);
    }
  }
}
