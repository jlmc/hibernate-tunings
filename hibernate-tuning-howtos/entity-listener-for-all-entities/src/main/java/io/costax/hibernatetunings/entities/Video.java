package io.costax.hibernatetunings.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table
public class Video {

   @Id
    private Integer id;

   @Column(nullable = false)
   private String name;

   @NotNull
   private String description;

   protected Video() {
   }

   private Video(final Integer id, final String name, @NotNull final String description) {
      this.id = id;
      this.name = name;
      this.description = description;
   }

   public static Video of(final Integer id, final String name, @NotNull final String description) {
      return new Video(id, name, description);
   }

   @Override
   public String toString() {
      return "Video{" +
              "id=" + id +
              ", name='" + name + '\'' +
              ", description='" + description + '\'' +
              '}';
   }

   @Override
   public boolean equals(final Object o) {
      if (this == o) return true;
      if (!(o instanceof Video)) return false;
      final Video video = (Video) o;
      return Objects.equals(id, video.id);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id);
   }

   public Integer getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }
}
