package atc.fs.controller;

import java.util.ArrayList;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import atc.fs.common.AtcFileDto;
import atc.fs.common.RestPaths;
import atc.fs.model.AccessEvent;
import atc.fs.model.AccessType;
import atc.fs.model.AtcFile;
import atc.fs.model.Record;
import atc.fs.repository.AccessEventRepository;
import atc.fs.repository.RecordRepository;
import atc.fs.service.AtcFsSecurityException;
import atc.fs.service.AuthorizationService;
import atc.fs.service.FileSystemStorage;

@RestController
public class RecordsController {
	@Autowired
	private RecordRepository recordRepository;

	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private AccessEventRepository accessEventRepository;
	@Autowired
	private FileSystemStorage fileSystemStorage;

	@RequestMapping(value = RestPaths.RECORDS_PATH, method = RequestMethod.POST)
	public ResponseEntity<String> createRecord(
			@RequestBody String recordData,
			HttpServletRequest webRequest) {
		Record record = new Record();
		record.setData(recordData);
		try {
			AccessEvent accessEvent = authorizationService
					.authorizeAndLogAccess(webRequest, AccessType.CREATE, null,
							null);

			record = recordRepository.save(record);

			accessEvent.setRecordId(record.getId());
			accessEventRepository.save(accessEvent);

			return new ResponseEntity<String>(record.getId(),
					HttpStatus.CREATED);
		} catch (AtcFsSecurityException afse) {
			return new ResponseEntity<String>(afse.getMessage(),
					HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = RestPaths.SPECIFIC_RECORD_PATH, method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	public ResponseEntity<String> readRecord(
			@PathVariable(RestPaths.RECORD_ID_PARAM) String recordId,
			HttpServletRequest webRequest) {
		try {
			authorizationService.authorizeAndLogAccess(webRequest,
					AccessType.READ, recordId, null);

			Record record = recordRepository.findOne(recordId);
			if (record == null) {
				return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<String>(record.getData(),
						HttpStatus.OK);
			}
		} catch (AtcFsSecurityException afse) {
			return new ResponseEntity<String>(afse.getMessage(),
					HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = RestPaths.RECORD_FILES_PATH, method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	public ResponseEntity<?> viewRecordFiles(
			@PathVariable(RestPaths.RECORD_ID_PARAM) String recordId,
			HttpServletRequest webRequest) {
		try {
			authorizationService.authorizeAndLogAccess(webRequest,
					AccessType.VIEW_FILES, recordId, null);

			Record record = recordRepository.findOne(recordId);
			if (record == null) {
				return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
			} else {
				Set<AtcFile> files = record.getFiles();
				ArrayList<AtcFileDto> dtos = new ArrayList<AtcFileDto>(
						files.size());
				for (AtcFile file : files) {
					dtos.add(file.toDto());
				}
				return new ResponseEntity<ArrayList<AtcFileDto>>(dtos,
						HttpStatus.OK);
			}
		} catch (AtcFsSecurityException afse) {
			return new ResponseEntity<String>(afse.getMessage(),
					HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = RestPaths.SPECIFIC_RECORD_PATH, method = RequestMethod.PUT)
	public ResponseEntity<String> updateRecord(
			@PathVariable(RestPaths.RECORD_ID_PARAM) String recordId,
			@RequestBody String recordData,
			HttpServletRequest webRequest) {
		try {
			AccessEvent accessEvent = authorizationService
					.authorizeAndLogAccess(webRequest, AccessType.UPDATE,
							recordId, null);

			Record record = recordRepository.findOne(recordId);
			if (record == null) {
				return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
			} else {
				accessEvent.setOldData(record.getData());
				accessEventRepository.save(accessEvent);

				if (!record.getData().equals(recordData)) {
					record.setData(recordData);
					recordRepository.save(record);
				}
				return new ResponseEntity<String>(HttpStatus.OK);
			}
		} catch (AtcFsSecurityException afse) {
			return new ResponseEntity<String>(afse.getMessage(),
					HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = RestPaths.SPECIFIC_RECORD_PATH, method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteRecord(
			@PathVariable(RestPaths.RECORD_ID_PARAM) String recordId,
			HttpServletRequest webRequest) {
		try {
			AccessEvent accessEvent = authorizationService
					.authorizeAndLogAccess(webRequest, AccessType.DELETE,
							recordId, null);

			Record record = recordRepository.findOne(recordId);
			if (record == null) {
				return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
			} else {
				accessEvent.setOldData(record.getData());
				accessEventRepository.save(accessEvent);

				for (AtcFile file : record.getFiles()) {
					fileSystemStorage.deleteFileInStorage(file);
				}
				recordRepository.delete(record);
				return new ResponseEntity<String>(HttpStatus.OK);
			}
		} catch (AtcFsSecurityException afse) {
			return new ResponseEntity<String>(afse.getMessage(),
					HttpStatus.FORBIDDEN);
		}
	}
}
