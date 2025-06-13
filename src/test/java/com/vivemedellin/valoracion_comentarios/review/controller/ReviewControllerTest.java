import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static com.vivemedellin.valoracion_comentarios.shared.util.AuthUtils.getUserId;

import com.vivemedellin.valoracion_comentarios.review.application.commands.create_review.CreateReviewCommand;
import com.vivemedellin.valoracion_comentarios.review.application.commands.create_review.CreateReviewHandler;
import com.vivemedellin.valoracion_comentarios.review.controller.ReviewController;
import com.vivemedellin.valoracion_comentarios.review.dto.ReviewDto;
import com.vivemedellin.valoracion_comentarios.shared.util.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class ReviewControllerTest {

    private CreateReviewHandler createReviewHandler;
    private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        createReviewHandler = mock(CreateReviewHandler.class);
        reviewController = new ReviewController(createReviewHandler, null, null, null, null);
    }

    @Test
    void testCreateReview() {
        long fakeUserId = 123L;
        ReviewDto expectedReview = new ReviewDto();
        expectedReview.setComment("Test comment");

        try (MockedStatic<AuthUtils> authUtilsMock = Mockito.mockStatic(AuthUtils.class)) {
            authUtilsMock.when(AuthUtils::getUserId).thenReturn(fakeUserId);

            when(createReviewHandler.handle(any(CreateReviewCommand.class)))
                    .thenReturn(expectedReview);

            ReviewDto result = reviewController.createReview(1, 5, "Test comment");

            assertEquals("Test comment", result.getComment());
            verify(createReviewHandler, times(1)).handle(any(CreateReviewCommand.class));
        }
    }
}
