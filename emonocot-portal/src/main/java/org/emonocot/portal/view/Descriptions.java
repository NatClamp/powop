package org.emonocot.portal.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.emonocot.model.Description;
import org.emonocot.model.Taxon;
import org.emonocot.model.constants.DescriptionType;
import org.emonocot.model.registry.Organisation;

/**
 * Transforms list of descriptions into nested structure for display grouped by
 * source and description type.  E.g,
 * 
 *   FTEA => [
 *   	Habit => Perennial...,
 *   	Leaf  => Basal leaves...,
 *   ],
 *   FWTA => [ ... ]
 */
public class Descriptions {

	public class DescriptionsByType {
		public String type;
		public List<Description> descriptions;

		public DescriptionsByType(String type) {
			this.type = type;
			this.descriptions = new ArrayList<>();
		}
	}

	public class DescriptionsBySource {
		public Organisation source;
		public List<DescriptionsByType> byType;

		public DescriptionsBySource(Organisation source) {
			this.source = source;
			this.byType = new ArrayList<>();
		}
	}

	private Taxon taxon;
	private List<DescriptionsBySource> descriptionsBySource;
	private Set<DescriptionType> descriptionTypes;

	public Descriptions(Taxon taxon, Set<DescriptionType> descriptionTypes) {
		this.taxon = taxon;
		this.descriptionTypes = descriptionTypes;
	}
	
	public Collection<DescriptionsBySource> getBySource() {
		if(descriptionsBySource == null) {
			Map<Organisation, List<Description>> descriptionsByResource = new HashMap<>();
			for(Description description : taxon.getDescriptions()) {
				if(!descriptionsByResource.containsKey(description.getAuthority())) {
					descriptionsByResource.put(description.getAuthority(), new ArrayList<Description>());
				}

				descriptionsByResource.get(description.getAuthority()).add(description);
			}

			descriptionsBySource = new ArrayList<>();
			for(Map.Entry<Organisation, List<Description>> entry : descriptionsByResource.entrySet()) {
				DescriptionsBySource dbs = new DescriptionsBySource(entry.getKey());
				dbs.byType = new ArrayList<>(descriptionsByType(entry.getValue()));
				if(!dbs.byType.isEmpty()){
					descriptionsBySource.add(dbs);
				}
			}
		}
		Comparator<DescriptionsBySource> newestSourceFirst = new Comparator<DescriptionsBySource>() {
			public int compare(DescriptionsBySource dbs1, DescriptionsBySource dbs2) {
				if(dbs1.source.getCreated() != null && dbs2.source.getCreated() != null){
					return dbs2.source.getCreated().compareTo(dbs1.source.getCreated());
				}else if(dbs1.source.getCreated() != null){
					return -1;
				}else if(dbs2.source.getCreated() != null){
					return 1;
				}
				return 0;
			}	
		};
		Collections.sort(descriptionsBySource, newestSourceFirst);
		return descriptionsBySource;
	}

	private Set<DescriptionType> subTypes(List<DescriptionType> descriptionTypes) {
		Set<DescriptionType> subTypes = new HashSet<DescriptionType>();
		for(DescriptionType descriptionType : descriptionTypes) {
			subTypes.addAll(DescriptionType.getAll(descriptionType));
		}
		return subTypes;
	}
	
	private Collection<DescriptionsByType> descriptionsByType(List<Description> descriptions) {
		Map<DescriptionType, DescriptionsByType> byType = new EnumMap<>(DescriptionType.class);
		Comparator<Description> generalDescriptionsFirst = new Comparator<Description>() {
			public int compare(Description d1, Description d2) {
				return d1.getTypes().size() - d2.getTypes().size();
			}
		};

		for(Description description : descriptions) {
			if(descriptionTypes.contains(description.getType())) {
				if(!byType.containsKey(description.getType())) {
					byType.put(description.getType(), new DescriptionsByType(description.getType().toString()));
				}
				byType.get(description.getType()).descriptions.add(description);
			}
		}

		for(DescriptionsByType dbt : byType.values()) {
			Collections.sort(dbt.descriptions, generalDescriptionsFirst);
		}

		return byType.values();
	}
}
