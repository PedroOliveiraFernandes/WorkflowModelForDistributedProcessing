package isel.meic.tmf.commands.launchWorkflow.nodeSuppliers;

import isel.meic.tmf.models.infraestructure.InfraestructureSpecificationDto;
import isel.meic.tmf.models.infraestructure.InfraestructureNodeDto;
import isel.meic.tmf.models.workflowConfiguration.MappingActivitiesToNodesSpecification;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class NodeSupplierManualConfiguration implements INodeSupplier {
    private final InfraestructureSpecificationDto infraestructureConfiguration;
    private final List<KeyValuePair<String, RoundRobinSupplier<InfraestructureNodeDto>>> mappingConfiguration;

    public NodeSupplierManualConfiguration(InfraestructureSpecificationDto infraestructureConfiguration,
                                           MappingActivitiesToNodesSpecification activityToNodesMapping) {
        this.infraestructureConfiguration = infraestructureConfiguration;
        mappingConfiguration = new LinkedList<>();

        for (var activityToNodesmapping : activityToNodesMapping.mappingActivitiesToNodes) {
            var activityID = activityToNodesmapping.activityName;
            var nodes = Arrays.stream(activityToNodesmapping.nodeNames)
                              .map(this::getNodeWithId)
                              .toArray(InfraestructureNodeDto[]::new);

            mappingConfiguration.add(new KeyValuePair<>(activityID, new RoundRobinSupplier<>(nodes)));
        }
    }

    private InfraestructureNodeDto getNodeWithId(String e) {
        for (var node : infraestructureConfiguration.nodes) {
            if (Objects.equals(node.nodeName, e)) return node;
        }
        return null;
    }

    @Override
    public InfraestructureNodeDto getNodeForActivityWithId(String id) {
        for (var mapping : mappingConfiguration) {
            if (id.contains(mapping.key)) return mapping.value.get();
        }
        return null;
    }

    public class KeyValuePair<T, U> {
        public final T key;
        public final U value;

        public KeyValuePair(T key, U value) {
            this.key = key;
            this.value = value;
        }
    }
}
