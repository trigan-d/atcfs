package atc.fs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import atc.fs.model.AccessEvent;

public interface AccessEventRepository extends JpaRepository<AccessEvent, Long> {

}
