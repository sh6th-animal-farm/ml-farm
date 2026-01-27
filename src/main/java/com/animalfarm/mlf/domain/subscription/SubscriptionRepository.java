package com.animalfarm.mlf.domain.subscription;

import org.apache.ibatis.annotations.Mapper;

import com.animalfarm.mlf.domain.subscription.dto.SubscriptionHistDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionApplicationDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionSelectDTO;

@Mapper
public interface SubscriptionRepository {

	public abstract SubscriptionHistDTO select(SubscriptionSelectDTO subscriptionSelectDTO);

	public abstract boolean subscriptionApplication(SubscriptionApplicationDTO subscriptionInsertDTO);

	public abstract boolean updateSubscriptionStatus(SubscriptionApplicationDTO subscriptionInsertDTO);

	public abstract boolean updatePlusAmount(SubscriptionApplicationDTO subscriptionInsertDTO);

	public abstract Long selectUclId(SubscriptionApplicationDTO subscriptionInsertDTO);
}
