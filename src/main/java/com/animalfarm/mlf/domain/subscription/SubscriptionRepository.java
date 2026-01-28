package com.animalfarm.mlf.domain.subscription;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.animalfarm.mlf.domain.subscription.dto.SubscriptionHistDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionApplicationDTO;

@Mapper
public interface SubscriptionRepository {
	
	public abstract SubscriptionHistDTO select(@Param("userId") Long userId, @Param("projectId") Long projectId);

	public abstract int update(SubscriptionHistDTO subscriptionHistDTO);

	public abstract boolean subscriptionApplication(SubscriptionApplicationDTO subscriptionInsertDTO);

	public abstract boolean updateSubscriptionStatus(SubscriptionApplicationDTO subscriptionInsertDTO);

	public abstract boolean updatePlusAmount(SubscriptionApplicationDTO subscriptionInsertDTO);

	public abstract Long selectUclId(SubscriptionApplicationDTO subscriptionInsertDTO);
}
