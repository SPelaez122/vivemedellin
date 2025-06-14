
package com.vivemedellin.valoracion_comentarios.review.application.commands.update_review;

import com.vivemedellin.valoracion_comentarios.review.dto.ReviewDto;
import com.vivemedellin.valoracion_comentarios.review.exceptions.NotFoundReviewException;
import com.vivemedellin.valoracion_comentarios.review.exceptions.UpdateTimeLimitException;
import com.vivemedellin.valoracion_comentarios.review.mapper.ReviewMapper;
import com.vivemedellin.valoracion_comentarios.review.entity.Review;
import com.vivemedellin.valoracion_comentarios.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateReviewHandlerTest {

    private ReviewRepository reviewRepository;
    private ReviewMapper reviewMapper;
    private UpdateReviewHandler updateReviewHandler;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        reviewMapper = mock(ReviewMapper.class);
        updateReviewHandler = new UpdateReviewHandler(reviewRepository, reviewMapper);
    }

    @Test
    void shouldThrowNotFoundReviewExceptionIfReviewDoesNotExist() {
        UpdateReviewCommand command = new UpdateReviewCommand(1L, 2L, 5, "Buen servicio");

        when(reviewRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundReviewException.class, () -> updateReviewHandler.handle(command));
    }

    @Test
    void shouldThrowUpdateTimeLimitExceptionIfMoreThanOneDayPassed() {
        Review review = new Review();
        review.setCreatedAt(Instant.now().minusSeconds(86400 * 2)); // 2 días atrás

        when(reviewRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.of(review));

        UpdateReviewCommand command = new UpdateReviewCommand(1L, 2L, 4, "Comentario");

        assertThrows(UpdateTimeLimitException.class, () -> updateReviewHandler.handle(command));
    }

    @Test
    void shouldUpdateRatingAndCommentIfBothProvided() {
        Review review = new Review();
        review.setCreatedAt(Instant.now());

        UpdateReviewCommand command = new UpdateReviewCommand(1L, 2L, 5, "Muy bien");

        when(reviewRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        ReviewDto dto = new ReviewDto();
        when(reviewMapper.toDTO(review)).thenReturn(dto);

        ReviewDto result = updateReviewHandler.handle(command);

        assertEquals(dto, result);
        assertEquals(5, review.getRating());
        assertEquals("Muy bien", review.getComment());
    }

    @Test
    void shouldUpdateOnlyRatingIfCommentIsNull() {
        Review review = new Review();
        review.setCreatedAt(Instant.now());

        UpdateReviewCommand command = new UpdateReviewCommand(1L, 2L, 4, null);

        when(reviewRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDTO(review)).thenReturn(new ReviewDto());

        ReviewDto result = updateReviewHandler.handle(command);

        assertEquals(4, review.getRating());
        assertNull(review.getComment());
    }

    @Test
    void shouldUpdateOnlyCommentIfRatingIsNull() {
        Review review = new Review();
        review.setCreatedAt(Instant.now());

        UpdateReviewCommand command = new UpdateReviewCommand(1L, 2L, null, "Solo comentario");

        when(reviewRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDTO(review)).thenReturn(new ReviewDto());

        ReviewDto result = updateReviewHandler.handle(command);

        assertEquals("Solo comentario", review.getComment());
        assertNull(review.getRating());
    }
}
