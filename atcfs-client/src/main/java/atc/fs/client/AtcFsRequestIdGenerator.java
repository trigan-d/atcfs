package atc.fs.client;

public interface AtcFsRequestIdGenerator {
	public String generateNewRequestId(String visId, String visUser);
}
