# ConcurrentClient
榨干 Socks5 代理的最后一丝性能

```yaml
factory-config:
  client-config:
    type: socket # 目前支持 Netty、Socket 和 HTTP 三种类型的客户端
    protocol: udp # 传输层协议：TCP、UDP
    amount: 1 # 客户端的并发数量
    proxy:
      hostname: 10.80.31.181 # Sock5 代理的 IP
      port: 7070 # Socks5 代理的端口号
    app:
      hostname: 10.80.31.181 # 目标服务的 IP
      port: 8081 # 目标服务的端口号
  data-config:
    type: byte # 模拟数据目前支持字节数组和数据文件(二选一)
    path: # 数据文件的存储路径
    size: 1024 # 字节数组的大小(单位:Byte)
  channel-config:
    type: single
```
