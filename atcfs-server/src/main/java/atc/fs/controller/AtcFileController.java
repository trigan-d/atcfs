package atc.fs.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import atc.fs.common.AtcFileDto;
import atc.fs.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import atc.fs.common.RestPaths;
import atc.fs.model.AccessEvent;
import atc.fs.model.AccessType;
import atc.fs.model.AtcFile;
import atc.fs.model.Record;
import atc.fs.model.TmpLink;
import atc.fs.repository.AtcFileRepository;
import atc.fs.repository.RecordRepository;

@RestController
public class AtcFileController {
	@Autowired
	private AtcFileRepository atcFileRepository;

	@Autowired
	private RecordRepository recordRepository;

	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private FileSystemStorage fileSystemStorage;

	@Autowired
	private TmpFilelinksService tmpFilelinksService;

	@RequestMapping(value = RestPaths.FILES_PATH, method = RequestMethod.POST)
	public ResponseEntity<String> createFile(
			HttpServletRequest webRequest,
			@RequestParam(value = RestPaths.RECORD_ID_PARAM, required = false) String recordId,
			@RequestParam(RestPaths.FILE_NAME_PARAM) String fileName,
			InputStream fileStream) {
		Record record = null;
		if (!StringUtils.isEmpty(recordId)) {
			record = recordRepository.findOne(recordId);
			if (record == null) {
				return new ResponseEntity<String>("Unknown record ID: "
						+ recordId, HttpStatus.BAD_REQUEST);
			}
		}

		if (record != null
				&& !atcFileRepository.findByRecordAndFileName(record, fileName)
						.isEmpty()) {
			return new ResponseEntity<String>("Record already has file named "
					+ fileName, HttpStatus.BAD_REQUEST);
		}

		AtcFile atcFile = new AtcFile();
		atcFile.setContentType(webRequest.getHeader("Content-Type"));
		atcFile.setFileName(fileName);
		atcFile.setRecord(record);
		atcFile.setId(UUID.randomUUID().toString());

		try {
			authorizationService.authorizeAndLogAccess(webRequest,
					AccessType.CREATE, recordId, atcFile.getId());

			File destFile = fileSystemStorage.createDestinationFile(atcFile);
			atcFile.setPath(fileSystemStorage.getFilePathInStorage(destFile));
			FileCopyUtils.copy(fileStream, new FileOutputStream(destFile));
			atcFile.setSize(destFile.length());
			atcFileRepository.save(atcFile);
		} catch (AtcFsSecurityException afse) {
			return new ResponseEntity<String>(afse.getMessage(),
					HttpStatus.FORBIDDEN);
		} catch (IOException ioe) {
			return new ResponseEntity<String>("Error while saving file: "
					+ ioe.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<String>(atcFile.getId(), HttpStatus.CREATED);
	}

	@RequestMapping(value = { RestPaths.SPECIFIC_FILE_PATH,
			RestPaths.SPECIFIC_FILE_PATH_WITH_FILENAME }, method = RequestMethod.GET)
	public void readFile(@PathVariable(RestPaths.FILE_ID_PARAM) String fileId,
			HttpServletRequest webRequest, HttpServletResponse response)
			throws IOException {
		try {
			AtcFile atcFile = atcFileRepository.findOne(fileId);

			if (atcFile == null) {
				response.sendError(HttpStatus.NOT_FOUND.value());
			} else {
				authorizationService.authorizeAndLogAccess(webRequest,
						AccessType.READ, atcFile.getRecord() == null ? null
								: atcFile.getRecord().getId(), fileId);
				writeFileToResponse(response, atcFile);
			}
		} catch (AtcFsSecurityException afse) {
			response.sendError(HttpStatus.FORBIDDEN.value(), afse.getMessage());
		}
	}

	@RequestMapping(value = {RestPaths.SPECIFIC_FILE_INFO_PATH}, method = RequestMethod.GET)
	public ResponseEntity<?> getFileInfo(
			@PathVariable(RestPaths.FILE_ID_PARAM) String fileId,
			HttpServletRequest webRequest) throws IOException {
		try {
			AtcFile atcFile = atcFileRepository.findOne(fileId);
			authorizationService.authorizeAndLogAccess(webRequest, AccessType.READ,
					atcFile == null ? null :
							atcFile.getRecord() == null ? null : atcFile.getRecord().getId(),
					fileId);
			if (atcFile == null) {
				return new ResponseEntity<AtcFileDto>(HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<AtcFileDto>(atcFile.toDto(), HttpStatus.OK);
			}
		} catch (AtcFsSecurityException afse) {
			return new ResponseEntity<String>(afse.getMessage(),
					HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = { RestPaths.DOWNLOAD_FILE_BY_TMP_LINK_PATH,
			RestPaths.DOWNLOAD_FILE_BY_TMP_LINK_PATH_WITH_FILENAME }, method = RequestMethod.GET)
	public void readFileByTmpLink(
			@PathVariable(RestPaths.TMP_LINK_ID_PARAM) String tmpLinkId,
			HttpServletRequest webRequest, HttpServletResponse response)
			throws IOException {
		try {
			TmpLink tmpLink = tmpFilelinksService.findTmpLinkByTmpLinkId(tmpLinkId);

			if (tmpLink == null) {
				response.sendError(HttpStatus.NOT_FOUND.value());
			} else {
				authorizationService.logTmpLinkAccess(webRequest, tmpLink);
				writeFileToResponse(response, tmpLink.getAtcFile());
			}
			/*
			 * } catch (AtcFsSecurityException afse) {
			 * response.sendError(HttpStatus.FORBIDDEN.value(),
			 * afse.getMessage());
			 */
		} catch (TmpLinkExpiredException e) {
			response.sendError(HttpStatus.GONE.value(), "link expired");
		}
	}

	@RequestMapping(value = RestPaths.GET_TMP_FILE_LINK_PATH, method = RequestMethod.GET)
	public ResponseEntity<String> getFileTmpLink(
			@PathVariable(RestPaths.FILE_ID_PARAM) String fileId,
			HttpServletRequest webRequest) throws IOException {
		try {
			AtcFile atcFile = atcFileRepository.findOne(fileId);

			if (atcFile == null) {
				return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
			}

			AccessEvent accessEvent = authorizationService
					.authorizeAndLogAccess(webRequest,
							AccessType.CREATE_TMP_FILELINK,
							atcFile.getRecord() == null ? null : atcFile
									.getRecord().getId(), fileId);

			return new ResponseEntity<String>(
					tmpFilelinksService.createTmpLink(atcFile,
							accessEvent.getVis(), accessEvent.getVisUser()),
					HttpStatus.OK);
		} catch (AtcFsSecurityException afse) {
			return new ResponseEntity<String>(afse.getMessage(),
					HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = RestPaths.SPECIFIC_FILE_PATH, method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteFile(
			@PathVariable(RestPaths.FILE_ID_PARAM) String fileId,
			HttpServletRequest webRequest) {
		try {
			AtcFile atcFile = atcFileRepository.findOne(fileId);

			if (atcFile == null) {
				return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
			}

			authorizationService.authorizeAndLogAccess(webRequest,
					AccessType.DELETE, atcFile.getRecord() == null ? null
							: atcFile.getRecord().getId(), fileId);

			fileSystemStorage.deleteFileInStorage(atcFile);
			atcFileRepository.delete(atcFile);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (AtcFsSecurityException afse) {
			return new ResponseEntity<String>(afse.getMessage(),
					HttpStatus.FORBIDDEN);
		}
	}

	private void writeFileToResponse(HttpServletResponse response,
			AtcFile atcFile) throws IOException {
		response.setContentType(atcFile.getContentType());
		response.setContentLength((int) atcFile.getSize());
		response.setHeader(
				"Content-Disposition",
				"attachment; filename*=UTF-8''"
						+ URLEncoder.encode(atcFile.getFileName(), "UTF-8")
								.replace("+", "%20"));

		FileCopyUtils
				.copy(new FileInputStream(fileSystemStorage
						.getFileInStorage(atcFile)), response.getOutputStream());
	}
}
