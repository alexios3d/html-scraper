package web.scraper.entities;

import web.scraper.annotations.AttrValue;
import web.scraper.annotations.Selector;

@Selector(".article .list.detail .list_item")
public class IMDBMovie {

    private String title;
    private String duration;
    private String categories;
    private String description;
    private String director;
    private String stars;
    private String imageURL;

    @Selector("h4[itemprop=name] a[itemprop=url]")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Selector("time[itemprop=duration]")
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Selector("span[itemprop=genre],p > span.ghost")
    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    @Selector("div[itemprop=description]")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Selector("span[itemprop=director]")
    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    @Selector("span[itemprop=actors] span[itemprop=name]")
    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    @Selector("img[itemprop=image]")
    @AttrValue("src")
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
