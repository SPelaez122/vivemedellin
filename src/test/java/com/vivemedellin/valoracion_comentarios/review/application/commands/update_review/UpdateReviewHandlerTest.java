package com.vivemedellin.valoracion_comentarios.review.application.commands.update_review;

import com.vivemedellin.valoracion_comentarios.review.dto.ReviewDto;
import com.vivemedellin.valoracion_comentarios.review.entity.Review;
import com.vivemedellin.valoracion_comentarios.review.exceptions.NotFoundReviewException;
import com.vivemedellin.valoracion_comentarios.review.exceptions.UpdateTimeLimitException;
import com.vivemedellin.valoracion_comentarios.review.mapper.ReviewMapper;
import com.vivemedellin.valoracion_comentarios.review.repository.ReviewRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateReviewHandlerTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private UpdateReviewHandler handler;

    private AutoCloseable closeable;

    private final Long userId = 123L;
    private final Long reviewId = 1L;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSuccessfulUpdate() {
        var command = new UpdateReviewCommand(reviewId, userId, 4, "Updated comment");

        var existingReview = new Review();
        existingReview.setId(reviewId);
        existingReview.setUserId(userId);
        existingReview.setRating(5);
        existingReview.setComment("Old comment");
        existingReview.setCreatedAt(Instant.now());

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
    void testReviewNotFoundThrowsException() {
        var command = new UpdateReviewCommand(reviewId, userId, 3, "Doesn't matter");

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundReviewException.class, () -> handler.handle(command));
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void testUpdateTimeLimitExceededThrowsException() {
        var command = new UpdateReviewCommand(reviewId, userId, 5, "Late update");

        var oldReview = new Review();
        oldReview.setId(reviewId);
        oldReview.setUserId(userId);
        oldReview.setCreatedAt(Instant.now().minusSeconds(60 * 60 * 24 * 2)); // 2 days ago

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.of(oldReview));

        assertThrows(UpdateTimeLimitException.class, () -> handler.handle(command));
        verify(reviewRepository, never()).save(any());
    }
}
