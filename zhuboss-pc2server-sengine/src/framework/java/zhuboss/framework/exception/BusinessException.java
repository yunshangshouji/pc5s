package zhuboss.framework.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessException extends Exception{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String code;
	private String message;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4259146873296530795L;

	public BusinessException(String message){
		super(message);
		this.message = message;
	}

	public BusinessException(String code, String message){
		super(message);
		this.code = code;
		this.message = message;
		logger.warn("Business Exception["+code+","+message+"] at "+this.getStackTrace()[0].toString());
	}
	
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
	
	@Override
	public String toString() {
		return this.getMessage();
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
	
	
}
