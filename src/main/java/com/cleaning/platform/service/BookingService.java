package com.cleaning.platform.service;

import com.cleaning.platform.domain.*;
import com.cleaning.platform.dto.AcServiceDto;
import com.cleaning.platform.dto.BookingDto;
import com.cleaning.platform.dto.MovingServiceDto;
import com.cleaning.platform.dto.ReviewDto;
import com.cleaning.platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ServiceProviderRepository providerRepository;
    private final AcServiceRepository acServiceRepository;
    private final MovingServiceRepository movingServiceRepository;
    private final PostRepository postRepository;

    public BookingService(BookingRepository bookingRepository,
                          ReviewRepository reviewRepository,
                          UserRepository userRepository,
                          ServiceProviderRepository providerRepository,
                          AcServiceRepository acServiceRepository,
                          MovingServiceRepository movingServiceRepository,
                          PostRepository postRepository) {
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
        this.acServiceRepository = acServiceRepository;
        this.movingServiceRepository = movingServiceRepository;
        this.postRepository = postRepository;

    }


    @Transactional
    public void createBooking(BookingDto dto) {
        Address address = new Address(dto.getZipcode(), dto.getMainAddress(), dto.getDetailAddress());


        Users user = userRepository.findByEmail(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + dto.getUserId()));
        ServiceProvider provider = providerRepository.findByUsersId(dto.getProviderId())
                .orElseThrow(() -> new IllegalArgumentException("서비스 제공자를 찾을 수 없습니다: " + dto.getProviderId()));

        Post post = null; // post는 null일 수 있으므로 초기화
        if (dto.getPostId() != null && !dto.getPostId().isBlank()) {
            post = postRepository.findById(dto.getPostId())
                    .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + dto.getPostId()));
        }

        Booking booking = Booking.builder()
                .id("B-" + UUID.randomUUID().toString().substring(0, 7))
                .users(user)
                .provider(provider)
                .post(post)
                .serviceCategory(dto.getServiceCategory())
                .desiredDateTime(dto.getDesiredDateTime())
                .quotedPrice(dto.getQuotedPrice())
                .address(address)
                .build();
        bookingRepository.save(booking);
    }

    @Transactional
    public void createReview(String bookingId, String userEmail, ReviewDto dto) {
        Booking booking = findBookingByIdOrThrow(bookingId);
        if (!booking.getUsers().getEmail().equals(userEmail)) {
            throw new IllegalStateException("자신의 예약에만 리뷰를 작성할 수 있습니다.");
        }
        Review review = Review.builder()
                .id("R-" + UUID.randomUUID().toString().substring(0, 7))
                .booking(booking)
                .users(booking.getUsers())
                .provider(booking.getProvider())
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();
        reviewRepository.save(review);
    }

    @Transactional
    public void createAcService(String providerId, AcServiceDto dto) {
        ServiceProvider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("제공자 없음"));
        AcService acService = AcService.builder()
                .id("AC-" + UUID.randomUUID().toString().substring(0, 7))
                .provider(provider)
                .serviceType(dto.getServiceType())
                .productType(dto.getProductType())
                .priceRangeMin(dto.getPriceRangeMin())
                .priceRangeMax(dto.getPriceRangeMax())
                .description(dto.getDescription())
                .build();
        acServiceRepository.save(acService);
    }

    @Transactional
    public void createMovingService(String providerId, MovingServiceDto dto) {
        ServiceProvider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("제공자 없음"));
        MovingService movingService = MovingService.builder()
                .id("MV-" + UUID.randomUUID().toString().substring(0, 7))
                .provider(provider)
                .movingType(dto.getMovingType())
                .capacity(dto.getCapacity())
                .basePrice(dto.getBasePrice())
                .additionalInfo(dto.getAdditionalInfo())
                .build();
        movingServiceRepository.save(movingService);
    }

    public List<Booking> findBookingsByUser(String userEmail) {
        return bookingRepository.findByUsersEmailOrderByCreatedAtDesc(userEmail);
    }

    public List<Review> findAllReviews() {
        return reviewRepository.findAll();
    }

    public Booking findBookingByIdOrThrow(String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보가 없습니다."));
    }

}