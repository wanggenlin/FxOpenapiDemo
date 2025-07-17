# 公有Broker Rest API

## 通用API信息

* 所有的端点都会返回一个JSON object或者array.
* 数据返回的是一个 **升序**。更早的在前，更新的在后。
* 所有的时间/时间戳有关的变量都是milliseconds（毫秒级）。
* HTTP `4XX` 返回错误码是指请求内容有误，这个问题是在请求发起者这边。
* HTTP `429` 返回错误码是指请求次数上限被打破。
* HTTP `418` 返回错误码是指IP在收到`429`错误码后还继续发送请求被自动封禁。
* HTTP `5XX` 返回错误码是内部系统错误；这说明这个问题是在券商这边。在对待这个错误时，**千万** 不要把它当成一个失败的任务，因为执行状态 **未知**，有可能是成功也有可能是失败。
* 任何端点都可能返回ERROR（错误）； 错误的返回payload如下

```javascript
{
  "code": -1121,
  "msg": "Invalid symbol."
}
```

* 详细的错误码和错误信息在请见错误码文件。
* 对于`GET`端点，必须发送参数为`query string`（查询字串）。
* 对于`POST`, `PUT`, 和 `DELETE` 端点，必需要发送参数为`query string`（查询字串）或者发送参数在`request body`（请求主体）并设置content type（内容类型）为`application/x-www-form-urlencoded`。可以同时在`query string`或者`request body`里混合发送参数如果有需要的话。
* 参数可以以任意顺序发送。
* 如果有参数同时在`query string` 和 `request body`里存在，只有`query string`的参数会被使用。

### 限制

* 在 `/openapi/v1/brokerInfo`的`rateLimits` array里存在当前broker的`REQUEST_WEIGHT`和`ORDER`频率限制。
* 如果任一频率限额被超过，`429` 会被返回。
* 每条线路有一个`weight`特性，这个决定了这个请求占用多少容量（比如`weight`=2说明这个请求占用两个请求的量）。返回数据多的端点或者在多个symbol执行任务的端点可能有更高的`weight`。
* 当`429`被返回后，你有义务停止发送请求。
* **多次反复违反频率限制和/或者没有在收到429后停止发送请求的用户将会被收到封禁IP（错误码418）**
* IP封禁会被跟踪和 **调整封禁时长**（对于反复违反规定的用户，时间从 **2分钟到3天不等**）

### 端点安全类型

* 每个端点有一个安全类型，这决定了你会怎么跟其交互。
* API-key要以`X-BH-APIKEY`的名字传到REST API header里面。
* API-keys和secret-keys **要区分大小写**。
* 默认情况下，API-keys可以访问所有的安全节点。

安全类型 | 描述
------------ | ------------
NONE | 端点可以自由访问。
TRADE | 端点需要发送有效的API-Key和签名。
USER_DATA | 端点需要发送有效的API-Key和签名。
USER_STREAM | 端点需要发送有效的API-Key。
MARKET_DATA | 端点需要发送有效的API-Key。

* `TRADE` 和 `USER_DATA` 端点是 `SIGNED`（需要签名）的端点。

### SIGNED（有签名的）(TRADE和USER_DATA) 端点安全

* `SIGNED`（需要签名）的端点需要发送一个参数，`signature`，在`query string` 或者 `request body`里。
* 端点用`HMAC SHA256`签名。`HMAC SHA256 signature`是一个对key进行`HMAC SHA256`加密的结果。用你的`secretKey`作为key和`totalParams`作为value来完成这一加密过程。
* `signature`  **不区分大小写**。
* `totalParams` 是指 `query string`串联`request body`。

### 时效安全

* 一个`SIGNED`(有签名)的端点还需要发送一个参数，`timestamp`，这是当请求发起时的毫秒级时间戳。
* 一个额外的参数（非强制性）, `recvWindow`, 可以说明这个请求在多少毫秒内是有效的。如果`recvWindow`没有被发送，**默认值是5000**。
* 在当前，只有创建订单的时候才会用到`recvWindow`。
* 该参数的逻辑如下：

  ```javascript
  if (timestamp < (serverTime + 1000) && (serverTime - timestamp) <= recvWindow) {
    // process request
  } else {
    // reject request
  }
  ```

**严谨的交易和时效紧紧相关** 网络有时会不稳定或者不可靠，这会导致请求发送服务器的时间不一致。
有了`recvWindow`，你可以说明在多少毫秒内请求是有效的，否则就会被服务器拒绝。

