package ai.remifx.openapi.demo.order;


import ai.remifx.openapi.demo.base.ActionEnum;
import ai.remifx.openapi.demo.base.BaseTests;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedHashMap;


@SpringBootTest
public class OrderImpl extends BaseTests {

    //{"accountId":"1966608182328466945","symbol":"BTCUSDT","symbolName":"BTCUSDT","clientOrderId":"1752663451963252","orderId":"1995880174000937216","transactTime":"1752663457586","price":"0","origQty":"1","executedQty":"0","status":"FILLED","timeInForce":"GTC","type":"MARKET","side":"SELL"}

    /**
     * {
     * "accountId":"1966608182328466945",
     * "symbol":"BTCUSDT",
     * "symbolName":"BTCUSDT",
                 * "clientOrderId":"1752663451963252",
     * "orderId":"1995880174000937216",
     * "transactTime":"1752663457586",
     * "price":"0",
     * "origQty":"1",
     * "executedQty":"0",
     * "status":"FILLED",
     * "timeInForce":"GTC",
     * "type":"MARKET",
     * "side":"SELL"
     * }
     */

    /**
     * order create
     */
    @Test
    public void orderCreate() {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("symbol", "BTCUSDT");
        param.put("recvWindow", 50000L + "");
        param.put("side", "sell");
        param.put("type", "MARKET");
        param.put("quantity", "1.0");
        param.put("timestamp", System.currentTimeMillis() + "");
        bizRequest(ActionEnum.orderCreate, param);
    }

    //{"accountId":"1966608182328466945","exchangeId":"301","symbol":"BTCUSDT","symbolName":"BTCUSDT","clientOrderId":"1752663451963252","orderId":"1995880174000937216","price":"0","origQty":"1","executedQty":"1","cummulativeQuoteQty":"109199.99","avgPrice":"109199.99","status":"FILLED","timeInForce":"GTC","type":"MARKET","side":"SELL","stopPrice":"0.0","icebergQty":"0.0","time":"1752663457586","updateTime":"1752663457665","isWorking":true}

    /**
     * {
     * "accountId":"1966608182328466945",
     * "exchangeId":"301",
     * "symbol":"BTCUSDT",
     * "symbolName":"BTCUSDT",
     * "clientOrderId":"1752663451963252",
     * "orderId":"1995880174000937216",
     * "price":"0",
     * "origQty":"1",
     * "executedQty":"1",
     * "cummulativeQuoteQty":"109199.99",
     * "avgPrice":"109199.99",
     * "status":"FILLED",
     * "timeInForce":"GTC",
     * "type":"MARKET",
     * "side":"SELL",
     * "stopPrice":"0.0",
     * "icebergQty":"0.0",
     * "time":"1752663457586",
     * "updateTime":"1752663457665",
     * "isWorking":true
     * }
     */
    /**
     * order get
     */
    @Test
    public void orderGet() {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("timestamp", System.currentTimeMillis() + "");
        param.put("orderId", "1995880174000937216");
        param.put("clientOrderId", "1752663451963252");
        bizRequest(ActionEnum.orderGet, param);
    }


//{"timestamp":"1752665625807","orderId":"1995880174000937216","clientOrderId":"1752663451963252","signature":"54eda8a380528823806f9f64de8ead1d71a5aa190574a9cb7c4639f890d67943"}

    //{"code":-1145,"msg":"This order type does not support cancellation"}
    /**
     * order cancle
     */
    @Test
    public void orderDelete() {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("timestamp", System.currentTimeMillis() + "");
        param.put("orderId", "1995880174000937216");
        param.put("clientOrderId", "1752663451963252");
        bizRequest(ActionEnum.orderDelete, param);
    }

    //[{"accountId":"1966608182328466945","exchangeId":"301","symbol":"BTCUSDT","symbolName":"BTCUSDT","clientOrderId":"QUMYBF4Q72N3BCYR","orderId":"1995880173992548608","price":"118791.25","origQty":"44.69","executedQty":"0","cummulativeQuoteQty":"0","avgPrice":"0","status":"NEW","timeInForce":"GTC","type":"LIMIT","side":"SELL","stopPrice":"0.0","icebergQty":"0.0","time":"1752663457574","updateTime":"1752663457596","isWorking":true}]
    /**
     * Current Order get
     */

    @Test
    public void orderOpen() {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("timestamp", System.currentTimeMillis() + "");
        param.put("orderId", "1995880174000937216");
//        param.put("clientOrderId", "1752663451963252");
        bizRequest(ActionEnum.orderOpen, param);
    }

    /**
     * [{"accountId":"1966608182328466945","exchangeId":"301","symbol":"BTCUSDT","symbolName":"BTCUSDT","clientOrderId":"QUMYBF4Q72N3BCYR","orderId":"1995880173992548608","price":"118791.25","origQty":"44.69","executedQty":"0","cummulativeQuoteQty":"0","avgPrice":"0","status":"NEW","timeInForce":"GTC","type":"LIMIT","side":"SELL","stopPrice":"0.0","icebergQty":"0.0","time":"1752663457574","updateTime":"1752663457596","isWorking":true}]
     */

    /**
     *  Order  history get
     */


    @Test
    public void historyOrders() {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("timestamp", System.currentTimeMillis() + "");
        param.put("orderId", "1995880174000937216");
//        param.put("clientOrderId", "1752663451963252");
        bizRequest(ActionEnum.historyOrders, param);
    }


}
