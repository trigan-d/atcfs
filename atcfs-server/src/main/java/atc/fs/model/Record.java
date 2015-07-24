package atc.fs.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "records")
public class Record {
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(columnDefinition = "TEXT")
	private String data;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "record", cascade = CascadeType.REMOVE)
	private Set<AtcFile> files;

	public Record() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Set<AtcFile> getFiles() {
		return files;
	}

	public void setFiles(Set<AtcFile> files) {
		this.files = files;
	}
}