**建议使用一个相对小的recvWindow（5000或以下）！**

## 公共 API 端点

### 术语解释

* `base asset` 指的是symbol的`quantity`（即数量）。

* `quote asset` 指的是symbol的`price`（即价格）。

### ENUM 定义

**Symbol 状态:**

* TRADING - 交易中
* HALT - 终止
* BREAK - 断开

**Symbol 类型:**

* SPOT - 现货

**资产类型:**

* CASH - 现金
* MARGIN - 保证金

**订单状态:**

* NEW - 新订单，暂无成交
* PARTIALLY_FILLED - 部分成交
* FILLED - 完全成交
* CANCELED - 已取消
* PENDING_CANCEL - 等待取消
* REJECTED - 被拒绝

**订单类型:**

* LIMIT - 限价单
* MARKET - 市价单
* LIMIT_MAKER - maker限价单
* STOP_LOSS (unavailable now)  - 暂无
* STOP_LOSS_LIMIT (unavailable now) - 暂无
* TAKE_PROFIT (unavailable now) - 暂无
* TAKE_PROFIT_LIMIT (unavailable now) - 暂无
* MARKET_OF_PAYOUT (unavailable now) - 暂无

**订单方向:**

* BUY - 买单
* SELL - 卖单

**订单时效类型:**

* GTC
* IOC
* FOK

**k线/烛线图区间:**

m -> 分钟; h -> 小时; d -> 天; w -> 周; M -> 月

* 1m
* 3m
* 5m
* 15m
* 30m
* 1h
* 2h
* 4h
* 6h
* 8h
* 12h
* 1d
* 3d
* 1w
* 1M

**频率限制类型 (rateLimitType)**

* REQUESTS_WEIGHT
* ORDERS

**频率限制区间**

* SECOND
* MINUTE
* DAY

### 通用端点

#### 测试连接

```shell
GET /openapi/v1/ping
```

测试REST API的连接。

**Weight:**
0

**Parameters:**
NONE

**Response:**

```javascript
{}
```

#### 服务器时间

```shell
GET /openapi/v1/time
```

测试连接并获取当前服务器的时间。

**Weight:**
0

**Parameters:**
NONE

**Response:**

```javascript
{
  "serverTime": 1538323200000
}
```

#### Broker信息

```shell
GET /openapi/v1/brokerInfo
```

当前broker交易规则和symbol信息

**Weight:**
0

**Parameters:**
NONE

**Response:**

```json
{
  "timezone": "UTC",
  "serverTime": 1538323200000,
  "rateLimits": [
    {
      "rateLimitType": "REQUESTS_WEIGHT",
      "interval": "MINUTE",
      "limit": 1500
    },
    {
      "rateLimitType": "ORDERS",
      "interval": "SECOND",
      "limit": 20
    },
    {
      "rateLimitType": "ORDERS",
      "interval": "DAY",
      "limit": 350000
    }
  ],
  "brokerFilters": [],
  "symbols": [
    {
      "symbol": "ETHBTC",
      "status": "TRADING",
      "baseAsset": "ETH",
      "baseAssetPrecision": "0.001",
      "quoteAsset": "BTC",
      "quotePrecision": "0.01",
      "icebergAllowed": false,
      "filters": [
        {
          "filterType": "PRICE_FILTER",
          "minPrice": "0.00000100",
          "maxPrice": "100000.00000000",
          "tickSize": "0.00000100"
        },
        {
          "filterType": "LOT_SIZE",
          "minQty": "0.00100000",
          "maxQty": "100000.00000000",
          "stepSize": "0.00100000"
        },
        {
          "filterType": "MIN_NOTIONAL",
          "minNotional": "0.00100000"
        }
      ]
    }
  ],
  "contracts": [
    {
      "filters": [
        {
          "minPrice": "0.1",
          "maxPrice": "100000.00000000",
          "tickSize": "0.1",
          "filterType": "PRICE_FILTER"
        },
        {
          "minQty": "1",
          "maxQty": "100000.00000000",
          "stepSize": "1",
          "filterType": "LOT_SIZE"
        },
        {
          "minNotional": "0.000001",
          "filterType": "MIN_NOTIONAL"
        }
      ],
      "exchangeId": "301",
      "symbol": "BTC-PERP-REV",
      "symbolName": "BTC-PERP-REVBITCOIN TEST",
      "status": "TRADING",
      "baseAsset": "BTC-PERP-REV",
      "baseAssetPrecision": "1",
      "quoteAsset": "USDT",
      "quoteAssetPrecision": "0.1",
      "icebergAllowed": false,
      "inverse": true,
      "index": "BTCUSDT",
      "marginToken": "TBTC",
      "marginPrecision": "0.00000001",
      "contractMultiplier": "1.0",
      "underlying": "TBTC",
      "riskLimits": [
        {
          "riskLimitId": "200000001",
          "quantity": "1000000.0",
          "initialMargin": "0.01",
          "maintMargin": "0.005"
        },
        {
          "riskLimitId": "200000002",
          "quantity": "2000000.0",
          "initialMargin": "0.02",
          "maintMargin": "0.01"
        },
        {
          "riskLimitId": "200000003",
          "quantity": "3000000.0",
          "initialMargin": "0.03",
          "maintMargin": "0.015"
        },
        {
          "riskLimitId": "200000004",
          "quantity": "4000000.0",
          "initialMargin": "0.04",
          "maintMargin": "0.02"
        }
      ]
    }
  ],
  "tokens": [
    {
      "orgId": "6002",
      "tokenId": "BTC",
      "tokenName": "BTC",
      "tokenFullName": "Bitcoin",
      "allowWithdraw": true,
      "allowDeposit": true,
      "chainTypes": [
        {
          "chainType": "BTC",
          "allowDeposit": true,
          "allowWithdraw": true
        },
        {
          "chainType": "HECO",
          "allowDeposit": true,
          "allowWithdraw": true
        }
      ]
    }
  ]
}
```

