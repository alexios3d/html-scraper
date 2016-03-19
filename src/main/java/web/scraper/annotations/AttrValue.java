package web.scraper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Helps {@link web.scraper.HtmlScraper} determine if how to retrieve the value from a HTML element.
 *
 * @author Alex Lambrinos
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AttrValue {
    String value();
}
