package atc.fs.client;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.*;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import atc.fs.common.AtcFileDto;
import atc.fs.common.RestPaths;
import atc.fs.common.SecurityUtils;

public class AtcFsClient {
	private final String atcFsServerUrl;
	private final String visId;
	private final String visUser;
	private final String secretKey;
	private static final Gson gson = new Gson();

	private static HashMap<String, String> uriTemplates = new LinkedHashMap<String, String>();

	static {
		for (Field field : RestPaths.class.getDeclaredFields()) {
			if (field.getType() == String.class) {
				try {
					uriTemplates.put(field.getName(), prepareUriTemplate((String) field.get(null)));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new AtcFsClientException(e);
				}
			}
		}
	}

	private final AtcFsRequestIdGenerator requestIdGenerator;

	private final CloseableHttpClient httpClient;

	private final RequestConfig config;

	/* CLIENT CONFIGURATION */

	public AtcFsClient(String atcFsServerUrl, String visId, String visUser, String secretKey) {
		this(atcFsServerUrl, visId, visUser, secretKey, AtcFsClientBuilder
				.getDefaultRequestIdGenerator(), AtcFsClientBuilder.getDefaultHttpClient(),
				AtcFsClientBuilder.DEFALUT_CONNECT_TIMEOUT_SECONDS);
	}

	protected AtcFsClient(String atcFsServerUrl, String visId, String visUser, String secretKey,
			AtcFsRequestIdGenerator requestIdGenerator, CloseableHttpClient httpClient,
			int connectTimeoutSeconds) {
		this.atcFsServerUrl = atcFsServerUrl;
		this.visId = visId;
		this.visUser = visUser;
		this.secretKey = secretKey;
		this.requestIdGenerator = requestIdGenerator;
		this.httpClient = httpClient;
		config = RequestConfig.custom().setConnectTimeout(connectTimeoutSeconds * 1000).build();
	}

	/* OPERATIONS ON RECORDS */

	public <T> String createRecord(T recordData) {
		HttpPost httpPost = new HttpPost(buildUrl("RECORDS_PATH"));
		prepareRequestForJson(httpPost);
		httpPost.setEntity(createHttpEntityForJson(recordData));
		return executeAndExtractEntity(httpPost);
	}

	public <T> T readRecord(String recordId, Class<T> recordType) {
		HttpGet httpGet = new HttpGet(buildUrl("SPECIFIC_RECORD_PATH", recordId));
		prepareRequestForJson(httpGet);
		String entity = executeAndExtractEntity(httpGet);
		return recordType == String.class ? (T) entity : gson.fromJson(entity, recordType);
	}

	public <T> void updateRecord(String recordId, T recordData) {
		HttpPut httpPut = new HttpPut(buildUrl("SPECIFIC_RECORD_PATH", recordId));
		prepareRequestForJson(httpPut);
		httpPut.setEntity(createHttpEntityForJson(recordData));
		executeAndExtractEntity(httpPut);
	}

	public void deleteRecord(String recordId) {
		HttpDelete httpDelete = new HttpDelete(buildUrl("SPECIFIC_RECORD_PATH", recordId));
		prepareRequestForJson(httpDelete);
		executeAndExtractEntity(httpDelete);
	}

	public ArrayList<AtcFileDto> getRecordFiles(String recordId) {
		HttpGet httpGet = new HttpGet(buildUrl("RECORD_FILES_PATH", recordId));
		prepareRequestForJson(httpGet);
		Type listOfAtcFileDtos = new TypeToken<ArrayList<AtcFileDto>>() {
		}.getType();
		return gson.fromJson(executeAndExtractEntity(httpGet), listOfAtcFileDtos);
	}

	/* OPERATIONS ON FILES */

	public String createFile(final String fileName, final String contentType,
			final InputStream fileStream, final String recordId) {
		try {
			URIBuilder uriBuilder = new URIBuilder(atcFsServerUrl + RestPaths.FILES_PATH)
					.addParameter(RestPaths.FILE_NAME_PARAM, fileName);
			if (!(recordId == null || "".equals(recordId))) {
				uriBuilder.addParameter(RestPaths.RECORD_ID_PARAM, recordId);
			}
			URI uri = uriBuilder.build();
			HttpPost httpPost = new HttpPost(uri);
			performCommonPreparations(httpPost);
			httpPost.setEntity(new InputStreamEntity(fileStream));
			httpPost.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
			return executeAndExtractEntity(httpPost);
		} catch (URISyntaxException e) {
			throw new AtcFsClientException(e);
		}
	}

	public AtcFileDtoWithStream getFileDtoWithStream(String fileId) {
		HttpGet httpGet = new HttpGet(buildUrl("SPECIFIC_FILE_PATH", fileId));
		performCommonPreparations(httpGet);
		try {
			CloseableHttpResponse response = getHttpClient().execute(httpGet);
			try {
				checkResponseStatus(response);
				return new AtcFileDtoWithStream(extractAttachmentFilename(response), response
						.getEntity().getContentType().getValue(), response.getEntity()
						.getContentLength(), response.getEntity().getContent());
			} catch (AtcFsClientException e){
				response.close();
				throw e;
			}
		} catch (IOException e) {
			throw new AtcFsClientException(e);
		}
	}

	public AtcFileDto getFileDto(String fileId) {
		HttpGet httpGet = new HttpGet(buildUrl("SPECIFIC_FILE_INFO_PATH", fileId));
		performCommonPreparations(httpGet);
		return gson.fromJson(executeAndExtractEntity(httpGet), AtcFileDto.class);
	}

