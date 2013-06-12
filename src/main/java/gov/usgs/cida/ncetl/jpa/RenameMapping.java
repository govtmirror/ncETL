package gov.usgs.cida.ncetl.jpa;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the RENAME_MAPPING database table.
 * 
 */
@Entity
@Table(name="RENAME_MAPPING")
public class RenameMapping implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String fromName;
	private String toName;
	private ArchiveConfig archiveConfig;

	public RenameMapping() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Column(name="FROM_NAME")
	public String getFromName() {
		return this.fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}


	@Column(name="TO_NAME")
	public String getToName() {
		return this.toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fromName == null) ? 0 : fromName.hashCode());
		result = prime * result + id;
		result = prime * result + ((toName == null) ? 0 : toName.hashCode());
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
		RenameMapping other = (RenameMapping) obj;
		if (id != 0 && id == other.id) {
			return true;
		}
		if (fromName == null) {
			if (other.fromName != null)
				return false;
		} else if (!fromName.equals(other.fromName))
			return false;
		if (toName == null) {
			if (other.toName != null)
				return false;
		} else if (!toName.equals(other.toName))
			return false;
		return true;
	}

	
}