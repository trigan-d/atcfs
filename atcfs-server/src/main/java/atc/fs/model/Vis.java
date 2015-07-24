package atc.fs.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "vis")
public class Vis {
	@Id
	private String id;

	@NotBlank
	private String name;

	@NotBlank
	private String secretKey;

	private Integer maxRequestsPerMinute;

	public Vis() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public Integer getMaxRequestsPerMinute() {
		return maxRequestsPerMinute;
	}

	public void setMaxRequestsPerMinute(Integer maxRequestsPerMinute) {
		this.maxRequestsPerMinute = maxRequestsPerMinute;
	}
}
