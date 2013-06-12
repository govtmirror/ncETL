package gov.usgs.cida.ncetl.jpa;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the EXCLUDE_MAPPING database table.
 * 
 */
@Entity
@Table(name="EXCLUDE_MAPPING")
public class ExcludeMapping implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String excludeText;
	private ArchiveConfig archiveConfig;
	private ExcludeType excludeType;

	public ExcludeMapping() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Column(name="EXCLUDE_TEXT")
	public String getExcludeText() {
		return this.excludeText;
	}

	public void setExcludeText(String excludeText) {
		this.excludeText = excludeText;
	}

	//bi-directional many-to-one association to ArchiveConfig
	@ManyToOne
	@JoinColumn(name="ARCHIVE_ID")
	public ArchiveConfig getArchiveConfig() {
		return this.archiveConfig;
	}

	public void setArchiveConfig(ArchiveConfig archiveConfig) {
		this.archiveConfig = archiveConfig;
	}


	//bi-directional many-to-one association to ExcludeType
	@ManyToOne
	@JoinColumn(name="EXCLUDE_TYPE_ID")
	public ExcludeType getExcludeType() {
		return this.excludeType;
	}

	public void setExcludeType(ExcludeType excludeType) {
		this.excludeType = excludeType;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((excludeText == null) ? 0 : excludeText.hashCode());
		result = prime * result
				+ ((excludeType == null) ? 0 : excludeType.hashCode());
		result = prime * result + id;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		ExcludeMapping other = (ExcludeMapping) obj;
		if (id != 0 && id == other.id) {
			return true;
		}
		if (excludeType == null) {
			if (other.excludeType != null)
				return false;
		} else if (!excludeType.equals(other.excludeType))
			return false;
		if (excludeText == null) {
			if (other.excludeText != null)
				return false;
		} else if (!excludeText.equals(other.excludeText))
			return false;
		return true;
	}

}