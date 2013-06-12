package gov.usgs.cida.ncetl.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the ETL_HISTORY database table.
 * 
 */
@Entity
@Table(name="ETL_HISTORY")
public class EtlHistory implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String outcome;
	private Timestamp ts;
	private ArchiveConfig archiveConfig;
	
	public static final int COLUMN_SIZE = 255;

	public EtlHistory() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getOutcome() {
		return this.outcome;
	}

	public static String trimOutcome(String outcome) {
		if (outcome != null && outcome.length() > COLUMN_SIZE) {
			outcome = outcome.substring(0, COLUMN_SIZE-1);
		}
		return outcome;
	}
	
	public void setOutcome(String outcome) {
		this.outcome = trimOutcome(outcome);
	}

	@Column(insertable=false, updatable=false)
	public Timestamp getTs() {
		return this.ts;
	}

	public void setTs(Timestamp ts) {
		this.ts = ts;
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

}