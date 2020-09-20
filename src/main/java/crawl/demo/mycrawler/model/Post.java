package crawl.demo.mycrawler.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "posts", type = "crawler")
public class Post {
    @Id
    private String id;

    private String title;
    private String price;
    private String area;
    private String address;
    private String phoneNumber;
    private String image;
    private String link;

    public Post() {
    }

    public Post(String id, String title, String price, String area, String address, String phoneNumber, String image, String link) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.area = area;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