### 市场数据端点

#### 订单簿

```shell
GET /openapi/quote/v1/depth
```

**Weight:**

根据limit不同：

Limit | Weight
------------ | ------------
5, 10, 20, 50, 100 | 1
500 | 5
1000 | 10

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
symbol | STRING | YES |
limit | INT | NO | 默认 100; 最大 100.

**注意:** 如果设置limit=0会返回很多数据。

**Response:**

[价格, 数量]

```javascript
{
  "bids": [
    [
      "3.90000000",   // 价格
      "431.00000000"  // 数量
    ],
    [
      "4.00000000",
      "431.00000000"
    ]
  ],
  "asks": [
    [
      "4.00000200",  // 价格
      "12.00000000"  // 数量
    ],
    [
      "5.10000000",
      "28.00000000"
    ]
  ]
}
```

#### 最近成交

```shell
GET /openapi/quote/v1/trades
```

获取当前最新成交（最多60）

**Weight:**
1

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
symbol | STRING | YES |
limit | INT | NO | Default 60; max 60.

**Response:**

```javascript
[
  {
    "price": "4.00000100",
    "qty": "12.00000000",
    "time": 1499865549590,
    "isBuyerMaker": true
  }
]
```

#### k线/烛线图数据

```shell
GET /openapi/quote/v1/klines
```

symbol的k线/烛线图数据
K线会根据开盘时间而辨别。

**Weight:**
1

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
symbol | STRING | YES |
interval | ENUM | YES |
startTime | LONG | NO |
endTime | LONG | NO |
limit | INT | NO | 默认500; 最大1000.

* 如果startTime和endTime没有发送，只有最新的K线会被返回。

**Response:**

```javascript
[
  [
    1499040000000,      // 开盘时间
    "0.01634790",       // 开盘价
    "0.80000000",       // 最高价
    "0.01575800",       // 最低价
    "0.01577100",       // 收盘价
    "148976.11427815",  // 交易量
    1499644799999,      // 收盘时间
    "2434.19055334",    // Quote asset数量
    308,                // 交易次数
    "1756.87402397",    // Taker buy base asset数量
    "28.46694368"       // Taker buy quote asset数量
  ]
]
```

#### 24小时ticker价格变化数据

```shell
GET /openapi/quote/v1/ticker/24hr
```

24小时价格变化数据。**注意** 如果没有发送symbol，会返回很多数据。

**Weight:**

如果只有一个symbol，1; 如果symbol没有被发送，**40**。

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
symbol | STRING | NO |

* 如果symbol没有被发送，所有symbol的数据都会被返回。

**Response:**

