package atc.fs.service;

import atc.fs.model.TmpLink;
import atc.fs.model.Vis;
import org.springframework.stereotype.Service;

import atc.fs.model.AtcFile;

@Service
public interface TmpFilelinksService {
	public String createTmpLink(AtcFile atcFile, Vis vis, String visUser);

	public TmpLink findTmpLinkByTmpLinkId(String tmpLinkId) throws TmpLinkExpiredException;
}
