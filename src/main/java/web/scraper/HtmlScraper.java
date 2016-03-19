package web.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.scraper.annotations.AttrValue;
import web.scraper.annotations.Selector;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A factory used to read HTML and map element values to a class.
 *
 * <p>Under the hood we are using <a href="http://jsoup.org/">JSoup</a>.</p>
 *
 * <p>Annotations used to help provide meta data required for mapping data to classes.</p>
 * <ul>
 *     <li>{@link Selector}</li>
 *     <li>{@link AttrValue}</li>
 * </ul>
 *
 * @author Alex Lambrinos
 */
public class HtmlScraper<T> {
    private static final Logger logger = LoggerFactory.getLogger(HtmlScraper.class);

    private Class<T> clazz;

    public HtmlScraper(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Scrapes the provided HTML and maps element values to bean.
     *
     * @param html The HTML string to scrape.
     * @return ArrayList
     * @throws IllegalStateException
     */
    public List<T> parseHtml(String html) {
        Document doc = Jsoup.parse(html);
        return this.parseHtml(doc);
    }

    /**
     * Downloads HTML from provided URL and scrapes it mapping element values to bean.
     *
     * @param url The URL to download html from.
     * @return ArrayList
     * @throws IOException
     * @throws IllegalStateException
     */
    public List<T> parseHtml(URL url) throws IOException {
        Connection connection = Jsoup.connect(url.toString());
        Document doc = connection.get();
        return this.parseHtml(doc);
    }

    /**
     * Main method to kick off scraping the provided {@link Document}.
     *
     * @param doc The {@link Document} object to use
     * @return ArrayList
     * @throws IllegalStateException
     */
    private List<T> parseHtml(Document doc) {
        if (this.clazz == null) {
            throw new IllegalStateException("clazz mush be defined");
        }

        try {
            Selector elementsSelector = this.clazz.getAnnotation(Selector.class);
            PropertyDescriptor[] properties = Introspector.getBeanInfo(this.clazz).getPropertyDescriptors();

            // Select all elements
            Elements rows = doc.select(elementsSelector.value());

            return map(rows, this.clazz, properties);
        } catch (IntrospectionException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        } catch (InstantiationException e) {
            logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * Recursively maps HTML element(s) value to a property with the {@link Selector} annotation defined on its getter
     * method.
     *
     * @param rows The {@link Elements} object to traverse
     * @param clazz The clazz to instantiate.
     * @param properties The list of {@link PropertyDescriptor} to select and map element(s) values to.
     * @param <C>
     * @return ArrayList
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    private <C> List<C> map(Elements rows, Class<C> clazz, PropertyDescriptor[] properties) throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException {
        List<C> objects = new ArrayList<C>();
        for (Element row : rows) {
            logger.trace("Processing row");
            C obj = clazz.newInstance();

            for (PropertyDescriptor property : properties) {
                logger.trace("Mapping property '{}'", property.getName());

                Method readMethod = property.getReadMethod();
                Method writeMethod = property.getWriteMethod();
                Selector selector = readMethod.getAnnotation(Selector.class);

                if (selector != null) {
                    Class<?> childClass = selector.clazz();
                    PropertyDescriptor[] childClassProps = null;

                    // Select value(s)
                    Elements elems = row.select(selector.value());

                    if (!childClass.equals(void.class)) {
                        childClassProps = Introspector.getBeanInfo(childClass).getPropertyDescriptors();
                    }

                    if (childClassProps != null) {
                        List<?> children = map(elems, childClass, childClassProps);
                        if (elems.size() > 1) {
                            writeMethod.invoke(obj, children);
                        } else {
                            writeMethod.invoke(obj, children.get(0));
                        }
                    } else {
                        AttrValue attr = readMethod.getAnnotation(AttrValue.class);

                        if (attr != null) {
                            writeMethod.invoke(obj, elems.attr(attr.value()));
                        } else {
                            writeMethod.invoke(obj, elems.text());
                        }
                    }
                } else {
                    logger.trace("No @Selector specified for method '{}'. Skipping...", readMethod.getName());
                }
            }

            objects.add(obj);
        }

        return objects;
    }
}
