package com.iqbal.callmonitoring.repository;

import com.iqbal.callmonitoring.dto.request.CallMonitoringFilterRequest;
import com.iqbal.callmonitoring.entity.CallMonitoring;
import com.iqbal.callmonitoring.repository.base.BasePagingRepository;

public interface CallMonitoringRepository extends BasePagingRepository<CallMonitoring, CallMonitoringFilterRequest> {
}
