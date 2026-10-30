package com.example.appointmentservice.service;

import com.example.appointmentservice.dto.ApiResponseError;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class AppointmentService {

    @Autowired
    private RestTemplate restTemplate;

    @CircuitBreaker(name = "doctorServiceCB", fallbackMethod = "getDoctorFallback")
    public Object AddAppointment() {
        // Giả lập gọi sang Doctor-Service ở cổng 8082
        String doctorUrl = "http://localhost:8082/doctors/check";
        return restTemplate.getForObject(doctorUrl, String.class);
    }

    // Fallback method khi Doctor-Service sập hoặc Circuit Breaker đang OPEN
    public ApiResponseError getDoctorFallback(Exception e) {
        return ApiResponseError.builder()
                .message("Hiện tại không thể kiểm tra thông tin bác sĩ, vui lòng thử lại sau vài giây")
                .status(503)
                .error("Doctor Service Error")
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Overload dự phòng nếu method có tham số
    public ApiResponseError getDoctorFallback(Object request, Exception e) {
        return getDoctorFallback(e);
    }
}
