# YYL-BASE-LIB

## Introduction / 介绍


This is a lightweight Java utility library designed to boost development efficiency.
It provides common utilities for collections, date, string, type conversion, reflection, IO, network info, queue, encryption and more.
The library focuses on native basic capabilities, only encapsulates general underlying tools, without encapsulating complex business scenarios or targeting enterprise-level complex business requirements.

这是一款轻量级 Java 工具类库，旨在提升开发效率。
内置集合、日期、字符串、类型转换、反射、IO、网络信息、队列、加密等常用基础能力。
本库专注原生基础能力，仅封装通用底层工具，不做复杂业务场景封装，不对标企业级复杂业务需求。


## Features / 特性

- **Pure**: Relies only on native JDK APIs with no mandatory third-party dependencies, lightweight and out-of-the-box.
- **Lightweight**: Removes low-frequency, complex and business-specific functions, retaining only essential underlying methods to avoid over-encapsulation and heavy dependencies.
- **Universal**: Covers string processing, collection and array operations, type conversion, object null judgment, basic file operations and general exception handling, perfectly fitting minimalist and general development scenarios.
- **Compatible**: Supports JDK8 as the minimum version and provides compatible methods for smooth access of legacy projects.


- **纯净**：仅依赖 JDK 原生 API，无强制第三方依赖，体积轻量，引入即用。
- **轻量**：剔除低频、复杂、业务专属能力，仅保留开发必备底层方法，杜绝过度封装与重型依赖。
- **通用**：覆盖字符串、集合数组、类型转换、对象判空、基础文件、通用异常等底层能力，适配极简通用开发场景。
- **兼容**：最低兼容 JDK8，提供兼容过渡方法，支持老项目平滑接入。


## Usage / 使用

Introduce the Maven dependency to your project.

在项目中引入以下 Maven 依赖即可使用。

```xml
<dependency>
    <groupId>com.github.relucent</groupId>
    <artifactId>yyl-base-lib</artifactId>
    <version>0.4.4</version>
</dependency>
```

> 默认只引入 JDK 工具方法。如需激活国密（SM2/SM3/SM4）、MyBatis、Jackson、Jedis、jsqlparser 等可选能力，请**自行追加对应依赖**（pom.xml 中这些依赖均为 `optional`，不传递）。详见下方案例。

---

## Quick Start / 快速上手

高频的 API 速览

```java
import com.github.relucent.base.common.lang.ObjectUtil;
import com.github.relucent.base.common.lang.StringUtil;
import com.github.relucent.base.common.convert.ConvertUtil;
import com.github.relucent.base.common.codec.Base64;
import com.github.relucent.base.common.crypto.digest.DigestUtil;
import com.github.relucent.base.common.crypto.mac.MacUtil;
import com.github.relucent.base.common.crypto.mac.HmacAlgorithm;
```

```java
// 1. 判 null（注意：仅判 null，空字符串/空集合返回 false —— 空集合请用 CollectionUtil.isEmpty）
boolean isNull = ObjectUtil.isNull(value);
String safe = ObjectUtil.defaultIfNull(value, "fallback");

// 2. 字符串空白判断
boolean blank = StringUtil.isBlank("  \t\n");   // true

// 3. 类型转换：失败返回 null 或自定义默认值
Integer n = ConvertUtil.toInteger("42");            // 42
Integer m = ConvertUtil.toInteger("abc");          // null
Integer k = ConvertUtil.toInteger("abc", -1);      // -1（兜底）
String s  = ConvertUtil.toString(123);              // "123"

// 4. Base64 编解码
String encoded = Base64.encode("hello".getBytes()); // "aGVsbG8="
String raw     = new String(Base64.decode(encoded));

// 5. 摘要算法（Hex 输出）
String md5  = DigestUtil.md5Hex("hello");          // 5d41402abc4b2a76b9719d911017c592
String sha  = DigestUtil.sha256Hex("hello");

// 6. HMAC 鉴权
String mac  = MacUtil.hmacHex("data", "secret", HmacAlgorithm.HmacSHA256);
```

> `DigestUtil.sm3Hex(...)` / `MacUtil.hmacHex(..., HmacAlgorithm.HmacSM3)` 等国密算法需先引入 BouncyCastle：
> ```xml
> <dependency>
>     <groupId>org.bouncycastle</groupId>
>     <artifactId>bcprov-jdk18on</artifactId>
>     <version>1.84</version>
> </dependency>
> ```

