package atc.fs.service.impl;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import atc.fs.model.TmpLink;
import atc.fs.model.Vis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import atc.fs.model.AtcFile;
import atc.fs.service.TmpFilelinksService;
import atc.fs.service.TmpLinkExpiredException;

@Service
public class GuavaCachedTmpFilelinksService implements TmpFilelinksService {
	@Value("${tmplinks.lifetime.seconds}")
	private long linksLifetime;

	private Cache<String, TmpLink> cache;

	@PostConstruct
	public void initCache() {
		cache = CacheBuilder.newBuilder()
				.expireAfterWrite(linksLifetime, TimeUnit.SECONDS).build();
	}

	@Override
	public String createTmpLink(AtcFile atcFile, Vis vis, String visUser) {
		String tmpLinkId = UUID.randomUUID().toString();
		TmpLink tmpLink = new TmpLink();
		tmpLink.setId(tmpLinkId);
		tmpLink.setVis(vis);
		tmpLink.setVisUser(visUser);
		tmpLink.setAtcFile(atcFile);
		cache.put(tmpLinkId, tmpLink);
		return tmpLinkId;
	}

	@Override
	public TmpLink findTmpLinkByTmpLinkId(String tmpLinkId)
			throws TmpLinkExpiredException {
		return cache.getIfPresent(tmpLinkId);
	}
}
