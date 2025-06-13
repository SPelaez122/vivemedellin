package com.vivemedellin.valoracion_comentarios.moderation.service;

import com.vivemedellin.valoracion_comentarios.moderation.dto.UpdateReviewModerationDto;
import com.vivemedellin.valoracion_comentarios.review.dto.ReviewDto;
import com.vivemedellin.valoracion_comentarios.review.entity.Review;
import com.vivemedellin.valoracion_comentarios.review.exceptions.NotFoundReviewException;
import com.vivemedellin.valoracion_comentarios.review.mapper.ReviewMapper;
import com.vivemedellin.valoracion_comentarios.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModerationServiceTest {

    private ReviewRepository reviewRepository;
    private ReviewMapper reviewMapper;
    private ModerationService moderationService;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        reviewMapper = mock(ReviewMapper.class);
        moderationService = new ModerationService(reviewRepository, reviewMapper);
    }

    @Test
    void deleteReview_shouldDeleteAndReturnDto_whenReviewExists() {
        Long reviewId = 1L;
        Review review = new Review();
        review.setId(reviewId);
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(reviewId);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewMapper.toDTO(review)).thenReturn(reviewDto);

        ReviewDto result = moderationService.deleteReview(reviewId);

        verify(reviewRepository).delete(review);
        assertEquals(reviewId, result.getId());
    }

    @Test
    void deleteReview_shouldThrowNotFoundException_whenReviewNotFound() {
        Long reviewId = 2L;
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThrows(NotFoundReviewException.class, () -> moderationService.deleteReview(reviewId));
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void updateReview_shouldUpdateCommentAndReturnDto_whenReviewExists() {
        Long reviewId = 3L;
        String updatedComment = "Updated content";

        UpdateReviewModerationDto dto = new UpdateReviewModerationDto();
        dto.setReviewId(reviewId);
        dto.setComment(updatedComment);

        Review review = new Review();
        review.setId(reviewId);
        review.setComment("Old content");

        Review savedReview = new Review();
        savedReview.setId(reviewId);
        savedReview.setComment(updatedComment);

        ReviewDto expectedDto = new ReviewDto();
        expectedDto.setId(reviewId);
        expectedDto.setComment(updatedComment);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(savedReview);
        when(reviewMapper.toDTO(savedReview)).thenReturn(expectedDto);

        ReviewDto result = moderationService.updateReview(dto);

        assertEquals(updatedComment, result.getComment());
    }

    @Test
    void updateReview_shouldThrowNotFoundException_whenReviewNotFound() {
        Long reviewId = 4L;
        UpdateReviewModerationDto dto = new UpdateReviewModerationDto();
        dto.setReviewId(reviewId);
        dto.setComment("Test");

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThrows(NotFoundReviewException.class, () -> moderationService.updateReview(dto));
        verify(reviewRepository, never()).save(any());
    }
}
