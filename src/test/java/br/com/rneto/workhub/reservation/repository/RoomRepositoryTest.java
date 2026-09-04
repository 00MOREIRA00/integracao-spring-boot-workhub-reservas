package br.com.rneto.workhub.reservation.repository;

import br.com.rneto.workhub.reservation.entity.Room;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=none")
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveAndFindRoomById() {
        Room savedRoom = roomRepository.saveAndFlush(
                new Room("Ipe", 10, true)
        );

        UUID roomId = savedRoom.getId();
        entityManager.clear();

        Optional<Room> result = roomRepository.findById(roomId);

        assertTrue(result.isPresent());
        assertEquals("Ipe", result.get().getName());
        assertEquals(10, result.get().getCapacity());
        assertTrue(result.get().isActive());
    }

    @Test
    void shouldReturnTrueWhenRoomNameExists() {
        roomRepository.saveAndFlush(
                new Room("Cedro", 8, true)
        );
        entityManager.clear();

        assertTrue(roomRepository.existsByName("Cedro"));
    }

    @Test
    void shouldReturnFalseWhenRoomNameDoesNotExist() {
        assertFalse(roomRepository.existsByName("Sala inexistente"));
    }

    @Test
    void shouldFindOnlyActiveRooms() {
        roomRepository.saveAllAndFlush(List.of(
                new Room("Ipe", 10, true),
                new Room("Cedro", 8, false),
                new Room("Jatoba", 12, true)
        ));
        entityManager.clear();

        List<Room> result = roomRepository.findAllByActive(true);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Room::isActive));
    }

    @Test
    void shouldFindOnlyInactiveRooms() {
        roomRepository.saveAllAndFlush(List.of(
                new Room("Ipe", 10, true),
                new Room("Cedro", 8, false),
                new Room("Jatoba", 12, false)
        ));
        entityManager.clear();

        List<Room> result = roomRepository.findAllByActive(false);

        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(Room::isActive));
    }

    @Test
    void shouldRejectDuplicatedRoomName() {
        roomRepository.saveAndFlush(
                new Room("Ipe", 10, true)
        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> roomRepository.saveAndFlush(
                        new Room("Ipe", 20, false)
                )
        );
    }
}
