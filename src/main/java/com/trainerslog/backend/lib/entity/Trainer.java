package com.trainerslog.backend.lib.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @JsonFormat(pattern="HH:mm:ss")
    @Column(name = "start_of_day", columnDefinition = "TIME")
    private LocalTime startOfDay;

    @JsonFormat(pattern="HH:mm:ss")
    @Column(name = "end_of_day", columnDefinition = "TIME")
    private LocalTime endOfDay;

    @Column(columnDefinition = "integer default 1")
    private Integer totalClientsPerReservation = 1;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "trainer", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Reservation> trainerReservations;
}
