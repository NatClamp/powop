package org.emonocot.pager;

/**
 *
 * @author ben
 *
 */
public enum FacetName {

    CLASS("base.class_s", false),
    ORDER("taxon.order_s", false),
    FAMILY("taxon.family_ss", false),
    SUBFAMILY("taxon.subfamily_s", true),
    TRIBE("taxon.tribe_s", true),
    SUBTRIBE("taxon.subtribe_s", true),
    GENUS("taxon.genus_s", false),
    SPECIES("taxon.specific_epithet_s", false),
    CONTINENT("taxon.distribution_TDWG_0_ss", false),
    REGION("taxon.distribution_TDWG_1_ss", false),
    SOURCE("searchable.sources_ss", false),
    AUTHORITY("base.authority_s", false),
    RANK("taxon.taxon_rank_s", false),
    TAXONOMIC_STATUS("taxon.taxonomic_status_s", false),
    CONSERVATION_STATUS("taxon.measurement_or_fact_threatStatus_txt", false),
    LIFE_FORM("taxon.measurement_or_fact_Lifeform_txt", false),
    HABITAT("taxon.measurement_or_fact_Habitat_txt", false),
    NAME_PUBLISHED_IN_YEAR("taxon.name_published_in_year_i", false),
    RECORD_TYPE("annotation.record_type_s", false),
    TYPE("annotation.type_s", false),
    CODE("annotation.code_s", false),
    JOB_ID("annotation.job_id_l", false),
    EXIT_CODE("resource.exit_code_s", false),
    RESOURCE_TYPE("resource.resource_type_s", false),
    SCHEDULED("resource.scheduled_b", false),
    SCHEDULING_PERIOD("resource.scheduling_period_s", false),
    RESOURCE_STATUS("resource.status_s", false),
    RESOURCE_ORGANISATION("resource.organisation_s", false),
    LAST_HARVESTED("resource.last_harvested_dt", false),
    COMMENT_SUBJECT("comment.subject_s", false),
    COMMENT_PAGE_TYPE("comment.comment_page_class_s", false);

    private FacetName(String solrField, boolean includeMissing) {
        this.solrField = solrField;
        this.includeMissing = includeMissing;
    }

    private String solrField;
    
    private boolean includeMissing;
    
	/**
     * @return the solrField
     */
    public String getSolrField() {
        return solrField;
    }

    /**
     * @return the includeMissing
     */
    public boolean isIncludeMissing() {
        return includeMissing;
    }

    public static FacetName fromString(String string) {
		for(FacetName facetName : FacetName.values()) {
			if(facetName.solrField.equals(string)) {
				return facetName;
			}
		}
		throw new IllegalArgumentException(string + " is not a valid value for a facet");
	}

}
