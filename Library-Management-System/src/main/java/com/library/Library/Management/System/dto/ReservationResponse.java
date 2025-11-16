package com.library.Library.Management.System.dto;

public class ReservationResponse {

    private Long reservationId;
    private String userEmail;
    private String bookTitle;
    private String startDate;
    private String endDate;
    private String status;

    public ReservationResponse(Long reservationId, String userEmail, String bookTitle,
                               String startDate, String endDate, String status) {
        this.reservationId = reservationId;
        this.userEmail = userEmail;
        this.bookTitle = bookTitle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Long getReservationId() { return reservationId; }
    public String getUserEmail() { return userEmail; }
    public String getBookTitle() { return bookTitle; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getStatus() { return status; }
}
