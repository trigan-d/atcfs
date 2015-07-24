package atc.fs.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "tmp_link_access_events")
public class TemporaryLinkAccessEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date date;

	@NotNull
	@Column(name = "file_id")
	private String fileId;

	@NotNull
	@Column(name = "tmp_link_id")
	private String tmpLinkId;

	@Column(name="remote_addr")
	private String remoteAddr;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "creator_vis")
	private Vis creatorVis;

	@Column(name = "creator")
	private String creator;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public String getRemoteAddr()
	{
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr)
	{
		this.remoteAddr = remoteAddr;
	}

	public String getTmpLinkId()
	{
		return tmpLinkId;
	}

	public void setTmpLinkId(String tmpLinkId)
	{
		this.tmpLinkId = tmpLinkId;
	}

	public Vis getCreatorVis()
	{
		return creatorVis;
	}

	public void setCreatorVis(Vis creatorVis)
	{
		this.creatorVis = creatorVis;
	}

	public String getCreator()
	{
		return creator;
	}

	public void setCreator(String creator)
	{
		this.creator = creator;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
}
