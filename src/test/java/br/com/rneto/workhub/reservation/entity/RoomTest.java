package br.com.rneto.workhub.reservation.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomTest {

    @Test
    void shouldCreateRoomAndTrimName() {
        Room room = new Room("  Ipe  ", 10, true);

        assertEquals("Ipe", room.getName());
        assertEquals(10, room.getCapacity());
        assertTrue(room.isActive());
    }

    @Test
    void shouldCreateInactiveRoom() {
        Room room = new Room("Ipe", 10, false);

        assertFalse(room.isActive());
    }

    @Test
    void shouldRejectNullName() {
        assertInvalidRoomName(null);
    }

    @Test
    void shouldRejectEmptyName() {
        assertInvalidRoomName("");
    }

    @Test
    void shouldRejectBlankName() {
        assertInvalidRoomName("   ");
    }

    @Test
    void shouldRejectNullCapacity() {
        assertInvalidRoomCapacity(null);
    }

    @Test
    void shouldRejectZeroCapacity() {
        assertInvalidRoomCapacity(0);
    }

    @Test
    void shouldRejectNegativeCapacity() {
        assertInvalidRoomCapacity(-1);
    }

    @Test
    void shouldActivateRoom() {
        Room room = new Room("Ipe", 10, false);

        room.activate();

        assertTrue(room.isActive());
    }

    @Test
    void shouldDeactivateRoom() {
        Room room = new Room("Ipe", 10, true);

        room.deactivate();

        assertFalse(room.isActive());
    }

    private void assertInvalidRoomName(String name) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Room(name, 10, true)
        );

        assertEquals("Room name must not be blank.", exception.getMessage());
    }

    private void assertInvalidRoomCapacity(Integer capacity) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Room("Ipe", capacity, true)
        );

        assertEquals("Room capacity must be greater than zero.", exception.getMessage());
    }
}