---

## Modules / 模块速查

| 用途 | 模块包 | 入口类 |
| --- | --- | --- |
| 判 null / 对象默认值 | `lang` | `ObjectUtil` |
| 字符串空白、拼接、截取、去除前缀后缀 | `lang` | `StringUtil` |
| 数字 / 字符 / 枚举 / 布尔工具 | `lang` | `NumberUtil` `CharUtil` `EnumUtil` `BooleanUtil` |
| 数组、集合判空、过滤 | `lang` / `collection` | `ArrayUtil` `CollectionUtil` |
| 类型转换（任意 ↔ 任意） | `convert` | `ConvertUtil` `ConverterManager` `CastUtil` |
| Bean ↔ Map 互转、属性拷贝 | `bean` | `BeanUtil`（`describe`/`populate`/`newBean`） `BeanCopier` |
| 日期 / 时间 / 计时器 | `time` | `DateUtil` `LocalDateTimeUtil` `CalendarUtil` `StopWatch` |
| Cron 表达式解析 | `cron` | `CronExpression` |
| Hex / Base64 / URL / UTF-8 编解码 | `codec` | `Base64` `Hex` `CodecUtil` `Utf8` |
| 摘要（MD5/SHA/SM3） | `crypto.digest` | `DigestUtil` |
| HMAC 鉴权 | `crypto.mac` | `MacUtil` |
| 对称加密（AES/SM4/DESede）、AES-GCM 流式 | `crypto.symmetric` | `CipherUtil` `Sm4` `AesGcm` |
| 非对称加密（RSA/SM2）、密钥工具 | `crypto.asymmetric` | `Rsa` `Sm2` `KeyUtil` `SmUtil` `EcKeyUtil` `PemUtil` |
| 文件 / 路径 / 序列化 / Gzip | `io` | `IoUtil` `FileUtil` `FilenameUtil` `PathUtil` `SerializeUtil` `GzipIoUtil` |
| 反射（方法、字段、构造器、泛型） | `reflect` | `MethodUtil` `FieldUtil` `ConstructorUtil` `TypeUtil` `TypeReference` |
| 唯一 ID（UUID32/NanoId/ULID/雪花） | `identifier` | `IdUtil` `UUID32` `NanoId` `Ulid` `SnowflakeIdWorker` |
| 网络工具 / URL 解析 / SSL 跳过 | `net` | `NetworkUtil` `UrlUtil` `SslUtil` |
| 内存队列（去重 / 持久化抽象） | `queue` | `QueueStore` `QueueStoreBuilder` |
| JSON 序列化 | `json` | `JsonUtil` |
| 缓存抽象 | `cache` | `CacheManager` |
| 异常封装 | `exception` | `exception/*` |
| 正则 / 匹配 | `regex` / `matcher` | （按需选用） |
| 动态编译 Java 源码 | `compiler` | `JavaCompilerEngine` |
| AWT/Swing 桌面工具 | `awt` | `ScreenUtil` `RobotUtil` `LookAndFeelUtil` |
| 国际化与字符集常量 | `constant` | `CharsetConstant` `DatePatternConstant` `ZoneIdConstant` |

> 注：本表仅列常用入口。完整索引建议用 IDE 的全局搜索（包前缀 `com.github.relucent.base.common.*`）。

---

## Cookbook / 常用示例

### 类型转换（convert）

```java
import com.github.relucent.base.common.convert.ConvertUtil;
import com.github.relucent.base.common.convert.ConverterManager;
import com.github.relucent.base.common.convert.Converter;
import com.github.relucent.base.common.convert.impl.StringConverter;
```

```java
// (1) ConvertUtil 是统一入口，封装了数字/字符串/日期/集合/Map/Bean/枚举/数组的相互转换
Date   d  = ConvertUtil.toDate("2024-01-01");
String s  = ConvertUtil.toString(3.14);              // "3.14"
Listx  l  = ConvertUtil.toList("a,b,c");             // Listx 是项目自有 List 包装
Mapx   m  = ConvertUtil.toMap(userBean);             // Bean → Map

// (2) 转换失败兜底：所有 toXxx(value, defaultValue) 均遵循此约定
Integer n = ConvertUtil.toInteger("abc", 0);          // 0
Date    d = ConvertUtil.toDate("not-a-date", new Date()); // 当前时间

// (3) 复杂泛型目标（含 TypeReference）——例如 List<User>
TypeReference<List<User>> typeRef = new TypeReference<List<User>>() {};
List<User> users = ConvertUtil.convert(jsonArrayString, typeRef);

// (4) 注册自定义转换器
ConverterManager.register(new MyDateConverter(), LocalDate.class);
LocalDate localDate = ConvertUtil.convert("2024-01-01", LocalDate.class);
```

