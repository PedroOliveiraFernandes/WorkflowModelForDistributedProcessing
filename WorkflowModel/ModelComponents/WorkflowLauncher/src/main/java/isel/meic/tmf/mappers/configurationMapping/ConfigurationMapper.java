package isel.meic.tmf.mappers.configurationMapping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import isel.meic.tmf.models.infraestructure.InfraestructureSpecificationDto;
import isel.meic.tmf.models.workflowConfiguration.MappingActivitiesToNodesSpecification;
import isel.meic.tmf.models.workflowConfiguration.WorkflowSpecificationDto;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigurationMapper {

    public static InfraestructureSpecificationDto mapInfraestructureSpecification(String[] args) {
        return mapOrReturnNull(0, args, InfraestructureSpecificationDto.class);
    }

    public static WorkflowSpecificationDto mapWorkflowSpecification(String[] args) {
        return mapOrReturnNull(1, args, WorkflowSpecificationDto.class);
    }

    public static MappingActivitiesToNodesSpecification mapAtivityToNodesMappingSpecification(String[] args) {
        return mapOrReturnNull(3, args, MappingActivitiesToNodesSpecification.class);
    }

    private static <T> T mapOrReturnNull(int i, String[] args, Class<T> classOfT) {
        if (args.length < i + 1) return null;
        return mapFromFile(args[i], classOfT);
    }

    private static <T> T mapFromFile(String path, Class<T> classOfT) {
        try {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
                                         .create();
            Reader reader = Files.newBufferedReader(Paths.get(path));
            var resultsModel = gson.fromJson(reader, classOfT);
            reader.close();
            return resultsModel;
        } catch (IOException e) {
            return null;
        }
    }
}
