package atc.fs.service;

import java.io.File;

import org.springframework.stereotype.Service;

import atc.fs.model.AtcFile;

@Service
public interface FileSystemStorage {
	public File createDestinationFile(AtcFile atcFile);
	
	public String getFilePathInStorage(File file);
	
	public File getFileInStorage(AtcFile atcFile);

	public void deleteFileInStorage(AtcFile atcFile);
}
