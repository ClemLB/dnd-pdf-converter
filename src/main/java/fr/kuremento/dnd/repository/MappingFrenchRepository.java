package fr.kuremento.dnd.repository;

import fr.kuremento.dnd.model.data.DataMappingFrench;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MappingFrenchRepository extends JpaRepository<DataMappingFrench, Long> {

    List<DataMappingFrench> findByEnglishCategory(String englishCategory);

}
