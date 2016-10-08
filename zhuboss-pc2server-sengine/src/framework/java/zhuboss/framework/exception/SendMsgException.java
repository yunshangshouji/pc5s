package zhuboss.framework.exception;

public class SendMsgException extends RuntimeException{
	private static final long serialVersionUID = -4495237667520158506L;

	public SendMsgException(String message){
		super(message);
	}
}
