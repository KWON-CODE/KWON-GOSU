package com.cleaning.platform.service;

import com.cleaning.platform.domain.Booking;
import com.cleaning.platform.domain.PaymentHistory;
import com.cleaning.platform.dto.PaymentDto;
import com.cleaning.platform.repository.BookingRepository;
import com.cleaning.platform.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;


import com.siot.IamportRestClient.IamportClient;          // ✅
import com.siot.IamportRestClient.response.IamportResponse; // ✅
import com.siot.IamportRestClient.response.Payment;         // ✅
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentHistoryRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final IamportClient iamportClient;


    public PaymentService(PaymentHistoryRepository paymentRepository,
                       BookingRepository bookingRepository,
                       IamportClient iamportClient) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.iamportClient = iamportClient;
    }



    @Transactional
    public void verifyAndSavePayment(String impUid, String merchantUid, String userEmail) throws Exception {

        Booking booking = bookingRepository.findById(merchantUid)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다. ID: " + merchantUid));

        if (!booking.getUsers().getEmail().equals(userEmail)) {
            throw new IllegalStateException("자신의 예약에 대해서만 결제 상태를 확인할 수 있습니다.");
        }


        IamportResponse<Payment> paymentResponse = iamportClient.paymentByImpUid(impUid);
        long paidAmount = paymentResponse.getResponse().getAmount().longValue();


        if (booking.getQuotedPrice() != paidAmount) {
            throw new IllegalStateException("결제 금액이 예약된 금액과 일치하지 않습니다.");
        }


        if (paymentRepository.findByImpUid(impUid).isPresent()) {
            throw new IllegalStateException("이미 처리된 결제입니다.");
        }

        PaymentHistory payment = PaymentHistory.builder()
                .id("PAY-" + UUID.randomUUID().toString().substring(0, 7))
                .booking(booking)
                .amount(booking.getQuotedPrice())
                .paymentMethod(paymentResponse.getResponse().getPayMethod())
                .impUid(impUid)
                .build();
        paymentRepository.save(payment);
    }

    public List<PaymentHistory> findPaymentsByUser(String userEmail) {
        return paymentRepository.findByBookingUsersEmail(userEmail);
    }
}