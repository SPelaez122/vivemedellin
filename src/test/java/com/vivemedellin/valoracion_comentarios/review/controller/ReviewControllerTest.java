package com.vivemedellin.valoracion_comentarios.review.controller;

import com.vivemedellin.valoracion_comentarios.review.application.commands.create_review.CreateReviewCommand;
import com.vivemedellin.valoracion_comentarios.review.application.commands.create_review.CreateReviewHandler;
import com.vivemedellin.valoracion_comentarios.review.application.commands.delete_review.DeleteReviewCommand;
import com.vivemedellin.valoracion_comentarios.review.application.commands.delete_review.DeleteReviewHandler;
import com.vivemedellin.valoracion_comentarios.review.application.commands.update_review.UpdateReviewCommand;
import com.vivemedellin.valoracion_comentarios.review.application.commands.update_review.UpdateReviewHandler;
import com.vivemedellin.valoracion_comentarios.review.application.queries.get_review_by_user_and_event.GetUserReviewCommand;
import com.vivemedellin.valoracion_comentarios.review.application.queries.get_review_by_user_and_event.GetUserReviewHandler;
import com.vivemedellin.valoracion_comentarios.review.application.queries.get_reviews.GetReviewsByEventIdQuery;
import com.vivemedellin.valoracion_comentarios.review.application.queries.get_reviews.GetReviewsByEventIdHandler;
import com.vivemedellin.valoracion_comentarios.review.dto.ReviewDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    @Mock
    private CreateReviewHandler createReviewHandler;

    @Mock
    private GetReviewsByEventIdHandler getReviewsByEventIdHandler;

    @Mock
    private DeleteReviewHandler deleteReviewHandler;

    @Mock
    private UpdateReviewHandler updateReviewHandler;

    @Mock
    private GetUserReviewHandler getUserReviewHandler;

    @InjectMocks
    private ReviewController reviewController;

    private final Long userId = 1L;

    @BeforeEach
    public void setUp() {
        // You can also mock static method getUserId() with Mockito if needed using PowerMockito or similar
    }

    @Test
    public void testCreateReview() {
        ReviewDto mockDto = new ReviewDto();
        when(createReviewHandler.handle(any(CreateReviewCommand.class))).thenReturn(mockDto);

        ReviewDto result = reviewController.createReview(1, 5, "Great event");
        assertEquals(mockDto, result);
    }

    @Test
    public void testDeleteReview() {
        ReviewDto mockDto = new ReviewDto();
        when(deleteReviewHandler.handle(any(DeleteReviewCommand.class))).thenReturn(mockDto);

        ReviewDto result = reviewController.deleteReview(1);
        assertEquals(mockDto, result);
    }

    @Test
    public void testUpdateReview() {
        ReviewDto mockDto = new ReviewDto();
        when(updateReviewHandler.handle(any(UpdateReviewCommand.class))).thenReturn(mockDto);

        ReviewDto result = reviewController.updateReview(1, 4, "Updated review");
        assertEquals(mockDto, result);
    }

    @Test
    public void testAllReviewsByEventId() {
        List<ReviewDto> mockList = List.of(new ReviewDto());
        when(getReviewsByEventIdHandler.handle(any(GetReviewsByEventIdQuery.class))).thenReturn(mockList);

        List<ReviewDto> result = reviewController.allReviewsByEventId(1);
        assertEquals(mockList, result);
    }

    @Test
    public void testReviewByEventId() {
        ReviewDto mockDto = new ReviewDto();
        when(getUserReviewHandler.handle(any(GetUserReviewCommand.class))).thenReturn(mockDto);

        ReviewDto result = reviewController.reviewByEventId(1);
        assertEquals(mockDto, result);
    }
}
