# ConcurrentClient

榨干 Socks5 代理的最后一丝性能

```yaml
factory-config:
  client-config:
    type: netty # 目前支持 Netty、Socket 和 HTTP 三种类型的客户端
    protocol: tcp # 传输层协议：TCP、UDP
    baseUrl: /github/leo # HTTP 客户端独有的 url 地址
    amount: 10 # 客户端的并发数量
    proxy:
      hostname: 127.0.0.1 # Sock5 代理的 IP
      port: 7071 # Socks5 代理的端口号
    app:
      hostname: 127.0.0.1 # 目标服务的 IP
      port: 8081 # 目标服务的端口号
  data-config:
    type: byte # 模拟数据目前支持字节数组和数据文件(二选一)
    path: # 数据文件的存储路径
    size: 1024 # 字节数组的大小(单位:Byte)
  channel-config:
    type: single
```
