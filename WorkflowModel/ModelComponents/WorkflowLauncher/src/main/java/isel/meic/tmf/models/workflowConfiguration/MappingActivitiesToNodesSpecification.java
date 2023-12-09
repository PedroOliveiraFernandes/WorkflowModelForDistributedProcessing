package isel.meic.tmf.models.workflowConfiguration;

public class MappingActivitiesToNodesSpecification {
    public MappingActivityToNode[] mappingActivitiesToNodes;

    public class MappingActivityToNode {
        public String activityName;
        public String[] nodeNames;
    }
}