```javascript
{
  "time": 1538725500422,
  "symbol": "ETHBTC",
  "bestBidPrice": "4.00000200",
  "bestAskPrice": "4.00000200",
  "lastPrice": "4.00000200",
  "openPrice": "99.00000000",
  "highPrice": "100.00000000",
  "lowPrice": "0.10000000",
  "volume": "8913.30000000"
}
```

OR

```javascript
[
  {
    "time": 1752754975831,
    "symbol": "TRXUSDT",
    "volume": "0",
    "quoteVolume": "0",
    "lastPrice": "0",
    "highPrice": "0",
    "lowPrice": "0",
    "openPrice": "0"
  },
  {
    "time": 1752754953579,
    "symbol": "ETHUSDT",
    "volume": "14979.58",
    "quoteVolume": "50688289.0502",
    "lastPrice": "3430.6",
    "highPrice": "3458.09",
    "lowPrice": "2529.22",
    "openPrice": "2529.22"
  },
  {
    "time": 1752754951772,
    "symbol": "BTCUSDT",
    "volume": "1022.79",
    "quoteVolume": "118826068.1072",
    "lastPrice": "116277.61",
    "highPrice": "116277.61",
    "lowPrice": "107759.43",
    "openPrice": "107759.43"
  },
  {
    "time": 1752754975831,
    "symbol": "ETHBTC",
    "volume": "0",
    "quoteVolume": "0",
    "lastPrice": "0",
    "highPrice": "0",
    "lowPrice": "0",
    "openPrice": "0"
  },
  {
    "time": 1752754975831,
    "symbol": "BTCREMI",
    "volume": "0",
    "quoteVolume": "0",
    "lastPrice": "0",
    "highPrice": "0",
    "lowPrice": "0",
    "openPrice": "0"
  },
  {
    "time": 1752754975831,
    "symbol": "ETHREMI",
    "volume": "0",
    "quoteVolume": "0",
    "lastPrice": "0",
    "highPrice": "0",
    "lowPrice": "0",
    "openPrice": "0"
  },
  {
    "time": 1752754975831,
    "symbol": "EUTREMI",
    "volume": "0",
    "quoteVolume": "0",
    "lastPrice": "0",
    "highPrice": "0",
    "lowPrice": "0",
    "openPrice": "0"
  },
  {
    "time": 1752754975831,
    "symbol": "REMIUSDT",
    "volume": "0",
    "quoteVolume": "0",
    "lastPrice": "0",
    "highPrice": "0",
    "lowPrice": "0",
    "openPrice": "0"
  }
]
```

#### Symbol价格

```shell
GET /openapi/quote/v1/ticker/price
```

单个或多个symbol的最新价。

**Weight:**
1

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
symbol | STRING | NO |

* 如果symbol没有发送，所有symbol的最新价都会被返回。

**Response:**

```javascript
{
  "price": "4.00000200"
}
```

OR

```javascript
[
  {
    "symbol": "LTCBTC",
    "price": "4.00000200"
  },
  {
    "symbol": "ETHBTC",
    "price": "0.07946600"
  }
]
```

#### Symbol最佳订单簿价格

```shell
GET /openapi/quote/v1/ticker/bookTicker
```

单个或者多个symbol的最佳买单卖单价格。

**Weight:**
1

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
symbol | STRING | NO |

* 如果symbol没有被发送，所有symbol的最佳订单簿价格都会被返回。

**Response:**

```javascript
{
  "symbol": "BTCUSDT",
          "bidPrice": "109199.99",
          "bidQty": "16.34",
          "askPrice": "116256.89",
          "askQty": "153.4",
          "time": 1752735997350
}
```

OR

```javascript
[
  {
    "symbol": "TRXUSDT",
    "bidPrice": "0",
    "bidQty": "0",
    "askPrice": "0",
    "askQty": "0",
    "time": 1752736014138
  },
  {
    "symbol": "ETHUSDT",
    "bidPrice": "3363.61",
    "bidQty": "0.53",
    "askPrice": "3384.92",
    "askQty": "69.38",
    "time": 1752736089681
  },
  {
    "symbol": "BTCUSDT",
    "bidPrice": "109199.99",
    "bidQty": "16.34",
    "askPrice": "116256.89",
    "askQty": "153.4",
    "time": 1752735997350
  },
  {
    "symbol": "ETHBTC",
    "bidPrice": "0",
    "bidQty": "0",
    "askPrice": "0",
    "askQty": "0",
    "time": 1752736133989
  },
  {
    "symbol": "BTCREMI",
    "bidPrice": "0",
    "bidQty": "0",
    "askPrice": "0",
    "askQty": "0",
    "time": 1752736134498
  },
  {
    "symbol": "ETHREMI",
    "bidPrice": "0",
    "bidQty": "0",
    "askPrice": "0",
    "askQty": "0",
    "time": 1752736135006
  },
  {
    "symbol": "EUTREMI",
    "bidPrice": "0",
    "bidQty": "0",
    "askPrice": "0",
    "askQty": "0",
    "time": 1752736135007
  },
  {
    "symbol": "REMIUSDT",
    "bidPrice": "0",
    "bidQty": "0",
    "askPrice": "0",
    "askQty": "0",
    "time": 1752736135007
  }
]
```
#### 现货币对

