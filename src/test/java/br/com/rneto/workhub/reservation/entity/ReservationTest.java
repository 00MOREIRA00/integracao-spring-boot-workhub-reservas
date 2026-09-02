package br.com.rneto.workhub.reservation.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservationTest {

    private static final OffsetDateTime STARTS_AT =
            OffsetDateTime.parse("2026-08-20T14:00:00-03:00");

    private final Room activeRoom = new Room("Ipe", 10, true);

    @Test
    void shouldAcceptMinimumDuration() {
        assertDoesNotThrow(() -> new Reservation(
                activeRoom,
                "employee-193",
                STARTS_AT,
                STARTS_AT.plusMinutes(30)
        ));
    }

    @Test
    void shouldAcceptMaximumDuration() {
        assertDoesNotThrow(() -> new Reservation(
                activeRoom,
                "employee-193",
                STARTS_AT,
                STARTS_AT.plusHours(4)
        ));
    }

    @Test
    void shouldRejectDurationShorterThanMinimum() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(
                        activeRoom,
                        "employee-193",
                        STARTS_AT,
                        STARTS_AT.plusMinutes(29)
                )
        );

        assertEquals(
                "Reservation duration must be at least 30 minutes.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectDurationLongerThanMaximum() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(
                        activeRoom,
                        "employee-193",
                        STARTS_AT,
                        STARTS_AT.plusHours(4).plusMinutes(1)
                )
        );

        assertEquals(
                "Reservation duration must not exceed 4 hours.",
                exception.getMessage()
        );
    }

    @Test
    void shouldAcceptReservationAtOpeningTimeUsingUtcOffset() {
        assertDoesNotThrow(() -> new Reservation(
                activeRoom,
                "employee-193",
                OffsetDateTime.parse("2026-08-20T11:00:00Z"),
                OffsetDateTime.parse("2026-08-20T11:30:00Z")
        ));
    }

    @Test
    void shouldAcceptReservationEndingAtClosingTime() {
        assertDoesNotThrow(() -> new Reservation(
                activeRoom,
                "employee-193",
                OffsetDateTime.parse("2026-08-20T19:30:00-03:00"),
                OffsetDateTime.parse("2026-08-20T20:00:00-03:00")
        ));
    }

    @Test
    void shouldRejectReservationBeforeOpeningTime() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(
                        activeRoom,
                        "employee-193",
                        OffsetDateTime.parse("2026-08-20T07:30:00-03:00"),
                        OffsetDateTime.parse("2026-08-20T08:30:00-03:00")
                )
        );

        assertEquals(
                "Reservation must be between 08:00 and 20:00 in America/Sao_Paulo.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectReservationAfterClosingTime() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(
                        activeRoom,
                        "employee-193",
                        OffsetDateTime.parse("2026-08-20T19:30:00-03:00"),
                        OffsetDateTime.parse("2026-08-20T20:30:00-03:00")
                )
        );

        assertEquals(
                "Reservation must be between 08:00 and 20:00 in America/Sao_Paulo.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectReservationAcrossBusinessDays() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(
                        activeRoom,
                        "employee-193",
                        OffsetDateTime.parse("2026-08-20T19:30:00-03:00"),
                        OffsetDateTime.parse("2026-08-21T08:30:00-03:00")
                )
        );

        assertEquals(
                "Reservation must start and end on the same business day.",
                exception.getMessage()
        );
    }

    @Test
    void shouldAcceptThirtyMinuteIncrementUsingUtcOffset() {
        assertDoesNotThrow(() -> new Reservation(
                activeRoom,
                "employee-193",
                OffsetDateTime.parse("2026-08-20T12:30:00Z"),
                OffsetDateTime.parse("2026-08-20T13:00:00Z")
        ));
    }

    @Test
    void shouldRejectInvalidMinuteIncrement() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(
                        activeRoom,
                        "employee-193",
                        OffsetDateTime.parse("2026-08-20T09:10:00-03:00"),
                        OffsetDateTime.parse("2026-08-20T10:10:00-03:00")
                )
        );

        assertEquals(
                "Reservation start and end times must be in 30-minute increments.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectTimeWithSeconds() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(
                        activeRoom,
                        "employee-193",
                        OffsetDateTime.parse("2026-08-20T09:00:30-03:00"),
                        OffsetDateTime.parse("2026-08-20T10:00:30-03:00")
                )
        );

        assertEquals(
                "Reservation start and end times must be in 30-minute increments.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectTimeWithNanoseconds() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(
                        activeRoom,
                        "employee-193",
                        OffsetDateTime.parse("2026-08-20T09:00:00.500-03:00"),
                        OffsetDateTime.parse("2026-08-20T10:00:00.500-03:00")
                )
        );

        assertEquals(
                "Reservation start and end times must be in 30-minute increments.",
                exception.getMessage()
        );
    }

    @Test
    void shouldDetectOverlapAtBeginningOfExistingReservation() {
        Reservation reservation = createReservation("10:00", "11:00");

        assertTrue(reservation.overlaps(at("09:30"), at("10:30")));
    }

    @Test
    void shouldDetectOverlapAtEndOfExistingReservation() {
        Reservation reservation = createReservation("10:00", "11:00");

        assertTrue(reservation.overlaps(at("10:30"), at("11:30")));
    }

    @Test
    void shouldDetectIdenticalPeriodAsOverlap() {
        Reservation reservation = createReservation("10:00", "11:00");

        assertTrue(reservation.overlaps(at("10:00"), at("11:00")));
    }

    @Test
    void shouldDetectRequestedPeriodContainingExistingReservation() {
        Reservation reservation = createReservation("10:00", "11:00");

        assertTrue(reservation.overlaps(at("09:00"), at("12:00")));
    }

    @Test
    void shouldDetectRequestedPeriodInsideExistingReservation() {
        Reservation reservation = createReservation("10:00", "12:00");

        assertTrue(reservation.overlaps(at("10:30"), at("11:30")));
    }

    @Test
    void shouldAllowAdjacentPeriodBeforeExistingReservation() {
        Reservation reservation = createReservation("10:00", "11:00");

        assertFalse(reservation.overlaps(at("09:00"), at("10:00")));
    }

    @Test
    void shouldAllowAdjacentPeriodAfterExistingReservation() {
        Reservation reservation = createReservation("10:00", "11:00");

        assertFalse(reservation.overlaps(at("11:00"), at("12:00")));
    }

    @Test
    void shouldIgnoreOverlapWhenReservationIsCancelled() {
        Reservation reservation = createReservation("10:00", "11:00");
        reservation.cancel(Instant.parse("2026-08-19T12:00:00Z"));

        assertFalse(reservation.overlaps(at("10:00"), at("11:00")));
    }

    @Test
    void shouldRejectNullRequestedPeriod() {
        Reservation reservation = createReservation("10:00", "11:00");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservation.overlaps(null, at("11:00"))
        );

        assertEquals(
                "Requested start and end times must not be null.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectRequestedPeriodWithStartNotBeforeEnd() {
        Reservation reservation = createReservation("10:00", "11:00");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservation.overlaps(at("11:00"), at("11:00"))
        );

        assertEquals(
                "Period start must be before period end.",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullRoom() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(null, "employee-193", STARTS_AT, STARTS_AT.plusHours(1))
        );

        assertEquals("Room must not be null.", exception.getMessage());
    }

    @Test
    void shouldRejectInactiveRoom() {
        Room inactiveRoom = new Room("Cedro", 8, false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(inactiveRoom, "employee-193", STARTS_AT, STARTS_AT.plusHours(1))
        );

        assertEquals("Reservations can only be created for active rooms.", exception.getMessage());
    }

    @Test
    void shouldRejectNullRequesterId() {
        assertInvalidRequesterId(null);
    }

    @Test
    void shouldRejectEmptyRequesterId() {
        assertInvalidRequesterId("");
    }

    @Test
    void shouldRejectBlankRequesterId() {
        assertInvalidRequesterId("   ");
    }

    @Test
    void shouldTrimRequesterId() {
        Reservation reservation = new Reservation(
                activeRoom,
                "  employee-193  ",
                STARTS_AT,
                STARTS_AT.plusHours(1)
        );

        assertEquals("employee-193", reservation.getRequesterId());
    }

    @Test
    void shouldRejectNullStart() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(activeRoom, "employee-193", null, STARTS_AT.plusHours(1))
        );

        assertEquals("Reservation start must not be null.", exception.getMessage());
    }

    @Test
    void shouldRejectNullEnd() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(activeRoom, "employee-193", STARTS_AT, null)
        );

        assertEquals("Reservation end must not be null.", exception.getMessage());
    }

    @Test
    void shouldRejectStartEqualToEnd() {
        assertInvalidPeriodOrder(STARTS_AT, STARTS_AT);
    }

    @Test
    void shouldRejectStartAfterEnd() {
        assertInvalidPeriodOrder(STARTS_AT, STARTS_AT.minusMinutes(30));
    }

    @Test
    void shouldCreateActiveReservationWithCreationInstant() {
        Reservation reservation = createReservation("10:00", "11:00");

        assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());
        assertNotNull(reservation.getCreatedAt());
        assertEquals(activeRoom, reservation.getRoom());
        assertEquals(at("10:00"), reservation.getStartsAt());
        assertEquals(at("11:00"), reservation.getEndsAt());
    }

    @Test
    void shouldCancelReservation() {
        Reservation reservation = createReservation("10:00", "11:00");
        Instant cancellationInstant = Instant.parse("2026-08-19T12:00:00Z");

        reservation.cancel(cancellationInstant);

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertEquals(cancellationInstant, reservation.getCancelledAt());
    }

    @Test
    void shouldRejectNullCancellationInstant() {
        Reservation reservation = createReservation("10:00", "11:00");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservation.cancel(null)
        );

        assertEquals("Cancellation instant must not be null.", exception.getMessage());
        assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());
    }

    @Test
    void shouldKeepOriginalInstantWhenCancelledAgain() {
        Reservation reservation = createReservation("10:00", "11:00");
        Instant firstCancellation = Instant.parse("2026-08-19T12:00:00Z");

        reservation.cancel(firstCancellation);
        reservation.cancel(Instant.parse("2026-08-19T13:00:00Z"));

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertEquals(firstCancellation, reservation.getCancelledAt());
    }

    private Reservation createReservation(String start, String end) {
        return new Reservation(
                activeRoom,
                "employee-193",
                at(start),
                at(end)
        );
    }

    private OffsetDateTime at(String time) {
        return OffsetDateTime.parse("2026-08-20T" + time + ":00-03:00");
    }

    private void assertInvalidRequesterId(String requesterId) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(activeRoom, requesterId, STARTS_AT, STARTS_AT.plusHours(1))
        );

        assertEquals("Requester ID must not be blank.", exception.getMessage());
    }

    private void assertInvalidPeriodOrder(OffsetDateTime startsAt, OffsetDateTime endsAt) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Reservation(activeRoom, "employee-193", startsAt, endsAt)
        );

        assertEquals("Reservation start must be before reservation end.", exception.getMessage());
    }
}
