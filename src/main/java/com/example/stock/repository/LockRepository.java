package com.example.stock.repository;

import com.example.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/*
 * 편의성을 위해 jpa 네이티브 쿼리 기능, 동일한 데이터 소스 사용
 * 실무에서는 데이터소스를 분리해서 사용하기. 커넥션 풀의 커넥션 부족- 다른 서비스 영향
 */

public interface LockRepository extends JpaRepository<Stock, Long> {

	@Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
	void getLock(String key);

	@Query(value = "select release_lock(:key)", nativeQuery = true)
	void releaseLock(String key);
}
