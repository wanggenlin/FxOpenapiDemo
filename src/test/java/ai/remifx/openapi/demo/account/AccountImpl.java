package ai.remifx.openapi.demo.account;

import ai.remifx.openapi.demo.base.ActionEnum;
import ai.remifx.openapi.demo.base.BaseTests;
import org.junit.Test;

import java.util.LinkedHashMap;


/*
* account api demo
* */
public class AccountImpl extends BaseTests {

    //https://api.remifx-test.ai/openapi/v1/account?timestamp=1752668823359&signature=19c2cd355449582e7f4ed39c410ca4974c855c41cd44c6a7240aa0bbcc1fbf24
    // {"balances":[{"asset":"BTC","assetId":"BTC","assetName":"BTC","total":"100000607.500918962","free":"99841633.401018962","locked":"158974.0999"},{"asset":"ETH","assetId":"ETH","assetName":"ETH","total":"99999886.598223","free":"99998381.148223","locked":"1505.45"},{"asset":"USDT","assetId":"USDT","assetName":"USDT","total":"68273164.96859569016","free":"3274724.78358569016","locked":"64998440.18501"}]}

    /**
     * {
     * "balances":[
     * {
     * "asset":"BTC",
     * "assetId":"BTC",
     * "assetName":"BTC",
     * "total":"100000607.500918962",
     * "free":"99841633.401018962",
     * "locked":"158974.0999"
     * },
     * {
     * "asset":"ETH",
     * "assetId":"ETH",
     * "assetName":"ETH",
     * "total":"99999886.598223",
     * "free":"99998381.148223",
     * "locked":"1505.45"
     * },
     * {
     * "asset":"USDT",
     * "assetId":"USDT",
     * "assetName":"USDT",
     * "total":"68273164.96859569016",
     * "free":"3274724.78358569016",
     * "locked":"64998440.18501"
     * }
     * ]
     * }
     */

    /**
     * account info  (HMAC SHA256)
     */
    @Test
    public void info() {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("timestamp", System.currentTimeMillis() + "");

        bizRequest(ActionEnum.accountInfo, param);
    }


    //https://api.remifx-test.ai/openapi/v1/myTrades?timestamp=1752669147643&signature=3e29d807e3f907c4a545474f663d61905e211b948cf1d8b2edada6abf22f3dda
    // [{"id":"1995922399745415425","symbol":"ETHUSDT","symbolName":"ETHUSDT","orderId":"1995922399284041984","matchOrderId":"1995922375217125632","price":"3151.66","qty":"0.81","commission":"2.5528446","commissionAsset":"USDT","time":"1752668491236","isBuyer":false,"isMaker":false,"fee":{"feeTokenId":"USDT","feeTokenName":"USDT","fee":"2.5528446"},"feeTokenId":"USDT","feeAmount":"2.5528446","makerRebate":"0"}]

    /**
     * transaction history
     */
    @Test
    public void myTrades() {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("timestamp", System.currentTimeMillis() + "");
        bizRequest(ActionEnum.myTrades, param);
    }

    //https://api.remifx-test.ai/openapi/v1/depositOrders?timestamp=1752669400908&signature=b8859ff9bd117aa811b92489c051073682e8555a1247f6c5e4b21b2249730b30

    // []

    /**
     * Check a crypto withdrawal history
     */
    @Test
    public void depositOrders() {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("timestamp", System.currentTimeMillis() + "");
        bizRequest(ActionEnum.depositOrders, param);
    }


    //未完成交易
    @Test
    public void withdrawDetail() {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("orderId", "1995880174000937216");
        param.put("clientOrderId", "1752663451963252");
        bizRequest(ActionEnum.withdrawDetail, param);
    }

    //https://api.remifx-test.ai/openapi/v1/depositAddress?tokenId=BTC&signature=681eed3d783977b552c5f348d7f38cfd988aa6bdb3b634be23ee1b5a6ef161f2
    //{"allowDeposit":true,"address":"0x674a5f5f866b39e20a265390d55a6b696c7928c0c14005327f532fbf9875732b","addressExt":"","minQuantity":"5","needAddressTag":false,"requiredConfirmNum":15,"canWithdrawConfirmNum":30,"tokenType":"UNRECOGNIZED"}

    /**
     * Retrieve deposit address information by tokenId
     */
    @Test
    public void depositAddress() {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("tokenId", "REMI");
        bizRequest(ActionEnum.depositAddress, param);
    }

    //https://api.remifx-test.ai/openapi/v1/withdrawalOrders?orderId=1995880174000937216&clientOrderId=1752663451963252&signature=b6c336ad369903c96fd33c6933446a46e03fa70d01fa8a05f83b7aa4f3ce0069
    // []

    @Test
    public void withdrawalOrders() {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("orderId", "1995880174000937216");
        param.put("clientOrderId", "1752663451963252");
        bizRequest(ActionEnum.withdrawalOrders, param);
    }






}
