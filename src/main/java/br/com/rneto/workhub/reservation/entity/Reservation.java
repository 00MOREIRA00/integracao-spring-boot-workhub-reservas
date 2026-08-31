package br.com.rneto.workhub.reservation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;


@Getter
@Entity
@Table(name = "reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

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

        this.room = room;
        this.requesterId = requesterId.trim();
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.status = ReservationStatus.ACTIVE;
        this.createdAt = Instant.now();
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
