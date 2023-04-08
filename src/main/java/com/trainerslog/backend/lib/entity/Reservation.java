package com.trainerslog.backend.lib.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.trainerslog.backend.lib.types.ReservationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.IOException;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = { "client_id", "trainer_id", "time_interval_begin"})
)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonSerialize(using = UserSerializer.class)
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private User client;

    @JsonSerialize(using = TrainerSerializer.class)
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "trainer_id", referencedColumnName = "id")
    private Trainer trainer;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    @Column(name = "time_interval_begin")
    private LocalDateTime timeIntervalBegin;

    @Enumerated
    private ReservationType reservationType;

    static class UserSerializer extends JsonSerializer<User> {
        @Override
        @JsonSerialize()
        public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("username", user.getUsername());
            jsonGenerator.writeStringField("fullName", user.getFirstName() + " " + user.getLastName());
            jsonGenerator.writeEndObject();
        }
    }

    static class TrainerSerializer extends JsonSerializer<Trainer> {
        @Override
        @JsonSerialize()
        public void serialize(Trainer trainer, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("username", trainer.getUser().getUsername());
            jsonGenerator.writeStringField("fullName", trainer.getUser().getFirstName() + " " + trainer.getUser().getLastName());
            jsonGenerator.writeEndObject();
        }
    }
}
