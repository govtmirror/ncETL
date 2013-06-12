package gov.usgs.cida.ncetl.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the EXCLUDE_TYPE database table.
 * 
 */
@Entity
@Table(name="EXCLUDE_TYPE")
public class ExcludeType implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String type;
	private List<ExcludeMapping> excludeMappings;

	public ExcludeType() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}


	//bi-directional many-to-one association to ExcludeMapping
	@OneToMany(mappedBy="excludeType")
	public List<ExcludeMapping> getExcludeMappings() {
		return this.excludeMappings;
	}

	public void setExcludeMappings(List<ExcludeMapping> excludeMappings) {
		this.excludeMappings = excludeMappings;
	}

	public ExcludeMapping addExcludeMapping(ExcludeMapping excludeMapping) {
		getExcludeMappings().add(excludeMapping);
		excludeMapping.setExcludeType(this);

		return excludeMapping;
	}

	public ExcludeMapping removeExcludeMapping(ExcludeMapping excludeMapping) {
		getExcludeMappings().remove(excludeMapping);
		excludeMapping.setExcludeType(null);

		return excludeMapping;
	}

}