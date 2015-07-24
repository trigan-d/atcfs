package atc.fs.common;

public class RestPaths {
	public static final String RECORD_ID_PARAM = "recordId";
	public static final String FILE_NAME_PARAM = "fileName";
	public static final String FILE_ID_PARAM = "fileId";
	public static final String TMP_LINK_ID_PARAM = "tmpLinkId";

	public static final String RECORDS_PATH = "/records";
	public static final String SPECIFIC_RECORD_PATH = RECORDS_PATH + "/{" + RECORD_ID_PARAM + "}";
	public static final String RECORD_FILES_PATH = SPECIFIC_RECORD_PATH	+ "/files";

	public static final String FILES_PATH = "/files";
	public static final String FILE_INFO_PATH = "/fileinfo";
	public static final String SPECIFIC_FILE_PATH = FILES_PATH + "/{" + FILE_ID_PARAM + "}";
	public static final String SPECIFIC_FILE_PATH_WITH_FILENAME = SPECIFIC_FILE_PATH + "/{" + FILE_NAME_PARAM +"}";
	public static final String SPECIFIC_FILE_INFO_PATH = FILE_INFO_PATH + "/{" + FILE_ID_PARAM + "}";
	
	public static final String GET_TMP_FILE_LINK_PATH = "/tmpFileLink/{" + FILE_ID_PARAM + "}";
	public static final String DOWNLOAD_FILE_BY_TMP_LINK_PATH = "/tmpFiles/{" + TMP_LINK_ID_PARAM + "}";
	public static final String DOWNLOAD_FILE_BY_TMP_LINK_PATH_WITH_FILENAME = DOWNLOAD_FILE_BY_TMP_LINK_PATH + "/{" + FILE_NAME_PARAM +"}";
}
