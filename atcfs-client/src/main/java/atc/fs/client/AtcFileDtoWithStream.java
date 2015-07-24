package atc.fs.client;

import java.io.InputStream;

import atc.fs.common.AtcFileDto;

public class AtcFileDtoWithStream extends AtcFileDto {
	private InputStream fileStream;

	public AtcFileDtoWithStream(String fileName, String contentType, long size,
			InputStream filestStream) {
		super();
		setFileName(fileName);
		setContentType(contentType);
		setSize(size);
		setFileStream(filestStream);
	}

	public InputStream getFileStream() {
		return fileStream;
	}

	public void setFileStream(InputStream fileStream) {
		this.fileStream = fileStream;
	}
}
