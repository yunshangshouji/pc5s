package zhuboss.framework.util;

public class Constants {

	// 缓存中基金费率信息的key键头（后面是基金代码）
	public static String FUND_FEE_RATE_REDIS = "trade_fundFeeRate_";
	// 缓存中所有基金费率信息的key键
	public static String ALL_FUND_FEE_RATE_REDIS = "trade_fundFeeRateList";
	// 缓存中所有基金折扣率信息的key键
	public static String ALL_FUND_FEE_DISCOUNT_REDIS = "trade_fundFeeDiscountList";
	// 缓存中基金信息的key键头（后面是基金代码）
	public static String FUND_INFO_REDIS = "fundInfo_";
	// 缓存中所有基金信息的key键头
	public static String ALL_FUND_INFO_REDIS = "fundInfoList";
	//所有基金缓存，包括没有代销的
	public static String All_FUNDINFO_LIST = "allFundInfoList";
	//所有基金缓存，包括没有代销的
	public static String WEB_FUND_INFO_REDIS = "web_fundInfo_";
	// 缓存中节假日信息缓存的key键
	public static String HOLIDAYS = "holidays";
	
	
	public static String REDEEM_TO_PURSE = "赎回到钱包";
	
	public static String REDEEM_TO_OTHER_FUND = "调减至其他基金";
	
	public static String APPLY_FROM_OTHER_FUND = "其他基金调入";
	
	public static String APPLY_FROM_PURSE = "基金申购";
	
	public static String PURSE_FUND_FLAG = "WLT";
	
	// 组合状态：待确认
	public static String STAT_WAIT_COMFIRMING = "0";
	// 组合状态：有效
	public static String STAT_VALID = "1";
	// 组合状态：无效
	public static String STAT_INVALID = "2";
	// 组合状态：待支付
	public static String STAT_WAIT_PAYING = "3";
	// 组合状态：跟投
	public static String STAT_FOLLOW = "4";
	// 组合状态：失联
	public static String STAT_OUT_OF_CONTACT = "5";
	
	// 调仓状态：建仓中
	public static String STAT_CREATEING = "1";
	// 调仓状态：加仓中
	public static String STAT_GALLONING = "2";
	// 调仓状态：减仓中
	public static String STAT_UNDERWEIGHTING = "3";
	// 调仓状态：调仓中
	public static String STAT_TRANSFERING = "4";
	// 调仓状态：调仓中待支付
	public static String STAT_TRANSFERING_PAY_WAIT = "5";
	// 调仓状态：待跟调
	public static String STAT_WAIT_FOLLOW_TRANSFERING = "6";
	
	// 在金额区间内
	public static String IN_LiMIT_AMT_ARRAY = "InLimitAmtArray";
	// 调增
	public static String ADD = "add";
	// 调减
	public static String REDUCE = "reduce";
	//网点-用于钱包充值接口
	public static String NET_POINT = "9997";
	
	public static String FROM_CHANNEL = "0000";
	// 上一个
	public static String PRE = "pre";
	// 本次
	public static String CURRENT = "current";
	
	// 排序类型-跟投人数
	public static String ORDER_FOLLOW_COUNT = "followCount";
	// 排序类型-跟投资金规模
	public static String ORDER_FOLLOW_MARKET_VALUE = "followMarketValue";
	// 排序类型-评论数量
	public static String ORDER_COMMENT_COUNT = "commentCount";

	
}
