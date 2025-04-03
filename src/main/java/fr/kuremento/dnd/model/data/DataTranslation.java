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
@Table(schema = "dnd", name = "translation", uniqueConstraints = {@UniqueConstraint(columnNames = {"ENGLISH_VALUE", "FRENCH_VALUE"})})
public class DataTranslation {

    @Id
    @Column(name = "TRANSLATION_ID")
    private Long translationId;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID", nullable = false, referencedColumnName = "MAPPING_ID")
    private DataMapping mapping;

    @Column(name = "ENGLISH_VALUE")
    private String englishValue;

    @Column(name = "FRENCH_VALUE")
    private String frenchValue;

}
