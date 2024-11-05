package com.family_tree.familytree;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ExportConfigurationRepository extends CrudRepository<ExportConfiguration, Integer> {

    // Custom method to find export configurations by family tree ID
    List<ExportConfiguration> findByFamilyTreeId(Integer familyTreeId);

    //Find Export Configurations by Format
    List<ExportConfiguration> findByFormat(String format);

}
