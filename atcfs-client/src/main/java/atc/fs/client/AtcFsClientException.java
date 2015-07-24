package atc.fs.client;

public class AtcFsClientException extends RuntimeException {
	private static final long serialVersionUID = -360055734825683233L;

	public AtcFsClientException(Throwable cause) {
		super(cause);
	}

	public AtcFsClientException(String message) {
		super(message);
	}
}
