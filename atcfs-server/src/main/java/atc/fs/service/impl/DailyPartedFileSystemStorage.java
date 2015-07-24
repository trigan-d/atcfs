package atc.fs.service.impl;

import atc.fs.model.AtcFile;
import atc.fs.service.FileSystemStorage;
import org.apache.commons.io.filefilter.*;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.regex.Pattern;

import static org.apache.commons.io.filefilter.DirectoryFileFilter.*;
import static org.apache.commons.io.filefilter.FileFileFilter.*;

@Primary
@Service
public class DailyPartedFileSystemStorage implements FileSystemStorage
{
	private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

	private static final FastDateFormat HOUR_FORMAT = FastDateFormat.getInstance("HH");

	@Value("${filestorage.partialdirnameprefix}")
	private String partialDirNamePrefix;

	@Value("${filestorage.filesperdirectory}")
	private int filesPerFolder;

	@Value("${filestorage.directory}")
	private String fileStorageDir;

	@Override
	public File createDestinationFile(AtcFile atcFile) {
		File storageDir = new File(fileStorageDir);
		if (!storageDir.exists()) {
			storageDir.mkdirs();
		}
		File whereToCreateFile;
		File hourDir = getHourDirectory();
		if (hourDir.list(FILE).length >= filesPerFolder) {
				String[] dirNames = hourDir.list(new AndFileFilter(DIRECTORY, new RegexFileFilter(
						Pattern.quote(partialDirNamePrefix) + "\\d+")));
				int directoryNumber = 1;
				for (String name : dirNames) {
					int n = Integer.valueOf(name.substring(1));
					directoryNumber = directoryNumber > n ? directoryNumber : n;
				}
				File partialDir = new File(hourDir, "/" + partialDirNamePrefix + String.valueOf(directoryNumber));
				if (!partialDir.exists()) {
					partialDir.mkdirs();
				}
				if (partialDir.list(FILE).length >= filesPerFolder) {
					whereToCreateFile = new File(hourDir, "/" + partialDirNamePrefix
							+ String.valueOf(directoryNumber + 1));
				} else {
					whereToCreateFile = partialDir;
				}
				if (!whereToCreateFile.exists()) {
					whereToCreateFile.mkdirs();
				}
			} else {
				whereToCreateFile = hourDir;
			}
		return new File(whereToCreateFile, atcFile.getId());
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

	private File getHourDirectory() {
		File storageDir = new File(fileStorageDir);
		if (!storageDir.exists()) {
			storageDir.mkdirs();
		}
		Date d = new Date();
		String date = DATE_FORMAT.format(d);
		String hour = HOUR_FORMAT.format(d);
		File hourDir = new File(fileStorageDir + "/" + date + "/" + hour);
		if (!hourDir.exists()) {
			hourDir.mkdirs();
		}
		return hourDir;
	}
}
