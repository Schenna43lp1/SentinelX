package com.sentinelx.server.controller.web;

import com.sentinelx.server.domain.entity.Alert;
import com.sentinelx.server.domain.entity.Metric;
import com.sentinelx.server.domain.entity.Node;
import com.sentinelx.server.service.AlertService;
import com.sentinelx.server.service.MetricService;
import com.sentinelx.server.service.NodeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final NodeService nodeService;
    private final AlertService alertService;
    private final MetricService metricService;

    public DashboardController(NodeService nodeService, AlertService alertService, MetricService metricService) {
        this.nodeService = nodeService;
        this.alertService = alertService;
        this.metricService = metricService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        long nodesOnline = nodeService.countOnline();
        long nodesOffline = nodeService.countOffline();
        long activeAlerts = alertService.countOpen();
        double avgCpu = metricService.getAverageCpu();
        double avgRam = metricService.getAverageRam();

        model.addAttribute("nodesOnline", nodesOnline);
        model.addAttribute("nodesOffline", nodesOffline);
        model.addAttribute("activeAlerts", activeAlerts);
        model.addAttribute("avgCpu", String.format("%.1f", avgCpu));
        model.addAttribute("avgRam", String.format("%.1f", avgRam));

        // Recent open alerts for the alert panel
        List<Alert> recentAlerts = alertService.findOpen();
        model.addAttribute("recentAlerts", recentAlerts.stream().limit(10).toList());

        // Node list with latest metric for chart data
        List<Node> nodes = nodeService.findAll();
        model.addAttribute("nodes", nodes);

        return "dashboard";
    }
}
