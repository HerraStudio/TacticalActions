# Herra Tactical Actions

一个面向 Minecraft NeoForge 1.21.1 的战术动作 Mod，提供左右探头和趴下动作。

## 功能

- **Q**：左探头（按住）
- **E**：右探头（按住）
- **Z**：趴下/起身（切换）
- 探头使用位于髋部 `[0, 12, 0]` 的自定义旋转轴，只驱动上半身父层；`left_leg`、`right_leg` 不参与动画，双腿保持原地且不会从腰部断开。
- 第一人称启用 Player Animation Library 的玩家模型渲染，并附加平滑视角侧倾，因此 Q/E 在第一人称下也能直接看到效果。
- 动作切换使用非线性 Catmull-Rom 插值，进入和结束更平滑。
- 趴下时使用客户端 `SWIMMING` 姿态，同时播放自定义骨骼动画。

## 安装

1. 安装 Minecraft 1.21.1、NeoForge 21.1.251。
2. 安装 [Player Animation Library](https://modrinth.com/mod/playeranim)，版本至少为 `1.1.6+mc.1.21.1`。
3. 将 `tactical-actions-1.21.1-neoforge-1.0.0.jar` 放进 `mods` 文件夹。

Player Animation Library 的 Mod ID 是 `player_animation_library`，本 Mod 已声明为客户端必需依赖。

## 构建

```powershell
.\gradlew.bat clean test build --no-daemon
```

生成文件位于 `build/libs/`：

- `tactical-actions-1.21.1-neoforge-1.0.0.jar`
- `tactical-actions-1.21.1-neoforge-1.0.0-sources.jar`

## 注意事项

Q 和 E 会覆盖原版默认的丢弃物品、打开背包按键。可以在 Minecraft 的按键设置中重新绑定原版功能或本 Mod 按键。

当前版本主要负责本地动作表现。`SWIMMING` 姿态由客户端应用，未实现服务端网络状态同步；在多人服务器上，其他玩家是否能看到动作取决于 Player Animation Library 的客户端安装和服务器环境。

## 许可证

MIT
