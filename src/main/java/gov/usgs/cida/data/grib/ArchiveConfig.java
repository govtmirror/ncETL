package gov.usgs.cida.data.grib;

import java.util.List;
import java.util.Map;

public class ArchiveConfig {
	private String input_dir;
	private String output_dir;
	private String complete_dir;
	private int rfc_code;
	private String file_regex;
	private String unlim_dim;
	private String unlim_units;
	
	private List<String> dim_excludes;
	private List<String> var_excludes;
	private List<String> xy_excludes;
	private Map<String,String> renames;
	public String getInput_dir() {
		return input_dir;
	}
	public void setInput_dir(String input_dir) {
		this.input_dir = input_dir;
	}
	public String getOutput_dir() {
		return output_dir;
	}
	public void setOutput_dir(String output_dir) {
		this.output_dir = output_dir;
	}
	public String getComplete_dir() {
		return complete_dir;
	}
	public void setComplete_dir(String complete_dir) {
		this.complete_dir = complete_dir;
	}
	public int getRfc_code() {
		return rfc_code;
	}
	public void setRfc_code(int rfc_code) {
		this.rfc_code = rfc_code;
	}
	public String getFile_regex() {
		return file_regex;
	}
	public void setFile_regex(String file_regex) {
		this.file_regex = file_regex;
	}
	public String getUnlim_dim() {
		return unlim_dim;
	}
	public void setUnlim_dim(String unlim_dim) {
		this.unlim_dim = unlim_dim;
	}
	public String getUnlim_units() {
		return unlim_units;
	}
	public void setUnlim_units(String unlim_units) {
		this.unlim_units = unlim_units;
	}
	public List<String> getDim_excludes() {
		return dim_excludes;
	}
	public void setDim_excludes(List<String> dim_excludes) {
		this.dim_excludes = dim_excludes;
	}
	public List<String> getVar_excludes() {
		return var_excludes;
	}
	public void setVar_excludes(List<String> var_excludes) {
		this.var_excludes = var_excludes;
	}
	public List<String> getXy_excludes() {
		return xy_excludes;
	}
	public void setXy_excludes(List<String> xy_excludes) {
		this.xy_excludes = xy_excludes;
	}
	public Map<String, String> getRenames() {
		return renames;
	}
	public void setRenames(Map<String, String> renames) {
		this.renames = renames;
	}
	
	
	
}
