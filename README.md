# Public Rest API for Broker

## General API Information

* All endpoints return either a JSON object or array.
* Data is returned in **ascending** order. Oldest first, newest last.
* All time and timestamp related fields are in milliseconds.
* HTTP `4XX` return codes are used for for malformed requests;
  the issue is on the sender's side.
* HTTP `429` return code is used when breaking a request rate limit.
* HTTP `418` return code is used when an IP has been auto-banned for continuing to send requests after receiving `429` codes.
* HTTP `5XX` return codes are used for internal errors; the issue is on broker's side.
  It is important to **NOT** treat this as a failure operation; the execution status is
  **UNKNOWN** and could have been a success.
* Any endpoint can return an ERROR; the error payload is as follows:

```javascript
{
  "code": -1121,
  "msg": "Invalid symbol."
}
```

* Specific error codes and messages defined in another document.
* For `GET` endpoints, parameters must be sent as a `query string`.
* For `POST`, `PUT`, and `DELETE` endpoints, the parameters may be sent as a
  `query string` or in the `request body` with content type
  `application/x-www-form-urlencoded`. You may mix parameters between both the
  `query string` and `request body` if you wish to do so.
* Parameters may be sent in any order.
* If a parameter sent in both the `query string` and `request body`, the
  `query string` parameter will be used.

### LIMITS

* The `/openapi/v1/brokerInfo` `rateLimits` array contains objects related to the broker's `REQUEST_WEIGHT` and `ORDER` rate limits.
* A 429 will be returned when either rate limit is violated.
* Each route has a `weight` which determines for the number of requests each endpoint counts for. Heavier endpoints and endpoints that do operations on multiple symbols will have a heavier `weight`.
* When a 429 is recieved, it's your obligation as an API to back off and not spam the API.
* **Repeatedly violating rate limits and/or failing to back off after receiving 429s will result in an automated IP ban (http status 418).**
* IP bans are tracked and **scale in duration** for repeat offenders, **from 2 minutes to 3 days**.

### Endpoint security type

* Each endpoint has a security type that determines the how you will
  interact with it.
* API-keys are passed into the Rest API via the `X-BH-APIKEY`
  header.
* API-keys and secret-keys **are case sensitive**.
* API-keys can be configured to only access certain types of secure endpoints.
  For example, one API-key could be used for TRADE only, while another API-key
  can access everything except for TRADE routes.
* By default, API-keys can access all secure routes.

Security Type | Description
------------ | ------------
NONE | Endpoint can be accessed freely.
TRADE | Endpoint requires sending a valid API-Key and signature.
USER_DATA | Endpoint requires sending a valid API-Key and signature.
USER_STREAM | Endpoint requires sending a valid API-Key.
MARKET_DATA | Endpoint requires sending a valid API-Key.

* `TRADE` and `USER_DATA` endpoints are `SIGNED` endpoints.

### SIGNED (TRADE and USER_DATA) Endpoint security

* `SIGNED` endpoints require an additional parameter, `signature`, to be
  sent in the  `query string` or `request body`.
* Endpoints use `HMAC SHA256` signatures. The `HMAC SHA256 signature` is a keyed `HMAC SHA256` operation.
  Use your `secretKey` as the key and `totalParams` as the value for the HMAC operation.
* The `signature` is **not case sensitive**.
* `totalParams` is defined as the `query string` concatenated with the
  `request body`.

### Timing security

* A `SIGNED` endpoint also requires a parameter, `timestamp`, to be sent which
  should be the millisecond timestamp of when the request was created and sent.
* An additional parameter, `recvWindow`, may be sent to specify the number of
  milliseconds after `timestamp` the request is valid for. If `recvWindow`
  is not sent, **it defaults to 5000**.
* Currently, `recvWindow` is only used when creates order.
* The logic is as follows:

  ```javascript
  if (timestamp < (serverTime + 1000) && (serverTime - timestamp) <= recvWindow) {
    // process request
  } else {
    // reject request
  }
  ```

**Serious trading is about timing.** Networks can be unstable and unreliable,
which can lead to requests taking varying amounts of time to reach the
servers. With `recvWindow`, you can specify that the request must be
processed within a certain number of milliseconds or be rejected by the
server.

