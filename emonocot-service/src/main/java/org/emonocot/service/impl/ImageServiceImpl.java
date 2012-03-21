package org.emonocot.service.impl;

import org.emonocot.api.ImageService;
import org.emonocot.model.media.Image;
import org.emonocot.persistence.dao.ImageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ben
 *
 */
@Service
public class ImageServiceImpl extends SearchableServiceImpl<Image, ImageDao>
        implements ImageService {

    /**
     *
     * @param newImageDao Set the image dao
     */
    @Autowired
    public final void setImageDao(final ImageDao newImageDao) {
        super.dao = newImageDao;
    }

    /**
     * @param url Set the url
     * @return an image or null if one doesn't exist
     */
    @Transactional(readOnly = true)
    public final Image findByUrl(final String url) {
        return dao.findByUrl(url);
    }

}
