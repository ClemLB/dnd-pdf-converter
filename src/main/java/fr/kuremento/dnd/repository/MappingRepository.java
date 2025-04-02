package fr.kuremento.dnd.repository;

import fr.kuremento.dnd.model.data.DataMapping;
import fr.kuremento.dnd.model.data.MappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MappingRepository extends JpaRepository<DataMapping, MappingId> {

    List<DataMapping> findByEnglishCategory(String englishCategory);

}
