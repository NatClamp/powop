package org.emonocot.model.source;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.emonocot.model.common.BaseData;
import org.emonocot.model.job.Job;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

/**
 * Class that represents the authority an object is harvested from.
 *
 * @author ben
 *
 */
@Entity
@Indexed
public class Source extends BaseData {

    /**
     *
     */
    private static long serialVersionUID = -2463044801110563816L;

    /**
    *
    */
    private String uri;

    /**
    *
    */
    private Long id;

    /**
    *
    */
    private String creatorEmail;

    /**
    *
    */
    private String description;

    /**
    *
    */
    private String logoUrl;

    /**
    *
    */
    private String publisherName;

    /**
    *
    */
    private String publisherEmail;

    /**
    *
    */
    private String subject;

   /**
    *
    */
    private String title;

    /**
     *
     */
    private Set<Job> jobs;

    /**
     *
     * @param newId
     *            Set the identifier of this object.
     */
    public void setId(Long newId) {
        this.id = newId;
    }

    /**
     *
     * @return Get the identifier for this object.
     */
    @Id
    @GeneratedValue(generator = "system-increment")
    @DocumentId
    public Long getId() {
        return id;
    }

    /**
     * @return the uri
     */
    @URL
    public String getUri() {
        return uri;
    }

    /**
     * @param newUri
     *            the uri to set
     */
    public void setUri(String newUri) {
        this.uri = newUri;
    }

    /**
     *
     * @return the class name
     */
    @Transient
    @JsonIgnore
    public String getClassName() {
        return "Source";
    }

    /**
     * @return the creatorEmail
     */
    public String getCreatorEmail() {
        return creatorEmail;
    }

    /**
     * @param newCreatorEmail the creatorEmail to set
     */
    @Email
    public void setCreatorEmail(String newCreatorEmail) {
        this.creatorEmail = newCreatorEmail;
    }

    /**
     * @return the description
     */
    @Lob
    @Field
    @Length(max = 1431655761)
    public String getDescription() {
        return description;
    }

    /**
     * @param newDescription the description to set
     */
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    /**
     * @return the logoUrl
     */
    @URL
    public String getLogoUrl() {
        return logoUrl;
    }

    /**
     * @param logoUrl the logoUrl to set
     */
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    /**
     * @return the publisherName
     */
    @Field
    public String getPublisherName() {
        return publisherName;
    }

    /**
     * @param newPublisherName the publisherName to set
     */
    public void setPublisherName(String newPublisherName) {
        this.publisherName = newPublisherName;
    }

    /**
     * @return the publisherEmail
     */
    public String getPublisherEmail() {
        return publisherEmail;
    }

    /**
     * @param newPublisherEmail the publisherEmail to set
     */
    public void setPublisherEmail(String newPublisherEmail) {
        this.publisherEmail = newPublisherEmail;
    }

    /**
     * @return the subject
     */
    @Field
    public String getSubject() {
        return subject;
    }

    /**
     * @param newSubject the subject to set
     */
    public void setSubject(String newSubject) {
        this.subject = newSubject;
    }

    /**
     * @return the title
     */
    @Field
    public String getTitle() {
        return title;
    }

    /**
     * @param newTitle the title to set
     */
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    /**
     * @return the jobs
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "source")
    public Set<Job> getJobs() {
        return jobs;
    }

    /**
     * @param jobs the jobs to set
     */
    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }
}
