package br.com.rneto.workhub.reservation.repository;

import br.com.rneto.workhub.reservation.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    boolean existsByName(String name);

    List<Room> findAllByActive(boolean active);
}
