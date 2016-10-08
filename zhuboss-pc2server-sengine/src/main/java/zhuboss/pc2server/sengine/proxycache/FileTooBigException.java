package zhuboss.pc2server.sengine.proxycache;

public class FileTooBigException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7848830648636682784L;

	public FileTooBigException() {
		super();
	}

	public FileTooBigException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileTooBigException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileTooBigException(String message) {
		super(message);
	}

	public FileTooBigException(Throwable cause) {
		super(cause);
	}

	
}
