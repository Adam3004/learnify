package com.brightpath.learnify.persistance.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RatingsEmbeddableEntity {

    @Column(name = "ratings_count", nullable = false)
    private int ratingsCount;

    @Column(name = "ratings_sum", nullable = false)
    private int ratingsSum;

    @Column(name = "ratings_average", nullable = false)
    private float ratingsAverage;

    public void addRating(int rating) {
        ratingsCount++;
        ratingsSum += rating;
        ratingsAverage = computeAverageRating();
    }

    public void removeRating(int rating) {
        ratingsCount--;
        ratingsSum -= rating;
        ratingsAverage = computeAverageRating();
    }

    public void updateRating(int oldRating, int newRating) {
        ratingsSum = ratingsSum - oldRating + newRating;
        ratingsAverage = computeAverageRating();
    }

    private float computeAverageRating() {
        return ratingsCount == 0 ? -1.0f : ratingsSum / (float) ratingsCount;
    }
}
