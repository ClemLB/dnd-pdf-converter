package fr.kuremento.dnd.model.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MappingId.class)
@Table(schema = "dnd", name = "mapping", uniqueConstraints = {@UniqueConstraint(columnNames = {"ENGLISH_CATEGORY", "FRENCH_CATEGORY"})})
public class DataMapping {

    @Id
    @Column(name = "ENGLISH_CATEGORY")
    private String englishCategory;

    @Id
    @Column(name = "FRENCH_CATEGORY")
    private String frenchCategory;

    @Column(name = "IS_CHECK_BOX")
    private boolean isCheckBox;

}
