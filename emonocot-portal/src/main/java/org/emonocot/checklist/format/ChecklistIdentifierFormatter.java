package org.emonocot.checklist.format;


import java.text.ParseException;
import java.util.Locale;

import org.emonocot.checklist.model.Family;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.Formatter;

/**
 *
 * @author ben
 *
 */
public class ChecklistIdentifierFormatter implements Formatter<Long> {
    
    public static String IDENTIFIER_PREFIX = "urn:kew.org:wcs:taxon:";
   /**
    *
    */
   private static Logger logger
       = LoggerFactory.getLogger(ChecklistIdentifierFormatter.class);

   /**
    * @param value set the long value to print
    * @param locale set the locale
    * @return the formatted identifier
    */
    public final String print(final Long value, final Locale locale) {
        if (value == null) {
            return null;
        } else {
            if (value < 0) {
                return Family.IDENTIFIER_PREFIX + (-1 * value);
            } else {
                return IDENTIFIER_PREFIX + value;
            }
        }
    }

    /**
     * @param string the unparsed identifier
     * @param locale the locale
     * @return the primary key of the object
     * @throws ParseException if we are unable to parse the identifier
     */
    public final Long parse(
            final String string, final Locale locale) throws ParseException {
        logger.debug(string);
        if (string.startsWith(IDENTIFIER_PREFIX)) {
            try {
                return Long.parseLong(string
                        .substring(IDENTIFIER_PREFIX.length()));
            } catch (Exception e) {
                throw new ParseException(string
                        + " is not a valid identifier format",
                        IDENTIFIER_PREFIX.length());
            }
        } else if (string.startsWith(Family.IDENTIFIER_PREFIX)) {
            try {
                return (Long.parseLong(string
                        .substring(Family.IDENTIFIER_PREFIX.length()))) * -1;
            } catch (Exception e) {
                throw new ParseException(string
                        + " is not a valid identifier format",
                        Family.IDENTIFIER_PREFIX.length());
            }
        } else {
            throw new ParseException(string
                    + " is not a valid identifier format", 0);
        }
    }

}
