package br.com.rneto.workhub.reservation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;


@Getter
@Entity
@Table(name = "reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {
    private static final Duration MIN_DURATION = Duration.ofMinutes(30);
    private static final Duration MAX_DURATION = Duration.ofHours(4);
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("America/Sao_Paulo");
    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(20, 0);

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "requester_id", nullable = false, length = 100)
    private String requesterId;

    @Column(name = "starts_at", nullable = false)
    private OffsetDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private OffsetDateTime endsAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;


    public Reservation(Room room, String requesterId, OffsetDateTime startsAt, OffsetDateTime endsAt) {

        if (room == null) {
            throw new IllegalArgumentException("Room must not be null.");
        }

        if (!room.isActive()) {
            throw new IllegalArgumentException("Reservations can only be created for active rooms.");
        }

        if (requesterId == null || requesterId.isBlank()) {
            throw new IllegalArgumentException("Requester ID must not be blank.");
        }

        if (startsAt == null) {
            throw new IllegalArgumentException(
                    "Reservation start must not be null."
            );
        }

        if (endsAt == null) {
            throw new IllegalArgumentException(
                    "Reservation end must not be null."
            );
        }

        if (!startsAt.isBefore(endsAt)) {
            throw new IllegalArgumentException("Reservation start must be before reservation end.");
        }

        validateBusinessHours(startsAt, endsAt);

        Duration duration = Duration.between(startsAt, endsAt);

        if (duration.compareTo(MIN_DURATION) < 0) {
            throw new IllegalArgumentException("Reservation duration must be at least 30 minutes.");
        }

        if (duration.compareTo(MAX_DURATION) > 0) {
            throw new IllegalArgumentException("Reservation duration must not exceed 4 hours.");
        }

        validateTimeIncrement(startsAt, endsAt);

        this.room = room;
        this.requesterId = requesterId.trim();
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.status = ReservationStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    private static void validateBusinessHours(OffsetDateTime startsAt, OffsetDateTime endsAt) {
        ZonedDateTime localStart = startsAt.atZoneSameInstant(BUSINESS_ZONE);
        ZonedDateTime localEnd = endsAt.atZoneSameInstant(BUSINESS_ZONE);

        if (!localStart.toLocalDate().equals(localEnd.toLocalDate())) {
            throw new IllegalArgumentException(
                    "Reservation must start and end on the same business day."
            );
        }

        LocalTime startTime = localStart.toLocalTime();
        LocalTime endTime = localEnd.toLocalTime();

        if (startTime.isBefore(OPENING_TIME) || endTime.isAfter(CLOSING_TIME)) {
            throw new IllegalArgumentException(
                    "Reservation must be between 08:00 and 20:00 in America/Sao_Paulo."
            );
        }
    }

    private static void validateTimeIncrement(OffsetDateTime startsAt, OffsetDateTime endsAt) {
        ZonedDateTime localStart = startsAt.atZoneSameInstant(BUSINESS_ZONE);
        ZonedDateTime localEnd = endsAt.atZoneSameInstant(BUSINESS_ZONE);

        if (!isValidTimeIncrement(localStart.toLocalTime()) || !isValidTimeIncrement(localEnd.toLocalTime())) {
            throw new IllegalArgumentException(
                    "Reservation start and end times must be in 30-minute increments."
            );
        }
    }

    private static boolean isValidTimeIncrement(LocalTime time) {
        boolean validMinute =
                time.getMinute() == 0 || time.getMinute() == 30;

        boolean hasNoSeconds =
                time.getSecond() == 0 && time.getNano() == 0;

        return validMinute && hasNoSeconds;
    }

    public boolean overlaps(OffsetDateTime requestedStart, OffsetDateTime requestedEnd) {
        if (requestedStart == null || requestedEnd == null) {
            throw new IllegalArgumentException(
                    "Requested start and end times must not be null."
            );
        }

        if (!requestedStart.isBefore(requestedEnd)) {
            throw new IllegalArgumentException(
                    "Period start must be before period end."
            );
        }

        if (status == ReservationStatus.CANCELLED) {
            return false;
        }

        return startsAt.isBefore(requestedEnd) && endsAt.isAfter(requestedStart);
    }

    public void cancel(Instant cancelledAt) {
        if (status == ReservationStatus.CANCELLED) {
            return;
        }

        if (cancelledAt == null) {
            throw new IllegalArgumentException(
                    "Cancellation instant must not be null."
            );
        }

        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = cancelledAt;
    }

}
