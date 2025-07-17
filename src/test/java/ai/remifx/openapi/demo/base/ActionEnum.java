package ai.remifx.openapi.demo.base;

public enum ActionEnum {

    time("/v1/time","get",false),// server time
    brokerInfo("/v1/brokerInfo","get",false),// broker info
    quoteDepth("/quote/v1/depth","get",false),
    quoteTrades("/quote/v1/trades","get",false),
    quoteKlines("/quote/v1/klines","get",false),
    quoteTicker24hr("/quote/v1/ticker/24hr","get",false),
    quoteTickerPrice("quote/v1/ticker/price","get",false),
    quoteTickerBookTicker("quote/v1/ticker/bookTicker","get",false),
    orderCreate("v1/order","post",true),
    orderGet("v1/order","get",true),
    orderDelete("v1/order","delete",true),
    orderOpen("v1/openOrders","get",true),
    historyOrders ("v1/historyOrders","get",true),
    accountInfo ("v1/account","get",true),
    myTrades ("v1/myTrades","get",true),
    depositOrders ("v1/depositOrders","get",true),
    withdrawDetail ("v1/withdraw/detail","get",true),
    depositAddress ("v1/depositAddress","get",true),
    withdrawalOrders  ("v1/withdrawalOrders","get",true),
    pairs("/v1/pairs","get",false),
    ;

    /**
     *
     * @param action
     * @param method
     * @param sign  
     */
    ActionEnum(String action, String method, boolean sign) {
        this.action = action;
        this.method = method;
        this.sign = sign;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean isSign() {
        return sign;
    }

    public void setSign(boolean sign) {
        this.sign = sign;
    }

    private String action;
    private String method;

    private boolean sign;




}
