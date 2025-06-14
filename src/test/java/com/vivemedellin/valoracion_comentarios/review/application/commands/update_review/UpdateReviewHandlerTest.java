package com.vivemedellin.valoracion_comentarios.review.application.handler;

import com.vivemedellin.valoracion_comentarios.review.application.dto.UpdateReviewDTO;
import com.vivemedellin.valoracion_comentarios.review.application.mapper.ReviewMapper;
import com.vivemedellin.valoracion_comentarios.review.domain.exception.ReviewUpdateTimeExceededException;
import com.vivemedellin.valoracion_comentarios.review.domain.repository.ReviewRepository;
import com.vivemedellin.valoracion_comentarios.review.entity.Review;
import com.vivemedellin.valoracion_comentarios.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

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
    void testUpdateReviewWithin24Hours() {
        // Arrange
        Long reviewId = 1L;
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now().minus(23, ChronoUnit.HOURS);

        // Simulamos usuario y reseña
        User user = User.builder()
                .id(userId)
                .build();

        Review review = Review.builder()
                .id(reviewId)
                .user(user)
                .rating(3)
                .comment("Comentario original")
                .createdAt(createdAt)
                .build();

        UpdateReviewDTO updateReviewDTO = UpdateReviewDTO.builder()
                .rating(5)
                .comment("Comentario actualizado")
                .build();

        Review updatedReview = Review.builder()
                .id(reviewId)
                .user(user)
                .rating(updateReviewDTO.getRating())
                .comment(updateReviewDTO.getComment())
                .createdAt(createdAt)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);
        when(reviewMapper.toDto(any(Review.class))).thenReturn(updateReviewDTO);

        // Act
        UpdateReviewDTO result = updateReviewHandler.handle(reviewId, userId, updateReviewDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updateReviewDTO.getRating(), result.getRating());
        assertEquals(updateReviewDTO.getComment(), result.getComment());
    }

    @Test
    void testUpdateReviewAfter24Hours_throwsException() {
        // Arrange
        Long reviewId = 1L;
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now().minus(25, ChronoUnit.HOURS);

        User user = User.builder()
                .id(userId)
                .build();

        Review review = Review.builder()
                .id(reviewId)
                .user(user)
                .rating(3)
                .comment("Comentario original")
                .createdAt(createdAt)
                .build();

        UpdateReviewDTO updateReviewDTO = UpdateReviewDTO.builder()
                .rating(4)
                .comment("Intento fuera de tiempo")
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // Act & Assert
        assertThrows(ReviewUpdateTimeExceededException.class,
                () -> updateReviewHandler.handle(reviewId, userId, updateReviewDTO));
    }
}
