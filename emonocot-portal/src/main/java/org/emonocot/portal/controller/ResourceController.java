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
package org.emonocot.portal.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.groups.Default;

import net.java.truevfs.access.TPath;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.emonocot.api.AnnotationService;
import org.emonocot.api.OrganisationService;
import org.emonocot.api.ResourceService;
import org.emonocot.api.job.CouldNotLaunchJobException;
import org.emonocot.api.job.JobExecutionInfo;
import org.emonocot.api.job.ResourceAlreadyBeingHarvestedException;
import org.emonocot.model.Annotation;
import org.emonocot.model.constants.ResourceType;
import org.emonocot.model.constants.SchedulingPeriod;
import org.emonocot.model.registry.Resource;
import org.emonocot.model.registry.Resource.ReadResource;
import org.emonocot.pager.Page;
import org.emonocot.portal.controller.form.ResourceParameterDto;
import org.emonocot.portal.format.annotation.FacetRequestFormat;
import org.emonocot.portal.legacy.OldSearchBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/resource")
public class ResourceController extends GenericController<Resource, ResourceService> {
	private static Logger logger = LoggerFactory.getLogger(ResourceController.class);

	private AnnotationService annotationService;

	private OrganisationService organisationService;

	private JobExplorer jobExplorer;

	@Autowired
	public void setResourceService(ResourceService resourceService) {
		super.setService(resourceService);
	}

	@Autowired
	public void setAnnotationService(AnnotationService annotationService) {
		this.annotationService = annotationService;
	}

