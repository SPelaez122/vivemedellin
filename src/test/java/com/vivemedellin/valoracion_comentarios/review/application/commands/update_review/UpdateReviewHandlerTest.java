package com.vivemedellin.valoracion_comentarios.review.application.commands.update_review;

import com.vivemedellin.valoracion_comentarios.review.dto.ReviewDto;
import com.vivemedellin.valoracion_comentarios.review.entity.Review;
import com.vivemedellin.valoracion_comentarios.review.exceptions.NotFoundReviewException;
import com.vivemedellin.valoracion_comentarios.review.exceptions.UpdateTimeLimitException;
import com.vivemedellin.valoracion_comentarios.review.mapper.ReviewMapper;
import com.vivemedellin.valoracion_comentarios.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateReviewHandlerTest {

    private ReviewRepository reviewRepository;
    private ReviewMapper reviewMapper;
    private UpdateReviewHandler handler;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        reviewMapper = mock(ReviewMapper.class);
        handler = new UpdateReviewHandler(reviewRepository, reviewMapper);
    }

    @Test
    void shouldThrowNotFoundReviewExceptionWhenReviewNotFound() {
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UpdateReviewCommand command = new UpdateReviewCommand(reviewId, userId, 5, "New comment");

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundReviewException.class, () -> handler.handle(command));
    }

    @Test
    void shouldThrowUpdateTimeLimitExceptionWhenReviewIsOlderThanOneDay() {
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Review oldReview = new Review();
        oldReview.setId(reviewId);
        oldReview.setUserId(userId);
        oldReview.setCreatedAt(Instant.now().minusSeconds(86400 * 2)); // hace 2 días

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.of(oldReview));

        UpdateReviewCommand command = new UpdateReviewCommand(reviewId, userId, 4, "Updated comment");

        assertThrows(UpdateTimeLimitException.class, () -> handler.handle(command));
    }

    @Test
    void shouldUpdateReviewSuccessfully() {
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Review review = new Review();
        review.setId(reviewId);
        review.setUserId(userId);
        review.setCreatedAt(Instant.now());

        UpdateReviewCommand command = new UpdateReviewCommand(reviewId, userId, 3, "Updated text");

        Review savedReview = new Review();
        savedReview.setId(reviewId);
        savedReview.setUserId(userId);
        savedReview.setRating(3);
        savedReview.setComment("Updated text");

        ReviewDto dto = new ReviewDto();
        dto.setId(reviewId);
        dto.setUserId(userId);
        dto.setRating(3);
        dto.setComment("Updated text");

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        when(reviewMapper.toDTO(savedReview)).thenReturn(dto);

        ReviewDto result = handler.handle(command);

        assertNotNull(result);
        assertEquals(3, result.getRating());
        assertEquals("Updated text", result.getComment());
        assertEquals(userId, result.getUserId());

        verify(reviewRepository).save(review);
    }
}
