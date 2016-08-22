package org.emonocot.model.solr;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

public class SolrFieldNameMappings {

	public static final BiMap<String, String> map = new ImmutableBiMap.Builder<String, String>()
			.put("any", "main.query")
			.put("family", "taxon.family_ss")
			.put("genus", "taxon.genus_ss")
			.put("species", "taxon.species_ss")
			.put("all names", "all.names")
			.put("common names", "taxon.vernacular_names_ss")
			.put("any characteristic", "taxon.description_t")
			.put("appearance", "taxon.description_appearance_t")
			.put("inflorescence", "taxon.description_inflorescence_t")
			.put("fruit", "taxon.description_fruit_t")
			.put("leaves", "taxon.description_leaves_t")
			.put("flowers", "taxon.description_flowers_t")
			.put("location", "taxon.distribution_ss")
			.put("uses", "taxon.description_uses_t")
			.build();
}