	@Autowired
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}

	@Autowired
	public void setJobExplorer(JobExplorer jobExplorer) {
		this.jobExplorer = jobExplorer;
	}

	public ResourceController() {
		super("resource", Resource.class);
	}

	private void populateForm(Model model, Resource resource, ResourceParameterDto parameter) {
		model.addAttribute("resource", resource);
		model.addAttribute("parameter", parameter);
		model.addAttribute("resourceTypes", Arrays.asList(new ResourceType[] {ResourceType.DwC_Archive, ResourceType.GBIF, ResourceType.IUCN}));
		model.addAttribute("schedulingPeriods",Arrays.asList(SchedulingPeriod.values()));
	}

	@RequestMapping(value = "/{resourceId}/output", method = RequestMethod.GET)
	public String search(
			@PathVariable Long resourceId,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
			@RequestParam(value = "facet", required = false) @FacetRequestFormat List<FacetRequest> facets,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "view", required = false) String view,
			Model model) throws SolrServerException, IOException {
		Resource resource = getService().load(resourceId,"job-with-source");
		model.addAttribute("resource", resource);

		Map<String, String> selectedFacets = new HashMap<String, String>();
		if (facets != null && !facets.isEmpty()) {
			for (FacetRequest facetRequest : facets) {
				selectedFacets.put(facetRequest.getFacet(), facetRequest.getSelected());
			}
		}

		selectedFacets.put("base.class_s", "org.emonocot.model.Annotation");
		if(resource.getLastHarvestedJobId() == null) {
			selectedFacets.put("annotation.job_id_l","0");
			String[] codes = new String[] { "resource.not.harvested" };
			Object[] args = new Object[] { resource.getTitle() };
			DefaultMessageSourceResolvable message = new DefaultMessageSourceResolvable(codes, args);
			model.addAttribute("error", message);
		} else {
			selectedFacets.put("annotation.job_id_l", new Long(resource.getLastHarvestedJobId()).toString());
		}
		
		SolrQuery solrQuery =  new OldSearchBuilder().oldSearchBuilder(query, null, limit,
				start, new String[] {
						"annotation.code_s",
						"annotation.type_s",
						"annotation.record_type_s",
						"annotation.job_id_l"
		}, null, selectedFacets, null, "annotated-obj");
		Page<Annotation> result = annotationService.search(solrQuery, "annotated-obj");
		result.putParam("query", query);
		model.addAttribute("result", result);

		return "resource/output";
	}

	@RequestMapping(value = "/{resourceId}", method = RequestMethod.POST, produces = "text/html", params = {"!parameters"})
	public String update(
			@PathVariable Long resourceId, Model model,
			@Validated({Default.class, ReadResource.class}) Resource resource, BindingResult result,
			RedirectAttributes redirectAttributes) {
		Resource persistedResource = getService().load(resourceId);

		if (result.hasErrors()) {
			for(ObjectError objectError : result.getAllErrors()) {
				logger.error(objectError.getDefaultMessage());
			}
			populateForm(model, resource, new ResourceParameterDto());
			return "resource/update";
		}

		persistedResource.setUri(resource.getUri());
		persistedResource.setTitle(resource.getTitle());
		persistedResource.setResourceType(resource.getResourceType());
		persistedResource.setLastHarvested(resource.getLastHarvested());
		persistedResource.setJobId(resource.getJobId());
		persistedResource.setLastHarvestedJobId(resource.getLastHarvestedJobId());
		persistedResource.setStatus(resource.getStatus());
		persistedResource.setStartTime(resource.getStartTime());
		persistedResource.setDuration(resource.getDuration());
		persistedResource.setExitCode(resource.getExitCode());
		persistedResource.setExitDescription(resource.getExitDescription());
		persistedResource.setRecordsRead(resource.getRecordsRead());
		persistedResource.setReadSkip(resource.getReadSkip());
		persistedResource.setProcessSkip(resource.getProcessSkip());
		persistedResource.setWriteSkip(resource.getWriteSkip());
		persistedResource.setWritten(resource.getWritten());
		persistedResource.setParameters(resource.getParameters());
		persistedResource.setScheduled(resource.getScheduled());
		persistedResource.setSchedulingPeriod(resource.getSchedulingPeriod());
		persistedResource.updateNextAvailableDate();

		getService().saveOrUpdate(persistedResource);
		String[] codes = new String[] { "resource.was.updated" };
		Object[] args = new Object[] { resource.getTitle() };
		DefaultMessageSourceResolvable message = new DefaultMessageSourceResolvable(codes, args);
		redirectAttributes.addFlashAttribute("info", message);
		return "redirect:/resource/{resourceId}";
	}

	@RequestMapping(produces = "text/html", method = RequestMethod.GET, params = {"!form"})
	public String list(Model model,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "limit", required = false, defaultValue = "50") Integer limit,
			@RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
			@RequestParam(value = "facet", required = false) @FacetRequestFormat List<FacetRequest> facets,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "view", required = false) String view) throws SolrServerException, IOException {
		Map<String, String> selectedFacets = new HashMap<String, String>();
		if (facets != null && !facets.isEmpty()) {
			for (FacetRequest facetRequest : facets) {
				selectedFacets.put(facetRequest.getFacet(),
						facetRequest.getSelected());
			}
		}
		selectedFacets.put("base.class_searchable_b", "false");
		selectedFacets.put("base.class_s", "org.emonocot.model.registry.Resource");
		SolrQuery solrQuery = new OldSearchBuilder().oldSearchBuilder
		(query, null, limit, start,
				new String[] { "resource.exit_code_s",
						"resource.resource_type_s",
						"resource.scheduled_b",
						"resource.scheduling_period_s",
						"resource.status_s",
						"resource.last_harvested_dt",
						"resource.organisation_s"
		}, null, selectedFacets, sort, null);
		Page<Resource> result = getService().search(solrQuery, null);
		result.putParam("query", query);
		model.addAttribute("result", result);
		return "resource/list";
	}

	@RequestMapping(method = RequestMethod.GET, params = "form")
	public String create(
			@RequestParam(required = true) String organisation,
			Model model) {
		Resource resource = new Resource();
		resource.setOrganisation(organisationService.load(organisation));
		populateForm(model, resource, new ResourceParameterDto());

		return "resource/create";
	}

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String create(Model model,
			@Validated({Default.class, ReadResource.class}) Resource resource,
			BindingResult result,
			RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			populateForm(model, resource, new ResourceParameterDto());
			return "resource/create";
		}

		logger.error("Creating Resource " + resource + " with organisation " + resource.getOrganisation());
		getService().saveOrUpdate(resource);
		getDwcCOntents(resource.getId());
		String[] codes = new String[] { "resource.was.created" };
		Object[] args = new Object[] { resource.getTitle() };
		DefaultMessageSourceResolvable message = new DefaultMessageSourceResolvable(codes, args);
		redirectAttributes.addFlashAttribute("info", message);

		return "redirect:/resource/" + resource.getId();
	}

	@RequestMapping(value = "/{resourceId}", method = RequestMethod.GET, produces = "text/html", params = {"!run", "!form", "!parameters", "!delete"})
	public String show(@PathVariable Long resourceId, Model uiModel) {
		Resource resource = getService().load(resourceId,"job-with-source");
		uiModel.addAttribute("resource", resource);
		return "resource/show";
	}

	@RequestMapping(value = "/{resourceId}", method = RequestMethod.GET, params = "form")
	public String update(
			@PathVariable Long resourceId,
			Model model) {
		Resource resource = getService().load(resourceId,"job-with-source");
		populateForm(model, resource, new ResourceParameterDto());
		return "resource/update";
	}

	@RequestMapping(value = "/{resourceId}", params = { "parameters", "!delete" }, method = RequestMethod.POST)
	public String addParameter(@PathVariable Long resourceId,
			@ModelAttribute("parameter") ResourceParameterDto parameter,
			RedirectAttributes redirectAttributes) {
		Resource resource = getService().load(resourceId);
		resource.getParameters().put(parameter.getName(), "");
		getService().saveOrUpdate(resource);
		String[] codes = new String[] {"parameter.added.to.resource" };
		Object[] args = new Object[] { parameter.getName() };
		DefaultMessageSourceResolvable message = new DefaultMessageSourceResolvable(codes, args);
		redirectAttributes.addFlashAttribute("info", message);
		return "redirect:/resource/{resourceId}?form=true";
	}

	@RequestMapping(value = "/{resourceId}", params = { "parameters", "delete" }, method = RequestMethod.GET)
	public String removeParameter(@PathVariable Long resourceId,
			@RequestParam("name") String name, RedirectAttributes redirectAttributes) {
		Resource resource = getService().load(resourceId);
		resource.getParameters().remove(name);
		getService().saveOrUpdate(resource);
		String[] codes = new String[] {"parameter.removed.from.resource" };
		Object[] args = new Object[] { name };
		DefaultMessageSourceResolvable message = new DefaultMessageSourceResolvable(codes, args);
		redirectAttributes.addFlashAttribute("info", message);
		return "redirect:/resource/{resourceId}?form=true";
	}

	@RequestMapping(value = "/{resourceId}", method = RequestMethod.GET, produces = "text/html", params = "run")
	public String run(
			@PathVariable Long resourceId,
			@RequestParam(required = false, defaultValue = "true") Boolean ifModified,
			Model model,
			RedirectAttributes redirectAttributes) {

		try {
			getService().harvestResource(resourceId, ifModified);
			String[] codes = new String[] { "job.scheduled" };
			Object[] args = new Object[] {};
			DefaultMessageSourceResolvable message = new DefaultMessageSourceResolvable(
					codes, args);
			redirectAttributes.addFlashAttribute("info", message);
			return "redirect:/resource/{resourceId}";
		} catch(ResourceAlreadyBeingHarvestedException rabhe) {
			String[] codes = new String[] { "job.running" };
			Object[] args = new Object[] {};
			DefaultMessageSourceResolvable message = new DefaultMessageSourceResolvable(codes, args);
			redirectAttributes.addFlashAttribute("error", message);
			return "redirect:/resource/" + resourceId;
		} catch (CouldNotLaunchJobException cnlje) {
			String[] codes = new String[] { "job.failed" };
			Object[] args = new Object[] { cnlje.getMessage() };
			DefaultMessageSourceResolvable message = new DefaultMessageSourceResolvable(
					codes, args);
			redirectAttributes.addFlashAttribute("error", message);
			return "redirect:/resource/{resourceId}";
		}

	}

	@RequestMapping(value = "/{resourceId}/progress", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public JobExecutionInfo getProgress(@PathVariable("resourceId") Long resourceId) throws Exception {
		JobExecutionInfo jobExecutionInfo = new JobExecutionInfo();
		Resource resource = getService().load(resourceId,"job-with-source");

		JobExecution jobExecution = jobExplorer.getJobExecution(resource.getJobId());
		if(jobExecution != null) {
			jobExecutionInfo.setStatus(jobExecution.getStatus());
			if(jobExecution.getExitStatus() != null) {
				ExitStatus exitStatus = jobExecution.getExitStatus();
				jobExecutionInfo.setExitCode(exitStatus.getExitCode());
				jobExecutionInfo.setExitDescription(exitStatus.getExitDescription());

				Integer recordsRead = 0;
				Integer readSkip = 0;
				Integer processSkip = 0;
				Integer writeSkip = 0;
				Integer written = 0;
				for(StepExecution stepExecution : jobExecution.getStepExecutions()) {
					recordsRead += stepExecution.getReadCount();
					readSkip += stepExecution.getReadSkipCount();
					processSkip += stepExecution.getProcessSkipCount();
					writeSkip += stepExecution.getWriteSkipCount();
					written += stepExecution.getWriteCount();
				}
				jobExecutionInfo.setRecordsRead(recordsRead);
				jobExecutionInfo.setReadSkip(readSkip);
				jobExecutionInfo.setProcessSkip(processSkip);
				jobExecutionInfo.setWriteSkip(writeSkip);
				jobExecutionInfo.setWritten(written);
			}
			Float total = new Float(0);

			switch(jobExecution.getJobInstance().getJobName()) {
			case "DarwinCoreArchiveHarvesting":
				total = new Float(43);
				break;
			case "IUCNImport":
				total = new Float(11);
				break;
			case "GBIFImport":
				total = new Float(10);
				break;
			default:
				break;
			}

			Float steps = new Float(jobExecution.getStepExecutions().size());
			jobExecutionInfo.setProgress(Math.round((steps/ total) * 100f));
		}
		return jobExecutionInfo;
	}

	@RequestMapping(value = "/{resource_id}",  method = RequestMethod.GET, params = "delete", produces = "text/html")
	public String delete(@PathVariable Long resource_id, RedirectAttributes redirectAttributes){
		Resource resource = getService().find(resource_id);
		if(resource.getExitCode() != null && resource.getExitCode().equals("RECORDS DELETED")){
			getService().deleteById(resource_id);
			String[] codes = new String[] { "resource.deleted" };
			Object[] args = new Object[] { resource.getTitle() };
			DefaultMessageSourceResolvable message = new DefaultMessageSourceResolvable(codes, args);
			redirectAttributes.addFlashAttribute("info", message);
			return "redirect:/resource/";
		}
		try{
			getService().deleteResourceRecords(resource_id);
		}catch(ResourceAlreadyBeingHarvestedException rabhe) {
			String[] codes = new String[] { "job.running" };
			Object[] args = new Object[] {};
			DefaultMessageSourceResolvable message = new DefaultMessageSourceResolvable(codes, args);
			redirectAttributes.addFlashAttribute("error", "Could not delete -harvest in progress");
			return "redirect:/resource/";

		} catch (CouldNotLaunchJobException cnlje) {
			String[] codes = new String[] { "job.failed" };
			Object[] args = new Object[] { cnlje.getMessage() };
			DefaultMessageSourceResolvable message = new DefaultMessageSourceResolvable(
					codes, args);
			redirectAttributes.addFlashAttribute("error", message);
			return "redirect:/resource/";
		}
		return "redirect:/resource/";
	}

	public String getDwcCOntents(Long resource_id){
		Resource resource = getService().find(resource_id);
		Map<String,String> parameters = new HashMap<String, String>();
		if(resource.getResourceType() == ResourceType.DwC_Archive){
			String uri = resource.getUri();
			logger.debug("uri of dwc is " + uri);
			try {
				Path path = new TPath(new URI(uri + "/meta.xml"));
				logger.debug("uri of meta.xml is" +  path.toUri().getPath());
				try (InputStream in = Files.newInputStream(path)){
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line = reader.readLine();
					while(line != null){
						String[] words = line.split(" ");
						for(String word : words){
							if(word.startsWith("rowType=")){
								logger.debug("Rowtype of metaxml is " + word);
								if(word.contains("Taxon")){
									parameters.put("taxon.processing.mode", "SKIP_TAXA");
								}
								if(word.contains("Description")){
									parameters.put("description.processing.mode", "IMPORT_DESCRIPTIONS");
								}
								if(word.contains("Distribution")){
									parameters.put("distribution.processing.mode", "IMPORT_DISTRIBUTION");
								}
								if(word.contains("Reference")){
									parameters.put("reference.processing.mode", "IMPORT_REFERENCES");
								}
								if(word.contains("Identification")){
									parameters.put("identification.processing.mode", "IMPORT_IDENTIFICATIONS");
								}
							}
						}
						line = reader.readLine();
					}
				} catch (IOException e) {
					logger.debug("the file at " + uri + "could not be read");
					resource.setExitCode("COULD NOT GET PARAMETERS");
					resource.setExitDescription("The Darwin core archive parameters cannot be read."
							+ " Please check that the file is available at the specified location, or enter parameters manually");
				}
			} catch (URISyntaxException e) {
				logger.debug("the uri " + uri + "is invalid");
				resource.setExitCode("Invalid URI");
				getService().saveOrUpdate(resource);
				return null;
			}
		}
		resource.setParameters(parameters);
		resource.setExitCode("DEFAULT PARAMETERS SET");
		getService().saveOrUpdate(resource);
		return null;
	}
}