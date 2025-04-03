package fr.kuremento.dnd.repository;

import fr.kuremento.dnd.model.data.DataTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslationRepository extends JpaRepository<DataTranslation, Long> {

    List<DataTranslation> findByMapping_FrenchCategory(String frenchCategory);

}