```shell
GET /openapi/v1/pairs
```

列出所有现货的币对列表，包括baseToken和quoteToken

**Weight:**
1

**Parameters:**

无

**Response:**

```javascript
[
  {
    "symbol": "ETHUSDT",
    "quoteToken": "USDT",
    "baseToken": "ETH"
  },
  {
    "symbol": "BTCUSDT",
    "quoteToken": "USDT",
    "baseToken": "BTC"
  },
  {
    "symbol": "TRXUSDT",
    "quoteToken": "USDT",
    "baseToken": "TRX"
  },
  {
    "symbol": "EUTREMI",
    "quoteToken": "REMI",
    "baseToken": "EUT"
  },
  {
    "symbol": "ETHBTC",
    "quoteToken": "BTC",
    "baseToken": "ETH"
  },
  {
    "symbol": "ETHREMI",
    "quoteToken": "REMI",
    "baseToken": "ETH"
  },
  {
    "symbol": "BTCREMI",
    "quoteToken": "REMI",
    "baseToken": "BTC"
  },
  {
    "symbol": "REMIUSDT",
    "quoteToken": "USDT",
    "baseToken": "REMI"
  }
]
```

### 账户端点

#### 创建新订单  (TRADE)

```shell
POST /openapi/v1/order  (HMAC SHA256)
```

发送一个新的订单

**Weight:**
1

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
symbol | STRING | YES |
assetType | STRING | NO |
side | ENUM | YES |
type | ENUM | YES |
timeInForce | ENUM | NO |
quantity | DECIMAL | YES |
price | DECIMAL | NO |
newClientOrderId | STRING | NO | 一个自己给订单定义的ID，如果没有发送会自动生成。
stopPrice | DECIMAL | NO | 与 `STOP_LOSS`, `STOP_LOSS_LIMIT`, `TAKE_PROFIT`, 和`TAKE_PROFIT_LIMIT` 订单一起使用. **当前不可用**
icebergQty | DECIMAL | NO | 与 `LIMIT`, `STOP_LOSS_LIMIT`, 和 `TAKE_PROFIT_LIMIT` 来创建冰山订单. **当前不可用**
recvWindow | LONG | NO |
timestamp | LONG | YES |

在`type`上的额外强制参数:

类型 | 额外强制参数
------------ | ------------
`LIMIT` | `timeInForce`, `quantity`, `price`
`MARKET` | `quantity`
`STOP_LOSS` | `quantity`, `stopPrice`  **当前不可用**
`STOP_LOSS_LIMIT` | `timeInForce`, `quantity`,  `price`, `stopPrice` **当前不可用**
`TAKE_PROFIT` | `quantity`, `stopPrice` **当前不可用**
`TAKE_PROFIT_LIMIT` | `timeInForce`, `quantity`, `price`, `stopPrice` **当前不可用**
`LIMIT_MAKER` | `quantity`, `price`


**Response:**

```javascript
{
  "accountId": "1966608182328466945",
          "symbol": "BTCUSDT",
          "symbolName": "BTCUSDT",
          "clientOrderId": "1752663451963252",
          "orderId": "1995880174000937216",
          "transactTime": "1752663457586",
          "price": "0",
          "origQty": "1",
          "executedQty": "0",
          "status": "FILLED",
          "timeInForce": "GTC",
          "type": "MARKET",
          "side": "SELL"
}
```


#### 查询订单 (USER_DATA)

```shell
GET /openapi/v1/order (HMAC SHA256)
```

查询订单状态。

**Weight:**
1

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
orderId | LONG | YES |
origClientOrderId | STRING | YES |
recvWindow | LONG | NO |
timestamp | LONG | YES |

