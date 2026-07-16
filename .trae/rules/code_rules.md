# 代码修改规则

## 核心要求

- **所有代码注释必须使用中文**
- 禁止使用英文或其他语言编写注释
- 注释必须清晰、准确，能够帮助理解代码逻辑

## 适用范围

- **新增代码**：必须严格遵守本规则，所有注释使用中文
- **修改现有代码**：修改涉及的代码块中，新增的注释必须使用中文；建议同时将该代码块中现有的英文注释改为中文
- **历史代码**：不强制要求立即将所有历史代码的英文注释改为中文，但在后续维护时应逐步迁移

## 注释规范

### 1. 文件头部注释

每个 Kotlin/Java 文件必须包含文件头部注释，说明文件的功能和用途：

```kotlin
/**
 * 文件扫描服务
 * 负责在后台执行文件系统扫描任务
 */
class ScanService : Service() {
    // ...
}
```

### 2. 类/接口注释

所有类、接口、枚举等必须有中文注释：

```kotlin
/**
 * 存储分支节点
 * 表示文件系统中的目录，包含子节点
 */
data class StorageBranch(
    val name: String,
    val path: String,
    val children: List<StoragePrototype>
)
```

### 3. 函数/方法注释

所有非 trivial 的函数/方法必须有中文注释，包括参数说明和返回值说明：

```kotlin
/**
 * 扫描指定目录
 * 
 * @param path 要扫描的目录路径
 * @param callback 扫描结果回调
 * @return 扫描任务的标识符
 */
fun scanDirectory(path: String, callback: ScannerCallback): Long {
    // ...
}
```

### 4. 复杂逻辑注释

对于复杂的算法、条件分支或非直观的逻辑，必须添加中文注释：

```kotlin
// 计算文件大小，单位转换为可读格式
// 如果大小超过 1GB，显示 GB；超过 1MB 显示 MB，否则显示 KB
fun formatFileSize(bytes: Long): String {
    return when {
        bytes >= GB -> String.format("%.2f GB", bytes / GB.toDouble())
        bytes >= MB -> String.format("%.2f MB", bytes / MB.toDouble())
        bytes >= KB -> String.format("%.2f KB", bytes / KB.toDouble())
        else -> "$bytes B"
    }
}
```

### 5. 变量注释

对于含义不明确的变量，必须添加中文注释：

```kotlin
// 文件扫描的最大并行线程数
private const val MAX_THREAD_COUNT = 4

// 当前扫描进度（0-100）
private var progress: Int = 0
```

## 代码风格规范

### 1. 命名规范

- **变量名/函数名**：使用驼峰命名法（camelCase），英文单词
- **类名/接口名**：使用大驼峰命名法（PascalCase），英文单词
- **常量名**：使用全大写，下划线分隔（UPPER_SNAKE_CASE）

```kotlin
// 正确
val scanResult: StorageResult = scanDirectory(path)
fun formatFileSize(bytes: Long): String
const val MAX_THREAD_COUNT = 4

// 错误
val ScanResult = scanDirectory(path)  // 变量名使用了大驼峰
fun FormatFileSize(bytes: Long): String  // 函数名使用了大驼峰
const val maxThreadCount = 4  // 常量名使用了小驼峰
```

### 2. 空行规则

- 函数之间保留一个空行
- 逻辑块之间保留一个空行
- 避免多余的空行

### 3. 缩进规则

- 使用 4 个空格进行缩进
- 统一缩进风格，不要混用空格和制表符

### 4. 行长度限制

- 单行代码尽量不超过 120 个字符
- 过长的代码需要适当换行

## 修改规范

### 1. 修改前

- 理解现有代码的逻辑和结构
- 确认修改不会影响其他功能

### 2. 修改中

- 保持代码风格与现有代码一致
- 添加必要的中文注释
- 避免引入不必要的依赖

### 3. 修改后

- 检查代码是否符合注释规范
- 运行测试确保修改没有破坏现有功能
- 确保所有注释都是中文

## 禁止事项

- ❌ 禁止使用英文注释
- ❌ 禁止注释与代码逻辑不符
- ❌ 禁止冗余注释（如注释只是重复代码内容）
- ❌ 禁止提交未完成的代码或调试代码