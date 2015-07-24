package atc.fs.repository;

import atc.fs.model.TemporaryLinkAccessEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporaryLinkAccessEventRepository extends
		JpaRepository<TemporaryLinkAccessEvent, Long> {

}