> **陷阱**：`ConvertUtil` 内部委托给 `BasicConverter`，对 `source == null` **直接返回 null**，**不会**走到默认值逻辑——默认值兜底仅在类型无法转换时触发。

### 编解码（codec）

```java
import com.github.relucent.base.common.codec.Base64;
import com.github.relucent.base.common.codec.Hex;
import com.github.relucent.base.common.codec.CodecUtil;
```

```java
// (1) Base64 标准编解码（结果含换行，可用 encodeBase64Chunked 控制）
String b64   = Base64.encode("hello".getBytes());     // aGVsbG8=
String b64sp = Base64.encodeBase64String("hello".getBytes(), true); // 启用分块
byte[] raw   = Base64.decode(b64);

// (2) Hex 编码（结果小写）
String hex   = Hex.encodeToString("abc".getBytes());  // "616263"
byte[] bytes = Hex.decode(hex);

// (3) RFC 3986 URL 编码：空格 → "%20"（而非标准 URLEncoder 的 "+"）
String safe  = CodecUtil.encodeUriRfc3986("hello world&foo=bar");
// safe = "hello%20world%26foo%3Dbar"
```

### 加密（crypto）

```java
import com.github.relucent.base.common.crypto.digest.DigestUtil;
import com.github.relucent.base.common.crypto.mac.MacUtil;
import com.github.relucent.base.common.crypto.mac.HmacAlgorithm;
import com.github.relucent.base.common.crypto.symmetric.SymmetricAlgorithmEnum;
import com.github.relucent.base.common.crypto.symmetric.Sm4;
import com.github.relucent.base.common.crypto.symmetric.SymmetricCrypto;
import com.github.relucent.base.common.crypto.symmetric.AesGcm;
import com.github.relucent.base.common.crypto.symmetric.SecretKeyUtil;
import com.github.relucent.base.common.crypto.ModeEnum;
import com.github.relucent.base.common.crypto.PaddingEnum;
```

```java
// (1) 摘要（国密 SM3 需 BouncyCastle）
String md5 = DigestUtil.md5Hex("hello");
String sha = DigestUtil.sha256Hex("hello");
String sm3 = DigestUtil.sm3Hex("hello");

// (2) HMAC（String/String/String 重载 → 自动转字节）
String mac = MacUtil.hmacHex("data", "secret", HmacAlgorithm.HmacSHA256);
// 字节级重载
byte[] raw = MacUtil.hmac(dataBytes, keyBytes, HmacAlgorithm.HmacSHA256);

// (3) SM4 国密对称加密（16 字节密钥 = 128 位）
byte[] key = SecretKeyUtil.generateKey(SymmetricAlgorithmEnum.SM4.getValue());
byte[] iv  = SecretKeyUtil.generateIv(SymmetricAlgorithmEnum.SM4.getValue());
Sm4 sm4 = new Sm4(key, ModeEnum.CBC, PaddingEnum.PKCS5Padding, iv);
byte[] cipher = sm4.encrypt("hello".getBytes());
String plain  = sm4.decryptString(cipher);

// 同样接口也支持 AES / DESede
SymmetricCrypto aes = new Sm4(key, ModeEnum.CBC, PaddingEnum.PKCS5Padding, iv);
// 替换为 new Aes(key, ...) 即可，构造参数一致

// (4) AES-GCM 流式认证加密（IV 自动写入密文头部，无需手动管理）
AesGcm gcm = new AesGcm(key);
byte[] sealed = gcm.encrypt("hello".getBytes());       // IV || ciphertext || tag
String back   = gcm.decryptString(sealed);

// (5) 加盐 / 多轮摘要（Digester 比 DigestUtil 更细致）
Digester digester = new Digester(DigestAlgorithm.SHA_256);
// 可选：设置盐值 / salt 位置 / 摘要次数
// digester.setSalt(saltBytes);
// digester.setSaltPosition(SaltPosition.BEFORE);
// digester.setDigestCount(2);
String hex = digester.digestHex("payload");
```
