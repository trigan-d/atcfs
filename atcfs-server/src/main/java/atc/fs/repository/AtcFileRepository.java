package atc.fs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import atc.fs.model.AtcFile;
import atc.fs.model.Record;

public interface AtcFileRepository extends JpaRepository<AtcFile, String> {
	public List<AtcFile> findByRecordAndFileName(Record record, String fileName);
}
