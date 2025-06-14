
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UpdateReviewHandlerTest {

    private ReviewRepository reviewRepository;
    private ReviewMapper reviewMapper;
    private UpdateReviewHandler updateReviewHandler;

    private UUID reviewId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        reviewMapper = mock(ReviewMapper.class);
        updateReviewHandler = new UpdateReviewHandler(reviewRepository, reviewMapper);

        reviewId = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");
        userId = UUID.fromString("223e4567-e89b-12d3-a456-556642440000");
    }

    @Test
    void testSuccessfulUpdateReview() {
        Review review = new Review();
        review.setId(reviewId);
        review.setUserId(userId);
        review.setCreatedAt(Instant.now());
        review.setRating(3);
        review.setComment("Comentario original");

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(reviewMapper.toDTO(any())).thenReturn(new ReviewDto());

        UpdateReviewCommand command = new UpdateReviewCommand(reviewId, userId, 4, "Comentario actualizado");
        ReviewDto result = updateReviewHandler.handle(command);

        assertNotNull(result);
        assertEquals(4, review.getRating());
        assertEquals("Comentario actualizado", review.getComment());

        verify(reviewRepository).save(review);
        verify(reviewMapper).toDTO(review);
    }

    @Test
    void testUpdateFailsIfReviewNotFound() {
        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.empty());

        UpdateReviewCommand command = new UpdateReviewCommand(reviewId, userId, 4, "Comentario");

        assertThrows(NotFoundReviewException.class, () -> updateReviewHandler.handle(command));
    }

    @Test
    void testUpdateFailsIfMoreThanOneDayPassed() {
        Review review = new Review();
        review.setId(reviewId);
        review.setUserId(userId);
        review.setCreatedAt(Instant.now().minusSeconds(60 * 60 * 24 + 1)); // más de 1 día

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.of(review));

        UpdateReviewCommand command = new UpdateReviewCommand(reviewId, userId, 5, "Intento después de un día");

        assertThrows(UpdateTimeLimitException.class, () -> updateReviewHandler.handle(command));
    }

    @Test
    void testPartialUpdateOnlyRating() {
        Review review = new Review();
        review.setId(reviewId);
        review.setUserId(userId);
        review.setCreatedAt(Instant.now());
        review.setRating(2);
        review.setComment("Comentario original");

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(reviewMapper.toDTO(any())).thenReturn(new ReviewDto());

        UpdateReviewCommand command = new UpdateReviewCommand(reviewId, userId, 5, null);
        ReviewDto result = updateReviewHandler.handle(command);

        assertNotNull(result);
        assertEquals(5, review.getRating());
        assertEquals("Comentario original", review.getComment());
    }

    @Test
    void testPartialUpdateOnlyComment() {
        Review review = new Review();
        review.setId(reviewId);
        review.setUserId(userId);
        review.setCreatedAt(Instant.now());
        review.setRating(4);
        review.setComment("Comentario anterior");

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(reviewMapper.toDTO(any())).thenReturn(new ReviewDto());

        UpdateReviewCommand command = new UpdateReviewCommand(reviewId, userId, null, "Nuevo comentario");
        ReviewDto result = updateReviewHandler.handle(command);

        assertNotNull(result);
        assertEquals(4, review.getRating());
        assertEquals("Nuevo comentario", review.getComment());
    }
}