	public URI getFileTmpLink(String fileId) {
		HttpGet httpGet = new HttpGet(buildUrl("GET_TMP_FILE_LINK_PATH", fileId));
		prepareRequestForJson(httpGet);
		String tmpLinkId = executeAndExtractEntity(httpGet);
		return buildUrl("DOWNLOAD_FILE_BY_TMP_LINK_PATH", tmpLinkId);
	}

	public URI getFileTmpLinkWithFilename(String fileId, String fileName) {
		HttpGet httpGet = new HttpGet(buildUrl("GET_TMP_FILE_LINK_PATH", fileId));
		prepareRequestForJson(httpGet);
		String tmpLinkId = executeAndExtractEntity(httpGet);
		try {
			return buildUrl("DOWNLOAD_FILE_BY_TMP_LINK_PATH_WITH_FILENAME", tmpLinkId,
					URLEncoder.encode(fileName, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			throw new AtcFsClientException(e);
		}
	}

	public void deleteFile(String fileId) {
		HttpDelete httpDelete = new HttpDelete(buildUrl("SPECIFIC_FILE_PATH", fileId));
		prepareRequestForJson(httpDelete);
		executeAndExtractEntity(httpDelete);
	}

	/* PRIVATE HELPER METHODS */

	private <T> HttpEntity createHttpEntityForJson(T body) {
		String dataForEntity;
		if (!(body instanceof String)) {
			dataForEntity = gson.toJson(body);
		} else {
			dataForEntity = (String) body;
		}
		// try {
		return new StringEntity(dataForEntity, Charset.forName("utf-8"));
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		// return null;
	}

	private String executeAndExtractEntity(HttpUriRequest request) {
		try {
			CloseableHttpResponse response = getHttpClient().execute(request);
			try {
				checkResponseStatus(response);
				return EntityUtils.toString(response.getEntity());
			} finally {
				response.close();
			}
		} catch (IOException e) {
			throw new AtcFsClientException(e);
		}
	}

	private void prepareRequestForJson(HttpRequestBase httpRequest) {
		performCommonPreparations(httpRequest);
		httpRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=\"UTF-8\"");
		httpRequest.setHeader(HttpHeaders.CONTENT_ENCODING, "utf-8");
	}

	private void performCommonPreparations(HttpRequestBase httpRequest) {
		setConfiguration(httpRequest);
		setSecurityHeaders(httpRequest);
	}

	private void setConfiguration(HttpRequestBase request) {
		request.setConfig(config);
	}

	private void setSecurityHeaders(HttpRequest request) {
		request.addHeader(SecurityUtils.VIS_ID_HEADER, visId);
		request.addHeader(SecurityUtils.VIS_USER_HEADER, visUser);
		String requestId = requestIdGenerator.generateNewRequestId(visId, visUser);
		request.addHeader(SecurityUtils.REQUEST_ID_HEADER, requestId);
		request.addHeader(SecurityUtils.SIGN_HEADER,
				SecurityUtils.generateRequestSignature(visId, visUser, requestId, secretKey));
	}

	private URI buildUrl(String path, Object... urlVariables) {
		path = atcFsServerUrl + uriTemplates.get(path);
		try {
			return new URIBuilder(String.format(path, urlVariables)).build();
		} catch (URISyntaxException e) {
			throw new AtcFsClientException(e);
		}
	}

	private static String prepareUriTemplate(String recordIdParam) {
		int placeholderCounter = 1;
		while (recordIdParam.contains("{") && recordIdParam.contains("}")
				&& (recordIdParam.indexOf("{") < recordIdParam.indexOf("}"))) {
			recordIdParam = recordIdParam.replaceFirst("\\{.*?\\}",
					"%" + String.valueOf(placeholderCounter) + "\\$s");
			placeholderCounter++;
		}
		return recordIdParam;
	}

	private String extractAttachmentFilename(HttpResponse response) {
		Header header = response.getFirstHeader("Content-Disposition");
		if (header != null && header.getElements() != null) {
			for (HeaderElement element : header.getElements()) {
				if (element.getName().equals("attachment")) {
					for (NameValuePair param : element.getParameters()) {
						if (param.getName().equalsIgnoreCase("filename")
								|| param.getName().equalsIgnoreCase("filename*")) {
							// http://tools.ietf.org/html/rfc5987
							String[] tokens = param.getValue().split("'");
							if (tokens.length == 1) {
								return tokens[0];
							} else if (tokens.length == 3) {
								try {
									return URLDecoder.decode(tokens[2], tokens[0]);
								} catch (UnsupportedEncodingException e) {
									throw new AtcFsClientException(e);
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	private void checkResponseStatus(HttpResponse response) {
		if (response.getStatusLine().getStatusCode() > 399) {
			switch (response.getStatusLine().getStatusCode()) {
				case HttpStatus.SC_NOT_FOUND:
					throw new AtcFsClientNotFoundException(response.getStatusLine().getReasonPhrase());
				case HttpStatus.SC_FORBIDDEN:
					throw new AtcFsClientSecurityException(response.getStatusLine().getReasonPhrase());
				default:
					throw new AtcFsClientException(new HttpResponseException(response.getStatusLine()
							.getStatusCode(), response.getStatusLine().getReasonPhrase()));
			}
		}
	}

	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}
}
