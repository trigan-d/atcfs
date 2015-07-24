package atc.fs.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import atc.fs.model.Vis;

public interface VisRepository extends JpaRepository<Vis, String> {
	@Cacheable("vis")
	public Vis findOne(String id);
}
