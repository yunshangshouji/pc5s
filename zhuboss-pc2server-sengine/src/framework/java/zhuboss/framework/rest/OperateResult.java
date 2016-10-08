package zhuboss.framework.rest;
/** 
 * @author 作者 zhuzhengquan: 
 * @version 创建时间：2015年10月27日 上午8:51:08 
 * 类说明 
 */
public class OperateResult<T> {
	public static final String FAIL_CODE="9999";
	public static final String SUCCESS_CODE="0000";
	
	private String errcode = SUCCESS_CODE;
	private String errmsg;
	private T data;
	
	public OperateResult(){
		
	}
	
	public OperateResult(String errcode, String errmsg) {
		this.errcode = errcode;
		this.errmsg = errmsg;
	}
	
	public String getErrcode() {
		return errcode;
	}
	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
