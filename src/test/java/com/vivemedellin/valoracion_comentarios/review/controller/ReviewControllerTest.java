package com.vivemedellin.valoracion_comentarios.review.controller;

import com.vivemedellin.valoracion_comentarios.review.application.commands.create_review.CreateReviewCommand;
import com.vivemedellin.valoracion_comentarios.review.application.commands.create_review.CreateReviewHandler;
import com.vivemedellin.valoracion_comentarios.review.application.commands.delete_review.DeleteReviewCommand;
import com.vivemedellin.valoracion_comentarios.review.application.commands.delete_review.DeleteReviewHandler;
import com.vivemedellin.valoracion_comentarios.review.application.commands.update_review.UpdateReviewCommand;
import com.vivemedellin.valoracion_comentarios.review.application.commands.update_review.UpdateReviewHandler;
import com.vivemedellin.valoracion_comentarios.review.application.queries.get_review_by_user_and_event.GetUserReviewCommand;
import com.vivemedellin.valoracion_comentarios.review.application.queries.get_review_by_user_and_event.GetUserReviewHandler;
import com.vivemedellin.valoracion_comentarios.review.application.queries.get_reviews.GetReviewsByEventIdHandler;
import com.vivemedellin.valoracion_comentarios.review.application.queries.get_reviews.GetReviewsByEventIdQuery;
import com.vivemedellin.valoracion_comentarios.review.dto.ReviewDto;
import com.vivemedellin.valoracion_comentarios.shared.util.AuthUtils;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock private CreateReviewHandler createReviewHandler;
    @Mock private GetReviewsByEventIdHandler getReviewsByEventIdHandler;
    @Mock private DeleteReviewHandler deleteReviewHandler;
    @Mock private UpdateReviewHandler updateReviewHandler;
    @Mock private GetUserReviewHandler getUserReviewHandler;

    private static final UUID FAKE_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    
    private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        reviewController = new ReviewController(
                createReviewHandler,
                getReviewsByEventIdHandler,
                deleteReviewHandler,
                updateReviewHandler,
                getUserReviewHandler
        );
    }

    @Test
    void testCreateReview() {
        try (MockedStatic<AuthUtils> mocked = mockStatic(AuthUtils.class)) {
            mocked.when(AuthUtils::getUserId).thenReturn(FAKE_USER_ID);

            ReviewDto expected = new ReviewDto();
            when(createReviewHandler.handle(any(CreateReviewCommand.class))).thenReturn(expected);

            ReviewDto result = reviewController.createReview(1, 5, "Good event");

            assertEquals(expected, result);
            verify(createReviewHandler).handle(any(CreateReviewCommand.class));
        }
    }

    @Test
    void testDeleteReview() {
        try (MockedStatic<AuthUtils> mocked = mockStatic(AuthUtils.class)) {
            mocked.when(AuthUtils::getUserId).thenReturn(FAKE_USER_ID);

            ReviewDto expected = new ReviewDto();
            when(deleteReviewHandler.handle(any(DeleteReviewCommand.class))).thenReturn(expected);

            ReviewDto result = reviewController.deleteReview(10);

            assertEquals(expected, result);
            verify(deleteReviewHandler).handle(any(DeleteReviewCommand.class));
        }
    }

    @Test
    void testUpdateReview() {
        try (MockedStatic<AuthUtils> mocked = mockStatic(AuthUtils.class)) {
            mocked.when(AuthUtils::getUserId).thenReturn(FAKE_USER_ID);

            ReviewDto expected = new ReviewDto();
            when(updateReviewHandler.handle(any(UpdateReviewCommand.class))).thenReturn(expected);

            ReviewDto result = reviewController.updateReview(1, 4, "Updated");

            assertEquals(expected, result);
            verify(updateReviewHandler).handle(any(UpdateReviewCommand.class));
        }
    }

    @Test
    void testAllReviewsByEventId() {
        ReviewDto review1 = new ReviewDto();
        ReviewDto review2 = new ReviewDto();
        List<ReviewDto> expected = List.of(review1, review2);

        when(getReviewsByEventIdHandler.handle(any(GetReviewsByEventIdQuery.class))).thenReturn(expected);

        List<ReviewDto> result = reviewController.allReviewsByEventId(1);

        assertEquals(expected, result);
        verify(getReviewsByEventIdHandler).handle(any(GetReviewsByEventIdQuery.class));
    }

    @Test
    void testReviewByEventId() {
        try (MockedStatic<AuthUtils> mocked = mockStatic(AuthUtils.class)) {
            mocked.when(AuthUtils::getUserId).thenReturn(FAKE_USER_ID);

            ReviewDto expected = new ReviewDto();
            when(getUserReviewHandler.handle(any(GetUserReviewCommand.class))).thenReturn(expected);

            ReviewDto result = reviewController.reviewByEventId(1);

            assertEquals(expected, result);
            verify(getUserReviewHandler).handle(any(GetUserReviewCommand.class));
        }
    }
}
