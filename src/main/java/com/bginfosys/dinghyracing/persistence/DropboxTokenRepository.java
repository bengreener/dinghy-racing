package com.bginfosys.dinghyracing.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.bginfosys.dinghyracing.model.DropboxToken;

@RepositoryRestResource(exported=false)
public interface DropboxTokenRepository extends JpaRepository<DropboxToken, Long> {

	Optional<DropboxToken> findFirstByOrderByIdDesc();
	
}
