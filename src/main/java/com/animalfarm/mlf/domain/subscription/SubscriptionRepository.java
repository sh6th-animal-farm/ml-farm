package com.animalfarm.mlf.domain.subscription;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.animalfarm.mlf.domain.subscription.dto.SubscriptionHistDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionInsertDTO;

@Mapper
public interface SubscriptionRepository {
	
	public abstract SubscriptionHistDTO select(@Param("userId") Long userId, @Param("projectId") Long projectId);

	public abstract int update(SubscriptionHistDTO subscriptionHistDTO);

	public abstract boolean subscriptionApplication(SubscriptionInsertDTO subscriptionInsertDTO);

	public abstract boolean subscriptionApplicationResponse(SubscriptionInsertDTO subscriptionInsertDTO);

	public abstract boolean updatePlusAmount(SubscriptionInsertDTO subscriptionInsertDTO);

	public abstract Long selectUclId(SubscriptionInsertDTO subscriptionInsertDTO);
}