Notes:

* 单一 `orderId` 或者 `origClientOrderId` 必须被发送。
* 对于某些历史数据 `cummulativeQuoteQty` 可能会 < 0, 这说明数据当前不可用。

**Response:**

```javascript
{
  "accountId": "1966608182328466945",
          "exchangeId": "301",
          "symbol": "BTCUSDT",
          "symbolName": "BTCUSDT",
          "clientOrderId": "1752663451963252",
          "orderId": "1995880174000937216",
          "price": "0",
          "origQty": "1",
          "executedQty": "1",
          "cummulativeQuoteQty": "109199.99",
          "avgPrice": "109199.99",
          "status": "FILLED",
          "timeInForce": "GTC",
          "type": "MARKET",
          "side": "SELL",
          "stopPrice": "0.0",
          "icebergQty": "0.0",
          "time": "1752663457586",
          "updateTime": "1752663457665",
          "isWorking": true
}
```

#### 取消订单 (TRADE)

```shell
DELETE /openapi/v1/order  (HMAC SHA256)
```

取消当前正在交易的订单。

**Weight:**
1

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
orderId | LONG | YES |
clientOrderId | STRING | YES |
recvWindow | LONG | NO |
timestamp | LONG | YES |

单一 `orderId` 或者 `clientOrderId`必须被发送。

**Response:**

```javascript
{
  "symbol": "LTCBTC",
          "clientOrderId": "tU721112KM",
          "orderId": 1,
          "status": "CANCELED"
}
```

#### 当前订单(USER_DATA)

```shell
GET /openapi/v1/openOrders  (HMAC SHA256)
```

获取当前单个或者多个symbol的当前订单。**注意** 如果没有发送symbol，会返回很多数据。

**Weight:**
1

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
symbol | String | NO |
orderId | LONG | NO |
limit | INT | NO | 默认 500; 最多 1000.
recvWindow | LONG | NO |
timestamp | LONG | YES |

**Notes:**

* 如果`orderId`设定好了，会筛选订单小于`orderId`的。否则会返回最近的订单信息。

**Response:**

```javascript
[
  {
    "accountId": "1966608182328466945",
    "exchangeId": "301",
    "symbol": "BTCUSDT",
    "symbolName": "BTCUSDT",
    "clientOrderId": "QUMYBF4Q72N3BCYR",
    "orderId": "1995880173992548608",
    "price": "118791.25",
    "origQty": "44.69",
    "executedQty": "0",
    "cummulativeQuoteQty": "0",
    "avgPrice": "0",
    "status": "NEW",
    "timeInForce": "GTC",
    "type": "LIMIT",
    "side": "SELL",
    "stopPrice": "0.0",
    "icebergQty": "0.0",
    "time": "1752663457574",
    "updateTime": "1752663457596",
    "isWorking": true
  }
]
```

#### 历史订单 (USER_DATA)

```shell
GET /openapi/v1/historyOrders (HMAC SHA256)
```
获取当前账户的所有订单。亦或是取消的，完全成交的，拒绝的。

**Weight:**
5

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
symbol | String | NO |
orderId | LONG | NO |
startTime | LONG | NO |
endTime | LONG | NO |
limit | INT | NO | Default 500; max 1000.
recvWindow | LONG | NO |
timestamp | LONG | YES |

**Notes:**

* 如果`orderId`设定好了，会筛选订单小于`orderId`的。否则会返回最近的订单信息。

**Response:**

```javascript
[
  {
    "accountId": "1966608182328466945",
    "exchangeId": "301",
    "symbol": "ETHUSDT",
    "symbolName": "ETHUSDT",
    "clientOrderId": "MFQJ0HVAQJDUQPCA",
    "orderId": "1995872611243006208",
    "price": "3155.04",
    "origQty": "85.2",
    "executedQty": "85.2",
    "cummulativeQuoteQty": "268809.408",
    "avgPrice": "3155.04",
    "status": "FILLED",
    "timeInForce": "GTC",
    "type": "LIMIT",
    "side": "SELL",
    "stopPrice": "0.0",
    "icebergQty": "0.0",
    "time": "1752662556023",
    "updateTime": "1752662556100",
    "isWorking": true
  }
]
```

#### 账户信息 (USER_DATA)

```shell
GET /openapi/v1/account (HMAC SHA256)
```

获取当前账户信息

