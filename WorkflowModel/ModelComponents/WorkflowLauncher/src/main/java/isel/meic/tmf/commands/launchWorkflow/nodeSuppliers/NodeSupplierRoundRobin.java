package isel.meic.tmf.commands.launchWorkflow.nodeSuppliers;

import isel.meic.tmf.models.activityBucket.ActivityRequestBodyDto;
import isel.meic.tmf.models.infraestructure.InfraestructureSpecificationDto;
import isel.meic.tmf.models.infraestructure.InfraestructureNodeDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeSupplierRoundRobin implements INodeSupplier {
    private final InfraestructureSpecificationDto infraestructureConfiguration;
    private final List<ActivityRequestBodyDto> activityRequests;

    private final Map<String, InfraestructureNodeDto> map;
    private final RoundRobinSupplier<InfraestructureNodeDto> roundRobinSupplier;

    public NodeSupplierRoundRobin(InfraestructureSpecificationDto infraestructureConfiguration,
                                  List<ActivityRequestBodyDto> activityRequests) {
        this.infraestructureConfiguration = infraestructureConfiguration;
        this.activityRequests = activityRequests;
        map = new HashMap<>();
        init();
        this.roundRobinSupplier = new RoundRobinSupplier<>(infraestructureConfiguration.nodes);
    }

    private void init() {
        var nodes = infraestructureConfiguration.nodes;

        for (int i = 0, j = 0; i < activityRequests.size(); i++, j++) {
            if (j >= nodes.length) j = 0;
            map.put(activityRequests.get(i).activityName, nodes[j]);
        }
    }

    public InfraestructureNodeDto getNodeForActivityWithId(String id) {
        return roundRobinSupplier.get();
    }
}