**It recommended to use a small recvWindow of 5000 or less!**



**Symbol status:**

* TRADING
* HALT
* BREAK

**Symbol type:**

* SPOT

**Asset type:**

* CASH
* MARGIN

**Order status:**

* NEW
* PARTIALLY_FILLED
* FILLED
* CANCELED
* PENDING_CANCEL
* REJECTED

**Order types:**

* LIMIT
* MARKET
* LIMIT_MAKER
* STOP_LOSS (unavailable now)
* STOP_LOSS_LIMIT (unavailable now)
* TAKE_PROFIT (unavailable now)
* TAKE_PROFIT_LIMIT (unavailable now)
* MARKET_OF_PAYOUT (unavailable now)

**Order side:**

* BUY
* SELL

**Time in force:**

* GTC
* IOC
* FOK

**Kline/Candlestick chart intervals:**

m -> minutes; h -> hours; d -> days; w -> weeks; M -> months

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

**Rate limiters (rateLimitType)**

* REQUESTS_WEIGHT
* ORDERS

**Rate limit intervals**

* SECOND
* MINUTE
* DAY

### General endpoints

#### Test connectivity

```shell
GET /openapi/v1/ping
```

Test connectivity to the Rest API.

**Weight:**
0

**Parameters:**
NONE

**Response:**

```javascript
{}
```

#### Check server time

```shell
GET /openapi/v1/time
```

Test connectivity to the Rest API and get the current server time.

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

#### Broker information

```shell
GET /openapi/v1/brokerInfo
```

Current broker trading rules and symbol information

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

### Market Data endpoints

#### Order book

```shell
GET /openapi/quote/v1/depth
```

**Weight:**
Adjusted based on the limit:

Limit | Weight
------------ | ------------
5, 10, 20, 50, 100 | 1
500 | 5
1000 | 10

**Parameters:**

Name | Type | Mandatory | Description
------------ | ------------ | ------------ | ------------
symbol | STRING | YES |
limit | INT | NO | Default 100; max 100.

**Caution:** setting limit=0 can return a lot of data.

**Response:**

[PRICE, QTY]

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

#### Recent trades list

```shell
GET /openapi/quote/v1/trades
```

Get recent trades (up to last 60).

**Weight:**
1

**Parameters:**

Name | Type | Mandatory | Description
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

#### Kline/Candlestick data

```shell
GET /openapi/quote/v1/klines
```

Kline/candlestick bars for a symbol.
Klines are uniquely identified by their open time.

**Weight:**
1

**Parameters:**

Name | Type | Mandatory | Description
------------ | ------------ | ------------ | ------------
symbol | STRING | YES |
interval | ENUM | YES |
startTime | LONG | NO |
endTime | LONG | NO |
limit | INT | NO | Default 500; max 1000.

* If startTime and endTime are not sent, the most recent klines are returned.

**Response:**

```javascript
[
  [
    1499040000000,      // Open time
    "0.01634790",       // Open
    "0.80000000",       // High
    "0.01575800",       // Low
    "0.01577100",       // Close
    "148976.11427815",  // Volume
    1499644799999,      // Close time
    "2434.19055334",    // Quote asset volume
    308,                // Number of trades
    "1756.87402397",    // Taker buy base asset volume
    "28.46694368"       // Taker buy quote asset volume
  ]
]
```

#### 24hr ticker price change statistics

```shell
GET /openapi/quote/v1/ticker/24hr
```

24 hour price change statistics. **Careful** when accessing this with no symbol.

**Weight:**
1 for a single symbol; **40** when the symbol parameter is omitted

**Parameters:**

Name | Type | Mandatory | Description
------------ | ------------ | ------------ | ------------
symbol | STRING | NO |

* If the symbol is not sent, tickers for all symbols will be returned in an array.

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

#### Symbol price ticker

```shell
GET /openapi/quote/v1/ticker/price
```

Latest price for a symbol or symbols.

**Weight:**
1

**Parameters:**

Name | Type | Mandatory | Description
------------ | ------------ | ------------ | ------------
symbol | STRING | NO |

* If the symbol is not sent, prices for all symbols will be returned in an array.

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

#### Symbol order book ticker

```shell
GET /openapi/quote/v1/ticker/bookTicker
```

