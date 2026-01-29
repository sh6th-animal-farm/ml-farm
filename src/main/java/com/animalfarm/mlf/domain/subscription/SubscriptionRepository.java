package com.animalfarm.mlf.domain.subscription;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.animalfarm.mlf.domain.subscription.dto.AllocationTokenDTO;
import com.animalfarm.mlf.domain.subscription.dto.ProjectStartCheckDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionApplicationDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionHistDTO;

@Mapper
public interface SubscriptionRepository {

	public abstract SubscriptionHistDTO select(@Param("userId")
	Long userId, @Param("projectId")
	Long projectId);

	public abstract SubscriptionHistDTO selectPaid(@Param("userId")
	Long userId, @Param("projectId")
	Long projectId);

	public abstract int update(SubscriptionHistDTO subscriptionHistDTO);

	public abstract boolean subscriptionApplication(SubscriptionApplicationDTO subscriptionInsertDTO);

	public abstract boolean updateSubscriptionStatus(SubscriptionApplicationDTO subscriptionInsertDTO);

	public abstract boolean updatePlusAmount(SubscriptionApplicationDTO subscriptionInsertDTO);

	public abstract Long selectUclId(SubscriptionApplicationDTO subscriptionInsertDTO);

	public abstract List<ProjectStartCheckDTO> selectExpiredSubscriptions();

	public abstract boolean updateTokenDelete(Long tokenId);

	public abstract boolean updateProjectCanceled(Long projectId);

	public abstract boolean updateProjectTwoDay(Long projectId);

	public abstract boolean updateProjectInProgress(Long projectId);

	public abstract List<Long> selectSubscriberUserIds(Long projectId);

	public abstract List<String> selectUserEmail(Long projectId);

	public abstract Long selectUclId(Long userId);

	public abstract AllocationTokenDTO selectAllocationInfo(Long projectId);
}
