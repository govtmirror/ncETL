package gov.usgs.cida.ncetl.jpa;

import java.io.Serializable;
import javax.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The persistent class for the ARCHIVE_CONFIG database table.
 * 
 */
@Entity
@Table(name="ARCHIVE_CONFIG")
public class ArchiveConfig implements Serializable {
    private static final String RFC_CODE_REPLACE = "{rfc_code}";

	private static final long serialVersionUID = 1L;
	private int id;
	private String completeDir;
	private String fileRegex;
	private String inputDir;
	private String outputDir;
	private int rfcCode;
	private String unlimitedDim;
	private String unlimitedUnits;
	private List<ExcludeMapping> excludeMappings;
	private List<RenameMapping> renameMappings;

	public ArchiveConfig() {
	}


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


	//bi-directional many-to-one association to ExcludeMapping
	@OneToMany(mappedBy="archiveConfig")
	public List<ExcludeMapping> getExcludeMappings() {
		return this.excludeMappings;
	}

	public void setExcludeMappings(List<ExcludeMapping> excludeMappings) {
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
	@OneToMany(mappedBy="archiveConfig")
	public List<RenameMapping> getRenameMappings() {
		return this.renameMappings;
	}

	public void setRenameMappings(List<RenameMapping> renameMappings) {
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
	
	@Transient
	public List<String> getDim_excludes() {
		
		return getExcludes("dim");
	}

	@Transient
	private List<String> getExcludes(String type) {
		List<String> xm = new ArrayList<String>();
		
		for (ExcludeMapping m : excludeMappings) {
			if (m.getExcludeType().getType().equals(type)) {
				xm.add(m.getExcludeText());
			}
		}
		return xm;
	}


	@Transient
	public List<String> getVar_excludes() {
		return getExcludes("var");
	}


	@Transient
	public List<String> getXy_excludes() {
		return getExcludes("xy");
	}
	
	@Transient
	public Map<String, String> getRenames() {
		Map<String,String> renames = new HashMap<String,String>(renameMappings.size());
		
		for (RenameMapping rn : renameMappings) {
			renames.put(rn.getFromName(), rn.getToName());
		}
		
		return renames;
	}


}