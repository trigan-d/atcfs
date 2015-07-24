package atc.fs.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "access_events", indexes = { @Index(name = "idx_vis_user_request", columnList = "vis_id,vis_user,request_id", unique = true) })
public class AccessEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date date;

	@Column(name="remote_addr")
	private String remoteAddr;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "vis_id")
	private Vis vis;

	@NotBlank
	@Column(name = "vis_user")
	private String visUser;

	@NotBlank
	@Column(name = "request_id")
	private String requestId;

	@Column(name = "record_id")
	private String recordId;

	@Column(name = "file_id")
	private String fileId;

	@Enumerated(EnumType.STRING)
	@NotNull
	private AccessType accessType;

	@Column(name = "old_data", columnDefinition = "TEXT")
	private String oldData;

	public AccessEvent() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Vis getVis() {
		return vis;
	}

	public void setVis(Vis vis) {
		this.vis = vis;
	}

	public AccessType getAccessType() {
		return accessType;
	}

	public void setAccessType(AccessType accessType) {
		this.accessType = accessType;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getVisUser() {
		return visUser;
	}

	public void setVisUser(String visUser) {
		this.visUser = visUser;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getOldData() {
		return oldData;
	}

	public void setOldData(String oldData) {
		this.oldData = oldData;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
}
