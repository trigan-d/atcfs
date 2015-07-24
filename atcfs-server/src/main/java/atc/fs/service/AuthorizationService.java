package atc.fs.service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import atc.fs.common.SecurityUtils;
import atc.fs.model.AccessEvent;
import atc.fs.model.AccessType;
import atc.fs.model.TemporaryLinkAccessEvent;
import atc.fs.model.TmpLink;
import atc.fs.model.Vis;
import atc.fs.repository.AccessEventRepository;
import atc.fs.repository.TemporaryLinkAccessEventRepository;
import atc.fs.repository.VisRepository;

@Service
public class AuthorizationService {
	@Autowired
	private VisRepository visRepository;

	@Autowired
	private AccessEventRepository accessEventRepository;

	@Autowired
	private TemporaryLinkAccessEventRepository temporaryLinkAccessEventRepository;

	private Map<String, ConcurrentLinkedDeque<Long>> requestsTimeStamps = new ConcurrentHashMap<>();

	public AccessEvent authorizeAndLogAccess(HttpServletRequest request,
			AccessType accessType, String recordId, String fileId)
			throws AtcFsSecurityException, AtcFsTooManyRequestsException {
		String visId = request.getHeader(SecurityUtils.VIS_ID_HEADER);
		String visUser = request.getHeader(SecurityUtils.VIS_USER_HEADER);
		String requestId = request.getHeader(SecurityUtils.REQUEST_ID_HEADER);
		String sign = request.getHeader(SecurityUtils.SIGN_HEADER);

		if (StringUtils.isEmpty(visId) || StringUtils.isEmpty(visUser)
				|| StringUtils.isEmpty(requestId) || StringUtils.isEmpty(sign)) {
			throw new AtcFsSecurityException(
					"Http headers for authentication not set properly.");
		}

		Vis vis = visRepository.findOne(visId);
		if (vis == null) {
			throw new AtcFsSecurityException("Unknown vis ID: " + visId);
		}
		assureVisIsNotFlooding(visId, vis.getMaxRequestsPerMinute());

		if (!sign.equals(SecurityUtils.generateRequestSignature(visId, visUser,
				requestId, vis.getSecretKey()))) {
			throw new AtcFsSecurityException("Wrong request signature");
		}

		AccessEvent accessEvent = new AccessEvent();
		accessEvent.setAccessType(accessType);
		accessEvent.setDate(new Date());
		accessEvent.setVis(vis);
		accessEvent.setVisUser(visUser);
		accessEvent.setRequestId(requestId);
		accessEvent.setRecordId(recordId);
		accessEvent.setFileId(fileId);
		accessEvent.setRemoteAddr(request.getRemoteAddr());

		try {
			return accessEventRepository.save(accessEvent);
		} catch (DataIntegrityViolationException e) {
			throw new AtcFsSecurityException(
					"Duplicate requestId for that vis and user");
		}
		/*
		 * } catch (UnexpectedRollbackException ex) { if
		 * (ex.getMostSpecificCause() instanceof
		 * SQLIntegrityConstraintViolationException) { throw new
		 * AtcFsSecurityException( "Duplicate requestId for that vis and user");
		 * } else { throw ex; } }
		 */
	}

	public TemporaryLinkAccessEvent logTmpLinkAccess(HttpServletRequest request, TmpLink tmpLink)
	{
		TemporaryLinkAccessEvent temporaryLinkAccessEvent = new TemporaryLinkAccessEvent();

		temporaryLinkAccessEvent.setTmpLinkId(tmpLink.getId());
		temporaryLinkAccessEvent.setDate(new Date());
		temporaryLinkAccessEvent.setRemoteAddr(request.getRemoteAddr());
		temporaryLinkAccessEvent.setCreator(tmpLink.getVisUser());
		temporaryLinkAccessEvent.setCreatorVis(tmpLink.getVis());
		temporaryLinkAccessEvent.setFileId(tmpLink.getAtcFile().getId());

		return temporaryLinkAccessEventRepository.save(temporaryLinkAccessEvent);
	}

	private void assureVisIsNotFlooding(String visId, Integer maxRequestsPerMinute) throws AtcFsTooManyRequestsException {
		if (maxRequestsPerMinute != null && maxRequestsPerMinute > 0) {
			Long currentMillis = System.currentTimeMillis();
			if (!requestsTimeStamps.containsKey(visId)) {
				requestsTimeStamps.put(visId, new ConcurrentLinkedDeque<Long>());
			}
			ConcurrentLinkedDeque<Long> deque = requestsTimeStamps.get(visId);
			while (deque.size() > 0 && deque.peekFirst() < currentMillis - 60000) {
				deque.pollFirst();
			}

			if (deque.size() >= maxRequestsPerMinute) {
				throw new AtcFsTooManyRequestsException();
			}
			deque.addLast(currentMillis);
		}
	}
}