Best price/qty on the order book for a symbol or symbols.

**Weight:**
1

**Parameters:**

Name | Type | Mandatory | Description
------------ | ------------ | ------------ | ------------
symbol | STRING | NO |

* If the symbol is not sent, bookTickers for all symbols will be returned in an array.

**Response:**

```javascript
{
  "symbol": "LTCBTC",
  "bidPrice": "4.00000000",
  "bidQty": "431.00000000",
  "askPrice": "4.00000200",
  "askQty": "9.00000000"
}
```

OR

```javascript
[
  {
    "symbol": "LTCBTC",
    "bidPrice": "4.00000000",
    "bidQty": "431.00000000",
    "askPrice": "4.00000200",
    "askQty": "9.00000000"
  },
  {
    "symbol": "ETHBTC",
    "bidPrice": "0.07946700",
    "bidQty": "9.00000000",
    "askPrice": "100000.00000000",
    "askQty": "1000.00000000"
  }
]
```

#### Cryptoasset trading pairs

```shell
GET /openapi/v1/pairs
```
a summary on cryptoasset trading pairs available on the exchange

**Weight:**
1

**Parameters:**

None

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

### Account endpoints

#### New order  (TRADE)

```shell
POST /openapi/v1/order  (HMAC SHA256)
```

Send in a new order.

**Weight:**
1

**Parameters:**

Name | Type | Mandatory | Description
------------ | ------------ | ------------ | ------------
symbol | STRING | YES |
assetType | STRING | NO |
side | ENUM | YES |
type | ENUM | YES |
timeInForce | ENUM | NO |
quantity | DECIMAL | YES |
price | DECIMAL | NO |
newClientOrderId | STRING | NO | A unique id for the order. Automatically generated if not sent.
stopPrice | DECIMAL | NO | Used with `STOP_LOSS`, `STOP_LOSS_LIMIT`, `TAKE_PROFIT`, and `TAKE_PROFIT_LIMIT` orders. Unavailable
icebergQty | DECIMAL | NO | Used with `LIMIT`, `STOP_LOSS_LIMIT`, and `TAKE_PROFIT_LIMIT` to create an iceberg order. Unavailable
recvWindow | LONG | NO |
timestamp | LONG | YES |

Additional mandatory parameters based on `type`:

Type | Additional mandatory parameters
------------ | ------------
`LIMIT` | `timeInForce`, `quantity`, `price`
`MARKET` | `quantity`
`STOP_LOSS` | `quantity`, `stopPrice`
`STOP_LOSS_LIMIT` | `timeInForce`, `quantity`,  `price`, `stopPrice`
`TAKE_PROFIT` | `quantity`, `stopPrice`
`TAKE_PROFIT_LIMIT` | `timeInForce`, `quantity`, `price`, `stopPrice`
`LIMIT_MAKER` | `quantity`, `price`

Other info:

* `LIMIT_MAKER` are `LIMIT` orders that will be rejected if they would immediately match and trade as a taker.
* `STOP_LOSS` and `TAKE_PROFIT` will execute a `MARKET` order when the `stopPrice` is reached.
* Any `LIMIT` or `LIMIT_MAKER` type order can be made an iceberg order by sending an `icebergQty`.
* Any order with an `icebergQty` MUST have `timeInForce` set to `GTC`.

Trigger order price rules against market price for both MARKET and LIMIT versions:

* Price above market price: `STOP_LOSS` `BUY`, `TAKE_PROFIT` `SELL`
* Price below market price: `STOP_LOSS` `SELL`, `TAKE_PROFIT` `BUY`

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



#### Query order (USER_DATA)

```shell
GET /openapi/v1/order (HMAC SHA256)
```

Check an order's status.

**Weight:**
1

**Parameters:**

Name | Type | Mandatory | Description
------------ | ------------ | ------------ | ------------
orderId | LONG | YES |
origClientOrderId | STRING | YES |
recvWindow | LONG | NO |
timestamp | LONG | YES |

Notes:

* Either `orderId` or `origClientOrderId` must be sent.
* For some historical orders `cummulativeQuoteQty` will be < 0, meaning the data is not available at this time.

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

#### Cancel order (TRADE)

