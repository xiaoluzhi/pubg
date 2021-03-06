// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: .\\common.proto
package com.tencent.protocol.tga.common;

import com.squareup.tga.Message;
import com.squareup.tga.ProtoField;
import okiotga.ByteString;

import static com.squareup.tga.Message.Datatype.BYTES;
import static com.squareup.tga.Message.Datatype.UINT32;
import static com.squareup.tga.Message.Label.REQUIRED;

public final class GameItem extends Message {

  public static final ByteString DEFAULT_GAME_ID = ByteString.EMPTY;
  public static final ByteString DEFAULT_GAME_NAME = ByteString.EMPTY;
  public static final ByteString DEFAULT_GAME_ICON = ByteString.EMPTY;
  public static final ByteString DEFAULT_BK_IMAGE = ByteString.EMPTY;
  public static final ByteString DEFAULT_DESCRIPTION = ByteString.EMPTY;
  public static final Integer DEFAULT_SCREEM_MODE = 0;

  @ProtoField(tag = 1, type = BYTES, label = REQUIRED)
  public final ByteString game_id;

  /**
   * 游戏ID
   */
  @ProtoField(tag = 2, type = BYTES)
  public final ByteString game_name;

  /**
   * 游戏名称
   */
  @ProtoField(tag = 3, type = BYTES)
  public final ByteString game_icon;

  /**
   * 游戏图标
   */
  @ProtoField(tag = 4, type = BYTES)
  public final ByteString bk_image;

  /**
   * 游戏背景图
   */
  @ProtoField(tag = 5, type = BYTES)
  public final ByteString description;

  /**
   * 游戏描述
   */
  @ProtoField(tag = 6, type = UINT32)
  public final Integer screem_mode;

  public GameItem(ByteString game_id, ByteString game_name, ByteString game_icon, ByteString bk_image, ByteString description, Integer screem_mode) {
    this.game_id = game_id;
    this.game_name = game_name;
    this.game_icon = game_icon;
    this.bk_image = bk_image;
    this.description = description;
    this.screem_mode = screem_mode;
  }

  private GameItem(Builder builder) {
    this(builder.game_id, builder.game_name, builder.game_icon, builder.bk_image, builder.description, builder.screem_mode);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof GameItem)) return false;
    GameItem o = (GameItem) other;
    return equals(game_id, o.game_id)
        && equals(game_name, o.game_name)
        && equals(game_icon, o.game_icon)
        && equals(bk_image, o.bk_image)
        && equals(description, o.description)
        && equals(screem_mode, o.screem_mode);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = game_id != null ? game_id.hashCode() : 0;
      result = result * 37 + (game_name != null ? game_name.hashCode() : 0);
      result = result * 37 + (game_icon != null ? game_icon.hashCode() : 0);
      result = result * 37 + (bk_image != null ? bk_image.hashCode() : 0);
      result = result * 37 + (description != null ? description.hashCode() : 0);
      result = result * 37 + (screem_mode != null ? screem_mode.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<GameItem> {

    public ByteString game_id;
    public ByteString game_name;
    public ByteString game_icon;
    public ByteString bk_image;
    public ByteString description;
    public Integer screem_mode;

    public Builder() {
    }

    public Builder(GameItem message) {
      super(message);
      if (message == null) return;
      this.game_id = message.game_id;
      this.game_name = message.game_name;
      this.game_icon = message.game_icon;
      this.bk_image = message.bk_image;
      this.description = message.description;
      this.screem_mode = message.screem_mode;
    }

    public Builder game_id(ByteString game_id) {
      this.game_id = game_id;
      return this;
    }

    /**
     * 游戏ID
     */
    public Builder game_name(ByteString game_name) {
      this.game_name = game_name;
      return this;
    }

    /**
     * 游戏名称
     */
    public Builder game_icon(ByteString game_icon) {
      this.game_icon = game_icon;
      return this;
    }

    /**
     * 游戏图标
     */
    public Builder bk_image(ByteString bk_image) {
      this.bk_image = bk_image;
      return this;
    }

    /**
     * 游戏背景图
     */
    public Builder description(ByteString description) {
      this.description = description;
      return this;
    }

    /**
     * 游戏描述
     */
    public Builder screem_mode(Integer screem_mode) {
      this.screem_mode = screem_mode;
      return this;
    }

    @Override
    public GameItem build() {
      checkRequiredFields();
      return new GameItem(this);
    }
  }
}
