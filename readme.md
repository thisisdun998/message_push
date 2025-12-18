1. 项目概述
   开发一个基于 Spring Boot 的即时通讯（IM）原型，核心功能是实现基于 WebSocket 的**点对点（私聊）**通信。系统目前采用单机部署，暂不考虑消息持久化。

2. 技术栈
   后端: Spring Boot 3.x, Spring WebSocket.

协议: 原生 WebSocket (不使用 STOMP，保持轻量).

序列化: JSON (使用 Jackson).

3. 核心功能与逻辑
   A. 连接管理 (Connection)
   客户端连接 URL 格式: ws://localhost:8080/chat/{userId}。

后端需通过 userId 唯一标识一个连接，并将其存储在内存中（推荐使用 ConcurrentHashMap<String, WebSocketSession>）。

生命周期: 需处理 afterConnectionEstablished（建立连接）和 afterConnectionClosed（断开连接）。

B. 消息协议 (Message Protocol)
定义一个统一的消息传输对象 (DTO)，包含以下字段：

type: 消息类型 (例如: CONNECT, CHAT, ERROR).

fromId: 发送者 ID.

toId: 接收者 ID (私聊核心).

content: 消息文本内容.

timestamp: 时间戳.

C. 私聊路由逻辑 (Routing)
服务端收到消息后，解析 JSON 获取 toId。

根据 toId 从内存 Map 中查找对应的 WebSocketSession。

如果目标用户在线，通过其 Session 发送消息；如果目标不在线，暂时控制台输出“用户不在线”（暂不实现离线消息）。

4. 扩展性要求（为迭代预留）
   解耦设计: 请将“消息处理逻辑”与“WebSocket 拦截/配置”分开，方便后期引入 Service 层和 MySQL 存储。

群聊预留: 消息 DTO 中预留一个 groupId 字段，虽然目前不处理，但架构上要允许后期扩展。

异常处理: 需处理目标用户不存在、发送失败、非法消息格式等情况。

5. 交付物要求
   WebSocketConfig: 配置类。

ChatHandler: 核心处理器。

MessageDTO: 消息实体类。

Test Client: 一个简单的 HTML/JS 测试页面，能够模拟两个不同 ID 的用户进行对话。