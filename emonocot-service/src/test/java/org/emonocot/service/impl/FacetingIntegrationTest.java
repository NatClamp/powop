/*
 * This is eMonocot, a global online biodiversity information resource.
 *
 * Copyright © 2011–2015 The Board of Trustees of the Royal Botanic Gardens, Kew and The University of Oxford
 *
 * eMonocot is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * eMonocot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * The complete text of the GNU Affero General Public License is in the source repository as the file
 * ‘COPYING’.  It is also available from <http://www.gnu.org/licenses/>.
 */
package org.emonocot.service.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import org.apache.solr.client.solrj.SolrQuery;
import org.emonocot.api.AnnotationService;
import org.emonocot.api.ImageService;
import org.emonocot.api.OrganisationService;
import org.emonocot.api.PlaceService;
import org.emonocot.api.SearchableObjectService;
import org.emonocot.api.TaxonService;
import org.emonocot.model.Annotation;
import org.emonocot.model.Image;
import org.emonocot.model.Place;
import org.emonocot.model.SearchableObject;
import org.emonocot.model.Taxon;
import org.emonocot.model.auth.User;
import org.emonocot.model.constants.Location;
import org.emonocot.model.registry.Organisation;
import org.emonocot.pager.Page;
import org.emonocot.persistence.solr.QueryBuilder;
import org.emonocot.test.DataManagementSupport;
import org.gbif.ecat.voc.Rank;
import org.gbif.ecat.voc.TaxonomicStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:META-INF/spring/applicationContext*.xml" })
public class FacetingIntegrationTest extends DataManagementSupport {
	private static Logger logger = LoggerFactory.getLogger(FacetingIntegrationTest.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TaxonService taxonService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private AnnotationService annotationService;

	@Autowired
	private OrganisationService sourceService;

	@Autowired
	private PlaceService placeService;

	@Autowired
	private SearchableObjectService searchableObjectService;

	private UsernamePasswordAuthenticationToken token;

	@Before
	public final void setUp() throws Exception {
		setUpTestData();

		for (Object obj : getSetUp()) {
			if (obj.getClass().equals(Taxon.class)) {
				taxonService.saveOrUpdate((Taxon) obj);
			} else if (obj.getClass().equals(Image.class)) {
				imageService.saveOrUpdate((Image) obj);
			} else if (obj.getClass().equals(Annotation.class)) {
				annotationService.saveOrUpdate((Annotation) obj);
			} else if (obj.getClass().equals(Organisation.class)) {
				sourceService.saveOrUpdate((Organisation) obj);
			} else if (obj.getClass().equals(Place.class)) {
				placeService.saveOrUpdate((Place) obj);
			}
		}
		token = new UsernamePasswordAuthenticationToken("admin@e-monocot.org", "sPePhAz6");
		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		SecurityContextHolder.clearContext();
	}

	@After
	public final void tearDown() throws Exception {
		setSetUp(new ArrayList<Object>());
		token = new UsernamePasswordAuthenticationToken("admin@e-monocot.org", "sPePhAz6");
		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		while (!getTearDown().isEmpty()) {
			Object obj = getTearDown().pop();
			if (obj.getClass().equals(Taxon.class)) {
				taxonService.delete(((Taxon) obj).getIdentifier());
			} else if (obj.getClass().equals(Image.class)) {
				imageService.delete(((Image) obj).getIdentifier());
			} else if (obj.getClass().equals(Annotation.class)) {
				annotationService.delete(((Annotation) obj).getIdentifier());
			} else if (obj.getClass().equals(Organisation.class)) {
				sourceService.delete(((Organisation) obj).getIdentifier());
			} else if (obj.getClass().equals(Place.class)) {
				placeService.delete(((Place) obj).getIdentifier());
			}
		}
		SecurityContextHolder.clearContext();
	}

	@Override
	public final void setUpTestData() {
		Organisation source1 = createSource("test", "http://example.com", "Test Organisation", "test@example.com");
		Organisation source2 = createSource("source2", "http://source2.com", "Test Organisation 2", "test@example2.com");
		Taxon taxon1 = createTaxon("Aus", "1", null, null, "Ausaceae", null,
				null, "(1753)", Rank.GENUS, TaxonomicStatus.Accepted,
				source1, new Location[] {}, new Organisation[] {source1});
		Taxon taxon2 = createTaxon("Aus bus", "2", taxon1, null, "Ausaceae",
				null, null, "(1775)", Rank.SPECIES, TaxonomicStatus.Accepted,
				source1, new Location[] {Location.AUSTRALASIA,
						Location.BRAZIL, Location.CARIBBEAN }, new Organisation[] {source1,source2});
		Taxon taxon3 = createTaxon("Aus ceus", "3", taxon1, null, "Ausaceae",
				null, null, "(1805)", Rank.SPECIES, TaxonomicStatus.Accepted,
				source1, new Location[] {Location.NEW_ZEALAND }, new Organisation[] {source1,source2});
		Taxon taxon4 = createTaxon("Aus deus", "4", null, taxon2, "Ausaceae",
				null, null, "(1895)", Rank.SPECIES, TaxonomicStatus.Synonym,
				source1, new Location[] {}, new Organisation[] {source1});
		Taxon taxon5 = createTaxon("Aus eus", "5", null, taxon3, "Ausaceae",
				null, null, "(1935)", Rank.SPECIES, TaxonomicStatus.Synonym,
				source1, new Location[] {}, new Organisation[] {source1});
		Place place1 = createPlace("gb", "Great Britain");

	}

	@Test
	public final void testSearch() throws Exception {
		SolrQuery query = new QueryBuilder().addParam("main.query", "Aus").build();
		Page<SearchableObject> pager = searchableObjectService.search(query, null);
		assertEquals("there should be five objects saved", (Integer) 5,
				pager.getSize());
	}





	/**
	 *
	 */
	@Test
	public final void testSearchWithSorting() throws Exception {
		SolrQuery query = new QueryBuilder().addParam("main.query", "Au*").build();
		Page<SearchableObject> results = searchableObjectService.search(query, null);

		query = new QueryBuilder().addParam("main.query", "Au*").addParam("sort", "searchable.label_sort").build();
		results = searchableObjectService.search(query, null);
		String[] actual = new String[results.getSize()];
		for (int i = 0; i < results.getSize(); i++) {
			if (results.getRecords().get(i).getClassName().equals("Taxon")) {
				actual[i] = ((Taxon) results.getRecords().get(i)).getScientificName();
			} else {
				actual[i] = ((Image) results.getRecords().get(i)).getTitle();
			}
		}

		String[] expected = new String[] {"Aus",  "Aus bus",
				"Aus ceus", "Aus deus", "Aus eus" };
		assertArrayEquals(expected, actual);
	}
}

