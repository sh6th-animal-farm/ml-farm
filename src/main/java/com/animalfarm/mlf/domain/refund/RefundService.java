package com.animalfarm.mlf.domain.refund;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefundService {
	@Autowired
	RefundRepository refundRepository;

	public void insertRefunds(List<RefundDTO> refundList) {
		for (RefundDTO refund : refundList) {
			refundRepository.insertRefund(refund);
		}
	}
}
