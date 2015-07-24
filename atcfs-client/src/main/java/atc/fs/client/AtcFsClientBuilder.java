package atc.fs.client;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class AtcFsClientBuilder {
	protected static final int DEFALUT_CONNECT_TIMEOUT_SECONDS = 2;

	private String atcFsServerUrl;
	private String visId;
	private String visUser;
	private String secretKey;

	private AtcFsRequestIdGenerator requestIdGenerator;

	private CloseableHttpClient httpClient;

	private int connectTimeoutSeconds = DEFALUT_CONNECT_TIMEOUT_SECONDS;

	/* CLIENT CONFIGURATION */

	public AtcFsClientBuilder(String atcFsServerUrl, String visId, String visUser, String secretKey) {
		this.atcFsServerUrl = atcFsServerUrl;
		this.visId = visId;
		this.visUser = visUser;
		this.secretKey = secretKey;
	}

	public AtcFsClientBuilder setConnectionTimeoutSeconds(int connectTimeoutSeconds) {
		this.connectTimeoutSeconds = connectTimeoutSeconds;
		return this;
	}

	public AtcFsClientBuilder setHttpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
		return this;
	}

	public AtcFsClientBuilder setRequestIdGenerator(AtcFsRequestIdGenerator requestIdGenerator) {
		this.requestIdGenerator = requestIdGenerator;
		return this;
	}

	public AtcFsClient build() {
		return new AtcFsClient(atcFsServerUrl, visId, visUser, secretKey,
				requestIdGenerator == null ? getDefaultRequestIdGenerator() : requestIdGenerator,
				httpClient == null ? getDefaultHttpClient() : httpClient, connectTimeoutSeconds);
	}

	protected static CloseableHttpClient getDefaultHttpClient() {
		return HttpClients.createDefault();
	}

	protected static AtcFsRequestIdGenerator getDefaultRequestIdGenerator() {
		return new UUIDRequestIdGenerator();
	}
}
