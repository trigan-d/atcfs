package atc.fs.service.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import atc.fs.model.AtcFile;
import atc.fs.service.FileSystemStorage;

@Service
public class TrivialFileSystemStorage implements FileSystemStorage {
	@Value("${filestorage.directory}")
	private String fileStorageDir;

	@Override
	public File createDestinationFile(AtcFile atcFile) {
		File storageDir = new File(fileStorageDir);
		if (!storageDir.exists()) {
			storageDir.mkdirs();
		}
		return new File(storageDir, atcFile.getId());
	}

	@Override
	public String getFilePathInStorage(File file) {
		return file.getPath().substring(fileStorageDir.length());
	}

	@Override
	public void deleteFileInStorage(AtcFile atcFile) {
		File file = new File(fileStorageDir + atcFile.getPath());
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	public File getFileInStorage(AtcFile atcFile) {
		return new File(fileStorageDir + atcFile.getPath());
	}
}
