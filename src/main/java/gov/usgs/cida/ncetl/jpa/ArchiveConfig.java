package gov.usgs.cida.ncetl.jpa;

import java.io.Serializable;
import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * The persistent class for the ARCHIVE_CONFIG database table.
 * 
 */
@Entity
@Table(name="ARCHIVE_CONFIG")
public class ArchiveConfig implements Serializable {
    private static final int MAX_HISTORY_SIZE = 64;

	private static final String XY = "xy";

	public static final String VAR = "var";

	public static final String DIM = "dim";

	private static final String RFC_CODE_REPLACE = "{rfc_code}";

	private static final long serialVersionUID = 1L;
	private int id;
	private String completeDir;
	private String fileRegex;
	private String inputDir;
	private String name;
	private String outputDir;
	private int rfcCode;
	private String unlimitedDim;
	private String unlimitedUnits;
	private Set<ExcludeMapping> excludeMappings;
	private Set<RenameMapping> renameMappings;
	private List<EtlHistory> etlHistories;
	private boolean active;
	
	public ArchiveConfig() {
	}

	private static Logger logger = LoggerFactory.getLogger(ArchiveConfig.class);
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Column(name="COMPLETE_DIR")
	public String getCompleteDir() {
		return this.completeDir;
	}

	public void setCompleteDir(String completeDir) {
		this.completeDir = completeDir;
	}


	@Column(name="FILE_REGEX")
	public String getFileRegex() {
		String re = this.fileRegex;
		return re.replace(RFC_CODE_REPLACE, String.valueOf(rfcCode));
	}

	public void setFileRegex(String fileRegex) {
		this.fileRegex = fileRegex;
	}


	@Column(name="INPUT_DIR")
	public String getInputDir() {
		return this.inputDir;
	}

	public void setInputDir(String inputDir) {
		this.inputDir = inputDir;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Column(name="OUTPUT_DIR")
	public String getOutputDir() {
		return this.outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}


	@Column(name="RFC_CODE")
	public int getRfcCode() {
		return this.rfcCode;
	}

	public void setRfcCode(int rfcCode) {
		this.rfcCode = rfcCode;
	}


	@Column(name="UNLIMITED_DIM")
	public String getUnlimitedDim() {
		return this.unlimitedDim;
	}

	public void setUnlimitedDim(String unlimitedDim) {
		this.unlimitedDim = unlimitedDim;
	}


	@Column(name="UNLIMITED_UNITS")
	public String getUnlimitedUnits() {
		return this.unlimitedUnits;
	}

	public void setUnlimitedUnits(String unlimitedUnits) {
		this.unlimitedUnits = unlimitedUnits;
	}

	@Column(name="ACTIVE")
	public boolean getActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}


	//bi-directional many-to-one association to ExcludeMapping
	@OneToMany(mappedBy="archiveConfig",fetch=FetchType.EAGER)
	public Set<ExcludeMapping> getExcludeMappings() {
		if (excludeMappings == null) {
			excludeMappings = new HashSet<ExcludeMapping>();
		}
		return this.excludeMappings;
	}

	public void setExcludeMappings(Set<ExcludeMapping> excludeMappings) {
		this.excludeMappings = excludeMappings;
	}

	public ExcludeMapping addExcludeMapping(ExcludeMapping excludeMapping) {
		getExcludeMappings().add(excludeMapping);
		excludeMapping.setArchiveConfig(this);

		return excludeMapping;
	}

	public ExcludeMapping removeExcludeMapping(ExcludeMapping excludeMapping) {
		getExcludeMappings().remove(excludeMapping);
		excludeMapping.setArchiveConfig(null);

		return excludeMapping;
	}


	//bi-directional many-to-one association to RenameMapping
	@OneToMany(mappedBy="archiveConfig",fetch=FetchType.EAGER)
	public Set<RenameMapping> getRenameMappings() {
		if (renameMappings == null) {
			renameMappings = new HashSet<RenameMapping>();
		}
		return this.renameMappings;
	}

