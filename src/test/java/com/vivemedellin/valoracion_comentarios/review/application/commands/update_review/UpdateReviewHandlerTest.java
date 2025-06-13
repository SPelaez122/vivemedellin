package com.vivemedellin.valoracion_comentarios.review.application.commands.update_review;

import com.vivemedellin.valoracion_comentarios.review.dto.ReviewDto;
import com.vivemedellin.valoracion_comentarios.review.entity.Review;
import com.vivemedellin.valoracion_comentarios.review.exceptions.NotFoundReviewException;
import com.vivemedellin.valoracion_comentarios.review.exceptions.UpdateTimeLimitException;
import com.vivemedellin.valoracion_comentarios.review.mapper.ReviewMapper;
import com.vivemedellin.valoracion_comentarios.review.repository.ReviewRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateReviewHandlerTest {

    private ReviewRepository reviewRepository;
    private ReviewMapper reviewMapper;
    private UpdateReviewHandler handler;

    private final UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final Long reviewId = 1L;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        reviewMapper = mock(ReviewMapper.class);
        handler = new UpdateReviewHandler(reviewRepository, reviewMapper);
    }

    @Test
    void testSuccessfulUpdate() throws Exception {
        var command = new UpdateReviewCommand(reviewId, userId, 4, "Updated comment");

        var existingReview = new Review();
        existingReview.setId(reviewId);
        existingReview.setRating(5);
        existingReview.setComment("Old comment");
        existingReview.setCreatedAt(Instant.now());

        // Use reflection to set userId
        Field userIdField = Review.class.getDeclaredField("userId");
        userIdField.setAccessible(true);
        userIdField.set(existingReview, userId);

        var updatedDto = new ReviewDto();
        updatedDto.setRating(4);
        updatedDto.setComment("Updated comment");

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(existingReview)).thenReturn(existingReview);
        when(reviewMapper.toDTO(existingReview)).thenReturn(updatedDto);

        ReviewDto result = handler.handle(command);

        assertEquals(4, result.getRating());
        assertEquals("Updated comment", result.getComment());
        verify(reviewRepository).save(existingReview);
    }

    @Test
    void testReviewNotFound() {
        var command = new UpdateReviewCommand(reviewId, userId, 4, "Updated");

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundReviewException.class, () -> handler.handle(command));
    }

    @Test
    void testUpdateTimeLimitExceeded() throws Exception {
        var command = new UpdateReviewCommand(reviewId, userId, 4, "Late update");

        var oldReview = new Review();
        oldReview.setId(reviewId);
        oldReview.setRating(3);
        oldReview.setComment("Old");
        oldReview.setCreatedAt(Instant.now().minusSeconds(86400 * 2)); // 2 days ago

        // Set userId via reflection
        Field userIdField = Review.class.getDeclaredField("userId");
        userIdField.setAccessible(true);
        userIdField.set(oldReview, userId);

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.of(oldReview));

        assertThrows(UpdateTimeLimitException.class, () -> handler.handle(command));
    }
}
