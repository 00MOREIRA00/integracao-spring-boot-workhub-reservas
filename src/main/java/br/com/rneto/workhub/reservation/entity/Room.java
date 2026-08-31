package br.com.rneto.workhub.reservation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private boolean active;

    public Room(String name, Integer capacity, boolean active) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Room name must not be blank.");
        }

        if (capacity == null || capacity <= 0) {
            throw new IllegalArgumentException(
                    "Room capacity must be greater than zero."
            );
        }

        this.name = name.trim();
        this.capacity = capacity;
        this.active = active;
    }

    public void activate(){
        this.active = true;
    }

    public void deactivate(){
        this.active = false;
    }

}
