package atc.fs.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotBlank;

import atc.fs.common.AtcFileDto;

@Entity
@Table(name = "files", indexes = { @Index(name = "idx_record_file_name", columnList = "record_id,file_name") })
public class AtcFile {
	@Id
	private String id;

	@Column(name = "file_name")
	@NotBlank
	private String fileName;

	@Column(name = "content_type")
	private String contentType;

	private long size;

	@NotBlank
	private String path;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "record_id")
	private Record record;

	public AtcFile() {
	}

	@Transient
	public AtcFileDto toDto() {
		AtcFileDto dto = new AtcFileDto();
		dto.setId(id);
		dto.setContentType(contentType);
		dto.setFileName(fileName);
		dto.setSize(size);
		return dto;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Record getRecord() {
		return record;
	}

	public void setRecord(Record record) {
		this.record = record;
	}
}
