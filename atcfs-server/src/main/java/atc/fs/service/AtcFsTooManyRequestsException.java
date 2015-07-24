package atc.fs.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS, reason = "Too many requests per minute, check ATC File Storage settings")
public class AtcFsTooManyRequestsException extends RuntimeException {
	private static final long serialVersionUID = -1098629594793854114L;

}
