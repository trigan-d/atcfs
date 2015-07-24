package atc.fs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import atc.fs.model.Record;

public interface RecordRepository extends JpaRepository<Record, String> {

}
