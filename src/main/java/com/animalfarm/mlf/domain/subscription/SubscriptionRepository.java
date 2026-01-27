package com.animalfarm.mlf.domain.subscription;

import org.apache.ibatis.annotations.Mapper;

import com.animalfarm.mlf.domain.subscription.dto.SubscriptionHistDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionInsertDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionSelectDTO;

@Mapper
public interface SubscriptionRepository {

	public abstract SubscriptionHistDTO select(SubscriptionSelectDTO subscriptionSelectDTO);

	public abstract boolean subscriptionApplication(SubscriptionInsertDTO subscriptionInsertDTO);

	public abstract boolean subscriptionApplicationResponse(SubscriptionInsertDTO subscriptionInsertDTO);

	public abstract boolean updatePlusAmount(SubscriptionInsertDTO subscriptionInsertDTO);

	public abstract Long selectUclId(SubscriptionInsertDTO subscriptionInsertDTO);
}
