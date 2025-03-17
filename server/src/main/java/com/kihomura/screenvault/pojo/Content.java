package com.kihomura.screenvault.pojo;

import com.kihomura.screenvault.enums.Category;
import com.kihomura.screenvault.enums.Genre;
import com.kihomura.screenvault.enums.SourceType;
import jakarta.persistence.*;
import java.time.LocalDate;

@Table(name = "contents")
@Entity
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title", length = 500, nullable = false)
    private String title;

    @Column(name = "other_title", length = 500)
    private String otherTitle;

    @Column(name = "country", length = 2)
    private String country;

    @Column(name = "language", length = 2)
    private String language;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image", length = 500)
    private String image;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", length = 20)
    private Genre genre;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", length = 20)
    private SourceType sourceType;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = true)
    private User user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOtherTitle() {
        return otherTitle;
    }

    public void setOtherTitle(String otherTitle) {
        this.otherTitle = otherTitle;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Content{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", otherTitle='" + otherTitle + '\'' +
                ", country='" + country + '\'' +
                ", language='" + language + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", releaseDate=" + releaseDate +
                ", genres=" + genre +
                ", category=" + category +
                ", sourceType=" + sourceType +
                ", user=" + user +
                '}';
    }
}