package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockServiceTest {

	@Autowired
	private StockService stockService;

	@Autowired
	private StockRepository stockRepository;

	@BeforeEach
	public void before() {
		Stock stock = new Stock(1L, 100L);
		stockRepository.saveAndFlush(stock);
	}

	@AfterEach
	public void after() {
		stockRepository.deleteAll();
	}

	@Test
	public void stock_decrease() {
		stockService.decrease(1L, 1L);

		// 100 - 1 = 99

		Stock stock = stockRepository.findById(1L).orElseThrow();

		assertEquals(99, stock.getQuantity());
	}

	/*
	 * 레이스 컨디션(Race Condition) 문제 발생
	 * -> 정상 결과: 0 / 테스트 결과: 89 (테스트 실패)
	 * -> 하나의 쓰레드가 작업을 완료한 후에 다른 쓰레드가 데이터에 접근할 수 있도록 하여 동시성 문제를 해결해야 함!
	 */
	@Test
	public void 동시에_100개의_요청() throws InterruptedException {
		int threadCnt = 100;
		// ExecutorService: 비동기로 실행하는 작업을 단순화하여 실행될 수 있도록 도와줌. 병렬 작업.
		ExecutorService executorService = Executors.newFixedThreadPool(30); // 쓰레드 풀의 쓰레드 갯수 임의로 설정
		// CountDownLatch: 다른 쓰레드에서 수행 중인 작업이 완료될 때까지 대기할 수 있도록 도와줌. 쓰레드들이 모두 끝나기를 기다리는 쪽 입장에서 await() 메서드를 호출한다.
		CountDownLatch latch = new CountDownLatch(threadCnt);

		for (int i = 0; i < threadCnt; i++) {
			executorService.submit(() -> {
				try {
					stockService.decrease(1L, 1L);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		Stock stock = stockRepository.findById(1L).orElseThrow();

		// 100 - (1*100) = 0 예상
		assertEquals(0L, stock.getQuantity());
	}

}