package crawl.demo.mycrawler.model;

public class PostRequest {
    private String seedPage;
    private String titleTag;
    private String priceTag;
    private String areaTag; //diện tích
    private String addressTag;
    private String phoneNumberTag;
    private String imageTag;
    private String linkTag;
    private String postWrapperTag;
    private String nextPageTag;

    public PostRequest() {
    }

    public PostRequest(String seedPage, String titleTag, String priceTag, String areaTag, String addressTag, String phoneNumberTag, String imageTag, String linkTag, String postWrapperTag, String nextPageTag) {
        this.seedPage = seedPage;
        this.titleTag = titleTag;
        this.priceTag = priceTag;
        this.areaTag = areaTag;
        this.addressTag = addressTag;
        this.phoneNumberTag = phoneNumberTag;
        this.imageTag = imageTag;
        this.linkTag = linkTag;
        this.postWrapperTag = postWrapperTag;
        this.nextPageTag = nextPageTag;
    }

    public String getSeedPage() {
        return seedPage;
    }

    public void setSeedPage(String seedPage) {
        this.seedPage = seedPage;
    }

    public String getTitleTag() {
        return titleTag;
    }

    public void setTitleTag(String titleTag) {
        this.titleTag = titleTag;
    }

    public String getPriceTag() {
        return priceTag;
    }

    public void setPriceTag(String priceTag) {
        this.priceTag = priceTag;
    }

    public String getAreaTag() {
        return areaTag;
    }

    public void setAreaTag(String areaTag) {
        this.areaTag = areaTag;
    }

    public String getAddressTag() {
        return addressTag;
    }

    public void setAddressTag(String addressTag) {
        this.addressTag = addressTag;
    }

    public String getPhoneNumberTag() {
        return phoneNumberTag;
    }

    public void setPhoneNumberTag(String phoneNumberTag) {
        this.phoneNumberTag = phoneNumberTag;
    }

    public String getImageTag() {
        return imageTag;
    }

    public void setImageTag(String imageTag) {
        this.imageTag = imageTag;
    }

    public String getLinkTag() {
        return linkTag;
    }

    public void setLinkTag(String linkTag) {
        this.linkTag = linkTag;
    }

    public String getPostWrapperTag() {
        return postWrapperTag;
    }

    public void setPostWrapperTag(String postWrapperTag) {
        this.postWrapperTag = postWrapperTag;
    }

    public String getNextPageTag() {
        return nextPageTag;
    }

    public void setNextPageTag(String nextPageTag) {
        this.nextPageTag = nextPageTag;
    }

    @Override
    public String toString() {
        return "PostRequest{" +
                "seedPage='" + seedPage + '\'' +
                ", titleTag='" + titleTag + '\'' +
                ", priceTag='" + priceTag + '\'' +
                ", areaTag='" + areaTag + '\'' +
                ", addressTag='" + addressTag + '\'' +
                ", phoneNumberTag='" + phoneNumberTag + '\'' +
                ", imageTag='" + imageTag + '\'' +
                ", linkTag='" + linkTag + '\'' +
                ", postWrapperTag='" + postWrapperTag + '\'' +
                ", nextPageTag='" + nextPageTag + '\'' +
                '}';
    }
}
