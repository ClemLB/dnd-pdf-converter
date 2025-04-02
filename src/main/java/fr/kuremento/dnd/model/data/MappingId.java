package fr.kuremento.dnd.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MappingId implements Serializable {
    private String englishCategory;
    private String frenchCategory;
}
