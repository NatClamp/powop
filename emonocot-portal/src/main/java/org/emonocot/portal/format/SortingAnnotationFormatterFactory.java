package org.emonocot.portal.format;

import java.util.HashSet;
import java.util.Set;

import org.emonocot.api.Sorting;
import org.emonocot.portal.format.annotation.SortingFormat;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

/**
 *
 * @author jk00kg
 *
 */
public class SortingAnnotationFormatterFactory implements
        AnnotationFormatterFactory<SortingFormat> {

   /**
    *
    */
   private static final Set<Class<?>> FIELD_TYPES = new HashSet<Class<?>>();

   static {
       FIELD_TYPES.add(Sorting.class);
   }

   /**
    * @return the field types
    */
    public final Set<Class<?>> getFieldTypes() {
        return FIELD_TYPES;
    }

    /**
     * @param sortingFormat Set the sorting format annotation
     * @param fieldType Set the field type
     * @return the parser
     */
    public final Parser<?> getParser(
            final SortingFormat sortingFormat,
            final Class<?> fieldType) {
        return new SortingFormatter();
    }

    /**
     * @param sortingFormat Set the sorting format annotation
     * @param fieldType Set the field type
     * @return the printer
     */
    public final Printer<?> getPrinter(
            final SortingFormat sortingFormat, final Class<?> fieldType) {
        return new SortingFormatter();
    }

}