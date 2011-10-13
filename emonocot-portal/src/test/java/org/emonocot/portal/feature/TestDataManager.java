package org.emonocot.portal.feature;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

import org.emonocot.model.common.Base;
import org.emonocot.model.description.Distribution;
import org.emonocot.model.description.Feature;
import org.emonocot.model.description.TextContent;
import org.emonocot.model.geography.GeographicalRegion;
import org.emonocot.model.geography.GeographyConverter;

import org.emonocot.model.media.Image;
import org.emonocot.model.reference.Reference;
import org.emonocot.model.taxon.Rank;
import org.emonocot.model.taxon.Taxon;
import org.emonocot.model.taxon.TaxonomicStatus;
import org.emonocot.model.user.User;

import org.emonocot.api.ImageService;
import org.emonocot.api.ReferenceService;
import org.emonocot.api.TaxonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import cucumber.annotation.After;
import cucumber.annotation.en.Given;



/**
 *
 * @author ben
 *
 */
public class TestDataManager {

    /**
     *
     */
    private GeographyConverter geographyConverter = new GeographyConverter();

    /**
     *
     */
    private Stack<Base> data = new Stack<Base>();

    /**
     *
     */
    private String username;

    /**
     *
     */
    private String password;

    /**
     * @throws IOException if there is a problem loading the properties file
     *
     */
    public TestDataManager() throws IOException {
        Resource propertiesFile = new ClassPathResource(
                "application.properties");
        Properties properties = new Properties();
        properties.load(propertiesFile.getInputStream());
        username = properties.getProperty("functional.test.username", null);
        password = properties.getProperty("functional.test.password", null);
    }

    /**
    *
    */
   @Autowired
   private ImageService imageService;

    /**
     *
     */
    @Autowired
    private TaxonService taxonService;

    /**
     *
     */
    @Autowired
    private ReferenceService referenceService;

    /**
     *
     */
    private Authentication previousAuthentication = null;

    /**
    *
    * @param imageRows set the image rows
    */
   @Given("^there are images with the following properties:$")
    public final void thereAreImagesWithTheFollowingProperties(
            final List<ImageRow> imageRows) {
       enableAuthentication();
        for (ImageRow imageRow : imageRows) {
            Image i = new Image();
            i.setCaption(imageRow.caption);
            i.setUrl(imageRow.url);
            i.setIdentifier(imageRow.identifier);
            imageService.save(i);
            data.push(i);
        }
        disableAuthentication();
    }

   /**
    *
    */
   private void disableAuthentication() {
       SecurityContext securityContext = SecurityContextHolder.getContext();
       securityContext.setAuthentication(previousAuthentication);
    }

   /**
    *
    */
    private void enableAuthentication() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        previousAuthentication = securityContext.getAuthentication();
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        securityContext
          .setAuthentication(
                  new TestAuthentication(user));

    }

/**
    *
    * @param name Set the name
    */
   @Given("^there are no taxa called \"([^\"]*)\"$")
   public final void thereAreNoTaxaCalled(final String name) {
       for (int i = 0; i < data.size(); i++) {
           Base b = data.get(i);
           if (b.getClass().equals(Taxon.class)) {
               Taxon t = (Taxon) b;
               if (name.equals(t.getName())) {
                   fail();
               }
           }
       }
   }

    /**
    *
    * @param taxonRows set the taxon rows
    */
   @Given("^there are taxa with the following properties:$")
   public final void thereAreTaxaWithTheFollowingProperties(
           final List<TaxonRow> taxonRows) {
       enableAuthentication();
       for (TaxonRow taxonRow : taxonRows) {
           Taxon t = new Taxon();
           data.push(t);
           t.setName(taxonRow.name);
           t.setFamily(taxonRow.family);
           t.setIdentifier(taxonRow.identifier);
           if (taxonRow.rank != null) {
               t.setRank(Rank.valueOf(taxonRow.rank));
           }
           if (taxonRow.status != null) {
               t.setStatus(TaxonomicStatus.valueOf(taxonRow.status));
           }
           if (taxonRow.diagnostic != null) {
               createTextualData(t, taxonRow.diagnostic, Feature.diagnostic);
           }
           if (taxonRow.habitat != null) {
               createTextualData(t, taxonRow.habitat, Feature.habitat);
           }
           if (taxonRow.protologue != null) {
               Reference reference = new Reference();
               reference.setIdentifier(taxonRow.protologue);
               t.setProtologue(reference);
           }
           if (taxonRow.image1 != null) {
               Image image = new Image();
               image.setIdentifier(taxonRow.image1);
               t.getImages().add(image);
           }
           if (taxonRow.image2 != null) {
               Image image = new Image();
               image.setIdentifier(taxonRow.image2);
               t.getImages().add(image);
           }
           if (taxonRow.image3 != null) {
               Image image = new Image();
               image.setIdentifier(taxonRow.image3);
               t.getImages().add(image);
           }
           if (taxonRow.distribution1 != null) {
               Distribution distribution = new Distribution();
               GeographicalRegion geographicalRegion
                   = geographyConverter.convert(taxonRow.distribution1);
               distribution.setRegion(geographicalRegion);
               distribution.setTaxon(t);
               t.getDistribution().put(geographicalRegion, distribution);
           }
           if (taxonRow.distribution2 != null) {
               Distribution distribution = new Distribution();
               GeographicalRegion geographicalRegion
                   = geographyConverter.convert(taxonRow.distribution2);
               distribution.setRegion(geographicalRegion);
               distribution.setTaxon(t);
               t.getDistribution().put(geographicalRegion, distribution);
           }
           if (taxonRow.distribution3 != null) {
               Distribution distribution = new Distribution();
               GeographicalRegion geographicalRegion
                   = geographyConverter.convert(taxonRow.distribution3);
               distribution.setRegion(geographicalRegion);
               distribution.setTaxon(t);
               t.getDistribution().put(geographicalRegion, distribution);
           }
           taxonService.save(t);
       }
       disableAuthentication();
   }

   /**
   *
   * @param referenceRows set the reference rows
   */
  @Given("^there are references with the following properties:$")
  public final void thereAreReferencesWithTheFollowingProperties(
          final List<ReferenceRow> referenceRows) {
      enableAuthentication();
      for (ReferenceRow referenceRow : referenceRows) {
          Reference r = new Reference();
          data.push(r);
          r.setIdentifier(referenceRow.identifier);
          r.setTitle(referenceRow.title);
          r.setDatePublished(referenceRow.datePublished);
          r.setVolume(referenceRow.volume);
          r.setPages(referenceRow.page);
          referenceService.save(r);
      }
      disableAuthentication();
  }

  /**
   *
   * @param taxon Set the taxon
   * @param text Set the text
   * @param feature Set the feature
   */
  private void createTextualData(final Taxon taxon, final String text,
          final Feature feature) {
      TextContent textContent = new TextContent();
      textContent.setContent(text);
      textContent.setFeature(feature);
      textContent.setTaxon(taxon);
      taxon.getContent().put(feature, textContent);
  }

    /**
     *
     */
    @After
    public final void tearDown() {
        enableAuthentication();
        while (!data.isEmpty()) {
            Base base = data.pop();
            if (base instanceof Taxon) {
                taxonService.delete(base.getIdentifier());
            } else if (base instanceof Image) {
                imageService.delete(base.getIdentifier());
            } else if (base instanceof Reference) {
                referenceService.delete(base.getIdentifier());
            }
        }
        disableAuthentication();
    }
}