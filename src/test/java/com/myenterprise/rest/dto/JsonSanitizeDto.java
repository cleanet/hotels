package com.myenterprise.rest.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.myenterprise.rest.annotation.sanitizehtml.SanitizeHtml;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonSanitizeDto {
    private String id;
    @SanitizeHtml
    private String name;
    @SanitizeHtml
    private String description;
    @SanitizeHtml
    private String shortDescription;
    private Integer idTridion;
    private MediaDTO heroMedia;
    private String slug;
    private PriceDTO price;
    private CountryDTO country;
    private ContinentDTO continent;
    private List<HotelDTO> hotels;
    private List<GalleryItemDTO> gallery;
    private List<GalleryItemDTO> galleryVideos;
    private List<GalleryItemDTO> gallery360;
    private MediaDTO destinationDescriptionMedia;
    private MediaDTO destinationMedia;
    private MediaDTO countryMedia;
    private MediaDTO mostBookedMedia;
    private MediaDTO hotelCardMedia;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public Integer getIdTridion() { return idTridion; }
    public void setIdTridion(Integer idTridion) { this.idTridion = idTridion; }

    public MediaDTO getHeroMedia() { return heroMedia; }
    public void setHeroMedia(MediaDTO heroMedia) { this.heroMedia = heroMedia; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public PriceDTO getPrice() { return price; }
    public void setPrice(PriceDTO price) { this.price = price; }

    public CountryDTO getCountry() { return country; }
    public void setCountry(CountryDTO country) { this.country = country; }

    public ContinentDTO getContinent() { return continent; }
    public void setContinent(ContinentDTO continent) { this.continent = continent; }

    public List<HotelDTO> getHotels() { return hotels; }
    public void setHotels(List<HotelDTO> hotels) { this.hotels = hotels; }

    public List<GalleryItemDTO> getGallery() { return gallery; }
    public void setGallery(List<GalleryItemDTO> gallery) { this.gallery = gallery; }

    public List<GalleryItemDTO> getGalleryVideos() { return galleryVideos; }
    public void setGalleryVideos(List<GalleryItemDTO> galleryVideos) { this.galleryVideos = galleryVideos; }

    public List<GalleryItemDTO> getGallery360() { return gallery360; }
    public void setGallery360(List<GalleryItemDTO> gallery360) { this.gallery360 = gallery360; }

    public MediaDTO getDestinationDescriptionMedia() { return destinationDescriptionMedia; }
    public void setDestinationDescriptionMedia(MediaDTO destinationDescriptionMedia) { this.destinationDescriptionMedia = destinationDescriptionMedia; }

    public MediaDTO getDestinationMedia() { return destinationMedia; }
    public void setDestinationMedia(MediaDTO destinationMedia) { this.destinationMedia = destinationMedia; }

    public MediaDTO getCountryMedia() { return countryMedia; }
    public void setCountryMedia(MediaDTO countryMedia) { this.countryMedia = countryMedia; }

    public MediaDTO getMostBookedMedia() { return mostBookedMedia; }
    public void setMostBookedMedia(MediaDTO mostBookedMedia) { this.mostBookedMedia = mostBookedMedia; }

    public MediaDTO getHotelCardMedia() { return hotelCardMedia; }
    public void setHotelCardMedia(MediaDTO hotelCardMedia) { this.hotelCardMedia = hotelCardMedia; }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PriceDTO {
        private float amount;
        private float amountOffer;
        private String currency;
        private String currencyOffer;

        public float getAmount() { return amount; }
        public void setAmount(float amount) { this.amount = amount; }

        public float getAmountOffer() { return amountOffer; }
        public void setAmountOffer(float amountOffer) { this.amountOffer = amountOffer; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public String getCurrencyOffer() { return currencyOffer; }
        public void setCurrencyOffer(String currencyOffer) { this.currencyOffer = currencyOffer; }
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MediaDTO {
        private String path;
        @SanitizeHtml
        private String title;
        @SanitizeHtml
        private String caption;
        private MetadataDTO metadata;

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public MetadataDTO getMetadata() { return metadata; }
        public void setMetadata(MetadataDTO metadata) { this.metadata = metadata; }

        public String getCaption() { return caption; }
        public void setCaption(String caption) { this.caption = caption; }
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MetadataDTO {
        @SanitizeHtml
        private String name;
        private String mimeType;
        @SanitizeHtml
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String caption;
        private String fileSize;
        private String height;
        private String width;
        private String format;
        @SanitizeHtml
        private String subject;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }

        public String getFileSize() { return fileSize; }
        public void setFileSize(String fileSize) { this.fileSize = fileSize; }

        public String getHeight() { return height; }
        public void setHeight(String height) { this.height = height; }

        public String getWidth() { return width; }
        public void setWidth(String width) { this.width = width; }

        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }

        public String getCaption() { return caption; }
        public void setCaption(String caption) { this.caption = caption; }
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CountryDTO {
        private String id;
        @SanitizeHtml
        private String name;
        private String slug;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContinentDTO {
        private String id;
        @SanitizeHtml
        private String name;
        private String slug;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HotelDTO {
        private String id;
        @SanitizeHtml
        private String name;
        private String slug;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GalleryItemDTO {
        private MediaDTO image;
        private Boolean showInMediaBanner;

        public MediaDTO getImage() { return image; }
        public void setImage(MediaDTO image) { this.image = image; }

        public Boolean getShowInMediaBanner() { return showInMediaBanner; }
        public void setShowInMediaBanner(Boolean showInMediaBanner) { this.showInMediaBanner = showInMediaBanner; }
    }

    public String toJsonString(){
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}