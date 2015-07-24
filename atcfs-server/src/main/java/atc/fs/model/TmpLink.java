package atc.fs.model;

/**
 * Created by mgtriffid on 16.04.2014.
 */
public class TmpLink {
	Vis vis;

	String visUser;

	String id;

	AtcFile atcFile;

	public Vis getVis() {
		return vis;
	}

	public void setVis(Vis vis) {
		this.vis = vis;
	}

	public String getVisUser() {
		return visUser;
	}

	public void setVisUser(String visUser) {
		this.visUser = visUser;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AtcFile getAtcFile() {
		return atcFile;
	}

	public void setAtcFile(AtcFile atcFile) {
		this.atcFile = atcFile;
	}
}
