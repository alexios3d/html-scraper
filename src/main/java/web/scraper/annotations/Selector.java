package web.scraper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Helps {@link web.scraper.HtmlScraper} retrieve HTML element(s). It also supports nested beans so if you need to
 * map a nested bean then setting the {@link Selector#clazz()} attribute is required.
 * When the {@link web.scraper.HtmlScraper} traverses element(s) it will use {@link Selector} definitions on the
 * nested bean to map values to the containing properties. This process is recursive.
 *
 * @author Alex Lambrinos
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Selector {
    /**
     * The jQuery selector to use with parsing HTML.
     *
     * @return String
     */
    String value();

    /**
     * Optional class definition if you need to map a nested beans.
     *
     * @return Class
     */
    Class<?> clazz() default void.class;
}
