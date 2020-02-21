// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\match_subscribe.proto
package com.tencent.protocol.tga.matchsubscribe;

import com.squareup.tga.ProtoEnum;

public enum EnterType
    implements ProtoEnum {
  SmobaActivityTab(5),
  /**
   * 王者电视台活动面板
   */
  MspeedNormal(6),
  /**
   * 飞车电视台赛程入口
   */
  MspeedH5(7),
  /**
   * 飞车电视台h5入口
   */
  MspeedOperatePush(8),
  /**
   * 飞车直播运营推送
   */
  MspeedSubscribeLayer(9),
  /**
   * 飞车订阅浮层
   */
  SmobaSaishiH5(10),
  /**
   * 赛事中心h5
   */
  MspeedDaka(11),
  /**
   * 飞车观赛打卡活动
   */
  PpkdcNormal(12),
  /**
   * 卡丁车电视台赛程入口
   */
  HpjyNormal(13);
  /**
   * 和平精英电视台赛程入口
   */
  private final int value;

  private EnterType(int value) {
    this.value = value;
  }

  @Override
  public int getValue() {
    return value;
  }
}