package com.droplet.helix.client.task;

import com.droplet.helix.client.entity.RuntimeDetail;
import com.droplet.helix.client.utils.MonitorUtil;
import com.droplet.helix.client.utils.NetUtil;
import jakarta.annotation.Resource;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class MonitorJobBean extends QuartzJobBean {

    @Resource
    MonitorUtil monitorUtil;

    @Resource
    NetUtil netUtil;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        RuntimeDetail runtimeDetail = monitorUtil.monitorRuntimeDetail();
        netUtil.updateRuntimeDetails(runtimeDetail);
    }
}
