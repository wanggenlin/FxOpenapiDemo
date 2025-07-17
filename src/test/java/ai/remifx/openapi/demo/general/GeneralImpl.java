package ai.remifx.openapi.demo.general;

import ai.remifx.openapi.demo.base.ActionEnum;
import ai.remifx.openapi.demo.base.BaseTests;
import org.junit.Test;

public class GeneralImpl extends BaseTests {
    /**
     * server time
     */
    @Test
    public void time(){
        bizRequest(ActionEnum.time,null);
    }

    // {"timezone":"UTC","serverTime":"1752671722883","brokerFilters":[],"symbols":[{"filters":[{"minPrice":"0.01","maxPrice":"100000.00000000","tickSize":"0.01","filterType":"PRICE_FILTER"},{"minQty":"0.01","maxQty":"100000.00000000","stepSize":"0.0001","filterType":"LOT_SIZE"},{"minNotional":"10","filterType":"MIN_NOTIONAL"}],"exchangeId":"301","symbol":"ETHUSDT","symbolName":"ETHUSDT","status":"TRADING","baseAsset":"ETH","baseAssetName":"ETH","baseAssetPrecision":"0.0001","quoteAsset":"USDT","quoteAssetName":"USDT","quotePrecision":"0.01","icebergAllowed":false,"isAggregate":false,"allowMargin":true},{"filters":[{"minPrice":"0.01","maxPrice":"100000.00000000","tickSize":"0.01","filterType":"PRICE_FILTER"},{"minQty":"0.0005","maxQty":"100000.00000000","stepSize":"0.000001","filterType":"LOT_SIZE"},{"minNotional":"1","filterType":"MIN_NOTIONAL"}],"exchangeId":"301","symbol":"BTCUSDT","symbolName":"BTCUSDT","status":"TRADING","baseAsset":"BTC","baseAssetName":"BTC","baseAssetPrecision":"0.000001","quoteAsset":"USDT","quoteAssetName":"USDT","quotePrecision":"0.01","icebergAllowed":false,"isAggregate":false,"allowMargin":true},{"filters":[{"minPrice":"0.01","maxPrice":"100000.00000000","tickSize":"0.01","filterType":"PRICE_FILTER"},{"minQty":"45.01","maxQty":"100000.00000000","stepSize":"0.01","filterType":"LOT_SIZE"},{"minNotional":"0.01","filterType":"MIN_NOTIONAL"}],"exchangeId":"301","symbol":"TRXUSDT","symbolName":"TRXUSDT","status":"TRADING","baseAsset":"TRX","baseAssetName":"TRX","baseAssetPrecision":"0.01","quoteAsset":"USDT","quoteAssetName":"USDT","quotePrecision":"0.01","icebergAllowed":false,"isAggregate":false,"allowMargin":false},{"filters":[{"minPrice":"0.01","maxPrice":"100000.00000000","tickSize":"0.01","filterType":"PRICE_FILTER"},{"minQty":"0.01","maxQty":"100000.00000000","stepSize":"0.01","filterType":"LOT_SIZE"},{"minNotional":"0.01","filterType":"MIN_NOTIONAL"}],"exchangeId":"301","symbol":"EUTREMI","symbolName":"EUTREMI","status":"TRADING","baseAsset":"EUT","baseAssetName":"EUT","baseAssetPrecision":"0.01","quoteAsset":"REMI","quoteAssetName":"REMI","quotePrecision":"0.01","icebergAllowed":false,"isAggregate":false,"allowMargin":false},{"filters":[{"minPrice":"0.01","maxPrice":"100000.00000000","tickSize":"0.01","filterType":"PRICE_FILTER"},{"minQty":"0.01","maxQty":"100000.00000000","stepSize":"0.01","filterType":"LOT_SIZE"},{"minNotional":"0.01","filterType":"MIN_NOTIONAL"}],"exchangeId":"301","symbol":"ETHBTC","symbolName":"ETHBTC","status":"TRADING","baseAsset":"ETH","baseAssetName":"ETH","baseAssetPrecision":"0.01","quoteAsset":"BTC","quoteAssetName":"BTC","quotePrecision":"0.01","icebergAllowed":false,"isAggregate":false,"allowMargin":false},{"filters":[{"minPrice":"0.01","maxPrice":"100000.00000000","tickSize":"0.01","filterType":"PRICE_FILTER"},{"minQty":"0.01","maxQty":"100000.00000000","stepSize":"0.01","filterType":"LOT_SIZE"},{"minNotional":"0.01","filterType":"MIN_NOTIONAL"}],"exchangeId":"301","symbol":"ETHREMI","symbolName":"ETHREMI","status":"TRADING","baseAsset":"ETH","baseAssetName":"ETH","baseAssetPrecision":"0.01","quoteAsset":"REMI","quoteAssetName":"REMI","quotePrecision":"0.01","icebergAllowed":false,"isAggregate":false,"allowMargin":false},{"filters":[{"minPrice":"0.01","maxPrice":"100000.00000000","tickSize":"0.01","filterType":"PRICE_FILTER"},{"minQty":"0.01","maxQty":"100000.00000000","stepSize":"0.01","filterType":"LOT_SIZE"},{"minNotional":"0.01","filterType":"MIN_NOTIONAL"}],"exchangeId":"301","symbol":"BTCREMI","symbolName":"BTCREMI","status":"TRADING","baseAsset":"BTC","baseAssetName":"BTC","baseAssetPrecision":"0.01","quoteAsset":"REMI","quoteAssetName":"REMI","quotePrecision":"0.01","icebergAllowed":false,"isAggregate":false,"allowMargin":false},{"filters":[{"minPrice":"0.01","maxPrice":"100000.00000000","tickSize":"0.01","filterType":"PRICE_FILTER"},{"minQty":"0.01","maxQty":"100000.00000000","stepSize":"0.01","filterType":"LOT_SIZE"},{"minNotional":"0.01","filterType":"MIN_NOTIONAL"}],"exchangeId":"301","symbol":"REMIUSDT","symbolName":"REMIUSDT","status":"TRADING","baseAsset":"REMI","baseAssetName":"REMI","baseAssetPrecision":"0.01","quoteAsset":"USDT","quoteAssetName":"USDT","quotePrecision":"0.01","icebergAllowed":false,"isAggregate":false,"allowMargin":false}],"rateLimits":[{"rateLimitType":"REQUEST_WEIGHT","interval":"MINUTE","intervalUnit":1,"limit":3000},{"rateLimitType":"ORDERS","interval":"SECOND","intervalUnit":60,"limit":60}],"options":[],"contracts":[{"filters":[{"minPrice":"0.1","maxPrice":"100000.00000000","tickSize":"0.1","filterType":"PRICE_FILTER"},{"minQty":"1","maxQty":"100000.00000000","stepSize":"1","filterType":"LOT_SIZE"},{"minNotional":"0.000000001","filterType":"MIN_NOTIONAL"}],"exchangeId":"301","symbol":"BTC-SWAP-USDT","symbolName":"BTC-SWAP-USDT","status":"TRADING","baseAsset":"BTC-SWAP-USDT","baseAssetPrecision":"1","quoteAsset":"USDT","quoteAssetPrecision":"0.1","icebergAllowed":false,"inverse":false,"index":"BTCUSDT","marginToken":"USDT","marginPrecision":"0.0001","contractMultiplier":"0.0001","underlying":"BTC","riskLimits":[{"riskLimitId":"200000133","quantity":"1000000.0","initialMargin":"0.01","maintMargin":"0.005"},{"riskLimitId":"200000134","quantity":"2000000.0","initialMargin":"0.02","maintMargin":"0.01"},{"riskLimitId":"200000135","quantity":"3000000.0","initialMargin":"0.03","maintMargin":"0.015"},{"riskLimitId":"200000136","quantity":"4000000.0","initialMargin":"0.04","maintMargin":"0.02"}]},{"filters":[{"minPrice":"0.1","maxPrice":"100000.00000000","tickSize":"0.1","filterType":"PRICE_FILTER"},{"minQty":"1","maxQty":"100000.00000000","stepSize":"1","filterType":"LOT_SIZE"},{"minNotional":"0.000001","filterType":"MIN_NOTIONAL"}],"exchangeId":"301","symbol":"BTC-SWAP","symbolName":"BTC-SWAP","status":"TRADING","baseAsset":"BTC-SWAP","baseAssetPrecision":"1","quoteAsset":"USDT","quoteAssetPrecision":"0.1","icebergAllowed":false,"inverse":true,"index":"BTCUSDT","marginToken":"BTC","marginPrecision":"0.00000001","contractMultiplier":"1.0","underlying":"BTC","riskLimits":[{"riskLimitId":"200000137","quantity":"1000000.0","initialMargin":"0.01","maintMargin":"0.005"},{"riskLimitId":"200000138","quantity":"2000000.0","initialMargin":"0.02","maintMargin":"0.01"},{"riskLimitId":"200000139","quantity":"3000000.0","initialMargin":"0.03","maintMargin":"0.015"},{"riskLimitId":"200000140","quantity":"4000000.0","initialMargin":"0.04","maintMargin":"0.02"}]}],"tokens":[{"orgId":"9001","tokenId":"BTC","tokenName":"BTC","tokenFullName":"Bitcoin","allowWithdraw":false,"allowDeposit":false,"chainTypes":[]},{"orgId":"9001","tokenId":"ETH","tokenName":"ETH","tokenFullName":"Ethereum","allowWithdraw":false,"allowDeposit":false,"chainTypes":[]},{"orgId":"9001","tokenId":"EUT","tokenName":"EUT","tokenFullName":"EUT","allowWithdraw":true,"allowDeposit":true,"chainTypes":[]},{"orgId":"9001","tokenId":"REMI","tokenName":"REMI","tokenFullName":"REMI","allowWithdraw":true,"allowDeposit":true,"chainTypes":[]},{"orgId":"9001","tokenId":"TRX","tokenName":"TRX","tokenFullName":"TRON","allowWithdraw":false,"allowDeposit":false,"chainTypes":[]},{"orgId":"9001","tokenId":"USDT","tokenName":"USDT","tokenFullName":"TetherUS","allowWithdraw":true,"allowDeposit":true,"chainTypes":[{"chainType":"ERC20","allowDeposit":true,"allowWithdraw":true},{"chainType":"TRC20","allowDeposit":true,"allowWithdraw":true},{"chainType":"OMNI","allowDeposit":true,"allowWithdraw":true}]}]}
    /**
     * broker Info
     */
   @Test
    public void brokerInfo(){
        bizRequest(ActionEnum.brokerInfo,null);
    }

    //[{
    //	"symbol": "ETHUSDT",
    //	"quoteToken": "USDT",
    //	"baseToken": "ETH"
    //}, {
    //	"symbol": "BTCUSDT",
    //	"quoteToken": "USDT",
    //	"baseToken": "BTC"
    //}, {
    //	"symbol": "TRXUSDT",
    //	"quoteToken": "USDT",
    //	"baseToken": "TRX"
    //}, {
    //	"symbol": "EUTREMI",
    //	"quoteToken": "REMI",
    //	"baseToken": "EUT"
    //}, {
    //	"symbol": "ETHBTC",
    //	"quoteToken": "BTC",
    //	"baseToken": "ETH"
    //}, {
    //	"symbol": "ETHREMI",
    //	"quoteToken": "REMI",
    //	"baseToken": "ETH"
    //}, {
    //	"symbol": "BTCREMI",
    //	"quoteToken": "REMI",
    //	"baseToken": "BTC"
    //}, {
    //	"symbol": "REMIUSDT",
    //	"quoteToken": "USDT",
    //	"baseToken": "REMI"
    //}]
    /**
     * The endpoint requires a currency pair parameter
     */
  @Test
    public void pairs(){
        bizRequest(ActionEnum.pairs,null);
    }








}
