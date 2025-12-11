package com.example.applicationrftg;

import java.util.List;

public class Film {
    private int filmId;
    private String title;
    private String description;
    private int releaseYear;
    private Integer originalLanguageId;
    private int rentalDuration;
    private double rentalRate;
    private int length;
    private double replacementCost;
    private String rating;
    private String specialFeatures;
    private String lastUpdate;
    private List<Director> directors;
    private List<Actor> actors;
    private List<Category> categories;

    // Getters et setters
    public int getFilmId() { return filmId; }
    public void setFilmId(int filmId) { this.filmId = filmId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }

    public Integer getOriginalLanguageId() { return originalLanguageId; }
    public void setOriginalLanguageId(Integer originalLanguageId) { this.originalLanguageId = originalLanguageId; }

    public int getRentalDuration() { return rentalDuration; }
    public void setRentalDuration(int rentalDuration) { this.rentalDuration = rentalDuration; }

    public double getRentalRate() { return rentalRate; }
    public void setRentalRate(double rentalRate) { this.rentalRate = rentalRate; }

    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }

    public double getReplacementCost() { return replacementCost; }
    public void setReplacementCost(double replacementCost) { this.replacementCost = replacementCost; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getSpecialFeatures() { return specialFeatures; }
    public void setSpecialFeatures(String specialFeatures) { this.specialFeatures = specialFeatures; }

    public String getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }

    public List<Director> getDirectors() { return directors; }
    public void setDirectors(List<Director> directors) { this.directors = directors; }

    public List<Actor> getActors() { return actors; }
    public void setActors(List<Actor> actors) { this.actors = actors; }

    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> categories) { this.categories = categories; }

    // Classes internes
    public static class Director {
        private int directorId;
        private String firstName;
        private String lastName;
        private String lastUpdate;

        public int getDirectorId() { return directorId; }
        public void setDirectorId(int directorId) { this.directorId = directorId; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getLastUpdate() { return lastUpdate; }
        public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }
    }

    public static class Actor {
        private int actorId;
        private String firstName;
        private String lastName;
        private String lastUpdate;

        public int getActorId() { return actorId; }
        public void setActorId(int actorId) { this.actorId = actorId; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getLastUpdate() { return lastUpdate; }
        public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }
    }

    public static class Category {
        private int categoryId;
        private String name;
        private String lastUpdate;

        public int getCategoryId() { return categoryId; }
        public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getLastUpdate() { return lastUpdate; }
        public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }
    }
}


