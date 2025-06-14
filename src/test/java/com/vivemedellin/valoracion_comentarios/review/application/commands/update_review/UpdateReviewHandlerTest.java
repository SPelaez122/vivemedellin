package com.vivemedellin.valoracion_comentarios.review.application.commands.update_review;

import com.vivemedellin.valoracion_comentarios.review.dto.ReviewDto;
import com.vivemedellin.valoracion_comentarios.review.entity.Review;
import com.vivemedellin.valoracion_comentarios.review.exceptions.NotFoundReviewException;
import com.vivemedellin.valoracion_comentarios.review.exceptions.UpdateTimeLimitException;
import com.vivemedellin.valoracion_comentarios.review.mapper.ReviewMapper;
import com.vivemedellin.valoracion_comentarios.review.repository.ReviewRepository;
import com.vivemedellin.valoracion_comentarios.user.entity.User;
import com.vivemedellin.valoracion_comentarios.user.dto.UserDto;
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
    void testHandleUpdateReviewSuccessfully() {
        Long reviewId = 1L;
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now().minus(1, ChronoUnit.HOURS);

        User user = new User();
        user.setId(userId);

        Review existingReview = new Review();
        existingReview.setId(reviewId);
        existingReview.setUser(user);
        existingReview.setRating(3);
        existingReview.setComment("Old comment");
        existingReview.setCreatedAt(createdAt);

        Review updatedReview = new Review();
        updatedReview.setId(reviewId);
        updatedReview.setUser(user);
        updatedReview.setRating(5);
        updatedReview.setComment("New comment");
        updatedReview.setCreatedAt(createdAt);

        ReviewDto expectedDto = new ReviewDto();
        expectedDto.setId(reviewId);
        // Assuming you have a UserDto and a way to convert User to UserDto, e.g., a UserMapper or manual mapping
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        expectedDto.setUser(userDto);
        expectedDto.setRating(5);
        expectedDto.setComment("New comment");
        expectedDto.setCreatedAt(createdAt);

        UpdateReviewCommand command = new UpdateReviewCommand(reviewId, userId, 5, "New comment");

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any())).thenReturn(updatedReview);
        when(reviewMapper.toDTO(updatedReview)).thenReturn(expectedDto);

        ReviewDto result = updateReviewHandler.handle(command);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("New comment", result.getComment());
        verify(reviewRepository).save(any());
        verify(reviewMapper).toDTO(any());
    }

    @Test
    void testHandleUpdateTimeLimitExceeded() {
        Long reviewId = 2L;
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now().minus(25, ChronoUnit.HOURS);

        User user = new User();
        user.setId(userId);

        Review oldReview = new Review();
        oldReview.setId(reviewId);
        oldReview.setUser(user);
        oldReview.setRating(2);
        oldReview.setComment("Too old");
        oldReview.setCreatedAt(createdAt);

        UpdateReviewCommand command = new UpdateReviewCommand(reviewId, userId, 4, "Updated");

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.of(oldReview));

        assertThrows(UpdateTimeLimitException.class, () -> updateReviewHandler.handle(command));
        verify(reviewRepository, never()).save(any());
        verify(reviewMapper, never()).toDTO(any());
    }

    @Test
    void testHandleReviewNotFound() {
        Long reviewId = 3L;
        UUID userId = UUID.randomUUID();

        UpdateReviewCommand command = new UpdateReviewCommand(reviewId, userId, 4, "Doesn't exist");

        when(reviewRepository.findByIdAndUserId(reviewId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundReviewException.class, () -> updateReviewHandler.handle(command));
        verify(reviewRepository, never()).save(any());
        verify(reviewMapper, never()).toDTO(any());
    }
}
