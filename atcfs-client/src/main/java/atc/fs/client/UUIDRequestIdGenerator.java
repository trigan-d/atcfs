package atc.fs.client;

import java.util.UUID;

public class UUIDRequestIdGenerator implements AtcFsRequestIdGenerator {

	public String generateNewRequestId(String visId, String visUser) {
		return UUID.randomUUID().toString();
	}

}
