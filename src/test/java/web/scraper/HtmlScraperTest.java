package web.scraper;

import org.junit.Test;
import web.scraper.entities.IMDBMovie;

import java.net.URL;

/**
 * Created by alex on 19/03/2016
 */
public class HtmlScraperTest {

    @Test
    public void parseHtmlURL() throws Exception {
        HtmlScraper<IMDBMovie> scraper = new HtmlScraper<IMDBMovie>(IMDBMovie.class);
        scraper.parseHtml(new URL("http://www.imdb.com/movies-coming-soon/"));
    }
}