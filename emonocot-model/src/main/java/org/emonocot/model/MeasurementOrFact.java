package org.emonocot.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.emonocot.model.constants.MeasurementUnit;
import org.emonocot.model.marshall.json.ConceptTermDeserializer;
import org.emonocot.model.marshall.json.ConceptTermSerializer;
import org.emonocot.model.marshall.json.DateTimeDeserializer;
import org.emonocot.model.marshall.json.DateTimeSerializer;
import org.gbif.dwc.terms.ConceptTerm;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.joda.time.DateTime;

@Entity
public class MeasurementOrFact extends OwnedEntity {
	
	private static final long serialVersionUID = -1400551717852313792L;
	
	/**
	 * 
	 */
	private Long id;
	
	private ConceptTerm measurementType;
	
	private String measurementValue;
	
	private String measurementAccuracy;
	
	private MeasurementUnit measurementUnit;
	
	private DateTime measurementDeterminedDate;
	
	private String measurementDeterminedBy;
	
	private String measurementMethod;
	
	private String measurementRemarks;
	
	private String bibliographicCitation;
	
	private Taxon taxon;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();

	private String source;

	@Id
    @GeneratedValue(generator = "system-increment")
	public Long getId() {
		return id;
	}
	
	@Transient
    @JsonIgnore
    public final String getClassName() {
        return "MeasurementOrFact";
    }

	@JsonSerialize(using = ConceptTermSerializer.class)
	@Type(type="conceptTermUserType")
	public ConceptTerm getMeasurementType() {
		return measurementType;
	}

	@JsonDeserialize(using = ConceptTermDeserializer.class)
	public void setMeasurementType(ConceptTerm measurementType) {
		this.measurementType = measurementType;
	}

	public String getMeasurementValue() {
		return measurementValue;
	}

	public void setMeasurementValue(String measurementValue) {
		this.measurementValue = measurementValue;
	}

	public String getMeasurementAccuracy() {
		return measurementAccuracy;
	}

	public void setMeasurementAccuracy(String measurementAccuracy) {
		this.measurementAccuracy = measurementAccuracy;
	}

	@Enumerated(value = EnumType.STRING)
	public MeasurementUnit getMeasurementUnit() {
		return measurementUnit;
	}

	public void setMeasurementUnit(MeasurementUnit measurementUnit) {
		this.measurementUnit = measurementUnit;
	}

	@Type(type="dateTimeUserType")
	@JsonSerialize(using = DateTimeSerializer.class)
	public DateTime getMeasurementDeterminedDate() {
		return measurementDeterminedDate;
	}

	@JsonDeserialize(using = DateTimeDeserializer.class)
	public void setMeasurementDeterminedDate(DateTime measurementDeterminedDate) {
		this.measurementDeterminedDate = measurementDeterminedDate;
	}

	public String getMeasurementDeterminedBy() {
		return measurementDeterminedBy;
	}

	public void setMeasurementDeterminedBy(String measurementDeterminedBy) {
		this.measurementDeterminedBy = measurementDeterminedBy;
	}

	public String getMeasurementMethod() {
		return measurementMethod;
	}

	public void setMeasurementMethod(String measurementMethod) {
		this.measurementMethod = measurementMethod;
	}

	public String getMeasurementRemarks() {
		return measurementRemarks;
	}

	public void setMeasurementRemarks(String measurementRemarks) {
		this.measurementRemarks = measurementRemarks;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference("measurementsOrFacts-taxon")
	public Taxon getTaxon() {
		return taxon;
	}

	@JsonBackReference("measurementsOrFacts-taxon")
	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "annotatedObjId")
    @Where(clause = "annotatedObjType = 'MeasurementOrFact'")
    @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.DELETE })
    @JsonIgnore
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * @param annotations
     *            the annotations to set
     */
	@JsonIgnore
    public void setAnnotations(Set<Annotation> annotations) {
        this.annotations = annotations;
    }

	/**
	 * @return the bibliographicCitation
	 */
	public String getBibliographicCitation() {
		return bibliographicCitation;
	}

	/**
	 * @param bibliographicCitation the bibliographicCitation to set
	 */
	public void setBibliographicCitation(String bibliographicCitation) {
		this.bibliographicCitation = bibliographicCitation;
	}
	
	@Override
    public String toString() {
    	StringBuffer stringBuffer = new StringBuffer();
    	if(measurementType != null) {
    	    stringBuffer.append(measurementType.toString());
    	}
    	if(measurementValue != null) {
    		stringBuffer.append(": \"" + measurementValue + "\"");
    	}
    	return stringBuffer.toString();
    }

	public void setSource(String source) {
		this.source = source;
	}
	
	public String getSource() {
		return source;
	}
}
