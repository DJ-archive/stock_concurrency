package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;

@Service
public class StockService {

	private final StockRepository stockRepository;


	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	//@Transactional
	public synchronized void decrease(Long id, Long quantity) {
		// get stock
		Stock stock = stockRepository.findById(id).orElseThrow(IllegalArgumentException::new);
		// 재고 감소
		stock.decrease(quantity);
		// 저장
		stockRepository.saveAndFlush(stock);
	}
}
