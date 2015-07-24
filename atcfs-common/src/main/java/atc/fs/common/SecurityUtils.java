package atc.fs.common;

import org.apache.commons.codec.digest.DigestUtils;

public class SecurityUtils {
	public static final String VIS_ID_HEADER = "AtcFs-VisId";
	public static final String VIS_USER_HEADER = "AtcFs-VisUser";
	public static final String REQUEST_ID_HEADER = "AtcFs-RequestId";
	public static final String SIGN_HEADER = "AtcFs-Sign";

	public static String generateRequestSignature(String visId, String visUser,
			String requestId, String visSecretKey) {
		
		return DigestUtils.md5Hex(visId + "_" + visUser + "_" + requestId + "_"
				+ visSecretKey);
	}
}