	public void setRenameMappings(Set<RenameMapping> renameMappings) {
		this.renameMappings = renameMappings;
	}

	public RenameMapping addRenameMapping(RenameMapping renameMapping) {
		getRenameMappings().add(renameMapping);
		renameMapping.setArchiveConfig(this);

		return renameMapping;
	}

	public RenameMapping removeRenameMapping(RenameMapping renameMapping) {
		getRenameMappings().remove(renameMapping);
		renameMapping.setArchiveConfig(null);

		return renameMapping;
	}
	
	//bi-directional many-to-one association to EtlHistory
	@OneToMany(mappedBy="archiveConfig",cascade={CascadeType.ALL},fetch=FetchType.EAGER)
	@OrderBy("ts DESC")
	public List<EtlHistory> getEtlHistories() {
		return this.etlHistories;
	}

	public void setEtlHistories(List<EtlHistory> etlHistories) {
		this.etlHistories = etlHistories;
	}

	public EtlHistory addEtlHistory(EtlHistory etlHistory) {
		getEtlHistories().add(etlHistory);
		etlHistory.setArchiveConfig(this);

		List<EtlHistory> hh = getEtlHistories();
		if (hh.size() > MAX_HISTORY_SIZE) {
			EtlHistory h = hh.get(hh.size()-1);
			removeEtlHistory(h);
		}
		return etlHistory;
	}

	public EtlHistory removeEtlHistory(EtlHistory etlHistory) {
		getEtlHistories().remove(etlHistory);
		etlHistory.setArchiveConfig(null);

		return etlHistory;
	}

	@Transient
	private List<String> getExcludes(String type) {
		List<String> xm = new ArrayList<String>();
		
		for (ExcludeMapping m : getExcludeMappings()) {
			if (m.getExcludeType().getType().equals(type)) {
				xm.add(m.getExcludeText());
			}
		}
		return xm;
	}
	@Transient
	private void setExcludes(String type, List<String> xList) {
		for (String txt : xList) {
			ExcludeMapping xm = new ExcludeMapping();
			// TODO Only for testing; persisting will fail, need to look up by type
			// (or change JPA mapping to treat type as primary key, disallow inserts)
			ExcludeType xt = new ExcludeType();
			xt.setType(type);
			xm.setExcludeType(xt);
			xm.setExcludeText(txt);
			
			addExcludeMapping(xm);
		}
	}

	@Transient
	public List<String> getDim_excludes() {
		
		return getExcludes(DIM);
	}
	@Transient
	public void setDim_excludes(List<String> xList) {
		setExcludes(DIM, xList);
	}

	@Transient
	public List<String> getVar_excludes() {
		return getExcludes(VAR);
	}
	@Transient
	public void setVar_excludes(List<String> xList) {
		setExcludes(VAR, xList);
	}

	@Transient
	public List<String> getXy_excludes() {
		return getExcludes(XY);
	}
	@Transient
	public void setXy_excludes(List<String> xList) {
		setExcludes(XY, xList);
	}

	@Transient
	public Map<String, String> getRenames() {
		Map<String,String> renames = new HashMap<String,String>(renameMappings.size());
		
		for (RenameMapping rn : renameMappings) {
			renames.put(rn.getFromName(), rn.getToName());
		}
		
		return renames;
	}
	@Transient
	public void setRenames(Map<String, String> varMap) {
		for (Map.Entry<String, String> entry : varMap.entrySet()) {
			RenameMapping rm = new RenameMapping();
			rm.setFromName(entry.getKey());
			rm.setToName(entry.getValue());
			
			addRenameMapping(rm);
		}		
	}


	@Transient
	public ArchiveConfig addHistory(String outcome) {
		
		EtlHistory h = new EtlHistory();
		h.setOutcome(outcome);
		
		addEtlHistory(h);
		
		logger.debug("Added history {} to ArchiveConfig#{}", outcome, getId());
		
		return this;
	}


}