```shell
DELETE /openapi/v1/order  (HMAC SHA256)
```

Cancel an active order.

**Weight:**
1

**Parameters:**

Name | Type | Mandatory | Description
------------ | ------------ | ------------ | ------------
orderId | LONG | YES |
clientOrderId | STRING | YES |
recvWindow | LONG | NO |
timestamp | LONG | YES |

Either `orderId` or `clientOrderId` must be sent.

**Response:**

```javascript
{
  "symbol": "LTCBTC",
  "clientOrderId": "tU721112KM",
  "orderId": 1,
  "status": "CANCELED"
}
```

#### Current open orders (USER_DATA)

```shell
GET /openapi/v1/openOrders  (HMAC SHA256)
```

GET all open orders on a symbol. **Careful** when accessing this with no symbol.

**Weight:**
1

**Parameters:**

Name | Type | Mandatory | Description
------------ | ------------ | ------------ | ------------
symbol | String | NO |
orderId | LONG | NO |
limit | INT | NO | Default 500; max 1000.
recvWindow | LONG | NO |
timestamp | LONG | YES |

**Notes:**

* If `orderId` is set, it will get orders < that `orderId`. Otherwise most recent orders are returned.

**Response:**

```javascript
[
  {
    "symbol": "LTCBTC",
    "orderId": 1,
    "clientOrderId": "t7921223K12",
    "price": "0.1",
    "origQty": "1.0",
    "executedQty": "0.0",
    "cummulativeQuoteQty": "0.0",
    "avgPrice": "0.0",
    "status": "NEW",
    "timeInForce": "GTC",
    "type": "LIMIT",
    "side": "BUY",
    "stopPrice": "0.0",
    "icebergQty": "0.0",
    "time": 1499827319559,
    "updateTime": 1499827319559,
    "isWorking": true
  }
]
```

#### History orders (USER_DATA)

```shell
GET /openapi/v1/historyOrders (HMAC SHA256)
```

GET all orders of the account;  canceled, filled or rejected.

**Weight:**
5

**Parameters:**

Name | Type | Mandatory | Description
------------ | ------------ | ------------ | ------------
symbol | String | NO |
orderId | LONG | NO |
startTime | LONG | NO |
endTime | LONG | NO |
limit | INT | NO | Default 500; max 1000.
recvWindow | LONG | NO |
timestamp | LONG | YES |

**Notes:**

* If `orderId` is set, it will get orders < that `orderId`. Otherwise most recent orders are returned.

**Response:**

```javascript
[
  {
    "symbol": "LTCBTC",
    "orderId": 1,
    "clientOrderId": "987yjj2Ym",
    "price": "0.1",
    "origQty": "1.0",
    "executedQty": "0.0",
    "cummulativeQuoteQty": "0.0",
    "avgPrice": "0.0",
    "status": "NEW",
    "timeInForce": "GTC",
    "type": "LIMIT",
    "side": "BUY",
    "stopPrice": "0.0",
    "icebergQty": "0.0",
    "time": 1499827319559,
    "updateTime": 1499827319559,
    "isWorking": true
  }
]
```

#### Account information (USER_DATA)

```shell
GET /openapi/v1/account (HMAC SHA256)
```

GET current account information.

**Weight:**
5

**Parameters:**

Name | Type | Mandatory | Description
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

#### Account trade list (USER_DATA)

```shell
GET /openapi/v1/myTrades  (HMAC SHA256)
```

GET trades for a specific account.

**Weight:**
5

**Parameters:**

Name | Type | Mandatory | Description
------------ | ------------ | ------------ | ------------
startTime | LONG | NO |
endTime | LONG | NO |
fromId | LONG | NO | TradeId to fetch from.
toId | LONG | NO | TradeId to fetch to.
limit | INT | NO | Default 500; max 1000.
recvWindow | LONG | NO |
timestamp | LONG | YES |

**Notes:**

* If only `fromId` is set，it will get orders < that `fromId` in descending order.
* If only `toId` is set, it will get orders > that `toId` in ascending order.
* If `fromId` is set and `toId` is set, it will get orders < that `fromId` and > that `toId` in descending order.
* If `fromId` is not set and `toId` it not set, most recent order are returned in descending order.

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


