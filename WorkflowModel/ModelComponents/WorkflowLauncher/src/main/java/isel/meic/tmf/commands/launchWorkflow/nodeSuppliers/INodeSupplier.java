package isel.meic.tmf.commands.launchWorkflow.nodeSuppliers;

import isel.meic.tmf.models.infraestructure.InfraestructureNodeDto;

public interface INodeSupplier {
    InfraestructureNodeDto getNodeForActivityWithId(String id);
}
