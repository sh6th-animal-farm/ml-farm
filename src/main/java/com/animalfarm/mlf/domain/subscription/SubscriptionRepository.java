package com.animalfarm.mlf.domain.subscription;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.animalfarm.mlf.domain.subscription.dto.SubscriptionHistDTO;

@Mapper
public interface SubscriptionRepository {

	public abstract SubscriptionHistDTO select(
		@Param("userId")
		Long userId,
		@Param("projectId")
		Long projectId);

	public abstract int update(SubscriptionHistDTO subscriptionHistDTO);
}
