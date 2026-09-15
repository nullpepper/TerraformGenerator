include("common")

// 只保留 1.21.9+ 与 26.x 的版本模块。
// 原因：buildProj 的 manifest 使用 mojang 命名空间（paperweight-mappings-namespace=mojang），
// 上游注释明确说明这会让 1.21.9 之前的版本全部失效；那些老模块即使构建出来也用不了，
// 却要为每个版本额外拉一份 NMS 工件（spigot/craftbukkit 快照 30~50MB）。
// 老版本服务器上插件会走 Version.getInjector() 的优雅降级分支并提示“不支持该版本”，不会崩溃。
include("implementation:v1_21_R6")
include("implementation:v1_21_R7")
include("implementation:v26_1")
include("implementation:v26_2")

// Spigot 变体默认不构建（需要 buildtools 产物），按需用 -PincludeSpigot 打开
// include("implementation:Spigotv1_21_R6")
// include("implementation:Spigotv1_21_R7")
// include("implementation:Spigotv26_1")
// include("implementation:Spigotv26_2")

include("buildProj")