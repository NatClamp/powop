package org.emonocot.model.solr;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.solr.common.SolrInputDocument;
import org.emonocot.api.job.WCSPTerm;
import org.emonocot.model.Description;
import org.emonocot.model.Distribution;
import org.emonocot.model.Identification;
import org.emonocot.model.Identifier;
import org.emonocot.model.Image;
import org.emonocot.model.MeasurementOrFact;
import org.emonocot.model.Reference;
import org.emonocot.model.Taxon;
import org.emonocot.model.TypeAndSpecimen;
import org.emonocot.model.VernacularName;
import org.emonocot.model.constants.Location;
import org.emonocot.pager.FacetName;
import org.gbif.dwc.terms.IucnTerm;
import org.gbif.ecat.voc.Rank;

public class TaxonSolrInputDocument extends BaseSolrInputDocument {
	
	private Taxon taxon;

	public TaxonSolrInputDocument(Taxon taxon) {
		super(taxon);
		this.taxon = taxon;
	}
	
	public SolrInputDocument build() {
		sid.addField("searchable.label_sort", taxon.getScientificName());
		
		if(Rank.FAMILY.equals(taxon.getTaxonRank()) && taxon.getFamily() == null) {
			addField(sid,"taxon.family_ns", taxon.getScientificName());
			addField(sid,"taxon.family_ss", taxon.getScientificName());
		} else {
			addField(sid,"taxon.family_ns", taxon.getFamily());
			addField(sid,"taxon.family_ss", taxon.getFamily());
		}
		if(taxon.getAcceptedNameUsage() != null) {
			addField(sid,"taxon.family_ss", taxon.getAcceptedNameUsage().getFamily());
		}

		addField(sid,FacetName.GENUS.getSolrField(), taxon.getGenus());
		if(taxon.getAcceptedNameUsage() != null) {
			addField(sid,FacetName.GENUS.getSolrField(), taxon.getAcceptedNameUsage().getGenus());
		}
		if(Rank.GENUS == taxon.getTaxonRank() && taxon.getGenus() == null) {
			addField(sid,"taxon.genus_ns", taxon.getScientificName());
			addField(sid,FacetName.GENUS.getSolrField(), taxon.getScientificName());
		} else {
			addField(sid,"taxon.genus_ns", taxon.getGenus());
		}

		addField(sid,"taxon.infraspecific_epithet_s", taxon.getInfraspecificEpithet());
		addField(sid,"taxon.infraspecific_epithet_ns", taxon.getInfraspecificEpithet());
		addField(sid,"taxon.order_s", taxon.getOrder());
		addField(sid,"taxon.scientific_name_t", taxon.getScientificName());
		addField(sid,"taxon.scientific_name_authorship_s", taxon.getScientificNameAuthorship());
		addField(sid,"taxon.specific_epithet_s", taxon.getSpecificEpithet());
		addField(sid,"taxon.specific_epithet_ns", taxon.getSpecificEpithet());

		addField(sid,FacetName.SUBFAMILY.getSolrField(), taxon.getSubfamily());
		if(Rank.Subfamily.equals(taxon.getTaxonRank()) && taxon.getSubfamily() == null) {
			addField(sid,FacetName.SUBFAMILY.getSolrField(), taxon.getScientificName());
		}
		if(taxon.getAcceptedNameUsage() != null) {
			addField(sid,FacetName.SUBFAMILY.getSolrField(), taxon.getAcceptedNameUsage().getSubfamily());
		}

		addField(sid,"taxon.subgenus_s", taxon.getSubgenus());

		addField(sid,FacetName.SUBTRIBE.getSolrField(), taxon.getSubtribe());
		if(Rank.Subtribe.equals(taxon.getTaxonRank()) && taxon.getSubtribe() == null) {
			addField(sid,FacetName.SUBTRIBE.getSolrField(), taxon.getScientificName());
		}
		if(taxon.getAcceptedNameUsage() != null) {
			addField(sid,FacetName.SUBTRIBE.getSolrField(), taxon.getAcceptedNameUsage().getSubtribe());
		}

		addField(sid,"taxon.taxonomic_status_s", ObjectUtils.toString(taxon.getTaxonomicStatus(), null));
		addField(sid,"taxon.taxon_rank_s", ObjectUtils.toString(taxon.getTaxonRank(), null));

		addField(sid,FacetName.TRIBE.getSolrField(), taxon.getTribe());
		if(Rank.Tribe.equals(taxon.getTaxonRank()) && taxon.getTribe() == null) {
			addField(sid,FacetName.TRIBE.getSolrField(), taxon.getScientificName());
		}
		if(taxon.getAcceptedNameUsage() != null) {
			addField(sid,FacetName.TRIBE.getSolrField(), taxon.getAcceptedNameUsage().getTribe());
		}

		sid.addField("taxon.descriptions_not_empty_b", !taxon.getDescriptions().isEmpty());

		for(Description d : taxon.getDescriptions()) {
			Set<String> sources = new HashSet<>();
			if(d.getAuthority() != null) {
				sources.add(d.getAuthority().getIdentifier());
			}
			
			sid.addField("searchable.sources_ss", sources);
		}

		sid.addField("taxon.distribution_not_empty_b", !taxon.getDistribution().isEmpty());
		for(Distribution d : taxon.getDistribution()) {
			sid.addField("taxon.distribution_ss", d.getLocation().getCode());
			switch(d.getLocation().getLevel()) {
			case 0:
				for(Location r : (Set<Location>)d.getLocation().getChildren()) {
					for(Location c : (Set<Location>)r.getChildren()) {
						for(Location l : (Set<Location>)c.getChildren()) {
							indexLocality(l,sid);
						}
					}
				}
				break;
			case 1:
				for(Location c : (Set<Location>)d.getLocation().getChildren()) {
					for(Location l : (Set<Location>)c.getChildren()) {
						indexLocality(l,sid);
					}
				}
				break;
			case 2:
				for(Location l : (Set<Location>)d.getLocation().getChildren()) {
					indexLocality(l,sid);
				}
				break;
			case 3:
				indexLocality(d.getLocation(),sid);
				break;
			default:
				break;
			}

			if(d.getAuthority() != null) {
				sid.addField("searchable.sources_ss", d.getAuthority().getIdentifier());
			}
		}

		sid.addField("taxon.images_not_empty_b", !taxon.getImages().isEmpty());
		for(Image i : taxon.getImages()) {
			if(i != null && i.getAuthority() != null) {
				sid.addField("searchable.sources_ss", i.getAuthority().getIdentifier());
			}
		}

		sid.addField("taxon.references_not_empty_b", !taxon.getReferences().isEmpty());
		for(Reference r : taxon.getReferences()) {
			if(r != null && r.getAuthority() != null) {
				sid.addField("searchable.sources_ss", r.getAuthority().getIdentifier());
			}
		}

		boolean hasTaxonomicPlacement = (taxon.getAcceptedNameUsage() != null || taxon.getParentNameUsage() != null);
		sid.addField("taxon.taxonomic_placement_not_empty_b", hasTaxonomicPlacement);

		sid.addField("taxon.types_and_specimens_not_empty_b", !taxon.getTypesAndSpecimens().isEmpty());
		for(TypeAndSpecimen t : taxon.getTypesAndSpecimens()) {
			if(t != null && t.getAuthority() != null) {
				sid.addField("searchable.sources_ss", t.getAuthority().getIdentifier());
			}
		}

		sid.addField("taxon.identifiers_not_empty_b", !taxon.getIdentifiers().isEmpty());
		for(Identifier i : taxon.getIdentifiers()) {
			if(i.getAuthority() != null) {
				sid.addField("searchable.sources_ss", i.getAuthority().getIdentifier());
			}
		}

		sid.addField("taxon.measurements_or_facts_not_empty_b", !taxon.getMeasurementsOrFacts().isEmpty());
		boolean hasLifeForm = false;
		boolean hasHabitat = false;
		boolean hasThreatStatus = false;
		for(MeasurementOrFact m : taxon.getMeasurementsOrFacts()) {
			sid.addField("taxon.measurement_or_fact_" + m.getMeasurementType().simpleName() + "_txt", m.getMeasurementValue());
			if(m.getMeasurementType().equals(WCSPTerm.Habitat)) {
				hasHabitat = true;
			} else if(m.getMeasurementType().equals(WCSPTerm.Lifeform)) {
				hasLifeForm = true;
			} else if(m.getMeasurementType().equals(IucnTerm.threatStatus)) {
				hasThreatStatus = true;
			}
			if(m.getAuthority() != null) {
				sid.addField("searchable.sources_ss", m.getAuthority().getIdentifier());
			}
		}
		if(!hasLifeForm) {
			sid.addField("taxon.measurement_or_fact_" + WCSPTerm.Lifeform.simpleName() + "_txt", "_NULL_");
		}
		if(!hasHabitat) {
			sid.addField("taxon.measurement_or_fact_" + WCSPTerm.Habitat.simpleName() + "_txt", "_NULL_");
		}
		if(!hasThreatStatus) {
			sid.addField("taxon.measurement_or_fact_" + IucnTerm.threatStatus.simpleName() + "_txt", "_NULL_");
		}

		sid.addField("taxon.vernacular_names_not_empty_b", !taxon.getVernacularNames().isEmpty());
		for(VernacularName v : taxon.getVernacularNames()) {
			if(v.getAuthority() != null) {
				sid.addField("searchable.sources_ss", v.getAuthority().getIdentifier());
			}
		}

		sid.addField("taxon.name_used_b", !taxon.getIdentifications().isEmpty());

		Set<String> usedAt = new HashSet<>();
		for(Identification identification : taxon.getIdentifications()) {
			usedAt.add(identification.getIdentifiedBy());
		}
		for(String used : usedAt) {
			sid.addField("taxon.name_used_at_ss", used);
		}

		sid.addField("taxon.has_data_b", hasUsefulData(sid));
		return sid;
	}
	
	private void indexLocality(Location g, SolrInputDocument sid) {
		if(g.getParent() != null) {
			indexLocality(g.getParent(), sid);
		}
		sid.addField("taxon.distribution_" + g.getPrefix() + "_" + g.getLevel() + "_ss", g.toString());
	}

	/*
	 * Used to determine if a taxon has useful data or if it is just a name
	 */
	private boolean hasUsefulData(SolrInputDocument sid) {
		String[] usefulFields = {
				"taxon.descriptions_not_empty_b",
				"taxon.distribution_not_empty_b",
				"taxon.identifiers_not_empty_b",
				"taxon.images_not_empty_b",
				"taxon.measurements_or_facts_not_empty_b",
				"taxon.name_used_b",
				"taxon.references_not_empty_b",
				"taxon.taxonomic_placement_not_empty_b",
				"taxon.types_and_specimens_not_empty_b",
				"taxon.vernacular_names_not_empty_b"
		};

		for(String field : usefulFields) {
			if((Boolean)sid.getFieldValue(field)) {
				return true;
			}
		}

		return false;
	}
}
