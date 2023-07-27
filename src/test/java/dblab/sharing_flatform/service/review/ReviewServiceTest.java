package dblab.sharing_flatform.service.review;

import dblab.sharing_flatform.domain.member.Member;
import dblab.sharing_flatform.domain.post.Post;
import dblab.sharing_flatform.domain.review.Review;
import dblab.sharing_flatform.domain.trade.Trade;
import dblab.sharing_flatform.dto.review.ReviewDto;
import dblab.sharing_flatform.dto.review.crud.create.ReviewRequestDto;
import dblab.sharing_flatform.dto.review.crud.create.ReviewResponseDto;
import dblab.sharing_flatform.dto.review.crud.read.request.ReviewPagingCondition;
import dblab.sharing_flatform.exception.review.ExistReviewException;
import dblab.sharing_flatform.exception.review.ImpossibleWriteReviewException;
import dblab.sharing_flatform.exception.trade.TradeNotCompleteException;
import dblab.sharing_flatform.factory.review.ReviewFactory;
import dblab.sharing_flatform.factory.trade.TradeFactory;
import dblab.sharing_flatform.helper.NotificationHelper;
import dblab.sharing_flatform.repository.emitter.EmitterRepository;
import dblab.sharing_flatform.repository.member.MemberRepository;
import dblab.sharing_flatform.repository.review.ReviewRepository;
import dblab.sharing_flatform.repository.trade.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dblab.sharing_flatform.factory.review.ReviewFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private NotificationHelper helper;

    Trade trade;
    Post post;
    Member member;
    Member reviewerMember;
    Review review;

    @BeforeEach
    public void beforeEach(){
        trade = TradeFactory.createTrade();
        post = trade.getPost();
        member = trade.getRenderMember();
        reviewerMember = trade.getBorrowerMember();
        review = createReview();
    }

    @Test
    @DisplayName("리뷰 작성 테스트")
    public void writeReviewTest(){
        // Given
        trade.isTradeComplete(true);
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(review.getReviewerMember().getUsername(), review.getContent(), review.getStarRating());

        given(tradeRepository.findById(trade.getId())).willReturn(Optional.of(trade));
        given(memberRepository.findByUsername(reviewerMember.getUsername())).willReturn(Optional.of(reviewerMember));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        // When
        ReviewResponseDto result = reviewService.writeReview(reviewRequestDto, trade.getId(), reviewerMember.getUsername());

        // Then
        assertThat(result.getContent()).isEqualTo("테스트 리뷰입니다.");
    }

    @Test
    @DisplayName("전체 리뷰 조회 테스트")
    public void findAllReviewTest(){
        // Given
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review("review 1", 5, member, reviewerMember));
        reviews.add(new Review("review 2", 4, reviewerMember , member));

        given(reviewRepository.findAll()).willReturn(reviews);

        // When
        List<ReviewResponseDto> result = reviewService.findAllReviews();

        // Then
        assertThat(result).hasSize(2);

        ReviewResponseDto reviewResponseDto1 = result.get(0);
        assertThat(reviewResponseDto1.getContent()).isEqualTo("review 1");
        assertThat(reviewResponseDto1.getStarRating()).isEqualTo(5);

        ReviewResponseDto reviewResponseDto2 = result.get(1);
        assertThat(reviewResponseDto2.getContent()).isEqualTo("review 2");
        assertThat(reviewResponseDto2.getStarRating()).isEqualTo(4);
    }

    @Test
    @DisplayName("현재 유저의 리뷰 조회 테스트")
    public void findCurrentUserReviewsTest(){
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review("review 1", 5, member, reviewerMember));
        reviews.add(new Review("review 2", 2, member, reviewerMember));

        String username = member.getUsername();

        given(reviewRepository.findAllWithMemberByMemberId(username)).willReturn(reviews);

        // When
        List<ReviewResponseDto> result = reviewService.findCurrentUserReviews(username);

        // Then
        assertThat(result).hasSize(2);
        ReviewResponseDto reviewResponseDto1 = result.get(0);
        assertThat(reviewResponseDto1.getContent()).isEqualTo("review 1");
        assertThat(reviewResponseDto1.getStarRating()).isEqualTo(5);

        ReviewResponseDto reviewResponseDto2 = result.get(1);
        assertThat(reviewResponseDto2.getContent()).isEqualTo("review 2");
        assertThat(reviewResponseDto2.getStarRating()).isEqualTo(2);
    }

    @Test
    @DisplayName("유저 이름을 검색조건으로 특정 리뷰 조회 테스트")
    public void findAllReviewsByUsername(){
        List<Review> reviews = new ArrayList<>();
        List<ReviewDto> reviewDtoList = new ArrayList<>();

        reviews.add(new Review("review 1", 5, member, reviewerMember));
        reviews.add(new Review("review 2", 2, member, reviewerMember));

        reviewDtoList.add(ReviewDto.toDto(reviews.get(0)));
        reviewDtoList.add(ReviewDto.toDto(reviews.get(1)));

        ReviewPagingCondition cond = new ReviewPagingCondition(1, 5, member.getUsername());

        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize(), Sort.by("review_id").ascending());
        Page<ReviewDto> resultPage = new PageImpl<>(reviewDtoList, pageable, reviews.size());

        given(reviewRepository.findAllByUsername(cond)).willReturn(resultPage);

        // When
        Page<ReviewDto> result = reviewService.findAllReviewsByUsername(cond);

        // Then
        assertThat(result).hasSize(2);
        ReviewDto reviewDto1 = result.getContent().get(0);
        assertThat(reviewDto1.getContent()).isEqualTo("review 1");
        assertThat(reviewDto1.getStarRating()).isEqualTo(5);

        ReviewDto reviewDto2 = result.getContent().get(1);
        assertThat(reviewDto2.getContent()).isEqualTo("review 2");
        assertThat(reviewDto2.getStarRating()).isEqualTo(2);
    }
    @Test
    @DisplayName("리뷰 삭제 테스트")
    public void deleteReviewTest(){
        // Given
        trade.addReview(review);
        given(tradeRepository.findById(trade.getId())).willReturn(Optional.of(trade));
        given(reviewRepository.findById(trade.getReview().getId())).willReturn(Optional.of(review));

        // When
        reviewService.deleteReview(trade.getId());

        //Then
        verify(reviewRepository).delete(review);
    }

    @Test
    @DisplayName("리뷰 작성 불가 예외 테스트")
    public void writeReviewImpossibleWriteReviewExceptionTest(){
        // Given
        review = createReviewWithMember(reviewerMember, reviewerMember);
        trade = new Trade(review.getMember(), review.getReviewerMember(), LocalDate.now(), LocalDate.now(), post);

        trade.isTradeComplete(true);
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(review.getReviewerMember().getUsername(), review.getContent(), review.getStarRating());

        given(tradeRepository.findById(trade.getId())).willReturn(Optional.of(trade));
        given(memberRepository.findByUsername(review.getReviewerMember().getUsername())).willReturn(Optional.of(review.getReviewerMember()));
        given(memberRepository.findById(review.getMember().getId())).willReturn(Optional.of(review.getMember()));

        // When & Then
        assertThatThrownBy(() -> reviewService.writeReview(reviewRequestDto, trade.getId(), review.getReviewerMember().getUsername())).isInstanceOf(ImpossibleWriteReviewException.class);
    }

    @Test
    @DisplayName("리뷰 중복 작성 불가 예외 테스트")
    public void writeReviewExistReviewExceptionTest(){
        // Given
        trade.isTradeComplete(true);
        trade.addReview(review);
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(review.getReviewerMember().getUsername(), review.getContent(), review.getStarRating());

        given(tradeRepository.findById(trade.getId())).willReturn(Optional.of(trade));
        given(memberRepository.findByUsername(review.getReviewerMember().getUsername())).willReturn(Optional.of(reviewerMember));
        given(memberRepository.findById(review.getMember().getId())).willReturn(Optional.of(member));

        // When & Then
        assertThatThrownBy(() -> reviewService.writeReview(reviewRequestDto, trade.getId(), review.getReviewerMember().getUsername())).isInstanceOf(ExistReviewException.class);
    }
    @Test
    @DisplayName("거래 미완료 리뷰 작성 불가 예외 테스트")
    public void writeReviewTradeNotCompleteException(){
        // Given
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto(review.getReviewerMember().getUsername(), review.getContent(), review.getStarRating());

        given(tradeRepository.findById(trade.getId())).willReturn(Optional.of(trade));
        given(memberRepository.findByUsername(review.getReviewerMember().getUsername())).willReturn(Optional.of(reviewerMember));
        given(memberRepository.findById(review.getMember().getId())).willReturn(Optional.of(member));

        // When & Then
        assertThatThrownBy(() -> reviewService.writeReview(reviewRequestDto, trade.getId(), review.getReviewerMember().getUsername())).isInstanceOf(TradeNotCompleteException.class);
    }
}