**Weight:**
5

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
recvWindow | LONG | NO |
timestamp | LONG | YES |

**Response:**

```javascript
{
  "balances": [
    {
      "asset": "BTC",
      "assetId": "BTC",
      "assetName": "BTC",
      "total": "100000610.268258962",
      "free": "99821200.178358962",
      "locked": "179410.0899"
    },
    {
      "asset": "ETH",
      "assetId": "ETH",
      "assetName": "ETH",
      "total": "99999873.687238",
      "free": "99998368.747238",
      "locked": "1504.94"
    },
    {
      "asset": "USDT",
      "assetId": "USDT",
      "assetName": "USDT",
      "total": "67567388.07219047016",
      "free": "3181759.86718047016",
      "locked": "64385628.20501"
    }
  ]
}
```

#### 账户交易记录 (USER_DATA)

```shell
GET /openapi/v1/myTrades  (HMAC SHA256)
```

获取当前账户历史成交记录

**Weight:**
5

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
startTime | LONG | NO |
endTime | LONG | NO |
fromId | LONG | NO | TradeId to fetch from.
toId | LONG | NO | TradeId to fetch to.
limit | INT | NO | Default 500; max 1000.
recvWindow | LONG | NO |
timestamp | LONG | YES |

**Notes:**

* 如果只有`fromId`，会返回订单号小于`fromId`的，倒序排列。
* 如果只有`toId`，会返回订单号小于`toId`的，升序排列。
* 如果同时有`fromId`和`toId`, 会返回订单号在`fromId`和`toId`的，倒序排列。
* 如果`fromId`和`toId`都没有，会返回最新的成交记录，倒序排列。
  **Response:**

```javascript
[
  {
    "id": "1996509040226736387",
    "symbol": "ETHUSDT",
    "symbolName": "ETHUSDT",
    "orderId": "1996175573823658240",
    "matchOrderId": "1996509036040820992",
    "price": "3400",
    "qty": "2.95",
    "commission": "11.033",
    "commissionAsset": "USDT",
    "time": "1752738423786",
    "isBuyer": false,
    "isMaker": true,
    "fee": {
      "feeTokenId": "USDT",
      "feeTokenName": "USDT",
      "fee": "11.033"
    }
  }
]
```



#### 获取某个提币记录 (USER_DATA)

```shell
GET /openapi/v1/withdraw/detail  (HMAC SHA256)
```

获取当前账户的提币记录

**Weight:**
2

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
orderId | LONG | NO | orderId和clientOrderId两者必须有一个有值
clientOrderId | STRING | NO | orderId和clientOrderId两者必须有一个有值

**Response:**

```javascript

{
  "time":"1536232111669",
        "orderId":"90161227158286336",
        "accountId":"517256161325920",
        "tokenId":"BHC",
        "tokenName":"BHC",
        "address":"0x815bF1c3cc0f49b8FC66B21A7e48fCb476051209",
        "addressExt":"address tag",
        "quantity":"14", // 提币金额
        "arriveQuantity":"14", // 到账金额
        "statusCode":"PROCESSING_STATUS",
        "status":3,
        "txid":"",
        "txidUrl":"",
        "walletHandleTime":"1536232111669",
        "feeTokenId":"BHC",
        "feeTokenName":"BHC",
        "fee":"0.1",
        "requiredConfirmNum":0, // 要求确认数
        "confirmNum":0, // 确认数
        "kernelId":"", // BEAM 和 GRIN 独有
        "isInternalTransfer": false // 是否内部转账
}
```

#### 根据tokenId获取充币地址信息

```shell
GET /openapi/v1/depositAddress  (HMAC SHA256)
```

获取充币地址信息

**Weight:**
1

**Parameters:**

名称 | 类型 | 是否强制 | 描述
------------ | ------------ | ------------ | ------------
tokenId | STRING | 是 | tokenId
chainType | string | 非必填 |chain type, USDT的chainType分别是OMNI ERC20 TRC20，默认OMNI |

**Notes:**

**Response:**


```javascript
   {
  "allowDeposit": true,
          "address": "0x674a5f5f866b39e20a265390d55a6b696c7928c0c14005327f532fbf9875732b",
          "addressExt": "",
          "minQuantity": "5",
          "needAddressTag": false,
          "requiredConfirmNum": 15,
          "canWithdrawConfirmNum": 30,
          "tokenType": "UNRECOGNIZED"
}
```

